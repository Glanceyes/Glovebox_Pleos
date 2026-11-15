package io.memorydrive.pleos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Stored routes are available offline. Only selected course stops are sent for routing. */
final class CourseRoads {
    private static final String PERMISSION = "osm-car-routing";
    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("road-preview", Context.MODE_PRIVATE);
    }
    static boolean allowed(Context context) { return prefs(context).getBoolean(PERMISSION, false); }
    static boolean connectedServiceAllowed(Context context) {
        return prefs(context).getBoolean("coordinate-consent",false);
    }
    static void authorize(Activity activity, Runnable fetch) {
        if (allowed(activity)) { fetch.run(); return; }
        new AlertDialog.Builder(activity).setTitle("도로 경로 보기")
            .setMessage("이 코스의 경유지 좌표를 OpenStreetMap의 자동차 경로 서비스에 보내 길을 찾습니다.")
            .setPositiveButton("경로 보기", (dialog, button) -> {
                prefs(activity).edit().putBoolean(PERMISSION,true).apply(); fetch.run();
            }).setNegativeButton("취소",null).show();
    }
    static String cached(Context context, String coordinates) {
        return context.getSharedPreferences("course-roads",0).getString(coordinates,null);
    }
    static Object fetch(Context context, List<?> stops) throws Exception {
        Object known=AlbumPlayer.savedRoad(context,stops);
        if(known!=null)return known;
        if(connectedServiceAllowed(context)&&stops.size()>=2){
            try{
                Object service=AlbumPlayer.cls("RoadRoutes").getField("INSTANCE").get(null);
                Object route=AlbumPlayer.call(service,"fetch",context.getApplicationContext(),stops);
                if(route!=null)return route;
            }catch(RuntimeException unavailable){android.util.Log.i("GloveboxMap","Connected route service unavailable");}
        }
        if(!allowed(context)||stops.size()<2)throw new IOException("No route request");
        Object codec=AlbumPlayer.cls("RoadRoute").getField("Companion").get(null);
        String coordinates=(String)AlbumPlayer.call(codec,"coordinates",stops);
        URL url=new URL("https://routing.openstreetmap.de/routed-car/route/v1/driving/"+coordinates+"?overview=full&geometries=geojson&steps=false");
        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        conn.setRequestProperty("User-Agent","Glovebox-Pleos/0.31 (drive album; interactive course preview)");
        conn.setConnectTimeout(8000);conn.setReadTimeout(12000);
        try {
            if(conn.getResponseCode()!=200)throw new IOException("Route service "+conn.getResponseCode());
            try(InputStream in=conn.getInputStream();ByteArrayOutputStream data=new ByteArrayOutputStream()){
                byte[] buffer=new byte[8192];int n;
                while((n=in.read(buffer))!=-1){if(data.size()+n>3*1024*1024)throw new IOException("Route too large");data.write(buffer,0,n);}
                String text=data.toString(StandardCharsets.UTF_8.name());
                Object route=AlbumPlayer.call(codec,"parse",text,stops.size());
                if(route==null)throw new IOException("No drivable route");
                context.getSharedPreferences("course-roads",0).edit().putString(coordinates,text).apply();
                return route;
            }
        } finally { conn.disconnect(); }
    }
}
