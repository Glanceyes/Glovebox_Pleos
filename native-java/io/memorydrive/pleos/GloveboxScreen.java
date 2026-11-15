package io.memorydrive.pleos;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import org.json.*;

/** The approved local design, connected to the original engine and music service. */
public final class GloveboxScreen {
    public static boolean visible;
    private static GloveboxScreen current;
    private final Activity activity;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final Map<String,String> photos=Collections.synchronizedMap(new HashMap<>());
    private WebView web;
    private final AlbumPlayer library=new AlbumPlayer();
    private CockpitSurface inset;
    private final YouTubeLink youtubeLink;
    private JournalLibrary journalLibrary;
    private DriveAudioHandoff driveAudio;
    private CoreDeviceMusic deviceMusic;
    private DriveNowPlaying driveBar;
    private boolean previouslyBlocked,refreshAudioOnResume;
    private JSONObject driveChoice;
    private boolean phoneOnly;
    public static String driveRecommendationReason(){
        GloveboxScreen s=current;
        if(s==null||s.driveChoice==null)return "";
        String why=s.driveChoice.optString("reason");
        int split=why.indexOf(" · ");
        return split<0?why:why.substring(0,split);
    }
    private static String playerTitle(String pkg){return DriveAudioHandoff.VIDEO.equals(pkg)?"YouTube":"YouTube Music";}
    private boolean installed(String pkg){
        try{activity.getPackageManager().getApplicationInfo(pkg,0);return true;}
        catch(android.content.pm.PackageManager.NameNotFoundException e){return false;}
    }
    private void albumAudio(String id,String choice)throws Exception{
        if(!library.permits(id))return;
        Object apps=AlbumPlayer.cls("VehicleCatalogApps").getConstructor(Context.class).newInstance(activity);
        Object codec=AlbumPlayer.cls("MusicCatalogLink").getField("Companion").get(null);
        Object link=GloveboxMotion.call(codec,"parse","https://music.youtube.com/watch?v="+id);
        List<?> available=(List<?>)GloveboxMotion.call(apps,"inspect",link);
        String[] packages="video".equals(choice)?new String[]{DriveAudioHandoff.VIDEO}:
                "music".equals(choice)?new String[]{DriveAudioHandoff.MUSIC}:new String[]{DriveAudioHandoff.MUSIC,DriveAudioHandoff.VIDEO};
        for(String pkg:packages)for(Object app:available){
            Object target=get(app,"getTarget");
            if(!pkg.equals(get(target,"getPackageName"))||!"AVAILABLE".equals(String.valueOf(get(app,"getAvailability"))))continue;
            android.net.Uri uri=android.net.Uri.parse(String.valueOf(get(target,"getUrl")));
            Intent open=new Intent(Intent.ACTION_VIEW,uri).setPackage(pkg);
            open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try{
                JSONObject t=library.identify(id);inset.clear();
                activity.startActivity(open);
                driveAudio.observePlayer(id,t.optString("title"),t.optString("artist"),pkg);
                web.evaluateJavascript("window.glNativeOpened("+JSONObject.quote(id)+","+JSONObject.quote(playerTitle(pkg))+")",null);
                return;
            }catch(ActivityNotFoundException|SecurityException unavailable){android.util.Log.w("GloveboxMusic","Vehicle player unavailable: "+pkg);}
        }
        String msg="video".equals(choice)?"차량용 YouTube 앱을 설치한 뒤 다시 재생해 주세요.":
                "music".equals(choice)?"이 차량에 YouTube Music 앱이 설치돼 있는지 확인해 주세요.":"차량에 YouTube Music 또는 차량용 YouTube 앱을 연결해 주세요.";
        web.evaluateJavascript("window.glMusicUnavailable("+JSONObject.quote(id)+","+JSONObject.quote(msg)+")",null);
    }
    private JSONObject phonePlayback(){
        JSONObject nativeState=driveAudio.phone.status();
        if(nativeState.has("track")||deviceMusic==null)return nativeState;
        return deviceMusic.status();
    }
    private void togglePhone(JSONObject state){
        if(state.optBoolean("hostRadio")&&deviceMusic!=null)deviceMusic.toggle();
        else driveAudio.phone.toggle();
    }
    private JSONObject vehicleAudio(){
        JSONObject local=DriveAudioService.status(),remote=driveAudio.status(),phone=phonePlayback();
        if(phoneOnly||phone.optBoolean("playing"))return phone;
        if(remote.optBoolean("playing"))return remote;
        if(local.optBoolean("playing"))return local;
        if(phone.has("track"))return phone;
        if(remote.has("track"))return remote;
        return local;
    }
    public static Object iphoneJournalPlayback(){
        GloveboxScreen screen=current;
        if(screen==null||screen.stopped||screen.driveAudio==null)return null;
        JSONObject state=screen.phonePlayback();
        if(!state.optBoolean("phone"))return null;
        JSONObject track=state.optJSONObject("track");
        if(track==null)return null;
        try{
            String title=track.getString("title"),artist=track.optString("artist");
            Object policy=AlbumPlayer.cls("PhoneMediaPolicy").getField("INSTANCE").get(null);
            Object id=AlbumPlayer.call(policy,"id",title,artist);
            if(id==null)return null;
            Object label=AlbumPlayer.call(policy,"label",title,artist);
            Class<?> link=AlbumPlayer.cls("MusicCatalogLink");
            String video=state.optString("cover");
            Object catalog=video.isEmpty()?null:AlbumPlayer.call(link.getField("Companion").get(null),"parse","https://music.youtube.com/watch?v="+video);
            return AlbumPlayer.cls("PhonePlayback").getConstructor(String.class,String.class,String.class,
                long.class,long.class,boolean.class,long.class,String.class,link,String.class)
                .newInstance(id,label,artist,state.optLong("position",-1),track.optLong("duration",-1),
                    state.optBoolean("playing"),System.currentTimeMillis(),state.optString("player","iPhone"),catalog,null);
        }catch(ReflectiveOperationException|org.json.JSONException e){
            android.util.Log.w("GloveboxPhoneAudio","Phone playback could not be read by the driving journal",e);return null;
        }
    }
    private void toggleDrive(){
        JSONObject playing=vehicleAudio();
        if(playing.optBoolean("phone")){togglePhone(playing);return;}
        if(phoneOnly){
            if(driveChoice!=null){
                String id=driveChoice.optString("id");
                driveAudio.selectPhone(id,driveChoice.optString("title"),driveChoice.optString("artist"));
            }
            return;
        }
        if(driveAudio.connected())driveAudio.toggle();
        else if(DriveAudioService.status().has("track"))DriveAudioService.command(activity,"toggle","",0);
        else if(driveChoice!=null)driveAudio.handoff(driveChoice.optString("id"),driveChoice.optString("title"),driveChoice.optString("artist"),inset.resumeAt(driveChoice.optString("id")));
    }
    private void listenToAlbum(String id,boolean phoneOnly){
        if(!library.permits(id))return;
        JSONObject t=library.identify(id);inset.clear();
        if(DriveAudioService.status().has("track"))DriveAudioService.command(activity,"stop","",0);
        if(phoneOnly){
            if(!driveAudio.phone.available()&&deviceMusic!=null&&deviceMusic.open(id,t.optString("title"),t.optString("artist")))return;
            driveAudio.selectPhone(id,t.optString("title"),t.optString("artist"));
        }
        else driveAudio.handoff(id,t.optString("title"),t.optString("artist"),0);
    }
    private void audioFiles(boolean ask){
        if(ask&&activity.checkSelfPermission(DriveAudioService.READ)!=android.content.pm.PackageManager.PERMISSION_GRANTED){
            refreshAudioOnResume=true;activity.requestPermissions(new String[]{DriveAudioService.READ},593);return;
        }
        JSONArray rows=DriveAudioService.files(activity);
        web.evaluateJavascript("window.glDeviceFiles&&glDeviceFiles("+rows+","+(activity.checkSelfPermission(DriveAudioService.READ)==0)+")",null);
    }
    public static void audioPermission(){GloveboxScreen s=current;if(s!=null&&s.ready)s.audioFiles(false);}
    private IphonePicker iphonePicker;
    public static void iphoneAccessResult(){
        GloveboxScreen s=current;
        if(s!=null&&s.driveAudio!=null&&s.driveAudio.phone.iphone.permitted())s.openIphonePicker();
    }
    private void showIphones(){
        if(locked())return;
        boolean canScan=android.os.Build.VERSION.SDK_INT<31||(activity.checkSelfPermission(IphonePicker.SCAN)==android.content.pm.PackageManager.PERMISSION_GRANTED&&activity.checkSelfPermission(IphoneInvitation.ADVERTISE)==android.content.pm.PackageManager.PERMISSION_GRANTED);
        if(!driveAudio.phone.iphone.permitted()||!canScan){
            activity.requestPermissions(new String[]{IphoneMediaRemote.CONNECT,IphonePicker.SCAN,IphoneInvitation.ADVERTISE},594);return;
        }
        openIphonePicker();
    }
    private void openIphonePicker(){
        if(locked()||activity.isFinishing()||activity.isDestroyed()||iphonePicker!=null&&iphonePicker.visible())return;
        iphonePicker=new IphonePicker(activity,driveAudio.phone.iphone);iphonePicker.show();
    }
    private static Object driveMusicState;
    public static boolean hasDriveMusicControl(){
        if(driveMusicState==null)driveMusicState=GloveboxMotion.call(
            GloveboxMotion.cls("androidx.compose.runtime.SnapshotStateKt"),"mutableStateOf$default",false,null,2,null);
        return Boolean.TRUE.equals(GloveboxMotion.call(driveMusicState,"getValue"));
    }
    private static void publishMusicControl(boolean shown){
        if(hasDriveMusicControl()!=shown)GloveboxMotion.call(driveMusicState,"setValue",shown);
    }
    private void driveControls(boolean blocked){
        if(!blocked){driveBar.setVisibility(View.GONE);publishMusicControl(false);return;}
        JSONObject state=vehicleAudio();
        JSONObject t=state.optJSONObject("track");if(t==null)t=driveChoice;
        String cover=state.optBoolean("phone")?state.optString("cover"):(t==null?"":t.optString("id").replaceFirst("^external:",""));
        String phoneState=driveAudio.phone.drivingMessage();
        if(phoneOnly&&!state.has("track")&&phoneState.isEmpty())phoneState=driveAudio.phone.missingInput();
        driveBar.show(t,state,cover,phoneState.isEmpty()?driveAudio.message:phoneState,driveAudio.openedProvider());
        publishMusicControl(t!=null&&!GloveboxMotion.flag("isSpeaking"));
    }
    private void storyRoad(String id)throws Exception{
        Object store=AlbumPlayer.cls("CourseStore").getConstructor(Context.class).newInstance(activity);
        for(Object c:(List<?>)GloveboxMotion.call(store,"all"))if(id.equals(get(c,"getId"))){
            List<?> stops=(List<?>)get(c,"getStops");
            CourseRoads.authorize(activity,()->new Thread(()->{try{
                Object road=CourseRoads.fetch(activity,stops);JSONArray line=new JSONArray();
                for(Object point:(List<?>)get(road,"getPoints"))line.put(new JSONArray().put(get(point,"getLongitude")).put(get(point,"getLatitude")));
                String js="window.glRoadForStory&&glRoadForStory("+JSONObject.quote(id)+","+line+","+get(road,"getMeters")+","+get(road,"getSeconds")+")";
                handler.post(()->{if(!stopped)web.evaluateJavascript(js,null);});
            }catch(Exception e){handler.post(()->web.evaluateJavascript("window.glStoryRoadError&&glStoryRoadError()",null));}},"course-road").start());return;
        }
    }
    private FrameLayout layer;
    private TextView back;
    private GloveboxMap map;
    private void closeMap(){if(map!=null){map.dispose();layer.removeView(map);map=null;}web.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);}
    private void openMap(){
        closeMap();
        web.evaluateJavascript("document.querySelectorAll('video,audio').forEach(x=>x.pause())",null);
        web.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        map=new GloveboxMap(activity,()->{closeMap();sync();},()->{closeMap();GloveboxFeatures.navigate("account");nativeMode=true;sync();});
        layer.addView(map,new FrameLayout.LayoutParams(-1,-1));
    }
    private boolean ready, nativeMode, stopped;
    private String catalog="";
    private String viewport="";
    private int courseRequest,shelfRequest;
    private long catalogChecked;
    private final Map<View,Integer> coveredViews=new java.util.WeakHashMap<>();
    private static final String ORIGIN="https://glovebox.local/";
    public static void install(Activity a){current=new GloveboxScreen(a);current.start();}
    public static void openDesign(String view){
        GloveboxScreen screen=current;if(screen==null)return;
        screen.handler.post(()->{if(screen.stopped||screen.locked())return;screen.nativeMode=false;screen.sync();screen.web.evaluateJavascript("go("+JSONObject.quote(view)+")",null);});
    }
    private GloveboxScreen(Activity a){activity=a;youtubeLink=new YouTubeLink(a);phoneOnly=a.getSharedPreferences("music-output",0).getBoolean("phoneOnly",false);}
    private Object engine(){return GloveboxMotion.engine;}
    private Object get(Object o,String method){return GloveboxMotion.call(o,method);}
    private boolean locked(){return engine()==null||!Boolean.TRUE.equals(get(engine(),"getPhotoAllowed"))||Boolean.TRUE.equals(get(engine(),"getConfirmedNonParked"));}
    private void start(){
        handler.postDelayed(()->{
            if(activity.isFinishing())return;
            layer=new FrameLayout(activity);
            web=new WebView(activity);web.setBackgroundColor(0xff15120f);
            if(0!=(activity.getApplicationInfo().flags&android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE))WebView.setWebContentsDebuggingEnabled(true);
            WebSettings s=web.getSettings();s.setUserAgentString(s.getUserAgentString()+" GloveboxPleos/1.0");s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMediaPlaybackRequiresUserGesture(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            web.addJavascriptInterface(new Bridge(),"GloveboxNative");
            web.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !r.getUrl().toString().startsWith(ORIGIN);}
                // The JavaScript bridge signals readiness after defining its receivers.
                // Fragment/history callbacks are not document-load notifications.
                public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){
                    String u=r.getUrl().toString();
                    if(u.matches("https://tile\\.openstreetmap\\.org/[0-9]{1,2}/[0-9]{1,7}/[0-9]{1,7}\\.png"))return null;
                    if(!u.startsWith(ORIGIN))return empty();
                    String path=r.getUrl().getPath().substring(1);
                    if(path.contains(".."))return empty();
                    try {
                        if(path.startsWith("community-photo/")){
                            String[] ids=path.split("/");WebResourceResponse photo=ids.length==3?SharedAlbum.photo(activity,ids[1],ids[2]):null;
                            return photo!=null?photo:empty();
                        }
                        if(path.startsWith("device-cover/")){
                            String key=path.substring(13);
                            if(!key.matches("[0-9]+")||activity.checkSelfPermission(DriveAudioService.READ)!=0)return empty();
                            android.net.Uri songUri=ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(key));
                            android.graphics.Bitmap cover=activity.getContentResolver().loadThumbnail(songUri,new android.util.Size(180,180),null);
                            ByteArrayOutputStream out=new ByteArrayOutputStream();cover.compress(android.graphics.Bitmap.CompressFormat.JPEG,86,out);cover.recycle();
                            return new WebResourceResponse("image/jpeg",null,new ByteArrayInputStream(out.toByteArray()));
                        }
                        if(path.startsWith("youtube-cover/")){WebResourceResponse cover=library.thumbnail(activity,path.substring("youtube-cover/".length()));return cover==null?empty():cover;}
                        if(path.startsWith("photo/")){
                            String file=photos.get(path);
                            if(file==null)return empty();
                            InputStream in=file.startsWith("content:")?activity.getContentResolver().openInputStream(android.net.Uri.parse(file)):new FileInputStream(file.replace("file://",""));
                            return new WebResourceResponse(mime(file),null,in);
                        }
                        String asset="glovebox-web/"+(path.isEmpty()?"index.html":path);
                        if(path.endsWith(".mp4"))return videoResponse(asset,r.getRequestHeaders().get("Range"));
                        return new WebResourceResponse(mime(path),"UTF-8",activity.getAssets().open(asset));
                    }catch(Exception e){return empty();}
                }
            });
            layer.addView(web,new FrameLayout.LayoutParams(-1,-1));
            driveAudio=new DriveAudioHandoff(activity);
            deviceMusic=new CoreDeviceMusic(()->{catalogChecked=0;if(!stopped)sync();});
            deviceMusic.setWatching(phoneOnly);deviceMusic.start();
            driveAudio.phone.watch();
            driveBar=new DriveNowPlaying(activity,library,this::toggleDrive);
            FrameLayout.LayoutParams ap=new FrameLayout.LayoutParams(-1,-2,Gravity.CENTER);
            layer.addView(driveBar,ap);
            layer.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or_,ob)->{
                if(r-l==or_-ol&&b-t==ob-ot)return;
                ap.width=(r-l)-12;ap.height=(int)((b-t)*.34);
                driveBar.setLayoutParams(ap);
            });
            journalLibrary=new JournalLibrary(activity);
            inset=new CockpitSurface(activity,layer,web,library);
            back=new TextView(activity);back.setText("‹  Glovebox 화면");back.setTextSize(16);back.setTextColor(0xfffaf7f0);back.setGravity(Gravity.CENTER);back.setBackgroundColor(0xee32291e);
            FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(220,52,Gravity.RIGHT|Gravity.BOTTOM);bp.rightMargin=16;bp.bottomMargin=12;layer.addView(back,bp);back.setVisibility(View.GONE);
            back.setOnClickListener(v->{nativeMode=false;sync();loadCourses();});
            activity.addContentView(layer,new ViewGroup.LayoutParams(-1,-1));
            web.loadUrl(ORIGIN+"index.html");
            activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
                public void onActivityResumed(Activity a){if(a==activity){stopped=false;web.onResume();if(ready&&refreshAudioOnResume){refreshAudioOnResume=false;handler.postDelayed(()->audioFiles(false),250);}if(ready&&visible&&!nativeMode&&!locked())web.evaluateJavascript("window.glSurfaceReturn&&glSurfaceReturn()",null);handler.post(tick);}}
                public void onActivityPaused(Activity a){if(a==activity){stopped=true;inset.clear();web.evaluateJavascript("document.querySelectorAll('video,audio').forEach(x=>x.pause())",null);web.onPause();handler.removeCallbacks(tick);}}
                public void onActivityDestroyed(Activity a){if(a==activity){stopped=true;youtubeLink.close();if(deviceMusic!=null)deviceMusic.close();if(iphonePicker!=null)iphonePicker.dismiss();driveAudio.phone.iphone.close();driveAudio.release();inset.clear();handler.removeCallbacksAndMessages(null);web.destroy();activity.getApplication().unregisterActivityLifecycleCallbacks(this);}}
                public void onActivityCreated(Activity a,Bundle b){}public void onActivityStarted(Activity a){}public void onActivityStopped(Activity a){}public void onActivitySaveInstanceState(Activity a,Bundle b){}
            });
            handler.post(tick);
        },700);
    }
    private final Runnable tick=new Runnable(){public void run(){if(stopped)return;sync();handler.postDelayed(this,300);}};
    private void sync(){
        if(web==null)return;
        try{
            boolean blocked=locked();boolean show=!nativeMode&&!blocked;
            if(blocked&&map!=null)closeMap();
            if(blocked&&iphonePicker!=null)iphonePicker.dismiss();
            if(blocked)driveAudio.phone.watch();
            if(blocked&&!previouslyBlocked&&inset!=null){inset.drive(driveAudio);driveAudio.phone.watch();}
            else if(!show&&inset!=null)inset.clear();
            previouslyBlocked=blocked;driveControls(blocked);
            if(visible!=show){
                visible=show;GloveboxVideo.screenChanged();if(show&&ready)web.evaluateJavascript("window.glSurfaceReturn&&glSurfaceReturn()",null);
                if(GloveboxMotion.clock!=null){if(show)GloveboxMotion.clock.pause();else if(!GloveboxMotion.reduced)GloveboxMotion.clock.resume();}
                ViewGroup parent=(ViewGroup)layer.getParent();
                for(int i=0;i<parent.getChildCount();i++)if(parent.getChildAt(i)!=layer){
                    View child=parent.getChildAt(i);
                    child.setImportantForAccessibility(show?View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS:View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
                    if(show){coveredViews.put(child,child.getVisibility());child.setVisibility(View.INVISIBLE);}
                    else {Integer prior=coveredViews.remove(child);if(prior!=null)child.setVisibility(prior);}
                }
            }
            if(web.getVisibility()!=(show?View.VISIBLE:View.GONE)){
                if(!show)web.evaluateJavascript("document.querySelectorAll('video,audio').forEach(x=>x.pause())",null);
                web.setVisibility(show?View.VISIBLE:View.GONE);
            }
            back.setVisibility(nativeMode&&!blocked?View.VISIBLE:View.GONE);
            if(!ready||!show)return;
            String size=web.getWidth()+","+web.getHeight();
            if(!size.equals(viewport)){viewport=size;web.evaluateJavascript("window.receiveNativeViewport&&window.receiveNativeViewport("+size+")",null);}
            long now=SystemClock.uptimeMillis();
            if(catalog.isEmpty()||now-catalogChecked>=1200){
            catalogChecked=now;
            JSONObject data=new JSONObject();JSONArray items=new JSONArray();
            List<?> list=(List<?>)get(engine(),"getVisibleMemories");
            try{library.refresh(engine());}catch(Exception ex){android.util.Log.w("GloveboxMusic","library",ex);}
            for(Object memory:list){
                JSONObject m=new JSONObject();String id=String.valueOf(get(memory,"getId"));m.put("nativeId",id);
                double lat=((Number)get(memory,"getLatitude")).doubleValue(),lon=((Number)get(memory,"getLongitude")).doubleValue();
                if(Double.isFinite(lat)&&Double.isFinite(lon)&&Math.abs(lat)<=90&&Math.abs(lon)<=180&&(lat!=0||lon!=0))m.put("latitude",lat).put("longitude",lon);
                for(String[] pair:new String[][]{{"title","getTitle"},{"place","getPlace"},{"description","getStory"},{"song","getTrack"},{"season","getDate"}})m.put(pair[0],String.valueOf(get(memory,pair[1])));
                JSONArray urls=new JSONArray();int i=0;
                for(Object p:(List<?>)get(memory,"getPhotoPaths")){String name=new File(String.valueOf(p)).getName();if(!name.matches("photo-[0-9]+\\.webp"))name="image.webp";String key="photo/"+android.net.Uri.encode(id)+"/"+(i++)+"/"+name;photos.put(key,String.valueOf(p));urls.put(ORIGIN+key);}
                m.put("photos",urls);if(urls.length()>0){try{library.enrich(memory,m);}catch(Exception ex){android.util.Log.w("GloveboxMusic","recommendation",ex);}}items.put(m);
            }
            Object journey=get(engine(),"getJourney");
            if(journey!=null && Boolean.TRUE.equals(get(journey,"getFromLocation")) &&
                    Arrays.asList("OFFERED","DEFERRED").contains(String.valueOf(get(journey,"getPhase")))){
                data.put("nearbyMemory",get(journey,"getMemoryId"));
            }
            Object musicPrefs=get(engine(),"getMusicPreferences$app_release");
            try{data.put("accountLink",youtubeLink.observe(musicPrefs));}
            catch(Exception error){android.util.Log.w("GloveboxMusic","Could not initialize the YouTube connection",error);}
            data.put("phoneLink",new JSONObject().put("device",deviceMusic.device).put("notice",deviceMusic.notice).put("pending",phoneOnly?driveAudio.phone.missingInput():""));
            data.put("musicSync",new JSONObject().put("count",get(musicPrefs,"getCount")).put("last",get(musicPrefs,"getLastSuccessfulAt"))
                .put("busy",get(musicPrefs,"getBusy")).put("status",get(musicPrefs,"getStatus")).put("record",get(get(engine(),"getDriveJournal"),"getEnabled")));
            driveAudio.phone.watch();
            data.put("phoneAudio",driveAudio.phone.available());data.put("vehicleMusic",installed(DriveAudioHandoff.MUSIC));data.put("vehicleYouTube",installed(DriveAudioHandoff.VIDEO));data.put("library",library.lists());data.put("accountConnected",library.accountConnected);data.put("accountGeneration",library.accountGeneration);data.put("memories",items);data.put("bookmarked",new JSONArray((Collection<?>)get(engine(),"getBookmarkedIds")));data.put("demo",get(engine(),"getDemoMode"));
            String next=data.toString();if(!next.equals(catalog)){catalog=next;web.evaluateJavascript("window.receiveNativeCatalog&&window.receiveNativeCatalog("+next+")",null);}
            }
            Object playback=Class.forName("io.memorydrive.pleos.MusicPlayback").getField("INSTANCE").get(null);
            JSONObject status=new JSONObject();JSONObject currentAudio=vehicleAudio();status.put("vehicleAudio",currentAudio);status.put("musicWait",driveAudio.audioMessage());status.put("playing",get(playback,"getPlaying"));status.put("position",get(playback,"getPosition"));status.put("duration",get(playback,"getDuration"));status.put("message",get(playback,"getMessage"));
            web.evaluateJavascript("window.receiveNativePlayback&&window.receiveNativePlayback("+status+")",null);
        }catch(Exception e){android.util.Log.e("GloveboxScreen","sync",e);}
    }
    private void loadMusicShelf() throws Exception {
        final int request=++shelfRequest;
        Object journal=get(engine(),"getDriveJournal");
        final JSONArray trips=MusicShelf.tripEvents(journal);
        final Object session=get(AlbumPlayer.cls("PlatformSessionStore").getConstructor(Context.class).newInstance(activity),"load");
        final boolean remember=MusicShelf.albumSettings(activity,session).getBoolean("rememberPlayer",false);
        new Thread(()->{
            JSONArray album=null;
            try{album=MusicShelf.albumEvents(activity,session);}catch(Exception e){android.util.Log.w("GloveboxMusic","Read vehicle listening history",e);}
            final JSONArray records=album;
            handler.post(()->{
                if(stopped||request!=shelfRequest||locked())return;
                try{
                    Object active=get(AlbumPlayer.cls("PlatformSessionStore").getConstructor(Context.class).newInstance(activity),"load");
                    Object owner=session==null?null:get(session,"getNamespace");
                    if(!java.util.Objects.equals(owner,active==null?null:get(active,"getNamespace"))){loadMusicShelf();return;}
                    web.evaluateJavascript("window.glShelfHistory&&glShelfHistory("+trips+","+records+","+remember+")",null);
                }catch(Exception e){android.util.Log.w("GloveboxMusic","Read active account",e);}
            });
        },"glovebox-shelf").start();
    }
    private final class Bridge{
        @JavascriptInterface public void action(String name,String id){handler.post(()->{
            try{
                if("ready".equals(name)){ready=true;catalog="";viewport="";sync();return;}
                if(locked())return;
                switch(name){
                    case "open-recording":
                        activity.getClass().getMethod("access$switchExperience",activity.getClass(),boolean.class).invoke(null,activity,true);
                        return;
                    case "inset":if(!nativeMode&&map==null)inset.position(new JSONObject(id));return;
                    case "inset-close":inset.clear();return;
                    case "shelf-album-setting":
                        Object shelfSession=get(AlbumPlayer.cls("PlatformSessionStore").getConstructor(Context.class).newInstance(activity),"load");
                        if(id.equals("on")||id.equals("off"))MusicShelf.albumSettings(activity,shelfSession).edit().putBoolean("rememberPlayer",id.equals("on")).apply();
                        loadMusicShelf();return;
                    case "youtube-link-start":youtubeLink.begin();catalogChecked=0;sync();return;
                    case "youtube-link-dismiss":youtubeLink.dismiss();catalogChecked=0;sync();return;
                    case "shelf-manage":
                        if(GloveboxFeatures.navigate("music")){nativeMode=true;sync();}return;
                    case "shelf-history":loadMusicShelf();return;
                    case "shelf-sync":
                        Object sync=get(engine(),"getMusicPreferences$app_release");
                        if(Boolean.TRUE.equals(get(sync,"getConnected"))&&!Boolean.TRUE.equals(get(sync,"getBusy")))GloveboxMotion.call(sync,"refreshIfDue",true);
                        catalogChecked=0;sync();return;
                    case "audio-files":audioFiles(false);return;
                    
                    case "output-mode":
                        phoneOnly="phone".equals(id);deviceMusic.setWatching(phoneOnly);
                        activity.getSharedPreferences("music-output",0).edit().putBoolean("phoneOnly",phoneOnly).apply();return;
                    case "recommendation":
                        JSONObject selection=new JSONObject(id);
                        driveChoice=library.selected(selection.optString("album"),selection.optString("track"));
                        return;
                    case "album-auto":albumAudio(id,"auto");return;
                    case "iphone-open-track":
                        if(library.permits(id)&&deviceMusic!=null){
                            JSONObject songToOpen=library.identify(id);
                            deviceMusic.open(id,songToOpen.optString("title"),songToOpen.optString("artist"));
                        }return;
                    case "album-phone":listenToAlbum(id,true);return;
                    case "iphone-media-setup":showIphones();return;
                    case "album-system-audio":listenToAlbum(id,false);return;
                    case "album-music":albumAudio(id,"music");return;
                    case "album-video":albumAudio(id,"video");return;
                    case "audio-access":audioFiles(true);return;
                    case "audio-choose":if(DriveAudioService.file(activity,id)!=null){inset.clear();driveAudio.stopExternal();DriveAudioService.command(activity,"choose",id,0);}return;
                    case "audio-toggle":JSONObject activeAudio=vehicleAudio();if(phoneOnly||activeAudio.optBoolean("phone")){togglePhone(activeAudio);return;}if(driveAudio.connected()){driveAudio.toggle();return;}DriveAudioService.command(activity,"toggle","",0);return;
                    case "audio-stop":driveAudio.stopExternal();DriveAudioService.command(activity,"stop","",0);return;
                    case "story-song":
                        JSONObject song=library.courseSong(activity,new JSONObject(id));
                        if(song!=null)web.evaluateJavascript("window.glPlayStoryTrack("+song+")",null);return;
                    case "coast":SharedAlbum.coast(activity);loadCourses();return;
                    case "keep-story":
                        if(SharedAlbum.savePublic(activity,id))loadCourses();return;
                    case "share-saved-story":
                        new Thread(()->{
                            try{
                                Intent chooser=SharedAlbum.shareSaved(activity,id);
                                handler.post(()->{
                                    if(stopped||locked())return;
                                    if(chooser!=null)try{activity.startActivity(chooser);return;}catch(ActivityNotFoundException ignored){}
                                    android.widget.Toast.makeText(activity,"코스 파일을 다운로드의 Glovebox 폴더에 저장했습니다.",android.widget.Toast.LENGTH_LONG).show();
                                });
                            }catch(Exception e){handler.post(()->{if(!stopped)android.widget.Toast.makeText(activity,"코스 파일을 만들지 못했습니다.",android.widget.Toast.LENGTH_LONG).show();});}
                        },"glovebox-share-course").start();return;
                    case "export-story":
                        new Thread(()->{try{String[] file=SharedAlbum.export(activity,new JSONObject(id));handler.post(()->{if(!stopped)web.evaluateJavascript("glCardSaved("+JSONObject.quote(file[0])+","+JSONObject.quote(file[1])+")",null);});}
                            catch(Exception error){android.util.Log.w("GloveboxRoutes","save shared card",error);handler.post(()->{if(!stopped)web.evaluateJavascript("glCardFailed()",null);});}},"course-export").start();return;
                    case "story-road":storyRoad(id);return;
                    case "course-road":if(!nativeMode&&map==null&&!locked())inset.loadCourseRoad(id);return;
                    case "inset-seek":inset.seekTo(Long.parseLong(id));return;
                    case "audio-seek":
                        long ms=Long.parseLong(id);JSONObject playbackSource=vehicleAudio();
                        if(ms<0||!playbackSource.optBoolean("canSeek"))return;
                        if(playbackSource.optBoolean("phone"))driveAudio.phone.seekTo(ms);
                        else if(playbackSource.optJSONObject("track").optString("id").startsWith("external:"))driveAudio.seekTo(ms);
                        else DriveAudioService.seekCurrent(ms);
                        return;
                    case "inset-play":inset.setPlaying(true);return;
                    case "inset-pause":inset.setPlaying(false);return;
                    case "inset-toggle":inset.toggle();return;
                    case "map":inset.clear();openMap();break;
                    case "courses":loadCourses();break;
                    case "journal-edit":journalLibrary.link(new JSONObject(id));
                    case "journal":
                        JSONObject journalData=journalLibrary.read(engine());
                        web.evaluateJavascript("window.receiveNativeJournal&&receiveNativeJournal("+journalData+")",null);break;
                    case "courseRoute":
                        if(!GloveboxRoutes.request(id))throw new IllegalArgumentException("Unknown route");
                        nativeMode=true;sync();break;
                    case "native":
                        if(id.equals("music")){openDesign("music-shelf");return;}
                        if(!id.isEmpty()&&!GloveboxFeatures.navigate(id))throw new IllegalStateException("Navigation is not ready");
                        nativeMode=true;sync();break;
                    case "quiet":GloveboxMotion.call(engine(),"toggleQuiet");break;
                    case "narrate":GloveboxMotion.call(engine(),"narrate");break;
                    case "open":GloveboxMotion.call(engine(),"openMemory",id);break;
                    case "bookmark":GloveboxMotion.call(engine(),"toggleBookmark",id);catalog="";break;
                    case "hide":GloveboxMotion.call(engine(),"openMemory",id);GloveboxMotion.call(engine(),"hideMemory");catalog="";break;
                    case "restore":GloveboxMotion.call(engine(),"restoreHidden");catalog="";break;
                    case "play":case "pause":
                        if(!id.isEmpty())GloveboxMotion.call(engine(),"openMemory",id);
                        Intent intent=new Intent();intent.setClassName(activity,"io.memorydrive.pleos.MemoryMusicService");intent.setAction(name);activity.startService(intent);break;
                }
                sync();
            }catch(Exception e){android.util.Log.e("GloveboxScreen","action",e);Toast.makeText(activity,"원래 앱 화면에서 이 기능을 확인해 주세요.",Toast.LENGTH_LONG).show();}
        });}
    }
    private void loadCourses(){
        final int request=++courseRequest;
        new Thread(()->{
            JSONObject result;
            try{
                boolean samples=activity.getSharedPreferences("experience",0).getBoolean("demoMode",false);
                if(samples&&activity.getSharedPreferences("coast-collection",0).getInt("edition",0)<3)SharedAlbum.coast(activity);
                else SharedAlbum.updateOpenedCoast(activity);
                result=GloveboxRoutes.read(activity.getApplicationContext());
            }
            catch(Exception e){result=new JSONObject();try{result.put("error","보관한 길을 읽지 못했어요. 다시 시도해 주세요.");}catch(Exception ignored){}android.util.Log.e("GloveboxRoutes","read",e);}
            final String json=result.toString();
            handler.post(()->{if(request==courseRequest&&!stopped&&!activity.isDestroyed())web.evaluateJavascript("window.receiveNativeCourses&&window.receiveNativeCourses("+json+")",null);});
            try{
                JSONObject feed=SharedAlbum.publicFeed(activity);
                handler.post(()->{if(request==courseRequest&&!stopped)web.evaluateJavascript("window.glCommunityFeed&&glCommunityFeed("+feed+")",null);});
            }catch(Exception ignored){ }
        },"glovebox-courses").start();
    }
    private WebResourceResponse videoResponse(String asset,String range)throws IOException {
        long length;
        try(android.content.res.AssetFileDescriptor fd=activity.getAssets().openFd(asset)){length=fd.getLength();}
        long start=0,end=length-1;
        boolean partial=range!=null&&range.startsWith("bytes=");
        if(partial){
            try{
                String[] parts=range.substring(6).split("-",-1);
                if(parts.length!=2)throw new NumberFormatException();
                if(parts[0].isEmpty()){long suffix=Long.parseLong(parts[1]);if(suffix<=0)throw new NumberFormatException();start=Math.max(0,length-suffix);}
                else {start=Long.parseLong(parts[0]);if(!parts[1].isEmpty())end=Math.min(end,Long.parseLong(parts[1]));}
                if(start<0||start>=length||end<start)throw new NumberFormatException();
            }catch(NumberFormatException e){return new WebResourceResponse("video/mp4",null,416,"Range Not Satisfiable",Collections.singletonMap("Content-Range","bytes */"+length),new ByteArrayInputStream(new byte[0]));}
        }
        InputStream input=activity.getAssets().open(asset);
        try{long remaining=start;while(remaining>0){long skipped=input.skip(remaining);if(skipped<=0)throw new EOFException();remaining-=skipped;}}catch(IOException e){input.close();throw e;}
        final long count=end-start+1;
        InputStream bounded=new FilterInputStream(input){long left=count;public int read()throws IOException{if(left==0)return -1;int value=super.read();if(value>=0)left--;return value;}public int read(byte[] b,int off,int len)throws IOException{if(len==0)return 0;if(left==0)return -1;int n=in.read(b,off,(int)Math.min(left,len));if(n>0)left-=n;return n;}};
        Map<String,String> headers=new HashMap<>();headers.put("Accept-Ranges","bytes");headers.put("Content-Length",Long.toString(count));
        if(partial)headers.put("Content-Range","bytes "+start+"-"+end+"/"+length);
        return new WebResourceResponse("video/mp4",null,partial?206:200,partial?"Partial Content":"OK",headers,bounded);
    }
    private static WebResourceResponse empty(){return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));}
    private static String mime(String path){String p=path.toLowerCase(Locale.ROOT);if(p.endsWith(".css"))return "text/css";if(p.endsWith(".js"))return "application/javascript";if(p.endsWith(".svg"))return "image/svg+xml";if(p.endsWith(".webp"))return "image/webp";if(p.endsWith(".png"))return "image/png";if(p.endsWith(".jpg")||p.endsWith(".jpeg"))return "image/jpeg";if(p.endsWith(".mp4"))return "video/mp4";if(p.endsWith(".woff2"))return "font/woff2";if(p.endsWith(".woff"))return "font/woff";if(p.endsWith(".ttf"))return "font/ttf";return "text/html";}
}
