package io.memorydrive.pleos;

import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Hands an explicitly selected catalog link to Xcode's paired iPhone during emulator use. */
final class CoreDeviceMusic {
    private final Handler main=new Handler(Looper.getMainLooper());
    private final ExecutorService writes=Executors.newSingleThreadExecutor();
    private final Runnable changed;
    private volatile boolean closed;
    private volatile LocalServerSocket server;
    private volatile LocalSocket client;
    private volatile PrintWriter writer;
    String device="",notice="";
    private int sequence;
    private boolean watching;
    private JSONObject media=new JSONObject();
    private long receivedAt;
    private long commandAt;
    private Boolean expected;
    private String pickedId="",pickedTitle="",pickedArtist="";
    CoreDeviceMusic(Runnable changed){this.changed=changed;}
    void start(){
        if(!(Build.HARDWARE.contains("ranchu")||Build.HARDWARE.contains("goldfish")||Build.MODEL.contains("emulator")))return;
        new Thread(this::listen,"glovebox-coredevice").start();
    }
    private void listen(){
        try(LocalServerSocket listener=new LocalServerSocket("io.memorydrive.roads.iphone."+Process.myUid()/100000)){
            server=listener;
            while(!closed){
                try(LocalSocket peer=listener.accept()){
                    Credentials identity=peer.getPeerCredentials();
                    if(closed||(identity.getUid()!=0&&identity.getUid()!=2000))continue;
                    client=peer;peer.setSoTimeout(12000);
                    try(BufferedReader in=new BufferedReader(new InputStreamReader(peer.getInputStream(),StandardCharsets.UTF_8));
                        PrintWriter out=new PrintWriter(peer.getOutputStream(),true)){
                        String hello=in.readLine();if(hello==null||hello.length()>180)continue;
                        String name=new JSONObject(hello).optString("device");
                        if(name.isEmpty()||name.length()>64)continue;
                        writer=out;
                        main.post(()->{if(client==peer&&!closed){device=name;notice="";sendWatch();changed.run();}});
                        String line;
                        while(!closed&&(line=in.readLine())!=null&&line.length()<8192){
                            JSONObject reply=new JSONObject(line);
                            if(reply.has("media")){
                                JSONObject sample=reply.optJSONObject("media");
                                main.post(()->{if(client==peer&&!closed&&watching)receive(sample);});
                                continue;
                            }
                            if(!reply.has("seq"))continue;
                            int seq=reply.getInt("seq");boolean ok=reply.optBoolean("opened");
                            main.post(()->{if(client==peer&&seq==sequence){notice=ok?"iPhone에 곡을 보냈습니다.":"iPhone에서 곡을 열지 못했습니다.";changed.run();}});
                        }
                    }
                }catch(Exception ignored){}finally{
                    client=null;writer=null;
                    main.post(()->{if(client==null&&!closed){device="";notice="";receive(null);changed.run();}});
                }
            }
        }catch(Exception ignored){}finally{server=null;}
    }
    private void send(JSONObject event){
        PrintWriter out=writer;LocalSocket target=client;
        if(closed||out==null||target==null)return;
        writes.execute(()->{if(!closed&&client==target)out.println(event.toString());});
    }
    void setWatching(boolean active){
        watching=active;
        if(!active)receive(null);
        sendWatch();
    }
    private void sendWatch(){
        try{send(new JSONObject().put("watch",watching));}catch(org.json.JSONException ignored){}
    }
    private void receive(JSONObject sample){
        media=sample==null?new JSONObject():sample;
        receivedAt=SystemClock.elapsedRealtime();
        if(expected!=null&&media.has("playing")&&media.optBoolean("playing")==expected)expected=null;
        if(!media.has("playing"))expected=null;
    }
    JSONObject status(){
        JSONObject out=new JSONObject();
        if(!watching||device.isEmpty()||SystemClock.elapsedRealtime()-receivedAt>3800||!media.has("playing"))return out;
        String title=media.optString("title"),artist=media.optString("artist");
        boolean match=!pickedId.isEmpty()&&PhoneAudioRoute.metadataMatches(title,artist,pickedTitle,pickedArtist);
        if(expected!=null&&SystemClock.elapsedRealtime()-commandAt>3200)expected=null;
        try{
            JSONObject track=new JSONObject().put("id",match?"external:"+pickedId:"iphone:"+artist+"/"+title)
                .put("title",title.isEmpty()?"iPhone 음악":title).put("artist",artist).put("duration",media.optLong("duration",-1));
            out.put("track",track).put("position",media.optLong("position",-1)).put("playing",media.optBoolean("playing"))
                .put("phone",true).put("hostRadio",true).put("player","iPhone").put("cover",match?pickedId:"")
                .put("canSeek",false).put("canToggle",expected==null);
        }catch(org.json.JSONException ignored){}
        return out;
    }
    void toggle(){
        JSONObject current=status();
        if(!current.optBoolean("canToggle"))return;
        expected=!current.optBoolean("playing");commandAt=SystemClock.elapsedRealtime();
        try{send(new JSONObject().put("transport",expected?"play":"pause"));}catch(org.json.JSONException ignored){}
    }
    boolean open(String id,String title,String artist){
        if(writer==null||client==null||device.isEmpty()||!id.matches("[A-Za-z0-9_-]{11}"))return false;
        pickedId=id;pickedTitle=title;pickedArtist=artist;
        notice="iPhone으로 곡을 보내는 중";
        try{send(new JSONObject().put("seq",++sequence).put("id",id));}catch(org.json.JSONException e){return false;}
        changed.run();
        return true;
    }
    void close(){
        closed=true;writes.shutdownNow();
        try{if(client!=null)client.close();}catch(Exception ignored){}
        try{if(server!=null)server.close();}catch(Exception ignored){}
    }
}
