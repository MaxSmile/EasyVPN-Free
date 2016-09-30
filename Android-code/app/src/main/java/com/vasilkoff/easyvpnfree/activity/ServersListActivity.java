package com.vasilkoff.easyvpnfree.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.adapter.ServerListAdapter;
import com.vasilkoff.easyvpnfree.database.DBHelper;
import com.vasilkoff.easyvpnfree.model.Server;

import java.util.List;

public class ServersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_list);

        String country = getIntent().getStringExtra("country");
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new ServerListAdapter(this, new DBHelper(this).getServersByCountry(country)));
    }


}
