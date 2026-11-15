package io.memorydrive.pleos;
import android.content.Context;
import android.os.*;
import java.util.*;
import org.json.*;

/** Reads the original course store; navigation uses its existing Compose states. */
public final class GloveboxRoutes {
    private static String pending;
    private static final Handler main=new Handler(Looper.getMainLooper());
    public static JSONObject read(Context context)throws Exception {
        Object store=GloveboxMotion.cls("io.memorydrive.pleos.CourseStore").getConstructor(Context.class).newInstance(context);
        JSONArray courses=new JSONArray();
        List<Object> candidates=new ArrayList<>();
        for(Object course:(List<?>)GloveboxMotion.call(store,"all")){
            JSONObject row=(JSONObject)GloveboxMotion.call(course,"json");
            List<?> stops=(List<?>)GloveboxMotion.call(course,"getStops");
            Object road=null;
            try{road=AlbumPlayer.savedRoad(context,stops);}catch(Exception e){android.util.Log.w("GloveboxRoutes","Unavailable road for selected stops",e);}
            if(road!=null){
                JSONArray line=new JSONArray();
                for(Object point:(List<?>)GloveboxMotion.call(road,"getPoints"))
                    line.put(new JSONArray().put(GloveboxMotion.call(point,"getLongitude")).put(GloveboxMotion.call(point,"getLatitude")));
                row.put("roadPreview",line).put("meters",GloveboxMotion.call(road,"getMeters")).put("seconds",GloveboxMotion.call(road,"getSeconds"));
            }
            courses.put(row);
            Class<?> platform=AlbumPlayer.cls("PlatformCourse");
            Object local=platform.getConstructor(String.class,String.class,String.class,AlbumPlayer.cls("SharedCourse"),String.class,String.class)
                .newInstance(row.getString("id"),"","",course,"","private");
            candidates.add(local);
        }
        Object sessions=GloveboxMotion.cls("io.memorydrive.pleos.PlatformSessionStore").getConstructor(Context.class).newInstance(context);
        Object session=GloveboxMotion.call(sessions,"load");
        recommend(candidates,courses,null);
        return new JSONObject().put("courses",courses).put("connected",session!=null);
    }
    static void recommend(List<?> entries,JSONArray rows,Object hidden){
        try{
            Object engine=GloveboxMotion.engine;if(engine==null||entries.isEmpty())return;
            Object fix=GloveboxMotion.call(engine,"getLibraryFix");if(fix==null)return;
            long now=System.currentTimeMillis();
            Object policy=AlbumPlayer.cls("SharedRecommendationPolicy").getField("INSTANCE").get(null);
            List<?> ranked=(List<?>)GloveboxMotion.call(policy,"rank",entries,GloveboxMotion.call(engine,"getVisibleMemories"),
                    fix,GloveboxMotion.call(engine,"getTravelHeading"),now);
            Map<String,JSONObject> byId=new HashMap<>();
            for(int i=0;i<rows.length();i++)byId.put(rows.getJSONObject(i).getString("id"),rows.getJSONObject(i));
            for(Object suggestion:ranked){
                Object item=GloveboxMotion.call(suggestion,"getItem");
                if(hidden!=null&&Boolean.TRUE.equals(GloveboxMotion.call(hidden,"hidden",GloveboxMotion.call(item,"getId"),now)))continue;
                Object course=GloveboxMotion.call(item,"getCourse");
                JSONObject row=byId.get(GloveboxMotion.call(course,"getId"));
                if(row!=null)row.put("suggestionScore",GloveboxMotion.call(suggestion,"getScore"))
                    .put("suggestionReason",GloveboxMotion.call(suggestion,"getReason"));
            }
        }catch(Exception e){android.util.Log.w("GloveboxRoutes","Course recommendations unavailable",e);}
    }
    public static boolean request(String route){
        if(!Arrays.asList("explore","compose","shelf").contains(route)&&!route.matches("course:[a-fA-F0-9-]{36}"))return false;
        pending=route;
        if(!GloveboxFeatures.navigate("courses")){pending=null;return false;}
        return true;
    }
    public static void capture(Object selected,Object community,Object compose){
        if(pending==null)return;
        final String target=pending;pending=null;
        main.post(()->{
            if(GloveboxMotion.flag("getConfirmedNonParked"))return;
            GloveboxMotion.call(compose,"setValue",target.equals("compose"));
            GloveboxMotion.call(community,"setValue",target.equals("explore"));
            GloveboxMotion.call(selected,"setValue",target.startsWith("course:")?target.substring(7):null);
        });
    }
}
