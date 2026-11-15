package io.memorydrive.pleos;

import android.content.*;
import android.media.*;
import android.media.browse.MediaBrowser;
import android.media.session.*;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import java.util.*;
import org.json.*;

/** Continue a selected video as music only through a matching audio recording or an audio provider. */
final class DriveAudioHandoff {
    private final Context context;
    final PhoneAudioRoute phone;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final List<MediaBrowser> browsers=new ArrayList<>();
    private MediaController controller;
    private int revision;
    static final String MUSIC="com.google.android.apps.youtube.music";
    static final String VIDEO="com.google.android.apps.automotive.youtube";
    String requested="",message="",sourceId="",sourceArtist="";
    private String openedProvider="";
    DriveAudioHandoff(Context c){context=c;phone=new PhoneAudioRoute(c);}
    void release(){
        phone.close();
        resetPlayer();
    }
    private void resetPlayer(){
        revision++;
        for(MediaBrowser b:browsers)try{b.disconnect();}catch(Exception ignored){}
        browsers.clear();controller=null;requested="";message="";sourceId="";sourceArtist="";openedProvider="";
    }
    private void select(String id,String title,String artist){
        phone.cancelRequest();resetPlayer();sourceId=id;requested=title;sourceArtist=artist;
    }
    void observePlayer(String id,String title,String artist,String pkg){
        select(id,title,artist);
        openedProvider=VIDEO.equals(pkg)?"YouTube":"YouTube Music";
        message=openedProvider+"에서 재생 상태 확인";
        observeSession(pkg);
        openBrowser(pkg,0,false);
    }
    String openedProvider(){return openedProvider;}
    private void observeSession(String pkg){
        try{
            MediaSessionManager manager=(MediaSessionManager)context.getSystemService(Context.MEDIA_SESSION_SERVICE);
            for(MediaController c:manager.getActiveSessions(null))if(pkg.equals(c.getPackageName())&&same(c,requested,sourceArtist)){
                controller=c;message="";return;
            }
        }catch(SecurityException ignored){}
    }
    void handoff(String id,String title,String artist,int seconds){
        if(!openedProvider.isEmpty()&&id.equals(sourceId))return;
        select(id,title,artist);
        if(!openBrowser(MUSIC,seconds,true)){fallback(id,title,artist,seconds);return;}
        message="YouTube Music 연결 중";
        int active=revision;
        handler.postDelayed(()->{
            if(active!=revision||controller!=null)return;
            select(id,title,artist);
            fallback(id,title,artist,seconds);
        },5200);
    }
    private void fallback(String id,String title,String artist,int seconds){
        phone.select(id,title,artist);
        int active=revision;
        handler.postDelayed(()->{
            if(active!=revision||phone.status().has("track"))return;
            JSONObject local=DriveAudioService.match(context,title,artist);
            if(local!=null){phone.cancelRequest();DriveAudioService.command(context,"choose",local.optString("id"),seconds*1000);}
        },5000);
    }
    void selectPhone(String id,String title,String artist){
        select(id,title,artist);
        phone.select(id,title,artist);
    }
    String audioMessage(){return phone.message().isEmpty()?message:phone.message();}
    private boolean openBrowser(String pkg,int seconds,boolean start){
        Intent intent=new Intent("android.media.browse.MediaBrowserService").setPackage(pkg);int serial=revision;
        List<android.content.pm.ResolveInfo> services=context.getPackageManager().queryIntentServices(intent,0);
        for(android.content.pm.ResolveInfo info:services){
            MediaBrowser[] ref=new MediaBrowser[1];
            MediaBrowser b=new MediaBrowser(context,new ComponentName(pkg,info.serviceInfo.name),new MediaBrowser.ConnectionCallback(){
                @Override public void onConnected(){
                    if(serial!=revision||controller!=null)return;
                    MediaController c=new MediaController(context,ref[0].getSessionToken());
                    if(start){
                        if(MUSIC.equals(pkg)){
                            PlaybackState caps=c.getPlaybackState();
                            if(caps!=null&&(caps.getActions()&PlaybackState.ACTION_PLAY_FROM_SEARCH)!=0&&(caps.getActions()&PlaybackState.ACTION_PLAY_FROM_URI)==0){
                                Bundle extras=new Bundle();extras.putString(MediaStore.EXTRA_MEDIA_TITLE,requested);extras.putString(MediaStore.EXTRA_MEDIA_ARTIST,sourceArtist);
                                c.getTransportControls().playFromSearch(requested+" "+sourceArtist,extras);
                            }else c.getTransportControls().playFromUri(Uri.parse("https://music.youtube.com/watch?v="+sourceId),null);
                        }else if(same(c,requested,sourceArtist))c.getTransportControls().play();
                        else return;
                    }
                    verify(serial,c,requested,sourceArtist,seconds,0);
                }
            },null);ref[0]=b;browsers.add(b);
            try{b.connect();}catch(RuntimeException ignored){}
        }
        return !services.isEmpty();
    }
    private boolean same(MediaController c,String title,String artist){
        MediaMetadata m=c.getMetadata();if(m==null)return false;
        String name=m.getString(MediaMetadata.METADATA_KEY_TITLE),by=m.getString(MediaMetadata.METADATA_KEY_ARTIST);
        return name!=null&&DriveAudioService.normal(title).equals(DriveAudioService.normal(name))&&(artist.isEmpty()||by!=null&&DriveAudioService.normal(by).equals(DriveAudioService.normal(artist)));
    }
    private void verify(int serial,MediaController c,String title,String artist,int seconds,int attempt){
        if(serial!=revision||controller!=null)return;
        PlaybackState state=c.getPlaybackState();
        if(same(c,title,artist)&&state!=null&&state.getState()==PlaybackState.STATE_PLAYING){
            controller=c;message="";if(seconds>0&&(state.getActions()&PlaybackState.ACTION_SEEK_TO)!=0)c.getTransportControls().seekTo(seconds*1000L);return;
        }
        if(attempt<11)handler.postDelayed(()->verify(serial,c,title,artist,seconds,attempt+1),350);
    }
    boolean playing(){PlaybackState p=controller==null?null:controller.getPlaybackState();return p!=null&&p.getState()==PlaybackState.STATE_PLAYING;}
    void seekTo(long ms){
        PlaybackState s=controller==null?null:controller.getPlaybackState();
        if(s!=null&&(s.getActions()&PlaybackState.ACTION_SEEK_TO)!=0)controller.getTransportControls().seekTo(ms);
    }
    void toggle(){if(controller!=null){if(playing())controller.getTransportControls().pause();else controller.getTransportControls().play();}}
    JSONObject status(){
        JSONObject out=new JSONObject();
        if(controller==null)return out;
        try{PlaybackState state=controller.getPlaybackState();MediaMetadata data=controller.getMetadata();
            if(state==null||data==null)return out;
            String title=data.getString(MediaMetadata.METADATA_KEY_TITLE),artist=data.getString(MediaMetadata.METADATA_KEY_ARTIST);long duration=data.getLong(MediaMetadata.METADATA_KEY_DURATION);
            long ms=PhoneAudioRoute.elapsedTrackMs(state);
            out.put("track",new JSONObject().put("id","external:"+sourceId).put("title",title==null?requested:title).put("artist",artist==null?sourceArtist:artist).put("duration",duration));
            out.put("playing",playing()).put("position",ms).put("canSeek",(state.getActions()&PlaybackState.ACTION_SEEK_TO)!=0);
        }catch(Exception ignored){}return out;
    }
    void stopExternal(){phone.pauseOwned();if(controller!=null&&playing())controller.getTransportControls().pause();release();}
    boolean connected(){return controller!=null;}
}
