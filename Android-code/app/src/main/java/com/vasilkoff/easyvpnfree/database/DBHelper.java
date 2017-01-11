package com.vasilkoff.easyvpnfree.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.vasilkoff.easyvpnfree.model.Country;
import com.vasilkoff.easyvpnfree.model.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kusenko on 29.09.2016.
 */

public class DBHelper  extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Records.db";
    public static final String TABLE_SERVERS = "servers";
    private static final String TAG = "DBHelper";

    private static final String KEY_ID = "_id";
    private static final String KEY_HOST_NAME = "hostName";
    private static final String KEY_IP = "ip";
    private static final String KEY_SCORE = "score";
    private static final String KEY_PING = "ping";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_COUNTRY_LONG = "countryLong";
    private static final String KEY_COUNTRY_SHORT = "countryShort";
    private static final String KEY_NUM_VPN_SESSIONS = "numVpnSessions";
    private static final String KEY_UPTIME = "uptime";
    private static final String KEY_TOTAL_USERS = "totalUsers";
    private static final String KEY_TOTAL_TRAFFIC = "totalTraffic";
    private static final String KEY_LOG_TYPE = "logType";
    private static final String KEY_OPERATOR = "operator";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CONFIG_DATA = "configData";
    private static final String KEY_PREMIUM = "premium";
    private static final String KEY_INACTIVE = "inactive";
    private static final String KEY_CITY = "city";
    private static final String KEY_REGION_NAME = "regionName";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_SERVERS + "("
                + KEY_ID + " integer primary key,"
                + KEY_HOST_NAME + " text,"
                + KEY_IP + " text,"
                + KEY_SCORE + " text,"
                + KEY_PING + " text,"
                + KEY_SPEED + " text,"
                + KEY_COUNTRY_LONG + " text,"
                + KEY_COUNTRY_SHORT + " text,"
                + KEY_NUM_VPN_SESSIONS + " text,"
                + KEY_UPTIME + " text,"
                + KEY_TOTAL_USERS + " text,"
                + KEY_TOTAL_TRAFFIC + " text,"
                + KEY_LOG_TYPE + " text,"
                + KEY_OPERATOR + " text,"
                + KEY_MESSAGE + " text,"
                + KEY_CONFIG_DATA + " text,"
                + KEY_INACTIVE + " integer DEFAULT 0,"
                + KEY_CITY + " text,"
                + KEY_REGION_NAME + " text,"
                + KEY_LAT + " real,"
                + KEY_LON + " real,"
                + KEY_PREMIUM + " integer,"
                + "UNIQUE ("
                + KEY_HOST_NAME
                + ") ON CONFLICT IGNORE"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_SERVERS);
        onCreate(db);
    }

    public void setInactive(String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INACTIVE, 1);
        db.update(TABLE_SERVERS, values, KEY_IP + " = ?", new String[] {ip});

        db.close();
    }

    public void setIpInfo(JSONArray response) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject ipInfo = new JSONObject(response.get(i).toString());

                ContentValues values = new ContentValues();
                values.put(KEY_CITY, ipInfo.get(KEY_CITY).toString());
                values.put(KEY_REGION_NAME, ipInfo.get(KEY_REGION_NAME).toString());
                values.put(KEY_LAT, ipInfo.getDouble(KEY_LAT));
                values.put(KEY_LON, ipInfo.getDouble(KEY_LON));

                db.update(TABLE_SERVERS, values, KEY_IP + " = ?", new String[] {ipInfo.get("query").toString()});
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVERS, null, null);
        db.close();
    }

    public void putLine(String line, int premium) {
        String[] data = line.split(",");
        if (data.length == 15) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(KEY_HOST_NAME, data[0]);
            contentValues.put(KEY_IP, data[1]);
            contentValues.put(KEY_SCORE, data[2]);
            contentValues.put(KEY_PING, data[3]);
            contentValues.put(KEY_SPEED, data[4]);
            contentValues.put(KEY_COUNTRY_LONG, data[5]);
            contentValues.put(KEY_COUNTRY_SHORT, data[6]);
            contentValues.put(KEY_NUM_VPN_SESSIONS, data[7]);
            contentValues.put(KEY_UPTIME, data[8]);
            contentValues.put(KEY_TOTAL_USERS, data[9]);
            contentValues.put(KEY_TOTAL_TRAFFIC, data[10]);
            contentValues.put(KEY_LOG_TYPE, data[11]);
            contentValues.put(KEY_OPERATOR, data[12]);
            contentValues.put(KEY_MESSAGE, data[13]);
            contentValues.put(KEY_CONFIG_DATA, data[14]);
            contentValues.put(KEY_PREMIUM, premium);

            db.insert(TABLE_SERVERS, null, contentValues);
            db.close();
        }
    }

    public long getCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement("SELECT COUNT(*) FROM " + TABLE_SERVERS);
        long count = statement.simpleQueryForLong();
        db.close();
        return count;
    }

    public long getCountBasic() {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement("SELECT COUNT(*) FROM "
                + TABLE_SERVERS
                + " WHERE "
                + KEY_PREMIUM
                + " = 0");
        long count = statement.simpleQueryForLong();
        db.close();
        return count;
    }

    public long getCountAdditional() {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement("SELECT COUNT(*) FROM "
                + TABLE_SERVERS
                + " WHERE "
                + KEY_PREMIUM
                + " = 1");
        long count = statement.simpleQueryForLong();
        db.close();
        return count;
    }

    public List<Country> getUniqueCountries() {
        List<Country> countryList = new ArrayList<Country>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "
                + KEY_COUNTRY_LONG
                + ","
                + KEY_COUNTRY_SHORT
                + " FROM "
                + TABLE_SERVERS
                + " ORDER BY "
                + KEY_COUNTRY_LONG
                + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                countryList.add(new Country(cursor.getString(0), null, 0, 0, cursor.getString(1)));
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG ,"0 rows");
        }

        cursor.close();
        db.close();

        return countryList;
    }

    public List<Server> getServersByCountryCode(String country) {
        List<Server> serverList = new ArrayList<Server>();
        if (country != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_SERVERS, null, KEY_COUNTRY_SHORT + "=?", new String[]{country}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    serverList.add(parseServer(cursor));
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG ,"0 rows");
            }

            cursor.close();
            db.close();
        }

        return serverList;
    }

    private Server parseGoodRandomServer(Cursor cursor, SQLiteDatabase db) {
        List<Server> serverListExcellent = new ArrayList<Server>();
        List<Server> serverListGood = new ArrayList<Server>();
        List<Server> serverListBad = new ArrayList<Server>();

        if (cursor.moveToFirst()) {
            do {
                int speed = Integer.parseInt(cursor.getString(5));
                int sessions = Integer.parseInt(cursor.getString(8));

                int ping = 0;
                if (!(cursor.getString(4).equals("-") || cursor.getString(4).equals(""))) {
                    ping = Integer.parseInt(cursor.getString(4));
                }

                if (speed > 10000000 && ping < 30 && (sessions != 0 && sessions < 100)) {
                    serverListExcellent.add(parseServer(cursor));
                } else if (speed < 1000000 || ping > 100 || (sessions == 0 || sessions > 150)) {
                    serverListBad.add(parseServer(cursor));
                } else {
                    serverListGood.add(parseServer(cursor));
                }

            } while (cursor.moveToNext());
        } else {
            Log.d(TAG ,"0 rows");
        }

        cursor.close();
        db.close();

        Random random = new Random();
        if (serverListExcellent.size() > 0) {
            return serverListExcellent.get(random.nextInt(serverListExcellent.size()));
        } else if (serverListGood.size() > 0) {
            return serverListGood.get(random.nextInt(serverListGood.size()));
        } else if (serverListBad.size() > 0) {
            return serverListBad.get(random.nextInt(serverListBad.size()));
        }

        return null;
    }

    public Server getSimilarServer(String country, String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + TABLE_SERVERS
                + " WHERE "
                + KEY_INACTIVE
                + " <> 1 AND "
                + KEY_COUNTRY_LONG
                + " = ? AND "
                + KEY_IP
                + " <> ?", new String[] {country, ip});


        return parseGoodRandomServer(cursor, db);
    }

    public Server getGoodRandomServer(String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        if (country != null) {
            cursor = db.rawQuery("SELECT * FROM "
                    + TABLE_SERVERS
                    + " WHERE "
                    + KEY_INACTIVE
                    + " <> 1 AND "
                    + KEY_COUNTRY_LONG
                    + " = ?", new String[] {country});
        } else {
            cursor = db.rawQuery("SELECT * FROM "
                    + TABLE_SERVERS
                    + " WHERE "
                    + KEY_INACTIVE
                    + " <> 1", null);
        }

        return parseGoodRandomServer(cursor, db);
    }

    private Server parseServer(Cursor cursor) {
        return new Server(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12),
                cursor.getString(13),
                cursor.getString(14),
                cursor.getString(15),
                cursor.getInt(16),
                cursor.getString(17)
        );
    }
}
