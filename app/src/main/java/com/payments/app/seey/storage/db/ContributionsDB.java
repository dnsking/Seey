package com.payments.app.seey.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.payments.app.seey.App;

public class ContributionsDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="ContributionsDB.db";
    public static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="ContributionsDBTable";



    public static final  String time="time";
    public static final  String to="tocreator";
    public static final  String amount="amount";
    public static final  String social="social";


    private  static final String DATABASE_CREATE = "CREATE VIRTUAL TABLE "+ TABLE_NAME +
            "  USING fts3("
            + time  + " text not null UNIQUE, "
            + to    + " text not null, "
            + amount + " text not null, "
            + social + " text not null);";

    public ContributionsDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        App.Log(getClass().getSimpleName()+" Creating all the tables");
        try {
            db.execSQL(DATABASE_CREATE);


        } catch(SQLiteException ex) {
            App.Log("Create table exception "+ ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getTableName() {
        return TABLE_NAME;
    }
}
