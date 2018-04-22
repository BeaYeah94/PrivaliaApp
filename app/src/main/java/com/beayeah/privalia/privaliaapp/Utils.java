package com.beayeah.privalia.privaliaapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

public final class Utils {

    public static void showSnackbar(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbar(View v, String msg, View.OnClickListener listener) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
                .setAction("try again", listener)
                .show();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
