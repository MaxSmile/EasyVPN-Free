package com.vasilkoff.easyvpnfree.activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.adapter.ServerListAdapter;
import com.vasilkoff.easyvpnfree.model.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import de.blinkt.openvpn.core.VpnStatus;

public class ServersListActivity extends BaseActivity {
    private ListView listView;
    private final String BASE_URL = "http://ip-api.com/batch";

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

        buildList(true);
    }

    private void buildList(boolean getInfo) {
        String country = getIntent().getStringExtra(HomeActivity.EXTRA_COUNTRY);
        final List<Server> serverList = dbHelper.getServersByCountryCode(country);
        ServerListAdapter serverListAdapter = new ServerListAdapter(this, serverList);

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

        if (getInfo)
            getIpInfo(serverList);
    }

    private void getIpInfo(List<Server> serverList) {
        JSONArray jsonArray = new JSONArray();

        for (Server server : serverList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("query", server.getIp());
                jsonObject.put("lang", Locale.getDefault().getLanguage());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AndroidNetworking.post(BASE_URL)
                .addJSONArrayBody(jsonArray)
                .setTag("getIpInfo")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dbHelper.setIpInfo(response);
                        buildList(false);
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }
}