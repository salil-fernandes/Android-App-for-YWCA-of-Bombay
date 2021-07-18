package com.sevatech.ywca;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class InternetConnection {

    Context mContext;
    public InternetConnection(Context mContext) {
        this.mContext = mContext;

    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void Dialog(final Context mContext){
        this.mContext = mContext;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("ERROR")
                .setMessage("No Internet Connection,check your settings")
                .setNeutralButton("OK", null)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) mContext).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }

                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#417eca"));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
    }



}
