package com.vasilkoff.easyvpnfree.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        String country = getIntent().getStringExtra(HomeActivity.EXTRA_COUNTRY);
        ListView listView = (ListView) findViewById(R.id.list);
        final List<Server> serverList = new DBHelper(this).getServersByCountry(country);
        listView.setAdapter(new ServerListAdapter(this, serverList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ServerActivity.class);
                intent.putExtra(Server.class.getCanonicalName(), serverList.get(position));
                startActivity(intent);
            }
        });
    }


}
