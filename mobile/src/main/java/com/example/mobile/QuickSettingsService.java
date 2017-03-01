package com.example.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.service.quicksettings.Tile;

import java.util.Locale;

/**
 * Created by FYY on 28/02/2017.
 */

public class QuickSettingsService extends WLANwithShadowsocks {

    private static boolean QisActive;


    @Override
    public void onClick() {
        super.onClick();
        Tile tile = this.getQsTile();
        QisActive = (tile.getState()==tile.STATE_ACTIVE);
        if (checkInstall() == false) {
            showDialog(alert());
            this.stopSelf();
            return;
        }
        this.updateTile();
    }

    @Override
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


    public boolean getServiceStatus() {

        QisActive = !QisActive;


        return QisActive;
    }

    @Override
    public boolean updateTile() {

        final Tile tile = this.getQsTile();
        QisActive = getServiceStatus();
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final Icon newIcon;
        final String newLabel;
        final int newState;

        if (QisActive) {

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label2),
                    getString(R.string.service_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_vpn_lock_24px);

            newState = Tile.STATE_ACTIVE;


                wifi.setWifiEnabled(true);


            if (!this.getVPNStatus()) {
                changSS();
            }

        } else {
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label2),
                    getString(R.string.service_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            android.R.drawable.ic_dialog_alert);

            newState = Tile.STATE_INACTIVE;


            wifi.setWifiEnabled(false);


            if (this.getVPNStatus()) {
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


        return QisActive;

    }

}
