package io.memorydrive.pleos;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

/** Account library and the catalog association used by both the player and the cards. */
public final class AlbumPlayer {
    static Class<?> cls(String s) { return GloveboxMotion.cls("io.memorydrive.pleos."+s); }
    static Object call(Object o,String s,Object... args){ return GloveboxMotion.call(o,s,args); }
    public static List<?> recentTrips(List<?> list){
        ArrayList<Object> ordered=new ArrayList<>(list);
        ordered.sort((a,b)->Long.compare((Long)call(b,"getEndedAtMs"),(Long)call(a,"getEndedAtMs")));
        return ordered;
    }
    static List<?> trips(Object engine) throws Exception {
        Object journal=call(engine,"getDriveJournal");
        return recentTrips((List<?>)cls("AlbumRoadTrip").getMethod("visibleTrips",cls("VehicleDriveJournal")).invoke(null,journal));
    }
    static Object savedRoad(Context c,List<?> stops) throws Exception {
        if(stops.size()<2)return null;
        Object codec=cls("RoadRoute").getField("Companion").get(null);
        String key=(String)call(codec,"coordinates",stops);
        String result=CourseRoads.cached(c,key);
        if(result!=null){Object parsed=call(codec,"parse",result,stops.size());if(parsed!=null)return parsed;}
        try(InputStream raw=c.getAssets().open("glovebox-web/road-library.json");Scanner scanner=new Scanner(raw,"UTF-8")){
            JSONObject road=new JSONObject(scanner.useDelimiter("\\A").next()).optJSONObject(key);
            return road==null?null:call(codec,"parse",road.toString(),stops.size());
        }
    }
    private Object versionOwner,snapshot,contextIndex;
    private String activeConnection;
    int accountGeneration;
    private long version=-1;
    private boolean displayingSamples;
    boolean accountConnected;
    private JSONArray lists=new JSONArray();
    private final Map<Object,JSONObject> associations=new LinkedHashMap<>();
    private final Set<String> known=Collections.synchronizedSet(new HashSet<>());
    JSONArray lists(){return lists;}
    void refresh(Object e)throws Exception{
        Object sync=call(e,"getMusicPreferences$app_release");
        Object tokens=call(cls("YouTubePreferenceSync"),"access$getTokens$p",sync);
        String connection=(String)call(tokens,"getConnectionId");
        accountConnected=connection!=null;
        boolean changedAccount=!Objects.equals(connection,activeConnection);
        if(changedAccount){activeConnection=connection;accountGeneration++;}
        Object cache=cls("YouTubePreferenceSync").getMethod("access$getCache$p",cls("YouTubePreferenceSync")).invoke(null,sync);
        long revision=(Long)call(cache,"lastSuccessfulAt");
        boolean samples=Boolean.TRUE.equals(call(e,"getDemoMode"))&&!accountConnected;
        if(!changedAccount&&cache==versionOwner&&version==revision&&displayingSamples==samples)return;
        Object next=call(cache,"forPlayback",System.currentTimeMillis(),cls("YouTubePlaybackSurface").getField("NATIVE_APP").get(null));
        JSONArray sources=new JSONArray();Set<String> ids=new HashSet<>();
        for(Object source:(List<?>)call(next,"getSources")){
            JSONArray tracks=new JSONArray();
            for(Object track:(List<?>)call(source,"getTracks")){
                String id=(String)call(track,"getId");if(id==null||!id.matches("[A-Za-z0-9_-]{11}"))continue;
                ids.add(id);tracks.put(new JSONObject().put("id",id).put("title",call(track,"getTitle")).put("artist",String.valueOf(call(track,"getChannel")).replaceFirst(" - (Topic|주제)$","")));
            }
            if(tracks.length()>0)sources.put(new JSONObject().put("name",call(source,"getTitle")).put("tracks",tracks));
        }
        if(samples&&sources.length()==0){
            JSONArray tracks=new JSONArray();
            String[][] songs={{"S-fb0tMM7gc","Start Line","IRENE"},{"TNC6ZdLylXM","I SPY","NCT WISH"}};
            for(String[] song:songs){
                tracks.put(new JSONObject().put("id",song[0]).put("title",song[1]).put("artist",song[2]));
                ids.add(song[0]);
            }
            sources.put(new JSONObject().put("name","방문 앨범의 음악").put("tracks",tracks));
        }
        displayingSamples=samples;
        known.clear();known.addAll(ids);lists=sources;snapshot=accountConnected?next:null;contextIndex=null;versionOwner=cache;version=revision;associations.clear();
    }
    void enrich(Object m,JSONObject row)throws Exception{
        JSONObject track=associations.get(m);
        if(track==null){
            Object preferred=m;
            String id=id((String)call(m,"getMusicUrl"));
            Object prior=call(m,"getMusicEvidence");
            boolean tasteOnly=prior!=null&&"preference_only".equals(call(prior,"getRecommendationBasis"));
            boolean bundled=GloveboxSamples.isBundledVisit(m);
            boolean recommendation="recommendation".equals(call(m,"getMusicAssociation"));
            if(snapshot!=null&&(id==null||tasteOnly||accountConnected&&(bundled||recommendation))){
                if(contextIndex==null)contextIndex=cls("ContextualMusicIndex").getConstructor(cls("MusicPreferenceSnapshot"),long.class)
                    .newInstance(snapshot,System.currentTimeMillis());
                Object candidate=call(contextIndex,"attach",m,Collections.emptyMap(),Collections.emptyMap());
                String matched=candidate==null?null:id((String)call(candidate,"getMusicUrl"));
                if(matched!=null){id=matched;preferred=candidate;}
                if(!bundled&&recommendation&&prior!=null&&candidate==m&&accountTrack(id)==null){
                    id=null;row.put("song","");
                }
            }
            track=new JSONObject();
            if(id!=null){known.add(id);track.put("id",id).put("title",call(preferred,"getTrack"));
                track.put("association",call(preferred,"getMusicAssociation"));
                Object evidence=call(preferred,"getMusicEvidence");
                if(bundled&&evidence==null)track.put("albumMusic",true);
                if(evidence!=null){
                    track.put("basis",call(evidence,"getRecommendationBasis"));
                    track.put("reason",call(evidence,"getExplanation"));
                }
                JSONObject item=identify(id);
                if(item.has("artist"))track.put("artist",item.optString("artist"));
                else if("S-fb0tMM7gc".equals(id))track.put("artist","IRENE");}
            if(associations.size()>=256)associations.remove(associations.keySet().iterator().next());
            associations.put(m,track);
        }
        if(track.has("id"))row.put("yt",track).put("song",track.getString("title"));
    }
    private final Map<String,JSONObject> courseTracks=Collections.synchronizedMap(new LinkedHashMap<>());
    JSONObject courseSong(Context context,JSONObject selection)throws Exception{
        Object store=cls("CourseStore").getConstructor(Context.class).newInstance(context);
        for(Object c:(List<?>)call(store,"all")){
            if(!selection.getString("course").equals(call(c,"getId")))continue;
            JSONObject row=(JSONObject)call(c,"json"),xp=row.optJSONObject("experience");
            JSONObject artists=xp==null?null:xp.optJSONObject("artists");JSONArray stops=row.getJSONArray("stops");
            for(int i=0;i<stops.length();i++){JSONObject stop=stops.getJSONObject(i),music=stop.optJSONObject("music");
                String key=stop.getString("id");if(!key.equals(selection.getString("stop"))||music==null||!"YouTube Music".equals(music.optString("provider")))continue;
                String id=music.optString("trackID");if(!id.matches("[A-Za-z0-9_-]{11}"))continue;
                JSONObject result=new JSONObject().put("id",id).put("title",music.optString("title")).put("artist",artists==null?"":artists.optString(key,""));
                courseTracks.put(id,result);if(courseTracks.size()>32)courseTracks.remove(courseTracks.keySet().iterator().next());return result;
            }
        }
        return null;
    }
    JSONObject selected(String album,String id){
        if(!permits(id))return null;
        for(Map.Entry<Object,JSONObject> entry:associations.entrySet()){
            if(id.equals(entry.getValue().optString("id"))&&album.equals(call(entry.getKey(),"getId")))return entry.getValue();
        }
        return identify(id);
    }
    Object accountTrack(String id){
        if(snapshot==null||id==null)return null;
        for(Object source:(List<?>)call(snapshot,"getSources"))
            for(Object item:(List<?>)call(source,"getTracks"))
                if(id.equals(call(item,"getId")))return item;
        return null;
    }
    JSONObject identify(String id){
        for(int i=0;i<lists.length();i++){JSONArray tracks=lists.optJSONObject(i).optJSONArray("tracks");
            for(int k=0;k<tracks.length();k++)if(id.equals(tracks.optJSONObject(k).optString("id")))return tracks.optJSONObject(k);}
        if(courseTracks.containsKey(id))return courseTracks.get(id);
        for(JSONObject track:associations.values())if(id.equals(track.optString("id")))return track;
        return new JSONObject();
    }
    boolean permits(String id){return known.contains(id)||courseTracks.containsKey(id);}
    private static String id(String s){
        if(s==null)return null;Uri uri=Uri.parse(s);String host=uri.getHost();String v=null;
        if("youtu.be".equals(host))v=uri.getLastPathSegment();
        else if(Arrays.asList("www.youtube.com","youtube.com","music.youtube.com","m.youtube.com").contains(host))v=uri.getQueryParameter("v");
        return v!=null&&v.matches("[A-Za-z0-9_-]{11}")?v:null;
    }
    WebResourceResponse thumbnail(Context ctx,String id)throws Exception{
        if(!id.matches("[A-Za-z0-9_-]{11}")||!permits(id))return null;
        File dir=new File(ctx.getCacheDir(),"youtube-covers");dir.mkdirs();File target=new File(dir,id+".jpg");
        if(!target.isFile()){
            HttpURLConnection c=(HttpURLConnection)new URL("https://i.ytimg.com/vi/"+id+"/mqdefault.jpg").openConnection();
            c.setConnectTimeout(6500);c.setReadTimeout(6500);c.setInstanceFollowRedirects(false);
            try{
                if(c.getResponseCode()!=200)return null;
                File tmp=File.createTempFile("cover",".jpg",dir);
                try(InputStream in=c.getInputStream();OutputStream out=new FileOutputStream(tmp)){byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}
                if(!tmp.renameTo(target))tmp.delete();
            }finally{c.disconnect();}
        }
        return new WebResourceResponse("image/jpeg",null,new FileInputStream(target));
    }
}
