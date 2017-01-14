package com.vasilkoff.easyvpnfree.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;

import com.crashlytics.android.answers.CustomEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vasilkoff.easyvpnfree.BuildConfig;
import com.vasilkoff.easyvpnfree.R;

import com.vasilkoff.easyvpnfree.model.Country;
import com.vasilkoff.easyvpnfree.model.Server;
import com.vasilkoff.easyvpnfree.util.BitmapGenerator;

import com.vasilkoff.easyvpnfree.util.ConnectionQuality;
import com.vasilkoff.easyvpnfree.util.LoadData;
import com.vasilkoff.easyvpnfree.util.PropertiesService;
import com.vasilkoff.easyvpnfree.util.map.MapCreator;
import com.vasilkoff.easyvpnfree.util.map.MyMarker;


import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.overlay.Marker;

import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.List;




public class HomeActivity extends BaseActivity {

    private MapView mapView;

    public static final String EXTRA_COUNTRY = "country";
    private PopupWindow popupWindow;
    private RelativeLayout homeContextRL;

    private List<Server> countryList;
    private final String COUNTRY_FILE_NAME = "countries.json";

    private List<Country> countryLatLonList = null;

    private Layers layers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);
        countryList = dbHelper.getUniqueCountries();

        long totalServ = dbHelper.getCount();
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logCustom(new CustomEvent("Total servers")
                .putCustomAttribute("Total servers", totalServ));

        String totalServers = String.format(getResources().getString(R.string.total_servers), totalServ);
        ((TextView) findViewById(R.id.homeTotalServers)).setText(totalServers);

        initMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    private void initMap() {
        AndroidGraphicFactory.createInstance(getApplication());
        mapView = new MapView(this);

        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(false);
        mapView.setBuiltInZoomControls(false);
        mapView.setZoomLevelMin((byte) 2);
        mapView.setZoomLevelMax((byte) 10);

        mapView.setZoomLevel((byte) 2);
        mapView.getModel().displayModel.setBackgroundColor(ContextCompat.getColor(this, R.color.mapBackground));

        layers = mapView.getLayerManager().getLayers();

        MapCreator mapCreator = new MapCreator(this, layers);
        mapCreator.parseGeoJson("world_map.geo.json");

        initServerOnMap(layers);

        LinearLayout map = (LinearLayout) findViewById(R.id.map);
        map.addView(mapView);
    }


    @Override
    protected boolean useHomeButton() {
        return false;
    }

    public void homeOnClick(View view) {
        switch (view.getId()) {
            case R.id.homeBtnChooseCountry:
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();
                break;
            case R.id.homeBtnRandomConnection:
                sendTouchButton("homeBtnRandomConnection");
                Server randomServer = getRandomServer();
                if (randomServer != null) {
                    newConnecting(randomServer, true, true);
                } else {
                    String randomError = String.format(getResources().getString(R.string.error_random_country), PropertiesService.getSelectedCountry());
                    Toast.makeText(this, randomError, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void chooseCountry() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_up_choose_country,null);

        if (getResources().getConfiguration().orientation == 1) {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * 0.8f),
                    (int)(heightWindow * 0.7f)
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * 0.6f),
                    (int)(heightWindow * 0.8f)
            );
        }


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();
            countryListName.add(localeCountryName);
        }

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER,0, 0);
    }

    private void onSelectCountry(Server server) {
        Intent intent = new Intent(getApplicationContext(), ServersListActivity.class);
        intent.putExtra(EXTRA_COUNTRY, server.getCountryShort());
        startActivity(intent);
    }

    private void initServerOnMap(Layers layers) {
        Type listType = new TypeToken<ArrayList<Country>>(){}.getType();
        countryLatLonList =  new Gson().fromJson(LoadData.fromFile(COUNTRY_FILE_NAME, this), listType);

        for (Server server : countryList) {
            for (Country country : countryLatLonList) {
                if (server.getCountryShort().equals(country.getCountryCode())) {
                    LatLong position = new LatLong(country.getCapitalLatitude(), country.getCapitalLongitude());
                    Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(this,
                            getResources().getIdentifier(ConnectionQuality.getPointIcon(server.getQuality()),
                                    "drawable",
                                    getPackageName())));

                    MyMarker countryMarker = new MyMarker(position, bitmap, 0, 0, server) {
                        @Override
                        public boolean onTap(LatLong geoPoint, Point viewPosition,
                                             Point tapPoint) {

                            if (contains(viewPosition, tapPoint)) {
                                onSelectCountry((Server)getRelationObject());
                                return true;
                            }
                            return false;
                        }
                    };

                    layers.add(countryMarker);


                    String localeCountryName = localeCountries.get(country.getCountryCode()) != null ?
                            localeCountries.get(country.getCountryCode()) : country.getCountryName();

                    Drawable drawable = new BitmapDrawable(getResources(), BitmapGenerator.getTextAsBitmap(localeCountryName, 20, ContextCompat.getColor(this,R.color.mapNameCountry)));
                    Bitmap bitmapName = AndroidGraphicFactory.convertToBitmap(drawable);

                    Marker countryNameMarker = new Marker(position, bitmapName, 0, bitmap.getHeight() / 2);

                    layers.add(countryNameMarker);
                }
            }
        }
    }
}
