package com.example.fyy.quickturnoff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Switch mySwitch;
    private TextView switchStatus;
    private Switch wifiListen;
    private static final String TAG = "network test:    ";

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(MainActivity.this, QuickSettingsService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.startService(new Intent(MainActivity.this, QuickSettingsService.class));
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

                if (Utils.checkInstall(getPackageManager())) {
                    if (isChecked) {
                        switchStatus.setText("Wifi is currently ON");
                        wifi.setWifiEnabled(true);
                    } else {
                        switchStatus.setText("Wifi is currently OFF");
                        wifi.setWifiEnabled(false);
                    }
                } else {

                    alert();
                }

            }
        });

        if (mySwitch.isChecked() && Utils.checkInstall(getPackageManager())) {
            switchStatus.setText("Wifi is currently ON");
        } else {
            switchStatus.setText("Wifi is currently OFF");
        }
        wifiListen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utils.checkInstall(getPackageManager())) {
                    if (isChecked) {
                        QuickSettingsService.service.wifiListenpos = true;
                        Utils.showSnack("Wifi Listen start success", findViewById(R.id.activity_main));
                        onResume();
                    } else {
                        QuickSettingsService.service.wifiListenpos = false;
                        Utils.showSnack("Wifi Listen close success", findViewById(R.id.activity_main));
                        onResume();
                    }
                } else {

                    alert();
                }
            }
        });

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
        final AlertDialog Alert = builder.create();
        Alert.show();
    }
}
