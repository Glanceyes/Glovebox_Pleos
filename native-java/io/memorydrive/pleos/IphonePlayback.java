package io.memorydrive.pleos;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Apple Media Service player and track attributes, as last reported by the phone. */
final class IphonePlayback {
    static final int PLAY=0, PAUSE=1, TOGGLE=2, NONE=-1;
    String title="",artist="",player="";
    long duration=-1;
    private final boolean[] commands=new boolean[3];
    private int state=NONE;
    private long elapsed=-1,reportedAt;
    private double rate;
    boolean known(){return state==PLAYING||state==PAUSED;}
    private static final int PAUSED=0,PLAYING=1;
    boolean playing(){return state==PLAYING;}
    void commands(byte[] values){
        Arrays.fill(commands,false);
        for(byte value:values)if(value>=0&&value<commands.length)commands[value]=true;
    }
    int command(boolean wantPlay){
        if(!known()||playing()==wantPlay)return NONE;
        int command=wantPlay?PLAY:PAUSE;
        if(commands[command])return command;
        return commands[TOGGLE]?TOGGLE:NONE;
    }
    void attribute(int entity,int attribute,byte[] bytes,long now){
        String value=new String(bytes,StandardCharsets.UTF_8);
        if(entity==2){
            if(attribute==0)artist=value;
            else if(attribute==2)title=value;
            else if(attribute==3)duration=milliseconds(value);
        }else if(entity==0&&attribute==0)player=value;
        else if(entity==0&&attribute==1){
            String[] fields=value.split(",",-1);
            int newState=NONE;double newRate=0;long position=-1;
            if(fields.length==3)try{
                newState=Integer.parseInt(fields[0]);newRate=Double.parseDouble(fields[1]);position=milliseconds(fields[2]);
                if(!Double.isFinite(newRate))newRate=0;
            }catch(NumberFormatException ignored){newState=NONE;}
            state=newState;rate=newRate;elapsed=position;reportedAt=now;
        }
    }
    long position(long now){
        if(elapsed<0)return -1;
        long ms=elapsed+(playing()?Math.round(Math.max(0,now-reportedAt)*rate):0);
        return Math.max(0,duration>=0?Math.min(ms,duration):ms);
    }
    private static long milliseconds(String s){
        try{
            double seconds=Double.parseDouble(s);
            return Double.isFinite(seconds)&&seconds>=0?Math.round(seconds*1000):-1;
        }catch(NumberFormatException e){return -1;}
    }
}
