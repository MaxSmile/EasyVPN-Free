package com.vasilkoff.easyvpnfree.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.vasilkoff.easyvpnfree.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView aboutFullLicenses = (WebView)findViewById(R.id.aboutFullLicenses);
        aboutFullLicenses.loadUrl("file:///android_asset/full_licenses.html");
    }
}
