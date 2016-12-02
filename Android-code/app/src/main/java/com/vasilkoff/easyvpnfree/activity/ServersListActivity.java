package com.vasilkoff.easyvpnfree.activity;
import android.os.Bundle;
import android.widget.ListView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.adapter.ServerListAdapter;
import com.vasilkoff.easyvpnfree.model.Server;

import java.util.List;

import de.blinkt.openvpn.core.VpnStatus;

public class ServersListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_list);

        if (!VpnStatus.isVPNActive()) {
            connectedServer = null;
        }

        String country = getIntent().getStringExtra(HomeActivity.EXTRA_COUNTRY);
        ListView listView = (ListView) findViewById(R.id.list);
        final List<Server> serverList = dbHelper.getServersByCountry(country);
        listView.setAdapter(new ServerListAdapter(this, serverList));
    }

}