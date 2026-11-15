package io.memorydrive.pleos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.view.*;
import android.webkit.WebView;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.widget.FrameLayout;
import java.lang.reflect.Proxy;
import java.util.*;
import org.json.*;
import static io.memorydrive.pleos.AlbumPlayer.*;

/** Places only the Android player or interactive map in a slot measured by the original web layout. */
final class CockpitSurface {
    private final Activity activity;
    private final FrameLayout layer;
    private final WebView shell;
    private final AlbumPlayer catalog;
    private View surface;
    private Object controller;
    private String slot="";
    private int generation,seconds,state=-1,lengthSeconds;
    private long nextLengthRead;
    private MediaSession albumSession;
    private JSONObject albumTrack;
    private boolean pendingPlay,continueAudio;
    CockpitSurface(Activity activity,FrameLayout layer,WebView shell,AlbumPlayer catalog){
        this.activity=activity;this.layer=layer;this.shell=shell;this.catalog=catalog;
    }
    void clear(){
        generation++;slot="";lengthSeconds=0;nextLengthRead=0;albumTrack=null;
        if(albumSession!=null){albumSession.setActive(false);albumSession.release();albumSession=null;}
        pendingPlay=false;continueAudio=false;state=-1;seconds=0;
        if(surface!=null){layer.removeView(surface);surface=null;}
        if(controller!=null){call(controller,"close");controller=null;}
        if(shell.getLayerType()!=View.LAYER_TYPE_NONE)shell.setLayerType(View.LAYER_TYPE_NONE,null);
    }
    void position(JSONObject message)throws Exception{
        String kind=message.getString("kind"),id=message.getString("id");
        if(!Arrays.asList("track","trip","course").contains(kind))return;
        int x=message.getInt("x"),y=message.getInt("y"),w=message.getInt("w"),h=message.getInt("h");
        if(w<10||h<10||x<0||y<0||x+w>shell.getWidth()+3||y+h>shell.getHeight()+3){clear();return;}
        String key=kind+"/"+id;
        if(!key.equals(slot)){
            clear();slot=key;
            if("track".equals(kind)){if(!catalog.permits(id))return;createPlayer(id);}
            else createMap(kind,id);
        }
        if(surface==null)return;
        FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(w,h,Gravity.TOP|Gravity.LEFT);
        p.leftMargin=x+shell.getLeft();p.topMargin=y+shell.getTop();surface.setLayoutParams(p);
        if(surface.getParent()==null){layer.addView(surface,p);if(!kind.equals("track"))surface.post(()->{if(key.equals(slot)&&surface!=null)call(surface,"fit");});}
        if(message.optBoolean("start")&&controller!=null){pendingPlay=true;call(controller,"play");}
    }
    public static void loadOfficialPlayer(WebView view,String page){
        String origin="https://"+view.getContext().getPackageName();
        view.loadDataWithBaseURL(origin+"/",page.replace("https://io.memorydrive.pleos",origin),"text/html","UTF-8",null);
    }
    private void createPlayer(String id)throws Exception{
        final int active=generation;
        albumTrack=catalog.identify(id);
        Object track=catalog.accountTrack(id);
        Object session=call(cls("PlatformSessionStore").getConstructor(Context.class).newInstance(activity),"load");
        String namespace=session==null?"local":String.valueOf(call(session,"getNamespace"));
        android.content.SharedPreferences listening=activity.getSharedPreferences("listening-history-"+namespace,Context.MODE_PRIVATE);
        Object gate=cls("ListeningProgressGate").getConstructor().newInstance();
        boolean[] recorded={false};
        Class<?> fn=Class.forName("kotlin.jvm.functions.Function1");
        Object listener=Proxy.newProxyInstance(fn.getClassLoader(),new Class[]{fn},(proxy,m,args)->{
            if(!"invoke".equals(m.getName())||active!=generation||controller==null)return null;
            Object st=args[0];int next=(Integer)call(st,"getState"),s=((Double)call(st,"getSeconds")).intValue();
            boolean ready=(Boolean)call(st,"getReady");int error=(Integer)call(st,"getError");
            if(next==1)continueAudio=true;
            if(next==2||next==0||error>0)continueAudio=false;
            if(track!=null&&!recorded[0]&&listening.getBoolean("rememberPlayer",false)){
                long now=System.currentTimeMillis();
                double progress=(Double)call(st,"getSeconds");
                if(Boolean.TRUE.equals(call(gate,"observe",next==1,progress,now))){
                    recorded[0]=true;
                    new Thread(()->{
                        try{
                            if(!listening.getBoolean("rememberPlayer",false))return;
                            Object store=cls("ListeningHistoryStore").getConstructor(Context.class,cls("PlatformSession")).newInstance(activity,session);
                            call(store,"recordConfirmedPlayer",track,now);
                        }catch(Exception ex){android.util.Log.w("GloveboxAlbumPlayer","Listening history write failed",ex);}
                    },"album-listening-history").start();
                }
            }
            if(ready&&lengthSeconds==0)readLength(active);
            if(pendingPlay&&ready){pendingPlay=false;call(controller,"play");}
            if(next!=state||s!=seconds||error>0){
                if(next!=state||(s!=seconds&&s%5==0))android.util.Log.i("GloveboxAlbumPlayer","state="+next+" seconds="+s+" error="+error);
                state=next;seconds=s;publishAlbum();shell.evaluateJavascript("window.glTrackState&&glTrackState("+next+","+s+","+error+")",null);}
            return null;
        });
        controller=cls("ParkedYouTubeController").getConstructor(Context.class,String.class,fn).newInstance(activity,id,listener);
        surface=(View)call(controller,"getView");
    }
    private void readLength(int active){
        long now=android.os.SystemClock.elapsedRealtime();
        if(now<nextLengthRead||!(surface instanceof WebView))return;
        nextLengthRead=now+1500;
        ((WebView)surface).evaluateJavascript("window.memoryReady&&memoryPlayer.getDuration?Math.floor(memoryPlayer.getDuration()):0",v->{
            if(active!=generation)return;
            try{int length=Integer.parseInt(v);if(length<=0)return;lengthSeconds=length;}catch(NumberFormatException ignored){return;}
            setAlbumMetadata();publishAlbum();
            shell.evaluateJavascript("window.glTrackDuration&&glTrackDuration("+lengthSeconds+")",null);
        });
    }
    void seekTo(long ms){
        if(!(surface instanceof WebView)||!slot.startsWith("track/")||lengthSeconds<=0)return;
        long position=Math.max(0,Math.min(ms,lengthSeconds*1000L));
        ((WebView)surface).evaluateJavascript("if(window.memoryReady)memoryPlayer.seekTo("+position+"/1000,true)",null);
    }
    private void setAlbumMetadata(){
        if(albumSession==null||albumTrack==null)return;
        MediaMetadata.Builder b=new MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID,albumTrack.optString("id"))
            .putString(MediaMetadata.METADATA_KEY_TITLE,albumTrack.optString("title"))
            .putString(MediaMetadata.METADATA_KEY_ARTIST,albumTrack.optString("artist"));
        if(lengthSeconds>0)b.putLong(MediaMetadata.METADATA_KEY_DURATION,lengthSeconds*1000L);
        albumSession.setMetadata(b.build());
    }
    private void publishAlbum(){
        if(albumSession==null){
            if(state!=1||albumTrack==null)return;
            albumSession=new MediaSession(activity,"Glovebox album");
            final int created=generation;
            albumSession.setCallback(new MediaSession.Callback(){
                @Override public void onPlay(){if(created==generation)setPlaying(true);}
                @Override public void onPause(){if(created==generation)setPlaying(false);}
                @Override public void onSeekTo(long ms){if(created==generation)seekTo(ms);}
            });
            setAlbumMetadata();albumSession.setActive(true);
        }
        int mode=state==1?PlaybackState.STATE_PLAYING:state==2?PlaybackState.STATE_PAUSED:state==3?PlaybackState.STATE_BUFFERING:PlaybackState.STATE_STOPPED;
        long actions=PlaybackState.ACTION_PLAY|PlaybackState.ACTION_PAUSE|PlaybackState.ACTION_PLAY_PAUSE;
        if(lengthSeconds>0)actions|=PlaybackState.ACTION_SEEK_TO;
        albumSession.setPlaybackState(new PlaybackState.Builder().setActions(actions)
            .setState(mode,seconds*1000L,state==1?1f:0f).build());
    }
    private String parkedId="";
    private int parkedSeconds;
    int resumeAt(String id){return id.equals(parkedId)?parkedSeconds:0;}
    void drive(DriveAudioHandoff audio){
        if(controller!=null&&slot.startsWith("track/")){parkedId=slot.substring(6);parkedSeconds=seconds;}
        if(controller!=null&&continueAudio&&slot.startsWith("track/")){
            String id=slot.substring(6);JSONObject t=catalog.identify(id);audio.handoff(id,t.optString("title"),t.optString("artist"),seconds);
        }
        clear();
    }
    void setPlaying(boolean play){
        if(controller==null)return;
        pendingPlay=play&&state<0;
        if(!play)continueAudio=false;
        call(controller,play?"play":"pause");
    }
    void toggle(){setPlaying(state!=1&&state!=3);}
    private void createMap(String kind,String id)throws Exception{
        View map=(View)cls("CommunityMapView").getConstructor(Context.class).newInstance(activity);
        call(map,"enableJournalDisplay");surface=map;
        map.setOutlineProvider(new ViewOutlineProvider(){public void getOutline(View v,Outline out){out.setRoundRect(0,0,v.getWidth(),v.getHeight(),12f);}});map.setClipToOutline(true);
        if("trip".equals(kind)){
            shell.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            for(Object t:trips(GloveboxMotion.engine))if(id.equals(call(t,"getId"))){
                List<Object> points=new ArrayList<>();
                for(Object p:(List<?>)call(t,"getPoints"))points.add(cls("RoadPoint").getConstructor(double.class,double.class).newInstance(call(p,"getLatitude"),call(p,"getLongitude")));
                Object road=cls("RoadRoute").getConstructor(List.class,double.class,double.class,List.class).newInstance(points,0d,0d,Collections.emptyList());
                call(map,"update",Collections.emptyList(),Collections.emptyList(),null,road);return;
            }
        }else{
            Object store=cls("CourseStore").getConstructor(Context.class).newInstance(activity);
            for(Object c:(List<?>)call(store,"all"))if(id.equals(call(c,"getId"))){
                List<?> stops=(List<?>)call(c,"getStops");Object road=savedRoad(activity,stops);
                call(map,"update",Collections.emptyList(),stops,null,road);
                if(road==null && (CourseRoads.allowed(activity)||CourseRoads.connectedServiceAllowed(activity)))fetchCourse(id,stops,map);
                return;
            }
        }
    }
    void loadCourseRoad(String id)throws Exception {
        if(!slot.equals("course/"+id)||surface==null)return;
        Object store=cls("CourseStore").getConstructor(Context.class).newInstance(activity);
        View map=surface;
        for(Object c:(List<?>)call(store,"all"))if(id.equals(call(c,"getId"))){
            List<?> stops=(List<?>)call(c,"getStops");
            CourseRoads.authorize(activity,()->{if(map==surface)fetchCourse(id,stops,map);});return;
        }
    }
    private int requestVersion;
    private void fetchCourse(String id,List<?> stops,View map){
        int revision=generation,request=++requestVersion;
        new Thread(()->{
            try{
                Object road=CourseRoads.fetch(activity,stops);
                activity.runOnUiThread(()->{
                    if(generation!=revision||requestVersion!=request)return;
                    call(map,"update",Collections.emptyList(),stops,null,road);call(map,"fit");
                    shell.evaluateJavascript("window.glCourseDistance&&glCourseDistance("+JSONObject.quote(id)+","+call(road,"getMeters")+","+call(road,"getSeconds")+")",null);
                });
            }catch(Exception e){activity.runOnUiThread(()->{if(generation==revision&&requestVersion==request)shell.evaluateJavascript("window.glCourseRoadError&&glCourseRoadError()",null);});}
        },"glovebox-course-route").start();
    }

}
