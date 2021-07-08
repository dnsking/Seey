package com.payments.app.seey.storage.db.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.payments.app.seey.action.ContributionAction;
import com.payments.app.seey.storage.db.ContributionsDB;

import java.util.ArrayList;

public class ContributionsDBHelper {


    private SQLiteDatabase db;
    private Context context;

    private ContributionsDB dbhelper;
    public ContributionsDBHelper(Context c){
        context = c;
        this.dbhelper = new ContributionsDB(c, ContributionsDB.DATABASE_NAME, null,
                ContributionsDB.DATABASE_VERSION);
    }
    public Context getContext(){
        return context;
    }
    public void close()
    {
        db.close();
    }
    public SQLiteDatabase getDB(){
        return db;
    }
    public void setDB(SQLiteDatabase db){
        this.db =db;
    }
    public SQLiteOpenHelper getDbHelper(){
        return dbhelper;
    }
    public void open() throws SQLiteException
    {
        try {
            db = dbhelper.getWritableDatabase();
        } catch(SQLiteException ex) {
            db = dbhelper.getReadableDatabase();
        }
    }
    public long insertEntry(ContentValues cv){
        try{
            return db.insert(dbhelper.getTableName(), null, cv);
        }
        catch(SQLiteException ex){;
            return -1;
        }
    }


    public Cursor query()
    {
        return query(null, null, null, null, null, null);
    }
    public Cursor query( String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy)
    {

        Cursor c = db.query(dbhelper.getTableName(),columns, selection,
                selectionArgs, groupBy, having, orderBy);
        return c;
    }

    public void deleteEntry(String id){
        db.delete(dbhelper.getTableName(), ContributionsDB.time+"='"+id+"'", null);
    }
    public ContributionAction[] queryAll(){
        Cursor cursor =  query();
        ArrayList<ContributionAction> items = new ArrayList<>();
        //  cursor.moveToFirst();
        while (cursor.moveToNext()) {

            ContributionAction item = new ContributionAction();
            items.add(new ContributionAction(

                    cursor.getString(cursor.getColumnIndex(ContributionsDB.time)),

                    cursor.getString(cursor.getColumnIndex(ContributionsDB.to)),
                    cursor.getString(cursor.getColumnIndex(ContributionsDB.social)),

                    cursor.getString(cursor.getColumnIndex(ContributionsDB.amount)) ));


        }
        cursor.close();
        return items.toArray(new ContributionAction[]{});
    }

    public ContentValues insertEntry(ContributionAction item ){


        db.beginTransaction();
        try{

            ContentValues cv = new ContentValues();
            cv.put(ContributionsDB.amount,item.getAmount());
            cv.put(ContributionsDB.to,item.getUser());
            cv.put(ContributionsDB.time,item.getTime());
            cv.put(ContributionsDB.social,item.getSocial());

            getDB().insert(ContributionsDB.TABLE_NAME, null, cv);

            db.setTransactionSuccessful();

            db.endTransaction();
            return cv;
        }
        catch(SQLiteException ex) {
            return null;
        }

    }
}
