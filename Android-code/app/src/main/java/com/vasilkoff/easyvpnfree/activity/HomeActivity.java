package com.vasilkoff.easyvpnfree.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;
import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.database.DBHelper;
import com.vasilkoff.easyvpnfree.model.Country;
import com.vasilkoff.easyvpnfree.util.BitmapGenerator;
import com.vasilkoff.easyvpnfree.util.LoadData;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private GoogleMap mMap = null;
    private final float ZOOM = 4.0f;

    public static final String EXTRA_COUNTRY = "country";
    private PopupWindow popupWindow;
    private int widthWindow ;
    private int heightWindow;
    private RelativeLayout homeContextRL;

    private List<String> countryList;
    private final String COUNTRY_FILE_NAME = "countries.json";

    private List<Country> countryLatLonList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        widthWindow = dm.widthPixels;
        heightWindow = dm.heightPixels;

        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        countryList = new DBHelper(this).getCountries();
    }

    public void homeOnClick(View view) {
        chooseCountry();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_refresh) {
            startActivity(new Intent(this, LoaderActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void chooseCountry() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_up_choose_country,null);

        popupWindow = new PopupWindow(
                view,
                (int)(widthWindow * 0.8f),
                (int)(heightWindow * 0.7f)
        );

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryList);

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

    private void onSelectCountry(String country) {
        Intent intent = new Intent(getApplicationContext(), ServersListActivity.class);
        intent.putExtra(EXTRA_COUNTRY, country);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
       // mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag() != null)
                    onSelectCountry((String)marker.getTag());

                return false;
            }
        });

        int color =  ContextCompat.getColor(this,R.color.mapBackground);

        mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(-85, -170), new LatLng(85, -170), new LatLng(85, 170), new LatLng(-85, 170))
                .strokeWidth(0)
                .zIndex(-1)
                .fillColor(color));

        mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(-85, 0), new LatLng(85, 0), new LatLng(85, 179), new LatLng(-85, 179))
                .strokeWidth(0)
                .zIndex(-1)
                .fillColor(color));

        mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(-85, 0), new LatLng(85, 0), new LatLng(85, -180), new LatLng(-85, -180))
                .strokeWidth(0)
                .zIndex(-1)
                .fillColor(color));

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.countries,
                    getApplicationContext());

            GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
            polygonStyle.setFillColor(ContextCompat.getColor(this,R.color.mapFill));
            polygonStyle.setStrokeColor(ContextCompat.getColor(this,R.color.mapStroke));
            polygonStyle.setStrokeWidth(1);
            layer.addLayerToMap();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        initServerOnMap();

    }

    private void initServerOnMap() {
        Type listType = new TypeToken<ArrayList<Country>>(){}.getType();
        countryLatLonList =  new Gson().fromJson(LoadData.fromFile(COUNTRY_FILE_NAME, this), listType);

        for (String countryName : countryList) {
            for (Country country : countryLatLonList) {
                if (countryName.equals(country.getCountryName())) {
                    LatLng position = new LatLng(country.getCapitalLatitude(), country.getCapitalLongitude());

                    Marker markerServer = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .anchor(0.5f, 0.5f));
                    markerServer.setTag(countryName);

                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .anchor(0.5f, 0)
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapGenerator.getTextAsBitmap(countryName, 20, ContextCompat.getColor(this,R.color.mapNameCountry)))));
                }
            }
        }
    }
}
