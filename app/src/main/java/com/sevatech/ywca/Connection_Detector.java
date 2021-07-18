package com.sevatech.ywca;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static android.content.Context.MODE_PRIVATE;

public class Connection_Detector extends AsyncTask<String, Void, Integer> {

    @SuppressLint("StaticFieldLeak")
    Context context;

    public Connection_Detector(Context context)
    {
        this.context = context;
    }

    public Boolean isConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info!=null)
            {
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Integer doInBackground(String... strings) {

        Integer result = 0;


        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
            socket.connect(socketAddress, 1500);
            socket.close();
            result = 1;
        } catch (IOException e) {
            result = 0;
        }
        SharedPreferences sp= context.getSharedPreferences("Flag",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Flag", result);
        editor.apply();
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (isConnected())
        {

            if (result == 0)
            {
                Toast.makeText(context,"Connection Available but no internet",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("ERROR")
                        .setMessage("No Internet Connection,check your settings")
                        .setNeutralButton("OK", null)
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity) context).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }

                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#417eca"));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
            }

        }

        else
        {
            Toast.makeText(context, "No internet Available", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("ERROR")
                    .setMessage("No Internet Connection,check your settings")
                    .setNeutralButton("OK", null)
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) context).startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }

                    });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#417eca"));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#417eca"));
        }
        super.onPostExecute(result);
    }
}
