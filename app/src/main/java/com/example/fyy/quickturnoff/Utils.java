package com.example.fyy.quickturnoff;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.List;

/**
 * Created by FYY on 28/02/2017.
 */

public class Utils {

    static void showSnack(String text, View view) {

        final Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    static boolean checkInstall(PackageManager pm) {

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.github.shadowsocks")) {
                return true;
            }
        }
        return false;
    }

    static boolean isMyServiceRunning(ActivityManager manager,Class<?> serviceClass) {
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
