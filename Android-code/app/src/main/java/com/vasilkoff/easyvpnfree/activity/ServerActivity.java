package com.vasilkoff.easyvpnfree.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.model.Server;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        Server server = (Server)getIntent().getParcelableExtra(Server.class.getCanonicalName());

        ((TextView) findViewById(R.id.serverCountry)).setText(server.getCountryLong());
        ((TextView) findViewById(R.id.serverIP)).setText(server.getIp());
        ((TextView) findViewById(R.id.serverSessions)).setText(server.getNumVpnSessions());

        String ping = server.getPing() + " " +  getString(R.string.ms);
        ((TextView) findViewById(R.id.serverPing)).setText(ping);

        double speedValue = (double) Integer.parseInt(server.getSpeed()) / 1048576;
        speedValue = new BigDecimal(speedValue).setScale(3, RoundingMode.UP).doubleValue();
        String speed = String.valueOf(speedValue) + " " + getString(R.string.mbps);
        ((TextView) findViewById(R.id.serverSpeed)).setText(speed);

    }
}
