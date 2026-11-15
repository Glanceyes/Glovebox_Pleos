package io.memorydrive.pleos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/** Reads the phone audio sources offered by the head unit through Android's public media API. */
final class PhoneAudioRoute {
    static final String PROJECTION="ai.umos.phoneprojection", BLUETOOTH="com.android.bluetooth";
    private final Context context;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final List<PhoneBrowser> browsers=new ArrayList<>();
    private long nextDiscovery;
    private final List<MediaController> inputs=new ArrayList<>();
    private boolean watching;
    private MediaController lastPlaying;
    private int request;
    private String selectedId="",selectedTitle="",selectedArtist="",notice="";
    final IphoneMediaRemote iphone;
    PhoneAudioRoute(Context context){this.context=context;iphone=new IphoneMediaRemote(context);}

    private final class PhoneBrowser extends MediaBrowser.ConnectionCallback {
        final ComponentName component;
        final MediaBrowser browser;
        MediaController controller;
        long retryAt;
        final MediaController.Callback lifecycle=new MediaController.Callback(){
            @Override public void onSessionDestroyed(){retry();}
        };
        PhoneBrowser(ComponentName component){
            this.component=component;
            browser=new MediaBrowser(context,component,this,null);
        }
        void clearController(){
            if(controller==null)return;
            inputs.remove(controller);
            if(lastPlaying==controller)lastPlaying=null;
            controller.unregisterCallback(lifecycle);controller=null;
        }
        void connect(){
            retryAt=Long.MAX_VALUE;
            try{browser.connect();}catch(RuntimeException e){retry();}
        }
        @Override public void onConnected(){
            if(!watching||!browsers.contains(this))return;
            clearController();
            controller=new MediaController(context,browser.getSessionToken());
            controller.registerCallback(lifecycle,handler);
            inputs.add(controller);
        }
        @Override public void onConnectionSuspended(){clearController();}
        @Override public void onConnectionFailed(){retry();}
        void retry(){
            clearController();
            if(!watching||!browsers.contains(this))return;
            try{browser.disconnect();}catch(RuntimeException ignored){}
            retryAt=SystemClock.elapsedRealtime()+5000;
        }
        void close(){
            clearController();
            try{browser.disconnect();}catch(RuntimeException ignored){}
        }
    }
    void watch(){
        iphone.watch();
        watching=true;
        long now=SystemClock.elapsedRealtime();
        if(now>=nextDiscovery){
            nextDiscovery=now+10000;
            for(String pkg:new String[]{PROJECTION,BLUETOOTH}){
                Intent intent=new Intent("android.media.browse.MediaBrowserService").setPackage(pkg);
                for(ResolveInfo info:context.getPackageManager().queryIntentServices(intent,0)){
                    ComponentName name=new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name);
                    boolean found=false;
                    for(PhoneBrowser bound:browsers)if(name.equals(bound.component)){found=true;break;}
                    if(!found)browsers.add(new PhoneBrowser(name));
                }
            }
        }
        for(PhoneBrowser bound:browsers)if(now>=bound.retryAt)bound.connect();
    }
    void close(){
        cancelRequest();watching=false;nextDiscovery=0;lastPlaying=null;
        for(PhoneBrowser b:browsers)b.close();
        browsers.clear();inputs.clear();
    }
    void cancelRequest(){request++;selectedId="";selectedTitle="";selectedArtist="";notice="";}
    private static String clean(String value){return DriveAudioService.normal(value==null?"":value.replaceFirst("(?i) - (Topic|주제)$",""));}
    static boolean metadataMatches(CharSequence name,CharSequence artist,String title,String by){
        return name!=null&&artist!=null&&!clean(by).isEmpty()&&clean(name.toString()).equals(clean(title))&&clean(artist.toString()).equals(clean(by));
    }
    static boolean matches(MediaController c,String id,String title,String artist){
        MediaMetadata item=c.getMetadata();
        if(item==null)return false;
        String mediaId=item.getString(MediaMetadata.METADATA_KEY_MEDIA_ID);
        return id.equals(mediaId)||metadataMatches(item.getString(MediaMetadata.METADATA_KEY_TITLE),item.getString(MediaMetadata.METADATA_KEY_ARTIST),title,artist);
    }
    private static boolean playing(MediaController c){PlaybackState s=c.getPlaybackState();return s!=null&&s.getState()==PlaybackState.STATE_PLAYING;}
    private static long transportAction(long supported,boolean play){
        long direct=play?PlaybackState.ACTION_PLAY:PlaybackState.ACTION_PAUSE;
        if((supported&direct)!=0)return direct;
        return supported&PlaybackState.ACTION_PLAY_PAUSE;
    }
    private static boolean control(MediaController c,boolean play){
        PlaybackState state=c.getPlaybackState();
        if(state==null)return false;
        if(state.getState()==(play?PlaybackState.STATE_PLAYING:PlaybackState.STATE_PAUSED))return true;
        long action=transportAction(state.getActions(),play);
        if(action==0)return false;
        if(action==PlaybackState.ACTION_PLAY_PAUSE){
            long now=SystemClock.uptimeMillis();
            KeyEvent down=new KeyEvent(now,now,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,0);
            boolean handled=c.dispatchMediaButtonEvent(down);
            boolean released=c.dispatchMediaButtonEvent(KeyEvent.changeAction(down,KeyEvent.ACTION_UP));
            return handled&&released;
        }
        if(play)c.getTransportControls().play();else c.getTransportControls().pause();
        return true;
    }
    static boolean requestSong(MediaController c,String id,String title,String artist,boolean allowCatalog){
        PlaybackState state=c.getPlaybackState();
        if(state==null||state.getState()==PlaybackState.STATE_ERROR)return false;
        long actions=state.getActions();
        MediaController.TransportControls controls=c.getTransportControls();
        if(matches(c,id,title,artist)){
            if(playing(c))return true;
            if(control(c,true))return true;
        }
        if((actions&PlaybackState.ACTION_SKIP_TO_QUEUE_ITEM)!=0){
            List<MediaSession.QueueItem> items=c.getQueue();
            if(items!=null)for(MediaSession.QueueItem q:items){
                MediaDescription d=q.getDescription();
                if(!id.equals(d.getMediaId())&&!metadataMatches(d.getTitle(),d.getSubtitle(),title,artist))continue;
                controls.skipToQueueItem(q.getQueueId());
                if(!playing(c))control(c,true);
                return true;
            }
        }
        if(allowCatalog&&(actions&PlaybackState.ACTION_PLAY_FROM_URI)!=0){
            controls.playFromUri(Uri.parse("https://music.youtube.com/watch?v="+id),null);return true;
        }
        if(allowCatalog&&(actions&PlaybackState.ACTION_PLAY_FROM_SEARCH)!=0){
            Bundle extra=new Bundle();extra.putString(MediaStore.EXTRA_MEDIA_TITLE,title);extra.putString(MediaStore.EXTRA_MEDIA_ARTIST,artist);
            controls.playFromSearch(title+" "+artist,extra);return true;
        }
        return false;
    }
    void select(String id,String title,String artist){
        watch();selectedId=id;selectedTitle=title;selectedArtist=artist;
        notice="휴대폰 음악 연결 중";int serial=++request;
        trySelect(serial,0);
    }
    private void trySelect(int serial,int attempt){
        if(serial!=request||!watching)return;
        // Prefer an already playing match across all transports before requesting a new song.
        for(MediaController c:inputs)if(playing(c)&&matches(c,selectedId,selectedTitle,selectedArtist)){notice="";return;}
        if(iphone.matches(selectedTitle,selectedArtist)&&(iphone.playing()||iphone.control(true))){notice="";return;}
        for(MediaController c:inputs){
            try{
                if(requestSong(c,selectedId,selectedTitle,selectedArtist,PROJECTION.equals(c.getPackageName()))){
                    notice="휴대폰 재생 확인 중";
                    handler.postDelayed(()->{if(serial==request&&(selected()==null||!playing(selected())))notice="휴대폰의 YouTube Music에서 이 곡을 선택해 주세요.";},4500);
                    return;
                }
            }catch(RuntimeException ignored){}
        }
        if(attempt<5){handler.postDelayed(()->trySelect(serial,attempt+1),300);return;}
        notice="휴대폰의 YouTube Music에서 이 곡을 선택해 주세요.";
    }
    private MediaController selected(){
        if(selectedId.isEmpty())return null;
        for(MediaController c:inputs)if(matches(c,selectedId,selectedTitle,selectedArtist))return c;
        return null;
    }
    private MediaController current(){
        MediaController chosen=selected();
        if(chosen!=null&&playing(chosen)){lastPlaying=chosen;return chosen;}
        for(MediaController c:inputs)if(playing(c)&&c.getMetadata()!=null){lastPlaying=c;return c;}
        if(chosen!=null&&chosen.getPlaybackState()!=null&&chosen.getPlaybackState().getState()==PlaybackState.STATE_PAUSED)return chosen;
        if(lastPlaying!=null&&inputs.contains(lastPlaying)&&lastPlaying.getPlaybackState()!=null&&lastPlaying.getPlaybackState().getState()==PlaybackState.STATE_PAUSED)return lastPlaying;
        for(MediaController c:inputs){
            PlaybackState s=c.getPlaybackState();MediaMetadata m=c.getMetadata();
            if(s!=null&&s.getState()==PlaybackState.STATE_PAUSED&&m!=null
                &&!android.text.TextUtils.isEmpty(m.getString(MediaMetadata.METADATA_KEY_TITLE)))return c;
        }
        return null;
    }
    void pauseOwned(){
        MediaController chosen=selected();
        if(chosen!=null&&playing(chosen))control(chosen,false);
    }
    boolean toggle(){
        if(useIphone())return iphone.control(!iphone.playing());
        MediaController c=current();if(c==null)return false;
        return control(c,!playing(c));
    }
    void seekTo(long ms){
        if(useIphone())return;
        MediaController c=current();PlaybackState s=c==null?null:c.getPlaybackState();
        if(s!=null&&(s.getActions()&PlaybackState.ACTION_SEEK_TO)!=0)c.getTransportControls().seekTo(ms);
    }
    boolean available(){
        if(iphone.ready())return true;
        for(MediaController c:inputs){
            PlaybackState s=c.getPlaybackState();
            if(s==null||s.getState()==PlaybackState.STATE_ERROR||s.getState()==PlaybackState.STATE_NONE)continue;
            if(c.getMetadata()!=null)return true;
        }
        return false;
    }
    private boolean usbIphone(){
        android.hardware.usb.UsbManager usb=context.getSystemService(android.hardware.usb.UsbManager.class);
        if(usb==null)return false;
        try{
            for(android.hardware.usb.UsbDevice device:usb.getDeviceList().values())
                if(device.getVendorId()==0x05ac&&"iPhone".equalsIgnoreCase(device.getProductName()))return true;
        }catch(SecurityException ignored){}
        return false;
    }
    String missingInput(){
        if(usbIphone())return "iPhone USB 연결 · 재생 정보 대기";
        return "휴대폰 오디오 연결 대기";
    }
    String drivingMessage(){
        if(notice.isEmpty())return "";
        return available()?"휴대폰 재생 정보 확인 중":missingInput();
    }
    String message(){return notice;}
    static long elapsedTrackMs(PlaybackState state){
        long position=state.getPosition();
        if(position<0)return PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        long timestamp=state.getLastPositionUpdateTime();
        if(state.getState()==PlaybackState.STATE_PLAYING&&timestamp>0){
            long elapsed=Math.max(0,SystemClock.elapsedRealtime()-timestamp);
            position+=(long)(elapsed*state.getPlaybackSpeed());
        }
        return Math.max(0,position);
    }
    private boolean useIphone(){
        if(!iphone.ready())return false;
        MediaController car=current();
        if(car==null)return true;
        if(iphone.playing())return true;
        return !playing(car);
    }
    JSONObject status(){
        if(useIphone())return iphone.status(selectedId,selectedTitle,selectedArtist);
        JSONObject out=new JSONObject();MediaController c=current();if(c==null)return out;
        try{
            MediaMetadata m=c.getMetadata();PlaybackState s=c.getPlaybackState();if(m==null||s==null)return out;
            String title=m.getString(MediaMetadata.METADATA_KEY_TITLE);if(title==null||title.trim().isEmpty())return out;
            String artist=m.getString(MediaMetadata.METADATA_KEY_ARTIST);
            boolean same=!selectedId.isEmpty()&&matches(c,selectedId,selectedTitle,selectedArtist);
            long ms=elapsedTrackMs(s);boolean on=playing(c);
            JSONObject track=new JSONObject().put("id",same?"external:"+selectedId:"phone:"+title).put("title",title)
                .put("artist",artist==null?"":artist).put("duration",m.getLong(MediaMetadata.METADATA_KEY_DURATION));
            out.put("player",PROJECTION.equals(c.getPackageName())?"CarPlay":"Bluetooth").put("track",track).put("playing",on).put("position",ms).put("phone",true).put("canSeek",(s.getActions()&PlaybackState.ACTION_SEEK_TO)!=0).put("canToggle",transportAction(s.getActions(),!on)!=0).put("cover",same?selectedId:"");
        }catch(RuntimeException|org.json.JSONException ignored){}
        return out;
    }
}
