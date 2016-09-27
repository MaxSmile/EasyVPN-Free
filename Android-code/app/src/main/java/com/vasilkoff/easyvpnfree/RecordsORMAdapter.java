package com.vasilkoff.easyvpnfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
    public Object getItem(final int position) {
        dbResults.moveAbsolute(position);
        return null;
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
//
//        User u = getItem(position);
//        u.toString();
//        ((TextView) v.findViewById(android.R.id.text1)).setText(u.device);
//        ((TextView) v.findViewById(android.R.id.text2)).setText(u.user);

        return v;
    }


}
