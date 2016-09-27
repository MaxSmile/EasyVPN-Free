package com.vasilkoff.easyvpnfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.vasilkoff.easyvpnfree.database.VPNGateServerRecordsHelper;
import com.vasilkoff.easyvpnfree.model.VPNGateServerRecord;

import java.sql.SQLException;

/**
 * Created by Vasilkoff on 27/09/16.
 */
public class RecordsORMAdapter extends BaseAdapter {

    private AndroidDatabaseResults dbResults;
    private final LayoutInflater inflater;

    public RecordsORMAdapter(Context c) {
        inflater = LayoutInflater.from(c);
        try {
            dbResults = (AndroidDatabaseResults) new VPNGateServerRecordsHelper(c).getDao().iterator().getRawResults();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getCount() {
        if (dbResults==null)
            return 0;
        return dbResults.getCount();
    }


    @Override
    public VPNGateServerRecord getItem(final int position) {
        dbResults.moveAbsolute(position);
        String[] s = new String[dbResults.getColumnCount()];
        // TODO: make sure about columns order
        for (int i = 0; i < dbResults.getColumnCount(); i++) {
            s[i] = dbResults.getString(i);
        }
        return new VPNGateServerRecord(s);
    }


    @Override
    public long getItemId(final int position) {
        return position;
    }


    @Override
    public View getView(final int position, View v, final ViewGroup parent) {
        if (v == null)
            v = inflater.inflate(R.layout.layout_server_record_row, parent, false);

        VPNGateServerRecord r = getItem(position);

        ((TextView) v.findViewById(R.id.textHostName)).setText(r.HostName);
        ((TextView) v.findViewById(R.id.textIP)).setText(r.IP);
        ((TextView) v.findViewById(R.id.textCountry)).setText(r.CountryLong);
        ((TextView) v.findViewById(R.id.textPing)).setText(r.Ping);

        return v;
    }


}
