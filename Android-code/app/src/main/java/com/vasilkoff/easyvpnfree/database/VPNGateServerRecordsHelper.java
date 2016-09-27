package com.vasilkoff.easyvpnfree.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vasilkoff.easyvpnfree.model.VPNGateServerRecord;

import java.sql.SQLException;

/**
 * Created by Vasilkoff on 27/09/16.
 */
public class VPNGateServerRecordsHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "Records";
    private static final int DATABASE_VERSION = 1;

    /**
     * The data access object used to interact with the Sqlite database to do C.R.U.D operations.
     */
    private Dao<VPNGateServerRecord, Long> tDao;

    public VPNGateServerRecordsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            /**
             * creates the Todo database table
             */
            TableUtils.createTable(connectionSource, VPNGateServerRecord.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            /**
             * Recreates the database when onUpgrade is called by the framework
             */
            TableUtils.dropTable(connectionSource, VPNGateServerRecord.class, false);
            onCreate(database, connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an instance of the data access object
     * @return
     * @throws SQLException
     */
    public Dao<VPNGateServerRecord, Long> getDao() throws SQLException {
        if(tDao == null) {
            tDao = getDao(VPNGateServerRecord.class);
        }
        return tDao;
    }
}