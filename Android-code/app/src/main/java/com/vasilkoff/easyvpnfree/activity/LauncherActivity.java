package com.vasilkoff.easyvpnfree.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {
    private static boolean loadStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }
}
