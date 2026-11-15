package io.memorydrive.pleos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;

/** Solicit the phone's Apple Media Service. iOS can initiate the BLE link as the central. */
final class IphoneInvitation {
    static final String ADVERTISE="android.permission.BLUETOOTH_ADVERTISE";
    interface Incoming { void connected(BluetoothDevice phone); }
    private final Context context;
    private final Incoming incoming;
    private final Handler main=new Handler(Looper.getMainLooper());
    private BluetoothGattServer server;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseCallback advertising;
    private long retryAfter;
    IphoneInvitation(Context context,Incoming incoming){this.context=context;this.incoming=incoming;}
    String carName(){
        try {
            BluetoothManager manager=context.getSystemService(BluetoothManager.class);
            BluetoothAdapter adapter=manager==null?null:manager.getAdapter();
            String name=adapter==null?null:adapter.getName();
            return name==null||name.isEmpty()?"차량":name;
        }catch(SecurityException ignored){return "차량";}
    }
    void start(){
        if(advertising!=null||SystemClock.elapsedRealtime()<retryAfter||Build.VERSION.SDK_INT<31
            ||context.checkSelfPermission(ADVERTISE)!=PackageManager.PERMISSION_GRANTED)return;
        retryAfter=SystemClock.elapsedRealtime()+25000;
        try {
            BluetoothManager manager=context.getSystemService(BluetoothManager.class);
            BluetoothAdapter adapter=manager==null?null:manager.getAdapter();
            if(adapter==null||(advertiser=adapter.getBluetoothLeAdvertiser())==null)return;
            if(server==null){
                server=manager.openGattServer(context,new BluetoothGattServerCallback(){
                    @Override public void onConnectionStateChange(BluetoothDevice device,int status,int state){
                        if(status==0&&state==BluetoothProfile.STATE_CONNECTED)
                            main.post(()->{if(server!=null)incoming.connected(device);});
                    }
                });
                if(server==null)return;
            }
            AdvertiseSettings settings=new AdvertiseSettings.Builder().setConnectable(true)
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED).build();
            AdvertiseData request=new AdvertiseData.Builder()
                .addServiceSolicitationUuid(new ParcelUuid(IphoneMediaRemote.SERVICE)).build();
            AdvertiseData name=new AdvertiseData.Builder().setIncludeDeviceName(true).build();
            advertising=new AdvertiseCallback(){
                @Override public void onStartFailure(int error){if(advertising==this)advertising=null;}
            };
            advertiser.startAdvertising(settings,request,name,advertising);
        }catch(RuntimeException e){stopAdvertising();}
    }
    void stopAdvertising(){
        AdvertiseCallback old=advertising;advertising=null;
        if(old!=null&&advertiser!=null)try{advertiser.stopAdvertising(old);}catch(RuntimeException ignored){}
    }
    void close(){
        stopAdvertising();BluetoothGattServer old=server;server=null;retryAfter=0;
        if(old!=null)try{old.close();}catch(RuntimeException ignored){}
    }
}
