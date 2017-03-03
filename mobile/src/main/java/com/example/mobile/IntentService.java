//package com.example.mobile;
//
//import android.app.IntentService;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.Network;
//import android.net.NetworkCapabilities;
//
///**
// * Created by FYY on 02/03/2017.
// */
//
//public class intentService extends IntentService {
//
//    public intentService() {
//        super("intentService");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//
//
//    }
//
//
//
//
//    public void changSS() {
//
//        Intent i = new Intent();
//
//        ComponentName comp = new ComponentName("com.github.shadowsocks", "com.github.shadowsocks.QuickToggleShortcut");
//
//        i.setComponent(comp);
//
//        i.setAction("android.intent.action.VIEW");
//
//        startActivity(i);
//    }
//
//
//
//    public void turnOff() {
//
//        if (getVPNStatus()) {
//            changSS();
//        }
//
//    }
//
//    public void turnOn() {
//
//        if (!getVPNStatus()) {
//            changSS();
//        }
//
//
//    }
//
//    public boolean getVPNStatus() {
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        Network[] networks = cm.getAllNetworks();
//
//        for (int i = 0; i < networks.length; i++) {
//
//            NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);
//
//
//            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
//                return true;
//            }
//        }
//        return false;
//
//    }
//
//
//
//
//
//}
