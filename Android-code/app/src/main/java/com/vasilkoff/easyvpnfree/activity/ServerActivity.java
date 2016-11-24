package com.vasilkoff.easyvpnfree.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.IBinder;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vasilkoff.easyvpnfree.BuildConfig;
import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.model.Server;
import com.vasilkoff.easyvpnfree.util.iap.IabHelper;
import com.vasilkoff.easyvpnfree.util.iap.IabResult;
import com.vasilkoff.easyvpnfree.util.iap.Inventory;
import com.vasilkoff.easyvpnfree.util.iap.Purchase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

public class ServerActivity extends BaseActivity {

    static final String TAG = "IAP";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int START_VPN_PROFILE = 70;
    private BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS";

    static final String TEST_ITEM_SKU = "android.test.purchased";
    static final String ITEM_SKU = "adblock";
    private String gmail = "";
    private String sku;
    static final int RC_REQUEST = 10001;
    IabHelper purchaseHelper;
    String base64EncodedPublicKey;

    protected OpenVPNService mService;
    private VpnProfile vpnProfile;

    private Server currentServer = null;
    private Button unblockCheck;
    private CheckBox adbBlockCheck;
    private Button serverConnect;

    private TextView lastLog;

    private static boolean filterAds = false;
    private static boolean defaultFilterAds = true;
    private static boolean availableFilterAds = false;
    private boolean availablePurchase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        base64EncodedPublicKey = getString(R.string.base64EncodedPublicKey);
        if (BuildConfig.DEBUG) {
            sku = TEST_ITEM_SKU;
        } else {
            sku = ITEM_SKU;
        }

        currentServer = (Server)getIntent().getParcelableExtra(Server.class.getCanonicalName());
        if (currentServer == null)
            currentServer = connectedServer;

        unblockCheck = (Button) findViewById(R.id.serverUnblockCheck);
        unblockCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
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

        initPurchaseHelper();

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

    private void initPurchaseHelper() {
        purchaseHelper = new IabHelper(this, base64EncodedPublicKey);
        purchaseHelper.enableDebugLogging(true);

        purchaseHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    availablePurchase = false;
                    Log.d(TAG, "Oh noes, there was a problem.");
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (purchaseHelper == null) return;

                availablePurchase = true;

                if (!availableFilterAds) {
                    checkPurchase();
                } else {
                    checkAvailableFilter();
                }
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");

            }
        });
    }

    private void launchPurchase() {
        getUserEmailFromAndroidAccounts();
        if (purchaseHelper != null) purchaseHelper.flagEndAsync();
        purchaseHelper.launchPurchaseFlow(this,
                sku, RC_REQUEST,
                mPurchaseFinishedListener,
                gmail + sku);
    }

    private void checkPurchase() {
        if (availablePurchase) {
            if (purchaseHelper != null) purchaseHelper.flagEndAsync();
            purchaseHelper.queryInventoryAsync(mGotInventoryListener);
        } else {
            Toast.makeText(this, getString(R.string.feature_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Oh noes, there was a problem.");
                return;
            } else {
                if (purchase.getSku().equals(sku)) {
                    if (verifyDeveloperPayload(purchase)) {
                        availableFilterAds = true;
                        checkAvailableFilter();
                        adbBlockCheck.setChecked(true);
                    }
                }
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + inventory);
            } else {
                if (inventory.hasPurchase(sku)) {
                    availableFilterAds = true;
                    checkAvailableFilter();
                }
            }
        }
    };

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }

        launchPurchase();
    }

    private void getUserEmailFromAndroidAccounts() {
        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                gmail = account.name;
            }
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String responsePayload = p.getDeveloperPayload();
        String computedPayload = gmail + sku;

        return responsePayload != null && responsePayload.equals(computedPayload);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    launchPurchase();
                } else {
                    // Permission Denied
                    Toast.makeText(this, getString(R.string.feature_not_available_without_perm), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        switch (view.getId()) {
            case R.id.serverConnect:
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
                break;
            case R.id.serverBtnCheckIp:
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
                vpnProfile.mDNS1 = "62.109.4.190";
                vpnProfile.mDNS2 = "176.103.130.130";
            }

            ProfileManager.getInstance(this).addProfile(vpnProfile);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void stopVpn() {
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
        if (purchaseHelper != null) purchaseHelper.dispose();
        purchaseHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_VPN_PROFILE :
                    VPNLaunchHelper.startOpenVpn(vpnProfile, this);
                    break;
                case RC_REQUEST:
                    Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
                    if (purchaseHelper == null) return;

                    // Pass on the activity result to the helper for handling
                    if (!purchaseHelper.handleActivityResult(requestCode, resultCode, data)) {
                        // not handled, so handle it ourselves (here's where you'd
                        // perform any handling of activity results not related to in-app
                        // billing...
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                    else {
                        Log.d(TAG, "onActivityResult handled by IABUtil.");
                    }
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
