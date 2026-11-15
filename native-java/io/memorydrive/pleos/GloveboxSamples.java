package io.memorydrive.pleos;
import java.io.*;
import java.util.*;
import org.json.*;

/** Design media is confined to the app's existing, explicitly labelled demo mode. */
public final class GloveboxSamples {
    static List<?> cached;
    static void selectFirstVisit(android.content.Context context){
        android.content.SharedPreferences pref=context.getSharedPreferences("experience",0);
        if(pref.getBoolean("firstVisitChecked",false))return;
        if(pref.getBoolean("demoMode",false)){
            pref.edit().putBoolean("firstVisitChecked",true).apply();
            return;
        }
        try{
            Class<?> sessions=AlbumPlayer.cls("PlatformSessionStore");
            Object account=AlbumPlayer.call(sessions.getConstructor(android.content.Context.class).newInstance(context),"load");
            boolean showSamples=false;
            if(account==null){
                Class<?> store=AlbumPlayer.cls("MemoryStore");
                Object local=store.getConstructor(android.content.Context.class,AlbumPlayer.cls("PlatformSession"),boolean.class).newInstance(context,null,true);
                showSamples=((List<?>)AlbumPlayer.call(local,"all")).isEmpty();
            }
            android.content.SharedPreferences.Editor change=pref.edit().putBoolean("firstVisitChecked",true);
            if(showSamples)change.putBoolean("demoMode",true);
            change.apply();
        }catch(Exception err){android.util.Log.w("GloveboxSamples","Could not check existing visits",err);}
    }
    /** Rendering only: a bundled demo with its own photos must not use coast_memory. */
    public static boolean isBundledVisit(Object memory){
        Object raw=GloveboxMotion.call(memory,"getPhotoPath");
        if(!(raw instanceof String))return false;
        String path=(String)raw;
        return path.contains("/glovebox-design-media/")||path.contains("/glovebox-recording/visit-v1/");
    }

    public static boolean hasDisplayPhotos(Object memory){
        if(Boolean.TRUE.equals(GloveboxMotion.call(memory,"getImported")))return true;
        Object path=GloveboxMotion.call(memory,"getPhotoPath");
        return GloveboxMotion.flag("getDemoMode") && path!=null && String.valueOf(path).contains("/glovebox-design-media/");
    }
    public static List<?> list(List<?> original){
        if(cached!=null)return cached;
        if(GloveboxMotion.context==null||original.isEmpty())return original;
        try{
            ByteArrayOutputStream json=new ByteArrayOutputStream();copy(GloveboxMotion.context.getAssets().open("glovebox/samples.json"),json);
            JSONArray rows=new JSONArray(json.toString("UTF-8"));List<Object> result=new ArrayList<>();Object base=original.get(0);
            String[] getters={"getId","getPlace","getDate","getTitle","getStory","getLatitude","getLongitude","getRadiusMeters","getTrack","getProvenance","getPhotoPath","getAudioPath","getMusicProvider","getMusicUrl","getLocationSource","getDateSource","getImported","getAutomatic","getCapturedAt","getMusicAssociation","getMusicEvidence","getVehicleRoute","getAdditionalPhotoPaths","getSharedTrackID","getSceneLabels","getSceneEvidenceSource","getPlaceKind"};
            for(int i=0;i<rows.length();i++){
                JSONObject row=rows.getJSONObject(i);Object[] values=new Object[getters.length];for(int j=0;j<values.length;j++)values[j]=GloveboxMotion.call(base,getters[j]);
                values[0]=i==0?"coast-2022":i==1?"lake-2021":i==2?"harbor-2020":"glovebox-demo-"+(i+1);
                values[1]=row.getString("place");values[2]=row.getString("season")+"의 기억";values[3]=row.getString("title");values[4]=row.getString("description");values[8]=row.getString("song");
                JSONArray photos=row.getJSONArray("photos");List<String> paths=new ArrayList<>();for(int j=0;j<photos.length();j++)paths.add(photo(photos.getInt(j)));
                values[10]=paths.get(0);values[22]=paths.subList(1,paths.size());values[9]="";
                result.add(GloveboxMotion.call(base,"copy",values));
            }
            File visitDir=new File(GloveboxMotion.context.getFilesDir(),"glovebox-recording/visit-v1");
            Object archive=AlbumPlayer.cls("MemoryArchive").getField("INSTANCE").get(null);
            try(InputStream input=GloveboxMotion.context.getAssets().open("glovebox/recording/visit.zip")){
                Object visit=archive.getClass().getMethod("unpack",InputStream.class,File.class).invoke(archive,input,visitDir);
                if(visit!=null)result.add(0,visit);
            }
            cached=result;return result;
        }catch(Exception e){android.util.Log.e("GloveboxDesign","demo media",e);return original;}
    }
    static String photo(int number)throws IOException{
        String name=String.format(java.util.Locale.ROOT,"photo-%02d.webp",number);File dir=new File(GloveboxMotion.context.getFilesDir(),"glovebox-design-media");dir.mkdirs();File target=new File(dir,name);
        if(!target.isFile()||target.length()==0){try(OutputStream out=new FileOutputStream(target)){copy(GloveboxMotion.context.getAssets().open("glovebox/"+name),out);}}
        return target.getAbsolutePath();
    }
    static void copy(InputStream input,OutputStream output)throws IOException{try(InputStream in=input){byte[] bytes=new byte[32768];int n;while((n=in.read(bytes))!=-1)output.write(bytes,0,n);}}
}
