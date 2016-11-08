package com.vasilkoff.easyvpnfree.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.util.NetworkState;

public class LauncherActivity extends Activity {
    private static boolean loadStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NetworkState.isOnline()) {
            if (loadStatus) {
                Intent myIntent = new Intent(this, HomeActivity.class);
                startActivity(myIntent);
                finish();
            } else {
                loadStatus = true;
                Intent myIntent = new Intent(this, LoaderActivity.class);
                startActivity(myIntent);
                finish();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.network_error))
                    .setMessage(getString(R.string.network_error_message))
                    .setNegativeButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    onBackPressed();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }



    }
}
