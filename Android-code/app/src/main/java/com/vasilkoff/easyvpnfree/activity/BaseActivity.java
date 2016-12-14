package com.vasilkoff.easyvpnfree.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vasilkoff.easyvpnfree.BuildConfig;
import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.database.DBHelper;
import com.vasilkoff.easyvpnfree.model.Server;
import com.vasilkoff.easyvpnfree.util.iap.IabHelper;
import com.vasilkoff.easyvpnfree.util.iap.IabResult;
import com.vasilkoff.easyvpnfree.util.iap.Inventory;
import com.vasilkoff.easyvpnfree.util.iap.Purchase;

import java.util.regex.Pattern;

/**
 * Created by Kusenko on 20.10.2016.
 */

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    static final int ADBLOCK_REQUEST = 10001;
    static final int PREMIUM_SERVERS_REQUEST = 10002;
    public static Server connectedServer = null;
    IabHelper iapHelper;
    public static final String IAP_TAG = "IAP";
    static final String TEST_ITEM_SKU = "android.test.purchased";
    static final String ADBLOCK_ITEM_SKU = "adblock";
    static final String MORE_SERVERS_ITEM_SKU = "more_servers";
    static String gmail = "";

    static boolean availableFilterAds = false;
    static boolean premiumServers = false;
    static String adblockSKU;
    static String moreServersSKU;
    static String currentSKU;
    static int currentRequest;

    static DBHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    public void setContentView(int layoutResID)
    {
        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (useToolbar()) {
            setSupportActionBar(toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
        }

        if (useHomeButton()) {
            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        initPurchaseHelper();

        if (BuildConfig.DEBUG) {
            moreServersSKU = TEST_ITEM_SKU;
            adblockSKU = TEST_ITEM_SKU;
        } else {
            moreServersSKU = MORE_SERVERS_ITEM_SKU;
            adblockSKU = ADBLOCK_ITEM_SKU;
        }

        dbHelper = new DBHelper(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iapHelper != null) iapHelper.dispose();
        iapHelper = null;
    }

    private void initPurchaseHelper() {
        iapHelper = new IabHelper(this, getString(R.string.base64EncodedPublicKey));
        iapHelper.enableDebugLogging(BuildConfig.DEBUG);

        iapHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    // Have we been disposed of in the meantime? If so, quit.
                    if (iapHelper == null) return;

                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(IAP_TAG, "Setup successful. Querying inventory.");

                    checkPurchase();
                } else {
                    Log.d(IAP_TAG, "Oh noes, there was a problem.");
                }
            }
        });
    }

    private void checkPurchase() {
        iapHelper.flagEndAsync();
        iapHelper.queryInventoryAsync(mGotInventoryListener);
    }

    private void launchPurchase() {
        getUserEmailFromAndroidAccounts();
        iapHelper.flagEndAsync();
        iapHelper.launchPurchaseFlow(this,
                currentSKU, currentRequest,
                mPurchaseFinishedListener,
                gmail + currentSKU);
    }

    void checkPermissions(String sku, int request) {
        currentSKU = sku;
        currentRequest = request;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }

        launchPurchase();
    }

    void getUserEmailFromAndroidAccounts() {
        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                gmail = account.name;
            }
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p, String sku) {
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
                    Toast.makeText(this, getString(R.string.feature_not_available_without_perm), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                Log.d(IAP_TAG, "Purchase finished: " + result + ", purchase: " + inventory);
            } else {
                if (inventory.hasPurchase(adblockSKU)) {
                    availableFilterAds = true;
                }
                if (inventory.hasPurchase(moreServersSKU)) {
                    premiumServers = true;
                }
            }
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(IAP_TAG, "Oh noes, there was a problem.");
                return;
            } else {
                if (purchase.getSku().equals(adblockSKU) && verifyDeveloperPayload(purchase, adblockSKU)) {
                    availableFilterAds = true;
                }
            }
        }
    };

    protected boolean useToolbar()
    {
        return true;
    }

    protected boolean useHomeButton()
    {
        return true;
    }

    protected boolean useMenu()
    {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == R.id.actionCurrentServer && connectedServer == null)
                menu.getItem(i).setVisible(false);

            if (premiumServers && menu.getItem(i).getItemId() == R.id.actionGetMoreServers)
                menu.getItem(i).setTitle(getString(R.string.current_servers_list));
        }

        return useMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionRefresh:
                startActivity(new Intent(getApplicationContext(), LoaderActivity.class));
                finish();
                return true;
            case R.id.actionAbout:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            case R.id.actionShare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.actionCurrentServer:
                if (connectedServer != null)
                    startActivity(new Intent(this, ServerActivity.class));
                return true;
            case R.id.actionGetMoreServers:
                if (premiumServers) {
                    startActivity(new Intent(this, ServersInfo.class));
                } else {
                    checkPermissions(moreServersSKU, PREMIUM_SERVERS_REQUEST);
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, MyPreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PREMIUM_SERVERS_REQUEST:
                    Log.d(IAP_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

                    if (iapHelper == null) return;

                    if (iapHelper.handleActivityResult(requestCode, resultCode, data)) {
                        Log.d(IAP_TAG, "onActivityResult handled by IABUtil.");
                        Intent intent = new Intent(getApplicationContext(), LoaderActivity.class);
                        intent.putExtra("firstPremiumLoad", true);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    }

    public Server getRandomServer() {
        Server randomServer;
        if (sharedPreferences.getBoolean("countryPriority", false)) {
            String selectedCountry = sharedPreferences.getString("selectedCountry", null);
            randomServer = dbHelper.getGoodRandomServer(selectedCountry);
        } else {
            randomServer = dbHelper.getGoodRandomServer(null);
        }
        return randomServer;
    }
}
