package com.example.fyy.quickturnoff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by FYY on 27/02/2017.
 */
public class QuickSettingsService extends TileService {

    private boolean isInstall = false;
    private boolean isActive;
    public static QuickSettingsService service;
    private static final String TAG = "network test:    ";
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";

    @Override
    public void onCreate() {

        super.onCreate();
        service = this;

        if(checkInstall() == false){

            this.stopSelf();
        }
    }

    @Override
    public void onClick() {
        if(checkInstall() == false){

            this.stopSelf();
            return;
        }
        service = this;
        try {
            updateTile(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean checkInstall(){

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals("com.github.shadowsocks")){
                return true;
            }
        }
        return false;
    }


    public void changSS(){

        Intent i = new Intent();

        ComponentName comp = new ComponentName("com.github.shadowsocks", "com.github.shadowsocks.QuickToggleShortcut");

        i.setComponent(comp);

        i.setAction("android.intent.action.VIEW");

        startActivity(i);
    }


    public void turnOff(boolean doWifi) throws InterruptedException {

        boolean tileStatus = getTileStatus();
        if(tileStatus){
            updateTile(doWifi);
        }

    }

    public void turnOn(boolean doWifi) throws InterruptedException {

        boolean tileStatus = getTileStatus();
        Log.i("TurnOn", tileStatus + " " + doWifi);
        if(!tileStatus){
            updateTile(doWifi);
        }

    }


    private boolean updateTile(boolean doWifi) throws InterruptedException {
//        Thread time = new Thread();

        Tile tile = this.getQsTile();
        isActive = getServiceStatus("com.google.android_quick_settings");
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        Icon newIcon;
        String newLabel;
        int newState;

        if (isActive) {

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_vpn_lock_24px);

            newState = Tile.STATE_ACTIVE;

            if (doWifi) {
                wifi.setWifiEnabled(true);
            }

            if(!getVPNStatus()){
                changSS();
            }

        } else {
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            android.R.drawable.ic_dialog_alert);

            newState = Tile.STATE_INACTIVE;

            if (doWifi) {
                wifi.setWifiEnabled(false);
            }

            if(getVPNStatus()){
                changSS();
            }
        }

//        time.sleep(2000);
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();

        return isActive;
    }
    private boolean getServiceStatus(String PREFERENCES_KEY) {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    public boolean getVPNStatus()  {


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo ni = cm.getActiveNetworkInfo();
        Network[] networks = cm.getAllNetworks();

//        Log.i(TAG, "Network count: " + networks.length);
        for(int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);

//            Log.i(TAG, "Network " + i + ": " + networks[i].toString());
//            Log.i(TAG, "VPN transport is: " + caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
//            Log.i(TAG, "NOT_VPN capability is: " + caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN));

            if(caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)==true){
                return true;
            }
        }
        return false;

    }


    public boolean getTileStatus(){

        Tile tile =getQsTile();
        if(tile.getState()==Tile.STATE_ACTIVE){
            return true;
        }else {
            return false;
        }

    }



    public static class WifiReceiver extends BroadcastReceiver{

        static NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;


        @Override
        public void onReceive(Context context, Intent intent) {

//            System.out.println("----");
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {

                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
//                    Log.i("Wifi", wifiState.toString());
                    if (state == NetworkInfo.State.CONNECTING) return;
                    if (wifiState == NetworkInfo.State.UNKNOWN) wifiState = state;
                    if (wifiState == state) return;
                    wifiState = state;
//                    Log.e("H3c", "isConnected " + isConnected);
                    if (isConnected) {
                        try {
                            QuickSettingsService.service.turnOn(false);
                            System.out.println("True " + QuickSettingsService.service);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            QuickSettingsService.service.turnOff(false);
                            System.out.println("False " + QuickSettingsService.service);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    }
                }
            }
        }




}
