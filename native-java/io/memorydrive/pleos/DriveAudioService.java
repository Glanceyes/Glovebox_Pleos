package io.memorydrive.pleos;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.*;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.*;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import java.util.*;
import org.json.*;

/** A car media session for tracks on the user's vehicle. Video and activity lifecycles do not own playback. */
public final class DriveAudioService extends MediaBrowserService {
    static final String READ=Build.VERSION.SDK_INT>=33?"android.permission.READ_MEDIA_AUDIO":"android.permission.READ_EXTERNAL_STORAGE";
    static DriveAudioService active;
    private static final String CHANNEL="drive-audio";
    private MediaPlayer player;
    private MediaSession session;
    private AudioManager audio;
    private AudioFocusRequest focus;
    private boolean prepared,wantPlay,resumeOnGain;
    private JSONObject track;
    private int initialSeek;
    static JSONArray files(Context ctx){
        JSONArray rows=new JSONArray();
        if(ctx.checkSelfPermission(READ)!=PackageManager.PERMISSION_GRANTED)return rows;
        String[] columns={"_id","title","artist","duration"};
        try(Cursor c=ctx.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,columns,"is_music!=0 AND duration>0",null,"title COLLATE NOCASE")){
            if(c!=null)while(c.moveToNext())rows.put(new JSONObject().put("id",c.getString(0)).put("title",c.getString(1)).put("artist","<unknown>".equals(c.getString(2))?"":c.getString(2)).put("duration",c.getLong(3)));
        }catch(Exception e){android.util.Log.w("GloveboxAudio","device library",e);}
        return rows;
    }
    static JSONObject file(Context c,String id){JSONArray rows=files(c);for(int i=0;i<rows.length();i++){JSONObject r=rows.optJSONObject(i);if(id.equals(r.optString("id")))return r;}return null;}
    static String normal(String text){return text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]","");}
    static JSONObject match(Context c,String title,String artist){
        JSONArray rows=files(c);JSONObject found=null;
        for(int i=0;i<rows.length();i++){JSONObject r=rows.optJSONObject(i);if(!normal(title).equals(normal(r.optString("title"))))continue;
            String a=normal(artist),b=normal(r.optString("artist"));if(a.isEmpty()||b.isEmpty()||!a.equals(b))continue;
            if(found!=null)return null;found=r;
        }return found;
    }
    private static synchronized String commandKey(Context c){
        SharedPreferences prefs=c.getSharedPreferences("internal-media",0);
        String key=prefs.getString("key",null);
        if(key==null){key=UUID.randomUUID().toString();prefs.edit().putString("key",key).apply();}
        return key;
    }
    static void command(Context ctx,String action,String id,int ms){
        Intent i=new Intent(ctx,DriveAudioService.class).putExtra("owner",commandKey(ctx)).setAction(action).putExtra("id",id).putExtra("position",ms);
        if("choose".equals(action))ctx.startForegroundService(i);else if(active!=null)ctx.startService(i);
    }
    static JSONObject status(){
        JSONObject o=new JSONObject();DriveAudioService s=active;if(s==null||s.track==null)return o;
        try{o.put("canSeek",s.prepared).put("track",s.track).put("playing",s.prepared&&s.player.isPlaying()).put("position",s.prepared?s.player.getCurrentPosition():0);}catch(Exception ignored){}return o;
    }
    static void seekCurrent(long ms){
        DriveAudioService s=active;
        if(s!=null&&s.prepared&&s.player!=null){
            s.player.seekTo((int)Math.max(0,Math.min(ms,s.player.getDuration())));s.publish();
        }
    }
    @Override public void onCreate(){super.onCreate();active=this;
        audio=(AudioManager)getSystemService(AUDIO_SERVICE);
        focus=new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            .setOnAudioFocusChangeListener(change->{if(change==AudioManager.AUDIOFOCUS_GAIN){if(player!=null)player.setVolume(1,1);if(resumeOnGain){resumeOnGain=false;startAudio();}}
                else if(change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){if(player!=null)player.setVolume(.2f,.2f);}
                else{boolean was=prepared&&player.isPlaying();pauseAudio();resumeOnGain=was&&change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;}}).build();
        session=new MediaSession(this,"Glovebox / 차량 음악");session.setCallback(new MediaSession.Callback(){
            @Override public void onPlay(){startAudio();}@Override public void onPause(){pauseAudio();}@Override public void onStop(){end();}
            @Override public void onSeekTo(long ms){if(prepared){player.seekTo((int)ms);publish();}}
            @Override public void onPlayFromMediaId(String id,Bundle args){choose(id,0);}
        });setSessionToken(session.getSessionToken());
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel(CHANNEL,"차량 음악",NotificationManager.IMPORTANCE_LOW));
    }
    @Override public BrowserRoot onGetRoot(String pkg,int uid,Bundle hints){
        if(uid!=getApplicationInfo().uid&&getPackageManager().checkSignatures(uid,android.os.Process.SYSTEM_UID)!=PackageManager.SIGNATURE_MATCH)return null;
        return new BrowserRoot("music",null);
    }
    @Override public void onLoadChildren(String parent,Result<List<MediaItem>> result){
        List<MediaItem> list=new ArrayList<>();if("music".equals(parent)){JSONArray tracks=files(this);for(int i=0;i<tracks.length();i++){JSONObject t=tracks.optJSONObject(i);list.add(new MediaItem(new MediaDescription.Builder().setMediaId(t.optString("id")).setTitle(t.optString("title")).setSubtitle(t.optString("artist")).build(),MediaItem.FLAG_PLAYABLE));}}result.sendResult(list);
    }
    @Override public int onStartCommand(Intent i,int flags,int startId){
        if(i!=null&&commandKey(this).equals(i.getStringExtra("owner"))){String a=i.getAction();if("choose".equals(a))choose(i.getStringExtra("id"),i.getIntExtra("position",0));else if("toggle".equals(a)){if(prepared&&player.isPlaying())pauseAudio();else startAudio();}else if("stop".equals(a))end();}return START_NOT_STICKY;
    }
    private Notification notice(){return new Notification.Builder(this,CHANNEL).setSmallIcon(android.R.drawable.ic_media_play).setContentTitle(track==null?"차량 음악":track.optString("title")).setContentText(track==null?"":track.optString("artist")).setStyle(new Notification.MediaStyle().setMediaSession(session.getSessionToken())).build();}
    private void choose(String id,int ms){
        JSONObject next=id==null?null:file(this,id);startForeground(734,notice());if(next==null){end();return;}
        releasePlayer();track=next;getSharedPreferences("internal-media",0).edit().putString("last-track",id).apply();initialSeek=Math.max(0,ms);wantPlay=true;session.setActive(true);
        session.setMetadata(new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_TITLE,track.optString("title")).putString(MediaMetadata.METADATA_KEY_ARTIST,track.optString("artist")).putLong(MediaMetadata.METADATA_KEY_DURATION,track.optLong("duration")).build());
        try{player=new MediaPlayer();player.setAudioAttributes(focus.getAudioAttributes());player.setDataSource(this,ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(id)));
            player.setOnPreparedListener(p->{if(p!=player)return;prepared=true;if(initialSeek>0)p.seekTo(Math.min(initialSeek,p.getDuration()));if(wantPlay)startAudio();else publish();});
            player.setOnCompletionListener(p->{if(p==player){wantPlay=false;publish();audio.abandonAudioFocusRequest(focus);}});player.setOnErrorListener((p,w,e)->{if(p==player)end();return true;});player.prepareAsync();publish();
        }catch(Exception e){android.util.Log.w("GloveboxAudio","open audio",e);end();}
    }
    private void startAudio(){wantPlay=true;if(prepared&&audio.requestAudioFocus(focus)==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){player.start();publish();}}
    private void pauseAudio(){wantPlay=false;resumeOnGain=false;if(prepared&&player.isPlaying())player.pause();publish();}
    private void publish(){boolean running=prepared&&player.isPlaying();session.setPlaybackState(new PlaybackState.Builder().setActions(PlaybackState.ACTION_PLAY|PlaybackState.ACTION_PAUSE|PlaybackState.ACTION_PLAY_PAUSE|PlaybackState.ACTION_SEEK_TO|PlaybackState.ACTION_STOP|PlaybackState.ACTION_PLAY_FROM_MEDIA_ID).setState(!prepared?PlaybackState.STATE_BUFFERING:running?PlaybackState.STATE_PLAYING:PlaybackState.STATE_PAUSED,prepared?player.getCurrentPosition():0,running?1:0).build());if(track!=null)startForeground(734,notice());}
    private void releasePlayer(){prepared=false;if(player!=null){player.release();player=null;}}
    private void end(){releasePlayer();track=null;session.setActive(false);audio.abandonAudioFocusRequest(focus);stopForeground(true);stopSelf();}
    @Override public void onDestroy(){releasePlayer();track=null;audio.abandonAudioFocusRequest(focus);session.release();if(active==this)active=null;super.onDestroy();}
}
