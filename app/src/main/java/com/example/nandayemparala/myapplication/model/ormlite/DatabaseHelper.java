package com.example.nandayemparala.myapplication.model.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Stop;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Nanda Yemparala on 9/8/16.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private static final String DATABASE_NAME = "click.db";
    private static final int DATABASE_VERSION = 23;

    private Dao<Route, Integer> routeDao;
    private Dao<Stop, Integer> stopDao;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Route.class);
            TableUtils.createTable(connectionSource, Stop.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, Route.class, true);
            TableUtils.dropTable(connectionSource, Stop.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    public Dao<Route, Integer> getRouteDao() throws SQLException {
        if (routeDao == null) {
            routeDao = getDao(Route.class);
        }
        return routeDao;
    }

    public Dao<Stop, Integer> getStopDao() throws SQLException {
        if (stopDao == null) {
            stopDao = getDao(Stop.class);
        }
        return stopDao;
    }
}
