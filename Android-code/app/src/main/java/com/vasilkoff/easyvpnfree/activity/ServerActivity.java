package com.vasilkoff.easyvpnfree.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.ViewGroup.LayoutParams;

import android.os.Bundle;


import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.model.Server;
import com.vasilkoff.easyvpnfree.util.ConnectUtil;
import com.vasilkoff.easyvpnfree.util.TotalTraffic;

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
    private BroadcastReceiver trafficReceiver;
    public final static String BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS";

    protected OpenVPNService mService;
    private VpnProfile vpnProfile;

    private Server currentServer = null;
    private Button unblockCheck;
    private CheckBox adbBlockCheck;
    private Button serverConnect;
    private TextView lastLog;
    private ProgressBar connectingProgress;
    private PopupWindow popupWindow;
    private LinearLayout parentLayout;
    private TextView trafficInTotally;
    private TextView trafficOutTotally;

    private static boolean filterAds = false;
    private static boolean defaultFilterAds = true;

    private boolean autoConnection;
    private boolean fastConnection;
    private Server autoServer;

    private boolean statusConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        parentLayout = (LinearLayout) findViewById(R.id.serverParentLayout);

        autoConnection = getIntent().getBooleanExtra("autoConnection", false);
        fastConnection = getIntent().getBooleanExtra("fastConnection", false);
        currentServer = (Server)getIntent().getParcelableExtra(Server.class.getCanonicalName());

        if (currentServer == null) {
            if (connectedServer != null) {
                currentServer = connectedServer;
            } else {
                startActivity(new Intent(this, HomeActivity.class));
            }
        }

        unblockCheck = (Button) findViewById(R.id.serverUnblockCheck);
        unblockCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("adsFiltering");
                launchPurchase(adblockSKU, ADBLOCK_REQUEST);
            }
        });

        adbBlockCheck = (CheckBox) findViewById(R.id.serverBlockingCheck);
        adbBlockCheck.setChecked(defaultFilterAds);
        adbBlockCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!checkStatus())
                    defaultFilterAds = isChecked;
            }
        });

        connectingProgress = (ProgressBar) findViewById(R.id.serverConnectingProgress);

        String totalIn = String.format(getResources().getString(R.string.traffic_in),
                TotalTraffic.getTotalTraffic().get(0));
        trafficInTotally = (TextView) findViewById(R.id.serverTrafficInTotally);
        trafficInTotally.setText(totalIn);

        String totalOut = String.format(getResources().getString(R.string.traffic_out),
                TotalTraffic.getTotalTraffic().get(1));
        trafficOutTotally = (TextView) findViewById(R.id.serverTrafficOutTotally);
        trafficOutTotally.setText(totalOut);

        ((ImageView) findViewById(R.id.serverFlag))
                .setImageResource(
                        getResources().getIdentifier(currentServer.getCountryShort().toLowerCase(),
                        "drawable",
                        getPackageName()));

        ((TextView) findViewById(R.id.serverCountry)).setText(currentServer.getCountryLong());
        ((TextView) findViewById(R.id.serverIP)).setText(currentServer.getIp());
        ((TextView) findViewById(R.id.serverSessions)).setText(currentServer.getNumVpnSessions());
        ((ImageView) findViewById(R.id.serverImageConnect))
                .setImageResource(
                        getResources().getIdentifier(ConnectUtil.getConnectIcon(currentServer),
                        "drawable",
                        getPackageName()));

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
            adbBlockCheck.setEnabled(false);
            adbBlockCheck.setChecked(filterAds);
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

        trafficReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (checkStatus()) {
                    String in = String.format(getResources().getString(R.string.traffic_in),
                            intent.getStringExtra(TotalTraffic.DOWNLOAD_SESSION));
                    ((TextView) findViewById(R.id.serverTrafficIn)).setText(in);

                    String out = String.format(getResources().getString(R.string.traffic_out),
                            intent.getStringExtra(TotalTraffic.UPLOAD_SESSION));
                    ((TextView) findViewById(R.id.serverTrafficOut)).setText(out);

                    String inTotall = String.format(getResources().getString(R.string.traffic_in),
                            intent.getStringExtra(TotalTraffic.DOWNLOAD_ALL));
                    trafficInTotally.setText(inTotall);

                    String outTotall = String.format(getResources().getString(R.string.traffic_out),
                            intent.getStringExtra(TotalTraffic.UPLOAD_ALL));
                    trafficOutTotally.setText(outTotall);
                }
            }
        };

        registerReceiver(trafficReceiver, new IntentFilter(TotalTraffic.TRAFFIC_ACTION));

        checkAvailableFilter();
    }

    private void checkAvailableFilter() {
        if (availableFilterAds) {
            adbBlockCheck.setVisibility(View.VISIBLE);
            unblockCheck.setVisibility(View.GONE);
        } else {
            adbBlockCheck.setVisibility(View.GONE);
            unblockCheck.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        statusConnection = true;

        if (fastConnection) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), ServersListActivity.class);
            intent.putExtra(HomeActivity.EXTRA_COUNTRY, currentServer.getCountryLong());
            startActivity(intent);
            finish();
        }
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
                statusConnection = true;
                connectingProgress.setVisibility(View.GONE);
                chooseAction();
                serverConnect.setText(getString(R.string.server_btn_disconnect));
                break;
            case LEVEL_NOTCONNECTED:
                serverConnect.setText(getString(R.string.server_btn_connect));
                break;
            default:
                serverConnect.setText(getString(R.string.server_btn_disconnect));
        }
    }

    private void prepareVpn() {
        connectingProgress.setVisibility(View.VISIBLE);
        if (loadVpnProfile()) {
            new WaitConnectionAsync().execute();
            serverConnect.setText(getString(R.string.server_btn_disconnect));
            startVpn();
        } else {
            connectingProgress.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.server_error_loading_profile), Toast.LENGTH_SHORT).show();
        }
    }

    public void serverOnClick(View view) {
        switch (view.getId()) {
            case R.id.serverConnect:
                sendTouchButton("serverConnect");
                if (checkStatus()) {
                    stopVpn();
                } else {
                    prepareVpn();
                }
                break;
            case R.id.serverBtnCheckIp:
                sendTouchButton("serverBtnCheckIp");
                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(getString(R.string.url_check_ip)));
                startActivity(browse);
                break;
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

            filterAds = adbBlockCheck.isChecked();
            if (filterAds) {
                vpnProfile.mOverrideDNS = true;
                vpnProfile.mDNS1 = "198.101.242.72";
                vpnProfile.mDNS2 = "23.253.163.53";
            }

            ProfileManager.getInstance(this).addProfile(vpnProfile);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void stopVpn() {
        statusConnection = true;
        connectingProgress.setVisibility(View.GONE);
        adbBlockCheck.setEnabled(availableFilterAds);
        lastLog.setText(R.string.server_not_connected);
        serverConnect.setText(getString(R.string.server_btn_connect));
        connectedServer = null;
        ProfileManager.setConntectedVpnProfileDisconnected(this);
        if (mService != null && mService.getManagement() != null)
            mService.getManagement().stopVPN(false);

    }

    private void startVpn() {
        connectedServer = currentServer;
        hideCurrentConnection = true;
        adbBlockCheck.setEnabled(false);

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

        if (connectedServer != null && currentServer.getIp().equals(connectedServer.getIp())) {
            hideCurrentConnection = true;
        }

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
            if (autoConnection) {
                prepareVpn();
            }
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
        unregisterReceiver(trafficReceiver);
        if ( popupWindow!=null && popupWindow.isShowing() ){
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_VPN_PROFILE :
                    VPNLaunchHelper.startOpenVpn(vpnProfile, this);
                    break;
                case ADBLOCK_REQUEST:
                    Log.d(IAP_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

                    if (iapHelper == null) return;

                    if (iapHelper.handleActivityResult(requestCode, resultCode, data)) {
                        Log.d(IAP_TAG, "onActivityResult handled by IABUtil.");
                        checkAvailableFilter();
                    }
                    break;
            }
        }
    }

    private void chooseAction() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_up_success_conected,null);

        popupWindow = new PopupWindow(
                view,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        ((Button)view.findViewById(R.id.successPopUpBtnPlayMarket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnPlayMarket");
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        ((Button)view.findViewById(R.id.successPopUpBtnBrowser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnBrowser");
                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse("http://google.com")));
            }
        });
        ((Button)view.findViewById(R.id.successPopUpBtnDesktop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnDesktop");
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
        ((Button)view.findViewById(R.id.successPopUpBtnClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnClose");
                popupWindow.dismiss();
            }
        });


        popupWindow.showAtLocation(parentLayout, Gravity.CENTER,0, 0);

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

    private class WaitConnectionAsync extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(120000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!statusConnection) {
                if (fastConnection) {
                    newConnecting(getRandomServer(), true, true);
                } else if (sharedPreferences.getBoolean("automaticSwitching", true)){
                    autoServer = dbHelper.getSimilarServer(currentServer.getCountryLong(), currentServer.getIp());
                    if (autoServer != null)
                        showAlert();
                }
            }
        }
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = String.format(getResources().getString(R.string.try_another_server_text), currentServer.getCountryLong());
        builder.setMessage(message)
                .setPositiveButton(getString(R.string.try_another_server_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                newConnecting(autoServer, false, true);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getString(R.string.try_another_server_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new WaitConnectionAsync().execute();
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
