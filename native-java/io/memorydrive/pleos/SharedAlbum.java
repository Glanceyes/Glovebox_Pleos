package io.memorydrive.pleos;

import org.json.*;
import java.util.*;
import android.content.Context;
import static io.memorydrive.pleos.AlbumPlayer.*;

/** Optional public byline, portrait and stop photographs in the portable course format. */
public final class SharedAlbum {
    private static String picture(String value){
        return value.length()<=175000 && value.matches("data:image/(?:jpeg|png|webp);base64,[A-Za-z0-9+/=]+")?value:"";
    }
    public static boolean same(JSONObject a,JSONObject b){
        if(a==null)a=new JSONObject();if(b==null)b=new JSONObject();
        if(!a.optString("name").equals(b.optString("name"))||!a.optString("avatar").equals(b.optString("avatar")))return false;
        for(String key:new String[]{"photos","artists"}){
            JSONObject x=a.optJSONObject(key),y=b.optJSONObject(key);
            if(x==null)x=new JSONObject();if(y==null)y=new JSONObject();if(x.length()!=y.length())return false;
            for(Iterator<String> it=x.keys();it.hasNext();){String id=it.next();if(!y.has(id)||!x.optString(id).equals(y.optString(id)))return false;}
        }
        return true;
    }
    public static JSONObject from(JSONObject file){
        JSONObject source=file.optJSONObject("experience");if(source==null)return null;
        try{
            JSONObject out=new JSONObject();
            String name=source.optString("name","").trim();out.put("name",name.substring(0,Math.min(60,name.length())));
            out.put("avatar",picture(source.optString("avatar")));
            JSONObject stops=source.optJSONObject("photos"),images=new JSONObject();
            if(stops!=null){Iterator<String> it=stops.keys();while(it.hasNext()&&images.length()<60){String key=it.next();String value=picture(stops.optString(key));if(!value.isEmpty())images.put(key,value);}}
            JSONObject names=source.optJSONObject("artists"),artists=new JSONObject();
            if(names!=null){Iterator<String> ids=names.keys();while(ids.hasNext()&&artists.length()<60){String key=ids.next(),artist=names.optString(key,"").trim();if(!artist.isEmpty()&&artist.length()<=100)artists.put(key,artist);}}
            return out.put("photos",images).put("artists",artists);
        }catch(JSONException e){return null;}
    }
    private static final Set<String> publicPhotos=java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static Object session(Context c)throws Exception{
        return call(cls("PlatformSessionStore").getConstructor(Context.class).newInstance(c),"load");
    }
    static android.webkit.WebResourceResponse photo(Context c,String course,String photo)throws Exception{
        Object auth=session(c);
        if(auth==null||!course.matches("[a-zA-Z0-9_-]{1,128}")||!photo.matches("[a-zA-Z0-9_-]{1,128}")||
            !publicPhotos.contains(call(auth,"getNamespace")+"/"+course+"/"+photo))return null;
        Object client=cls("PlatformClient").getConstructor(String.class).newInstance(call(auth,"getBase"));
        android.graphics.Bitmap b=(android.graphics.Bitmap)call(cls("CommunityApiKt"),"communityPhoto",client,auth,course,photo);
        if(b==null)return null;
        java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
        b.compress(android.graphics.Bitmap.CompressFormat.JPEG,86,out);b.recycle();
        return new android.webkit.WebResourceResponse("image/jpeg",null,new java.io.ByteArrayInputStream(out.toByteArray()));
    }
    static String[] export(Context c,JSONObject request)throws Exception{
        Object shelf=cls("CourseStore").getConstructor(Context.class).newInstance(c);
        for(Object row:(List<?>)call(shelf,"all")){
            if(!request.getString("id").equals(call(row,"getId")))continue;
            JSONObject source=(JSONObject)call(row,"json"),card=from(request);
            if(card==null||card.optString("name").isEmpty())throw new IllegalArgumentException("name");
            String id=UUID.randomUUID().toString();
            source.put("id",id).put("experience",card);
            Object parsed=call(cls("SharedCourse").getField("Companion").get(null),"parse",source);
            if(parsed==null)throw new IllegalArgumentException("course");
            byte[] bytes=((JSONObject)call(parsed,"json")).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if(bytes.length>250000)throw new IllegalArgumentException("file size");
            String filename="Glovebox-"+id.substring(0,8)+".json";
            android.net.Uri uri=download(c,filename,bytes);
            try{
                Object stored=call(shelf,"save",parsed);
                return new String[]{String.valueOf(call(stored,"getId")),filename};
            }catch(RuntimeException e){c.getContentResolver().delete(uri,null,null);throw e;}
        }
        throw new IllegalArgumentException("course missing");
    }
    private static android.net.Uri download(Context c,String name,byte[] bytes)throws Exception{
        android.content.ContentValues values=new android.content.ContentValues();
        values.put("_display_name",name);values.put("relative_path","Download/Glovebox");
        values.put("mime_type","application/json");values.put("is_pending",1);
        android.content.ContentResolver resolver=c.getContentResolver();
        android.net.Uri uri=resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,values);
        if(uri==null)throw new java.io.IOException("Download");
        try{
            try(java.io.OutputStream out=resolver.openOutputStream(uri)){if(out==null)throw new java.io.IOException("Open download");out.write(bytes);}
            values.clear();values.put("is_pending",0);
            if(resolver.update(uri,values,null,null)!=1)throw new java.io.IOException("publish");
            return uri;
        }catch(Exception e){resolver.delete(uri,null,null);throw e;}
    }
    private static final Map<String,android.net.Uri> sharedFiles=new HashMap<>();
    static synchronized android.content.Intent shareSaved(Context c,String id)throws Exception{
        Object shelf=cls("CourseStore").getConstructor(Context.class).newInstance(c);
        for(Object row:(List<?>)call(shelf,"all")){
            if(!id.equals(call(row,"getId")))continue;
            JSONObject course=(JSONObject)call(row,"json");
            byte[] bytes=course.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if(bytes.length>250000)throw new java.io.IOException("course too large");
            String digest=android.util.Base64.encodeToString(java.security.MessageDigest.getInstance("SHA-256").digest(bytes),
                android.util.Base64.URL_SAFE|android.util.Base64.NO_WRAP|android.util.Base64.NO_PADDING);
            android.net.Uri uri=sharedFiles.get(digest);
            if(uri!=null){
                try(android.os.ParcelFileDescriptor file=c.getContentResolver().openFileDescriptor(uri,"r")){
                    if(file==null)uri=null;
                }catch(java.io.FileNotFoundException missing){uri=null;}
            }
            if(uri==null){
                uri=download(c,"Glovebox-"+digest.substring(0,12)+".json",bytes);sharedFiles.put(digest,uri);
            }
            android.content.Intent send=new android.content.Intent(android.content.Intent.ACTION_SEND);
            send.setType("application/json").putExtra(android.content.Intent.EXTRA_STREAM,uri);
            send.putExtra(android.content.Intent.EXTRA_SUBJECT,course.optString("title"));
            send.setClipData(android.content.ClipData.newRawUri(course.optString("title"),uri));
            send.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(c.getPackageManager().queryIntentActivities(send,android.content.pm.PackageManager.MATCH_DEFAULT_ONLY).isEmpty())return null;
            return android.content.Intent.createChooser(send,"코스 보내기");
        }
        throw new java.io.FileNotFoundException("course");
    }
    private static final Map<String,String> publicCards=new java.util.concurrent.ConcurrentHashMap<>();
    static boolean savePublic(Context context,String publicId)throws Exception{
        Object auth=session(context);if(auth==null)return false;
        String json=publicCards.get(call(auth,"getNamespace")+"/"+publicId);
        if(json==null)return false;
        Object shelf=cls("CourseStore").getConstructor(Context.class).newInstance(context);
        Object parsed=call(cls("SharedCourse").getField("Companion").get(null),"parse",new JSONObject(json));
        if(parsed==null)return false;call(shelf,"save",parsed);return true;
    }
    static synchronized void updateOpenedCoast(Context context)throws Exception{
        android.content.SharedPreferences prefs=context.getSharedPreferences("coast-collection",0);
        if(prefs.getInt("edition",0)>=3)return;
        Object shelf=cls("CourseStore").getConstructor(Context.class).newInstance(context);
        for(Object c:(List<?>)call(shelf,"all"))if(Arrays.asList("d180b33a-c5e6-5404-af7d-774a58d223d2","27d554a6-4319-5c35-b94f-d6ab08c62ca5").contains(String.valueOf(call(c,"getId")))){coast(context);return;}
    }
    static synchronized void coast(Context context)throws Exception{
        Object shelf=cls("CourseStore").getConstructor(Context.class).newInstance(context);
        Map<String,Object> present=new HashMap<>();for(Object c:(List<?>)call(shelf,"all"))present.put(String.valueOf(call(c,"getId")),c);
        JSONObject imageUpdates;
        try(Scanner in=new Scanner(context.getAssets().open("glovebox-web/coast-image-updates.json"),"UTF-8")){imageUpdates=new JSONObject(in.useDelimiter("\\A").next());}
        for(String key:context.getAssets().list("glovebox-web/coast")){
            try(Scanner input=new Scanner(context.getAssets().open("glovebox-web/coast/"+key),"UTF-8")){
                JSONObject row=new JSONObject(input.useDelimiter("\\A").next());
                String id=row.getString("id");Object existing=present.get(id);
                if(existing!=null){
                    JSONObject stored=(JSONObject)call(existing,"json"),card=stored.optJSONObject("experience"),checks=imageUpdates.optJSONObject(id);
                    JSONObject photos=card==null?null:card.optJSONObject("photos");
                    if(photos==null||checks==null)continue;
                    boolean changed=false;JSONObject replacements=row.getJSONObject("experience").getJSONObject("photos");
                    for(Iterator<String> it=checks.keys();it.hasNext();){
                        String stop=it.next(),old=photos.optString(stop);if(old.isEmpty())continue;
                        byte[] hash=java.security.MessageDigest.getInstance("SHA-256").digest(old.getBytes("UTF-8"));
                        String hex=String.format(java.util.Locale.ROOT,"%064x",new java.math.BigInteger(1,hash));
                        if(!hex.equals(checks.optString(stop)))continue;
                        if(replacements.has(stop))photos.put(stop,replacements.getString(stop));else photos.remove(stop);changed=true;
                    }
                    if(!changed)continue;row=stored;
                }
                Object course=call(cls("SharedCourse").getField("Companion").get(null),"parse",row);if(course!=null)call(shelf,"save",course);
            }
        }
        context.getSharedPreferences("coast-collection",0).edit().putInt("edition",3).apply();
    }
    static JSONObject publicFeed(Context context)throws Exception{
        Object store=cls("PlatformSessionStore").getConstructor(Context.class).newInstance(context),session=call(store,"load");JSONArray rows=new JSONArray();
        if(session!=null){
            Object client=cls("PlatformClient").getConstructor(String.class).newInstance(call(session,"getBase"));
            Object page=call(cls("CommunityApiKt"),"community",client,session,false,"",null);
            List<?> entries=(List<?>)call(page,"getCourses");
            for(Object entry:entries){
                JSONObject c=(JSONObject)call(call(entry,"getCourse"),"json");
                JSONObject experience=c.optJSONObject("experience");if(experience==null)experience=new JSONObject();
                c.put("experience",experience.put("name",call(entry,"getOwner")));String id=String.valueOf(call(entry,"getId"));c.put("publicId",id);rows.put(c);publicCards.put(call(session,"getNamespace")+"/"+id,c.toString());
                JSONArray stops=c.optJSONArray("stops");
                if(stops!=null)for(int i=0;i<stops.length();i++){JSONArray photos=stops.getJSONObject(i).optJSONArray("photos");
                    if(photos!=null)for(int k=0;k<photos.length();k++){String photo=photos.optString(k);
                        if(photo.matches("[a-zA-Z0-9_-]{1,128}"))publicPhotos.add(call(session,"getNamespace")+"/"+id+"/"+photo);
                    }
                }
            }
            Object cache=cls("PlatformRecommendationCache").getConstructor(Context.class,cls("PlatformSession")).newInstance(context,session);
            GloveboxRoutes.recommend(entries,rows,cache);
        }
        return new JSONObject().put("courses",rows);
    }
}
