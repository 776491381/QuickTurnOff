package com.example.fyy.quickturnoff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Switch mySwitch;
    private TextView switchStatus;
    private Switch wifiListen;
    private static final String TAG = "network test:    ";

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, QuickSettingsService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        wifiListen = (Switch) findViewById(R.id.wifiListen);
        switchStatus = (TextView) findViewById(R.id.switchStatus);
        final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mySwitch.setChecked(wifi.isWifiEnabled());
        wifiListen.setChecked(QuickSettingsService.service.wifiListenpos);


        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (checkInstall()) {
                    if (isChecked) {
                        switchStatus.setText("Switch is currently ON");
                        wifi.setWifiEnabled(true);
                    } else {
                        switchStatus.setText("Switch is currently OFF");
                        wifi.setWifiEnabled(false);
                    }
                } else {

                    alert();
                }

            }
        });

        if (mySwitch.isChecked() && checkInstall()) {
            switchStatus.setText("Switch is currently ON");
        } else {
            switchStatus.setText("Switch is currently OFF");
        }

        wifiListen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkInstall()) {
                    if (isChecked) {
                        QuickSettingsService.service.wifiListenpos = true;
                        onResume();
                    } else {
                        QuickSettingsService.service.wifiListenpos = false;
                        onResume();
                    }
                } else {

                    alert();
                }
            }
        });

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


    public void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("don't have Shadowsocks" + "\n" + "Press yes to install")
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.github.shadowsocks");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAndRemoveTask();

                    }

                });
        // Create the AlertDialog object and return it
        final AlertDialog Alert = builder.create();
        Alert.show();
    }
}
