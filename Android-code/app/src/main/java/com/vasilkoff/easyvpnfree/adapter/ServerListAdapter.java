package com.vasilkoff.easyvpnfree.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.activity.BaseActivity;
import com.vasilkoff.easyvpnfree.activity.ServerActivity;
import com.vasilkoff.easyvpnfree.model.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasilkoff on 27/09/16.
 */
public class ServerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Server> serverList = new ArrayList<Server>();
    private Context context;

    private static final String CONNECT_BAD = "ic_connect_bad";
    private static final String CONNECT_GOOD = "ic_connect_good";
    private static final String CONNECT_EXCELLENT = "ic_connect_excellent";

    public ServerListAdapter(Context c, List<Server> serverList) {
        inflater = LayoutInflater.from(c);
        context = c;
        this.serverList =  serverList;
    }


    @Override
    public int getCount() {
        return serverList.size();
    }


    @Override
    public Server getItem(int position) {
        return serverList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {

        v = inflater.inflate(R.layout.layout_server_record_row, parent, false);

        final Server server = getItem(position);

        ((ImageView) v.findViewById(R.id.imageFlag))
                .setImageResource(
                        context.getResources().getIdentifier(server.getCountryShort().toLowerCase(),
                                "drawable",
                                context.getPackageName()));
        ((ImageView) v.findViewById(R.id.imageConnect))
                .setImageResource(
                        context.getResources().getIdentifier(getConnectIcon(server),
                                "drawable",
                                context.getPackageName()));

        ((TextView) v.findViewById(R.id.textHostName)).setText(server.getHostName());
        ((TextView) v.findViewById(R.id.textIP)).setText(server.getIp());
        ((TextView) v.findViewById(R.id.textCountry)).setText(server.getCountryLong());

        Button button = (Button) v.findViewById(R.id.serverListConnect);

        if (BaseActivity.connectedServer != null && BaseActivity.connectedServer.getHostName().equals(server.getHostName())) {
            button.setBackground(ContextCompat.getDrawable(context, R.drawable.connected_bg));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServerActivity.class);
                intent.putExtra(Server.class.getCanonicalName(), server);
                context.startActivity(intent);
            }
        });

        return v;
    }

    private String getConnectIcon(Server server) {
        int speed = Integer.parseInt(server.getSpeed());
        int sessions = Integer.parseInt(server.getNumVpnSessions());

        int ping = 0;
        if (!(server.getPing().equals("-") || server.getPing().equals(""))) {
            ping = Integer.parseInt(server.getPing());
        }

        if (speed > 10000000 && ping < 30 && (sessions != 0 && sessions < 100)) {
            return CONNECT_EXCELLENT;
        } else if (speed < 1000000 || ping > 100 || (sessions == 0 || sessions > 150)) {
            return CONNECT_BAD;
        } else {
            return CONNECT_GOOD;
        }
    }


}
