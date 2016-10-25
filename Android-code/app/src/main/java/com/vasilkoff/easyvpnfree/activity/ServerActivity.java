package com.vasilkoff.easyvpnfree.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.VpnService;
import android.os.IBinder;

import android.os.Bundle;

import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.model.Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;


import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

public class ServerActivity extends BaseActivity {

    private static final int START_VPN_PROFILE = 70;
    private BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS";

    protected OpenVPNService mService;
    private VpnProfile vpnProfile;

    private Server currentServer = null;
    private Button serverConnect;

    private TextView lastLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        currentServer = (Server)getIntent().getParcelableExtra(Server.class.getCanonicalName());
        if (currentServer == null)
            currentServer = connectedServer;

        ((ImageView) findViewById(R.id.serverFlag))
                .setImageResource(
                        getResources().getIdentifier(currentServer.getCountryShort().toLowerCase(),
                        "drawable",
                        getPackageName()));

        ((TextView) findViewById(R.id.serverCountry)).setText(currentServer.getCountryLong());
        ((TextView) findViewById(R.id.serverIP)).setText(currentServer.getIp());
        ((TextView) findViewById(R.id.serverSessions)).setText(currentServer.getNumVpnSessions());

        String ping = currentServer.getPing() + " " +  getString(R.string.ms);
        ((TextView) findViewById(R.id.serverPing)).setText(ping);

        double speedValue = (double) Integer.parseInt(currentServer.getSpeed()) / 1048576;
        speedValue = new BigDecimal(speedValue).setScale(3, RoundingMode.UP).doubleValue();
        String speed = String.valueOf(speedValue) + " " + getString(R.string.mbps);
        ((TextView) findViewById(R.id.serverSpeed)).setText(speed);

        lastLog = (TextView) findViewById(R.id.serverStatus);
        lastLog.setText(R.string.server_not_connected);

        serverConnect = (Button) findViewById(R.id.serverConnect);



        if (checkStatus()) {
            serverConnect.setText(getString(R.string.server_btn_disconnect));
            ((TextView) findViewById(R.id.serverStatus)).setText(VpnStatus.getLastCleanLogMessage(getApplicationContext()));
        } else {
            serverConnect.setText(getString(R.string.server_btn_connect));
        }

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (checkStatus()) {
                    changeServerStatus(VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status")));
                    lastLog.setText(VpnStatus.getLastCleanLogMessage(getApplicationContext()));
                }
            }
        };

        registerReceiver(br, new IntentFilter(BROADCAST_ACTION));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ServersListActivity.class);
        intent.putExtra(HomeActivity.EXTRA_COUNTRY, currentServer.getCountryLong());
        startActivity(intent);
        finish();
    }

    private boolean checkStatus() {
        if (connectedServer != null && connectedServer.getHostName().equals(currentServer.getHostName())) {
            return VpnStatus.isVPNActive();
        }

        return false;
    }

    private void changeServerStatus(VpnStatus.ConnectionStatus status) {
        switch (status) {
            case LEVEL_CONNECTED:
                serverConnect.setText(getString(R.string.server_btn_disconnect));
                break;
            case LEVEL_NOTCONNECTED:
                serverConnect.setText(getString(R.string.server_btn_connect));
                break;
            default:
                serverConnect.setText(getString(R.string.server_btn_disconnect));
        }
    }

    public void serverOnClick(View view) {
        if (checkStatus()) {
            stopVpn();
        } else {
            if (loadVpnProfile()) {
                serverConnect.setText(getString(R.string.server_btn_disconnect));
                startVpn();
            } else {
                Toast.makeText(this, getString(R.string.server_error_loading_profile), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean loadVpnProfile() {
        byte[] data = Base64.decode(currentServer.getConfigData(), Base64.DEFAULT);
        ConfigParser cp = new ConfigParser();
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(data));
        try {
            cp.parseConfig(isr);
            vpnProfile = cp.convertProfile();
            vpnProfile.mName = currentServer.getCountryLong();
            ProfileManager.getInstance(this).addProfile(vpnProfile);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void stopVpn() {
        lastLog.setText(R.string.server_not_connected);
        serverConnect.setText(getString(R.string.server_btn_connect));
        connectedServer = null;
        ProfileManager.setConntectedVpnProfileDisconnected(this);
        if (mService != null && mService.getManagement() != null)
            mService.getManagement().stopVPN(false);

    }

    private void startVpn() {
        connectedServer = currentServer;

        Intent intent = VpnService.prepare(this);

        if (intent != null) {
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                    VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                VpnStatus.logError(R.string.no_vpn_support_image);
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getAction() != null)
            stopVpn();

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (checkStatus()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!checkStatus()) {
                connectedServer = null;
                serverConnect.setText(getString(R.string.server_btn_connect));
                lastLog.setText(R.string.server_not_connected);
            }

        } else {
            serverConnect.setText(getString(R.string.server_btn_connect));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_VPN_PROFILE :
                    VPNLaunchHelper.startOpenVpn(vpnProfile, this);
                    break;
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };
}
