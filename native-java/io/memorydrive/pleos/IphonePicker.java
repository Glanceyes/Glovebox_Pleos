package io.memorydrive.pleos;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/** Parked-only chooser. Only the device the driver taps is paired and remembered. */
final class IphonePicker {
    static final String SCAN="android.permission.BLUETOOTH_SCAN";
    private final Activity activity;
    private final IphoneMediaRemote remote;
    private final Handler main=new Handler(Looper.getMainLooper());
    private final List<BluetoothDevice> devices=new ArrayList<>();
    private final ArrayAdapter<String> names;
    private AlertDialog dialog;
    private BluetoothLeScanner scanner;
    private final Runnable stop=this::stopScan;
    IphonePicker(Activity activity,IphoneMediaRemote remote){
        this.activity=activity;this.remote=remote;
        names=new ArrayAdapter<>(activity,android.R.layout.simple_list_item_1);
    }
    boolean visible(){return dialog!=null&&dialog.isShowing();}
    void dismiss(){if(visible())dialog.dismiss();}
    void show(){
        for(BluetoothDevice d:remote.paired())add(d,IphoneMediaRemote.label(d));
        AlertDialog.Builder builder=new AlertDialog.Builder(activity).setTitle("음악을 들을 iPhone")
            .setAdapter(names,(d,index)->select(devices.get(index)))
            .setPositiveButton("주변 기기 찾기",null).setNegativeButton("닫기",null);
        if(!remote.chosen().isEmpty())builder.setNeutralButton("선택 해제",(d,w)->remote.choose(null));
        dialog=builder.create();dialog.setOnDismissListener(d->{stopScan();remote.discover(null);});dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->scan());
        remote.discover(device->{if(visible())add(device,IphoneMediaRemote.label(device));});
        if(devices.isEmpty())scan();
    }
    private void select(BluetoothDevice device){
        try{
            if(device.getBondState()==BluetoothDevice.BOND_NONE&&!device.createBond()){
                Toast.makeText(activity,"차량의 블루투스 설정에서 이 iPhone을 연결해 주세요.",Toast.LENGTH_LONG).show();return;
            }
            remote.choose(device);
        }catch(SecurityException ignored){}
    }
    private void add(BluetoothDevice device,String name){
        if(name==null||name.trim().isEmpty()||devices.contains(device))return;
        devices.add(device);names.add(name);
    }
    private void scan(){
        if(scanner!=null)return;
        if(Build.VERSION.SDK_INT<31||activity.checkSelfPermission(SCAN)!=PackageManager.PERMISSION_GRANTED){
            dialog.dismiss();
            try{activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));}catch(android.content.ActivityNotFoundException ignored){}
            return;
        }
        try{
            BluetoothManager manager=activity.getSystemService(BluetoothManager.class);
            BluetoothAdapter adapter=manager==null?null:manager.getAdapter();
            scanner=adapter==null?null:adapter.getBluetoothLeScanner();
            if(scanner==null){dialog.setTitle("차량 블루투스를 켜 주세요");return;}
            dialog.setTitle("iPhone의 Bluetooth 설정에서 ‘"+remote.carName()+"’ 선택");dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("검색 중…");
            scanner.startScan(null,new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),callback);
            main.postDelayed(stop,14000);
        }catch(RuntimeException e){stopScan();}
    }
    private final ScanCallback callback=new ScanCallback(){
        @Override public void onScanResult(int type,ScanResult result){
            if(scanner==null||!visible()||!result.isConnectable())return;
            BluetoothDevice device=result.getDevice();ScanRecord record=result.getScanRecord();
            String name=record==null?null:record.getDeviceName();
            try{if(name==null)name=device.getName();add(device,name);}catch(SecurityException ignored){}
        }
        @Override public void onScanFailed(int error){stopScan();}
    };
    private void stopScan(){
        main.removeCallbacks(stop);BluetoothLeScanner old=scanner;scanner=null;
        if(old!=null)try{old.stopScan(callback);}catch(RuntimeException ignored){}
        if(visible()){
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("다시 찾기");
            if(devices.isEmpty())dialog.setTitle("iPhone에서 ‘"+remote.carName()+"’ 선택 또는 다시 찾기");
        }
    }
}
