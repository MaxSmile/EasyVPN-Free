package com.vasilkoff.easyvpnfree.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class LoadData {

    public static String fromFile(String nameFile, Context context) {
        String data = null;
        try {

            InputStream is = context.getAssets().open(nameFile);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            data = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return data;
    }
}
