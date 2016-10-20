package com.vasilkoff.easyvpnfree.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.activity.ServerActivity;
import com.vasilkoff.easyvpnfree.model.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasilkoff on 27/09/16.
 */
public class ServerListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<Server> serverList = new ArrayList<Server>();
    private Context context;

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

        ((ImageView) v.findViewById(R.id.imageFlag))
                .setImageResource(
                        context.getResources().getIdentifier(server.getCountryShort().toLowerCase(),
                                "drawable",
                                context.getPackageName()));

        ((TextView) v.findViewById(R.id.textHostName)).setText(server.getHostName());
        ((TextView) v.findViewById(R.id.textIP)).setText(server.getIp());
        ((TextView) v.findViewById(R.id.textCountry)).setText(server.getCountryLong());
        ((TextView) v.findViewById(R.id.textPing)).setText(server.getPing());

        Button button = (Button) v.findViewById(R.id.serverListConnect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServerActivity.class);
                intent.putExtra(Server.class.getCanonicalName(), getItem(position));
                context.startActivity(intent);
            }
        });

        return v;
    }


}
