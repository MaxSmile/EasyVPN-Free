package com.vasilkoff.easyvpnfree.activity;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.vasilkoff.easyvpnfree.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;
            String appName =  AboutActivity.this.getString(R.string.app_name);
            TextView versionText = (TextView)findViewById(R.id.appVersion);
            versionText.setText(
                    String.format("%s version %s build %d",
                            appName,
                            versionName,
                            versionNumber
                            )
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
