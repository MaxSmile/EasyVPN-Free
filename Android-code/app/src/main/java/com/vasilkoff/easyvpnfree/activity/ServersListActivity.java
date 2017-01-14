package com.vasilkoff.easyvpnfree.activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.adapter.ServerListAdapter;
import com.vasilkoff.easyvpnfree.model.Server;

import java.util.List;

import de.blinkt.openvpn.core.VpnStatus;

public class ServersListActivity extends BaseActivity {
    private ListView listView;
    private ServerListAdapter serverListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_list);

        if (!VpnStatus.isVPNActive())
            connectedServer = null;

        listView = (ListView) findViewById(R.id.list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidateOptionsMenu();

        buildList();
    }

    @Override
    protected void ipInfoResult() {
        serverListAdapter.notifyDataSetChanged();
    }

    private void buildList() {
        String country = getIntent().getStringExtra(HomeActivity.EXTRA_COUNTRY);
        final List<Server> serverList = dbHelper.getServersByCountryCode(country);
        serverListAdapter = new ServerListAdapter(this, serverList);

        listView.setAdapter(serverListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Server server = serverList.get(position);
                BaseActivity.sendTouchButton("detailsServer");
                Intent intent = new Intent(ServersListActivity.this, ServerActivity.class);
                intent.putExtra(Server.class.getCanonicalName(), server);
                ServersListActivity.this.startActivity(intent);
            }
        });

        getIpInfo(serverList);
    }
}