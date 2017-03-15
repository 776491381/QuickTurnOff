package com.example.mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.CountDownTimer;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * Created by FYY on 28/02/2017.
 */

public class WLANwithShadowsocks extends TileService {

    public static boolean isActive;
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    public static boolean TileFinalStatus = true ;
    private String filepath = "/mnt/sdcard/data/wifiState.txt";
    FileWriter fw = null;


    public WLANwithShadowsocks() {
    }


    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
//        Log.d("Tile", "Stop ------>");
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(WLANwithShadowsocks.this);
        builder.setMessage(R.string.alert1 + "\n" + R.string.alert2)
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

    @Override
    public void onClick() {
        super.onClick();
        if (checkInstall() == false) {
            showDialog(alert());
            this.stopSelf();
            return;
        }
        TileFinalStatus = updateTile();
        saveFile(String.valueOf(isActive));
        Log.d("TileFinalStatus", String.valueOf(TileFinalStatus));
    }



    private void saveFile(String state){
        try {
            File f=new File(filepath);
            fw = new FileWriter(f,false);
            PrintWriter pw = new PrintWriter(fw);
            pw.print(state);
            pw.flush();
            try {
                fw.flush();
                pw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public boolean getServiceStatus(String PREFERENCES_KEY) {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean updateTile() {

        final Tile tile = this.getQsTile();
        isActive = getServiceStatus("com.google.android_quick_settings");
        final Icon newIcon;
        final String newLabel;
        final int newState;

        if (isActive) {

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ok);

            newState = Tile.STATE_ACTIVE;

        } else {
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.close);

            newState = Tile.STATE_INACTIVE;

        }

        new CountDownTimer(1500, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                tile.setIcon(Icon.createWithResource(getApplicationContext(),
                        R.drawable.ic_waiting));
                tile.setLabel(getString(R.string.wait));
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








}
