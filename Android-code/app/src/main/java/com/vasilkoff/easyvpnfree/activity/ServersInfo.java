package com.vasilkoff.easyvpnfree.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.database.DBHelper;

public class ServersInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_info);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int widthWindow = dm.widthPixels;
        int heightWindow = dm.heightPixels;

        if (getResources().getConfiguration().orientation == 1) {
            getWindow().setLayout((int)(widthWindow * 0.7), (int)(heightWindow * 0.3));
        } else {
            getWindow().setLayout((int)(widthWindow * 0.4), (int)(heightWindow * 0.5));
        }

        DBHelper dbHelper = new DBHelper(this);

        String basicServers = String.format(getResources().getString(R.string.info_basic), dbHelper.getCountBasic());
        ((TextView) findViewById(R.id.infoBasic)).setText(basicServers);

        String additionalServers = String.format(getResources().getString(R.string.info_additional), dbHelper.getCountAdditional());
        ((TextView) findViewById(R.id.infoAdditional)).setText(additionalServers);
    }
}
