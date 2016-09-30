package com.vasilkoff.easyvpnfree.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.model.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasilkoff on 27/09/16.
 */
public class ServerListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<Server> serverList = new ArrayList<Server>();

    public ServerListAdapter(Context c, List<Server> serverList) {
        inflater = LayoutInflater.from(c);
        this.serverList =  serverList;
    }


    @Override
    public int getCount() {
        return serverList.size();
    }


    @Override
    public Server getItem(final int position) {
        return serverList.get(position);
    }


    @Override
    public long getItemId(final int position) {
        return position;
    }


    @Override
    public View getView(final int position, View v, final ViewGroup parent) {
        if (v == null)
            v = inflater.inflate(R.layout.layout_server_record_row, parent, false);

        Server server = getItem(position);

        ((TextView) v.findViewById(R.id.textHostName)).setText(server.getHostName());
        ((TextView) v.findViewById(R.id.textIP)).setText(server.getIp());
        ((TextView) v.findViewById(R.id.textCountry)).setText(server.getCountryLong());
        ((TextView) v.findViewById(R.id.textPing)).setText(server.getPing());

        return v;
    }


}
