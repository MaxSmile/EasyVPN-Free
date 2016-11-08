package com.vasilkoff.easyvpnfree.util.map;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Marker;

/**
 * Created by Kusenko on 04.11.2016.
 */

public class MyMarker extends Marker {

    private Object relationObject;
    /**
     * @param latLong          the initial geographical coordinates of this marker (may be null).
     * @param bitmap           the initial {@code Bitmap} of this marker (may be null).
     * @param horizontalOffset the horizontal marker offset.
     * @param verticalOffset   the vertical marker offset.
     */
    public MyMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset, Object relationObject) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
        this.relationObject = relationObject;
    }

    public Object getRelationObject() {
        return relationObject;
    }
}
