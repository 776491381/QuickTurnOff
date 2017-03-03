package com.example.mobile;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by FYY on 28/02/2017.
 */
public class WifiReceiver extends BroadcastReceiver {


    static NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
    private boolean ReceiveStatus;


    @Override
    public void onReceive(final Context context, final Intent intent) {


            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                        ReceiveStatus = WLANwithShadowsocks.service.TileFinalStatus;
                }

                @Override
                public void onFinish() {
                    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) && ReceiveStatus) {
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

                                turnOn(context);
                                Toast.makeText(context, "VPN has established", Toast.LENGTH_LONG).show();
                            } else {
                                turnOff(context);
                                Toast.makeText(context, "VPN has unestablished", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }.start();


        }




    public boolean getVPNStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();

        for (int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);


            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
                return true;
            }
        }
        return false;

    }

    public void turnOff(Context context) {

        if (getVPNStatus(context)) {
            changSS(context);
        }

    }

    public void turnOn(Context context) {

        if (!getVPNStatus(context)) {
            changSS(context);
        }


    }

    public void changSS(Context context) {

        Intent i = new Intent();

        ComponentName comp = new ComponentName("com.github.shadowsocks", "com.github.shadowsocks.QuickToggleShortcut");

        i.setComponent(comp);

        i.setAction("android.intent.action.VIEW");

        context.startActivity(i);
    }


}