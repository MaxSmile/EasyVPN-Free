package com.vasilkoff.easyvpnfree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ServersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_list);

        ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(new RecordsORMAdapter(this));
    }


}
