package com.vasilkoff.easyvpnfree.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapGenerator {

    private  static Paint paint = new Paint();

    public static Bitmap getTextAsBitmap(String text, float textSize, int textColor) {
        String textUp = text.toUpperCase();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(textUp) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 25.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(textUp, 0, baseline, paint);
        return image;
    }
}
