package com.example.fyy.quickturnoff;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by FYY on 27/02/2017.
 */
public class QuickSettingsService extends TileService {

    private static boolean isActive;
    public static QuickSettingsService service;
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    public static boolean wifiListenpos = true;

    @Override
    public void onCreate() {

        super.onCreate();
        service = this;
    }


    @Override
    public void onClick() {
        if (checkInstall() == false) {
            showDialog(alert());
            this.stopSelf();
            return;
        }
        service = this;
        updateTile(true);
    }


    public boolean checkInstall() {

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.github.shadowsocks")) {
                return true;
            }
        }
        return false;
    }


    public Dialog alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickSettingsService.this);
        builder.setMessage("don't have Shadowsocks" + "\n" + "Press yes to install")
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.github.shadowsocks");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }

        });
        final AlertDialog Alert = builder.create();
        return Alert;
    }

    public void changSS() {

        Intent i = new Intent();

        ComponentName comp = new ComponentName("com.github.shadowsocks", "com.github.shadowsocks.QuickToggleShortcut");

        i.setComponent(comp);

        i.setAction("android.intent.action.VIEW");

        startActivity(i);
    }


    public void turnOff(boolean doWifi) {

        boolean tileStatus = getTileStatus();
        if (tileStatus) {
            updateTile(doWifi);
        }

    }

    public void turnOn(boolean doWifi) {

        boolean tileStatus = getTileStatus();
//        Log.i("TurnOn", tileStatus + " " + doWifi);
        if (!tileStatus) {
            updateTile(doWifi);
        }

    }


    private boolean updateTile(boolean doWifi) {

        final Tile tile = this.getQsTile();
        isActive = getServiceStatus("com.google.android_quick_settings");
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final Icon newIcon;
        final String newLabel;
        final int newState;

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

            if (!getVPNStatus()) {
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

            if (getVPNStatus()) {
                changSS();
            }
        }

        new CountDownTimer(1500, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                tile.setIcon(Icon.createWithResource(getApplicationContext(),
                        R.drawable.ic_waiting));
                tile.setLabel("waiting...");
                tile.setState(Tile.STATE_UNAVAILABLE);
                tile.updateTile();
            }

            @Override
            public void onFinish() {
                tile.setLabel(newLabel);
                tile.setIcon(newIcon);
                tile.setState(newState);
                tile.updateTile();
            }
        }.start();


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

    public boolean getVPNStatus() {


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();

        for (int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);


            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
                return true;
            }
        }
        return false;

    }


    public boolean getTileStatus() {

        Tile tile = getQsTile();
        if (tile.getState() == Tile.STATE_ACTIVE) {
            return true;
        } else {
            return false;
        }
    }


    public class WifiReceiver extends BroadcastReceiver {

        NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;


        @Override
        public void onReceive(Context context, Intent intent) {

            if (QuickSettingsService.service == null) {
                context.startService(new Intent(context, QuickSettingsService.class));
            }
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (!Utils.isMyServiceRunning(manager, QuickSettingsService.class)) {
                startService(new Intent(QuickSettingsService.this, QuickSettingsService.class));
            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) && wifiListenpos) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {

                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;
                    if (state == NetworkInfo.State.CONNECTING) return;
                    if (wifiState == NetworkInfo.State.UNKNOWN) wifiState = state;
                    if (wifiState == state) return;
                    wifiState = state;
                    if (isConnected) {
                        QuickSettingsService.service.turnOn(false);
                        Toast.makeText(context, "VPN has established", Toast.LENGTH_LONG).show();
                    } else {
                        QuickSettingsService.service.turnOff(false);
                        Toast.makeText(context, "VPN has unestablished", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }


}
