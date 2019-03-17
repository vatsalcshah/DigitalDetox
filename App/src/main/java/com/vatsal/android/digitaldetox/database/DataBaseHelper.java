package com.vatsal.android.digitaldetox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class creates a sql database for the device and implements some useful database operations
 */
public class DataBaseHelper extends SQLiteOpenHelper{
    private static final String databaseName = "Limits.db";
    private static final String tableName = "minutestable";
    private static final String column_1 = "package";
    private static final String column_2 = "minutes";

    /**
     * class constructor
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, databaseName, null, 20);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE  "+tableName+" ("+column_1+" text primary key, "+column_2+" long ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    /**
     * adds a record for the package "pkg" whit limit "limit"
     * @param pkg
     * @param limit
     */
    public void addLimit(String pkg, long limit){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_1, pkg);
        contentValues.put(column_2, limit);
        database.insert(tableName, null, contentValues);

    }

    /**
     * returns a Cursor pointing to the first element of the databse
     * @return Cursor
     */
    public Cursor getAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName , null );
        cursor.moveToFirst();
        return  cursor;
    }

    /**
     * gets time limit for the package pkg
     * @param pkg
     * @return Long
     */
    public Long getLimit(String pkg){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select minutes from " + tableName + " where package='" + pkg + "';", null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex("minutes");
        if(cursor != null && cursor.getCount()>0){
            Long lim = cursor.getLong(index);
            cursor.close();
            return lim;
        }
        return -1L;

    }

    /**
     * replaces time limit for the package pkg
     * @param pkg
     * @param limit
     */
    public void updateLimit(String pkg, Long limit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_1, pkg);
        contentValues.put(column_2, limit);
        //run time string substitution
        db.update(tableName, contentValues, "package = ?" , new String[] { pkg });
    }

    /**
     * deletes database record for package pkg
     * @param pkg
     */
    public void deleteLimit(String pkg){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "package = ?" , new String[] { pkg });
    }

}
