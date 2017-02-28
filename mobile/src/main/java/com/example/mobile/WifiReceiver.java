package com.example.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.service.quicksettings.Tile;
import android.widget.Toast;

/**
 * Created by FYY on 28/02/2017.
 */
public  class WifiReceiver extends BroadcastReceiver {


    static NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;

    @Override
    public void onReceive(Context context, Intent intent) {
        Tile tile = WLANwithShadowsocks.service.getQsTile();
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) && (tile.getState()==tile.STATE_ACTIVE) ){
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
                    WLANwithShadowsocks.service.turnOn();
                    Toast.makeText(context, "VPN has established", Toast.LENGTH_LONG).show();
                } else {
                    WLANwithShadowsocks.service.turnOff();
                    Toast.makeText(context, "VPN has unestablished", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

