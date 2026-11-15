package io.memorydrive.pleos;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import org.json.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import static io.memorydrive.pleos.AlbumPlayer.*;

/** Resolves camera timestamps before associating individual photos with vehicle fixes. */
final class JournalLibrary {
    private final Context context;
    private final Set<String> tripIds=new HashSet<>(),photoIds=new HashSet<>();
    private String namespace="";
    JournalLibrary(Context context){this.context=context;}
    private SharedPreferences links(){return context.getSharedPreferences("trip-photos-"+namespace,0);}
    JSONObject read(Object engine)throws Exception{
        Object sessions=cls("PlatformSessionStore").getConstructor(Context.class).newInstance(context);
        Object session=call(sessions,"load");
        namespace=Boolean.TRUE.equals(call(engine,"getDemoMode"))?"exhibit":session==null?"local":String.valueOf(call(session,"getNamespace"));
        Map<String,JSONObject> metadata=new HashMap<>();
        if(!"exhibit".equals(namespace)){
            Object store=cls("UsbPhotoStore").getConstructor(Context.class,cls("PlatformSession")).newInstance(context,session);
            Method read=store.getClass().getDeclaredMethod("document");read.setAccessible(true);
            JSONArray records=((JSONObject)read.invoke(store)).optJSONArray("photos");
            if(records!=null)for(int i=0;i<records.length();i++){JSONObject r=records.optJSONObject(i);if(r!=null)metadata.put(r.optString("file"),r);}
        }
        JSONArray visitLocations=null;
        if("exhibit".equals(namespace)){
            try(Scanner input=new Scanner(context.getAssets().open("glovebox/recording/locations.json"),"UTF-8")){
                visitLocations=new JSONArray(input.useDelimiter("\\A").next());
            }
        }
        JSONArray trips=new JSONArray(),photos=new JSONArray();tripIds.clear();photoIds.clear();
        for(Object trip:AlbumPlayer.trips(engine)){
            JSONObject row=(JSONObject)call(cls("VehicleDriveJournalKt"),"json",trip);trips.put(row);tripIds.add(row.getString("id"));
        }
        for(Object memory:(List<?>)call(engine,"getVisibleMemories")){
            String id=String.valueOf(call(memory,"getId"));List<?> paths=(List<?>)call(memory,"getPhotoPaths");
            for(int i=0;i<paths.size();i++){
                String path=String.valueOf(paths.get(i)),file=new File(path).getName();String key=id+"/"+file;
                JSONObject camera=metadata.get(file);
                if("a1a05005-85b8-4946-8287-3f2488fac021".equals(id)&&visitLocations!=null)camera=visitLocations.optJSONObject(i);
                JSONObject row=new JSONObject().put("key",key).put("album",id).put("index",i).put("place",call(memory,"getPlace")).put("title",call(memory,"getTitle"));
                if(camera!=null){for(String field:new String[]{"at","lat","lon"})if(!camera.isNull(field))row.put(field,camera.get(field));}
                else if(paths.size()==1){Object at=call(memory,"getCapturedAt");if(at instanceof Number)row.put("at",Math.round(((Number)at).doubleValue()*1000));row.put("lat",call(memory,"getLatitude")).put("lon",call(memory,"getLongitude"));}
                String resource=file.matches("photo-[0-9]+\\.webp")?file:"image.webp";
                row.put("src","https://glovebox.local/photo/"+Uri.encode(id)+"/"+i+"/"+resource);
                if(links().contains(key))row.put("chosen",links().getString(key,""));
                if(links().contains("exclude/"+key))row.put("excluded",links().getString("exclude/"+key,""));
                photos.put(row);photoIds.add(key);
            }
        }
        return new JSONObject().put("trips",trips).put("photos",photos);
    }
    void link(JSONObject edit)throws Exception{
        String trip=edit.getString("trip");if(!tripIds.contains(trip))return;
        SharedPreferences.Editor change=links().edit();
        for(String group:new String[]{"add","remove"}){
            JSONArray keys=edit.getJSONArray(group);
            for(int i=0;i<keys.length();i++){String key=keys.getString(i);if(!photoIds.contains(key))continue;
                if(group.equals("add"))change.putString(key,trip).remove("exclude/"+key);
                else change.remove(key).putString("exclude/"+key,trip);}
        }
        change.apply();
    }
}
