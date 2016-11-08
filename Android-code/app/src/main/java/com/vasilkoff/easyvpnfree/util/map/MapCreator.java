package com.vasilkoff.easyvpnfree.util.map;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.util.LoadData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.overlay.Polygon;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Kusenko on 04.11.2016.
 */

public class MapCreator {

    private Paint paintFill;
    private Paint paintStroke;
    private Layers layers;
    private Context context;

    public MapCreator(Context context, Layers layers) {
        this.context = context;
        this.layers = layers;

        paintFill = AndroidGraphicFactory.INSTANCE.createPaint();
        paintFill.setColor(ContextCompat.getColor(context,R.color.mapFill));
        paintFill.setStyle(Style.FILL);

        paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStrokeWidth(1);
        paintStroke.setColor(ContextCompat.getColor(context,R.color.mapStroke));
        paintStroke.setStyle(Style.STROKE);
    }

    public void parseGeoJson(String nameFile) {
        String jsonString = null;

        jsonString = LoadData.fromFile(nameFile, context);

        if (jsonString != null)
            parseData(jsonString);
    }

    private void parseData(String jsonString) {
        try {
            JSONObject dataJsonObj = new JSONObject(jsonString);
            JSONArray features = dataJsonObj.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = new JSONObject(features.get(i).toString());
                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                if (geometry.get("type").equals("Polygon")) {
                    createPolygons(new JSONArray(coordinates.get(0).toString()));
                } else {
                    for (int j = 0; j < coordinates.length(); j++) {
                        JSONArray coordinate = new JSONArray(coordinates.get(j).toString());
                        createPolygons(new JSONArray(coordinate.get(0).toString()));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createPolygons(JSONArray coordinates) {
        Polygon polygon = new Polygon(paintFill, paintStroke,
                AndroidGraphicFactory.INSTANCE);

        List<LatLong> polygonList = polygon.getLatLongs();

        for (int j = 0; j < coordinates.length(); j++) {
            try {
                JSONArray arrLatLong = new JSONArray(coordinates.get(j).toString());
                polygonList.add(new LatLong(arrLatLong.getDouble(1), arrLatLong.getDouble(0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        layers.add(polygon);
    }

}
