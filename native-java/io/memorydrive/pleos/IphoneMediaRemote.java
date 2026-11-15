package io.memorydrive.pleos;

import android.bluetooth.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

/** A GATT client for the Apple Media Service on the driver's selected, paired iPhone. */
final class IphoneMediaRemote {
    static final String CONNECT="android.permission.BLUETOOTH_CONNECT";
    static final UUID SERVICE=UUID.fromString("89d3502b-0f36-433a-8ef4-c502ad55f8dc");
    private static final UUID COMMAND=UUID.fromString("9b3c81d8-57b1-4a8a-b8df-0e56f7ca51c2");
    private static final UUID UPDATE=UUID.fromString("2f7cabce-808d-411f-9a0c-bb92ba96c102");
    private static final UUID ATTRIBUTE=UUID.fromString("c6b2f38c-23ab-46d8-a6ab-a3a870bbd5d7");
    private static final UUID CCC=UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final Context context;
    private final SharedPreferences prefs;
    private final Handler main=new Handler(Looper.getMainLooper());
    private final ArrayDeque<Operation> queue=new ArrayDeque<>();
    private final HashMap<Integer,Integer> updates=new HashMap<>();
    private final Runnable timeout=this::lost;
    private final IphoneInvitation invitation;
    private IphoneInvitation.Incoming picker;
    private BluetoothDevice selectedDevice;
    private BluetoothGatt gatt;
    private BluetoothGattService service;
    private Operation pending;
    private IphonePlayback phone=new IphonePlayback();
    private boolean closed,commandPending,expectedPlaying,connectImmediately;
    private long retryAfter;
    private int commandVersion;
    IphoneMediaRemote(Context context){
        this.context=context.getApplicationContext();
        invitation=new IphoneInvitation(this.context,this::incoming);
        prefs=this.context.getSharedPreferences("glovebox-iphone-media",Context.MODE_PRIVATE);
    }
    boolean permitted(){return Build.VERSION.SDK_INT<31||context.checkSelfPermission(CONNECT)==PackageManager.PERMISSION_GRANTED;}
    private BluetoothAdapter adapter(){
        BluetoothManager manager=context.getSystemService(BluetoothManager.class);
        return manager==null?null:manager.getAdapter();
    }
    String chosen(){return prefs.getString("peer","");}
    List<BluetoothDevice> paired(){
        List<BluetoothDevice> devices=new ArrayList<>();BluetoothAdapter a=adapter();
        if(!permitted()||a==null)return devices;
        try{
            for(BluetoothDevice d:a.getBondedDevices()){
                BluetoothClass c=d.getBluetoothClass();
                if(c==null||c.getMajorDeviceClass()==BluetoothClass.Device.Major.PHONE||d.getType()!=BluetoothDevice.DEVICE_TYPE_CLASSIC)devices.add(d);
            }
            devices.sort(Comparator.comparing(IphoneMediaRemote::label));
        }catch(SecurityException ignored){}
        return devices;
    }
    static String label(BluetoothDevice d){String name=d.getName();return name==null||name.isEmpty()?d.getAddress():name;}
    String carName(){return invitation.carName();}
    void discover(IphoneInvitation.Incoming listener){
        picker=listener;
        if(listener!=null){
            invitation.start();
            try{
                BluetoothManager manager=context.getSystemService(BluetoothManager.class);
                if(manager!=null)for(BluetoothDevice device:manager.getConnectedDevices(BluetoothProfile.GATT_SERVER))
                    listener.connected(device);
            }catch(SecurityException ignored){}
        }
        else if(chosen().isEmpty())invitation.close();
    }
    private void incoming(BluetoothDevice device){
        if(closed)return;
        if(device.getAddress().equals(chosen())){
            selectedDevice=device;
            if(gatt==null){retryAfter=0;connectImmediately=true;watch();}
        }else if(picker!=null)picker.connected(device);
    }
    void choose(BluetoothDevice device){
        disconnect();selectedDevice=device;
        prefs.edit().putString("peer",device==null?"":device.getAddress()).apply();
        if(device==null)invitation.close();
        connectImmediately=true;retryAfter=0;watch();
    }
    void watch(){
        if(closed||chosen().isEmpty()||!permitted())return;
        if(service==null)invitation.start();
        if(gatt!=null||SystemClock.elapsedRealtime()<retryAfter)return;
        BluetoothAdapter a=adapter();retryAfter=SystemClock.elapsedRealtime()+12000;
        try{
            if(a==null||!a.isEnabled())return;
            BluetoothDevice peer=selectedDevice;
            if(peer==null)for(BluetoothDevice d:a.getBondedDevices())if(d.getAddress().equals(chosen())){peer=d;break;}
            if(peer==null)return;
            if(peer.getBondState()!=BluetoothDevice.BOND_BONDED)return;
            boolean direct=connectImmediately;
            gatt=peer.connectGatt(context,!direct,callbacks,BluetoothDevice.TRANSPORT_LE);
            if(gatt!=null&&direct){connectImmediately=false;main.postDelayed(timeout,15000);}
        }catch(IllegalArgumentException|SecurityException ignored){}
    }
    void close(){closed=true;picker=null;invitation.close();disconnect();}
    private void lost(){disconnect();retryAfter=SystemClock.elapsedRealtime()+8000;}
    private void disconnect(){
        main.removeCallbacks(timeout);BluetoothGatt old=gatt;gatt=null;service=null;
        queue.clear();pending=null;updates.clear();phone=new IphonePlayback();commandPending=false;commandVersion++;
        if(old!=null)try{old.disconnect();old.close();}catch(SecurityException ignored){}
    }
    private abstract static class Operation{
        abstract boolean start();
        void complete(byte[] data){}
    }
    private void next(){
        if(pending!=null||gatt==null)return;
        pending=queue.poll();if(pending==null)return;
        try{if(!pending.start()){lost();return;}}catch(SecurityException e){lost();return;}
        main.postDelayed(timeout,9000);
    }
    private void done(BluetoothGatt source,int status,byte[] bytes){
        if(source!=gatt||pending==null)return;
        if(status!=BluetoothGatt.GATT_SUCCESS){lost();return;}
        main.removeCallbacks(timeout);Operation op=pending;pending=null;op.complete(bytes);next();
    }
    private void subscribe(UUID id){
        BluetoothGattCharacteristic chr=service.getCharacteristic(id);
        queue.add(new Operation(){boolean start(){
            BluetoothGattDescriptor ccc=chr.getDescriptor(CCC);
            if(ccc==null||!gatt.setCharacteristicNotification(chr,true))return false;
            ccc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);return gatt.writeDescriptor(ccc);
        }});
    }
    private Operation write(UUID id,byte... data){
        return new Operation(){boolean start(){
            BluetoothGattCharacteristic c=service.getCharacteristic(id);c.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            c.setValue(data);return gatt.writeCharacteristic(c);
        }};
    }
    private void setup(BluetoothGatt source,int status){
        if(source!=gatt)return;
        main.removeCallbacks(timeout);
        if(status!=0){lost();return;}
        service=gatt.getService(SERVICE);
        if(service==null||service.getCharacteristic(COMMAND)==null||service.getCharacteristic(UPDATE)==null){lost();return;}
        invitation.stopAdvertising();
        subscribe(COMMAND);subscribe(UPDATE);
        queue.add(write(UPDATE,(byte)0,(byte)0,(byte)1));
        queue.add(write(UPDATE,(byte)2,(byte)0,(byte)2,(byte)3));next();
    }
    private void notification(BluetoothGatt source,UUID id,byte[] data){
        if(source!=gatt||data==null||service==null)return;
        if(COMMAND.equals(id)){phone.commands(data);return;}
        if(!UPDATE.equals(id)||data.length<3)return;
        int entity=data[0]&255,attr=data[1]&255,key=entity*256+attr;
        if(!(entity==0&&(attr==0||attr==1)||entity==2&&(attr==0||attr==2||attr==3)))return;
        int generation=updates.containsKey(key)?updates.get(key)+1:1;updates.put(key,generation);
        if((data[2]&1)==0){
            apply(entity,attr,Arrays.copyOfRange(data,3,data.length));return;
        }
        if(service.getCharacteristic(ATTRIBUTE)==null)return;
        queue.add(write(ATTRIBUTE,data[0],data[1]));
        queue.add(new Operation(){
            boolean start(){return gatt.readCharacteristic(service.getCharacteristic(ATTRIBUTE));}
            void complete(byte[] value){if(value!=null&&updates.get(key)==generation)apply(entity,attr,value);}
        });next();
    }
    private void apply(int entity,int attr,byte[] value){
        phone.attribute(entity,attr,value,SystemClock.elapsedRealtime());
        if(entity==0&&attr==1&&commandPending&&phone.known()&&phone.playing()==expectedPlaying){
            commandPending=false;commandVersion++;
        }
    }
    boolean ready(){return gatt!=null&&phone.known()&&!phone.title.trim().isEmpty();}
    boolean playing(){return ready()&&phone.playing();}
    String playerName(){return phone.player.trim().isEmpty()?"iPhone":phone.player;}
    boolean control(boolean play){
        if(!ready()||commandPending)return false;
        int cmd=phone.command(play);if(cmd==IphonePlayback.NONE)return false;
        commandPending=true;expectedPlaying=play;int seq=++commandVersion;
        queue.addFirst(write(COMMAND,(byte)cmd));next();
        main.postDelayed(()->{if(seq==commandVersion)commandPending=false;},3000);
        return true;
    }
    boolean matches(String title,String artist){
        return ready()&&PhoneAudioRoute.metadataMatches(phone.title,phone.artist,title,artist);
    }
    JSONObject status(String id,String title,String artist){
        JSONObject out=new JSONObject();if(!ready())return out;
        boolean same=!id.isEmpty()&&matches(title,artist);
        try{
            JSONObject track=new JSONObject().put("id",same?"external:"+id:"iphone:"+phone.artist+"/"+phone.title)
                .put("title",phone.title).put("artist",phone.artist).put("duration",phone.duration);
            out.put("player",playerName()).put("track",track).put("playing",phone.playing()).put("position",phone.position(SystemClock.elapsedRealtime()))
                .put("phone",true).put("appleMedia",true).put("cover",same?id:"")
                .put("canToggle",!commandPending&&phone.command(!phone.playing())!=IphonePlayback.NONE);
        }catch(JSONException ignored){}
        return out;
    }
    private final BluetoothGattCallback callbacks=new BluetoothGattCallback(){
        @Override public void onConnectionStateChange(BluetoothGatt s,int status,int state){main.post(()->{
            if(s!=gatt)return;
            if(status!=0||state==BluetoothProfile.STATE_DISCONNECTED){lost();return;}
            if(state==BluetoothProfile.STATE_CONNECTED){main.removeCallbacks(timeout);try{if(!s.discoverServices()){lost();return;}}catch(SecurityException e){lost();return;}main.postDelayed(timeout,10000);}
        });}
        @Override public void onServiceChanged(BluetoothGatt s){main.post(()->{if(s==gatt)lost();});}
        @Override public void onServicesDiscovered(BluetoothGatt s,int status){main.post(()->setup(s,status));}
        @Override public void onDescriptorWrite(BluetoothGatt s,BluetoothGattDescriptor d,int status){main.post(()->done(s,status,null));}
        @Override public void onCharacteristicWrite(BluetoothGatt s,BluetoothGattCharacteristic c,int status){main.post(()->done(s,status,null));}
        @Override public void onCharacteristicRead(BluetoothGatt s,BluetoothGattCharacteristic c,int status){byte[] value=c.getValue();read(s,status,value);}
        @Override public void onCharacteristicRead(BluetoothGatt s,BluetoothGattCharacteristic c,byte[] value,int status){read(s,status,value);}
        private void read(BluetoothGatt s,int status,byte[] v){byte[] data=v==null?null:v.clone();main.post(()->done(s,status,data));}
        @Override public void onCharacteristicChanged(BluetoothGatt s,BluetoothGattCharacteristic c){changed(s,c,c.getValue());}
        @Override public void onCharacteristicChanged(BluetoothGatt s,BluetoothGattCharacteristic c,byte[] value){changed(s,c,value);}
        private void changed(BluetoothGatt s,BluetoothGattCharacteristic c,byte[] v){
            if(v==null)return;byte[] data=v.clone();UUID id=c.getUuid();main.post(()->notification(s,id,data));
        }
    };
}
