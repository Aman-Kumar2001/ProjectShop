package com.example.android.myshop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDatabase extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDatabase.class.getSimpleName();

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 1;

    public ItemDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db,int oldVersion,int newVersion){
        onUpgrade(db,oldVersion,newVersion);
    }

    public final static String CREATE_TABLE = "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " ( "
            + ItemContract.ItemEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ItemContract.ItemEntry.COLUMN_NAME + " TEXT NOT NULL,"
            + ItemContract.ItemEntry.COLUMN_PRICE +" INTEGER NOT NULL,"
            + ItemContract.ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL,"
            + ItemContract.ItemEntry.COLUMN_QUANTITY_UNIT + " INTEGER NOT NULL,"
            + ItemContract.ItemEntry.COLUMN_SUPPLIERS + " INTEGER NOT NULL DEFAULT 0" + " )";

    public final static String DELETE_TABLE ="DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;
}
