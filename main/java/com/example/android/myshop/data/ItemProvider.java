package com.example.android.myshop.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ItemProvider extends ContentProvider {
    private static final int ITEM = 50;
    private static final int ITEM_ID = 51;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ItemContract.AUTHORITY,ItemContract.PATH_ITEM,ITEM);
        sUriMatcher.addURI(ItemContract.AUTHORITY,ItemContract.PATH_ITEM + "/#",ITEM_ID);
    }

    private ItemDatabase database;
    @Override
    public boolean onCreate() {
        database = new ItemDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = database.getReadableDatabase();
        int uriMatch = sUriMatcher.match(uri);

        Cursor cursor = null;

        switch (uriMatch){
            case ITEM:
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
                break;

            case ITEM_ID:
                selection = ItemContract.ItemEntry.COLUMN_ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case ITEM:
                return insertItem(uri,values);
            default:
                throw new IllegalArgumentException("Cannot insert item" + uri);
        }
    }

    private Uri insertItem(Uri uri,ContentValues values){
        String name = values.getAsString(ItemContract.ItemEntry.COLUMN_NAME);
        if(name == null){
            throw new IllegalArgumentException("Name is required");
        }
        Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRICE);
        if(price == null || price < 0){
            throw new IllegalArgumentException("Price is required");
        }
        Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_QUANTITY);
        if(quantity == null || quantity < 0){
            throw new IllegalArgumentException("Quantity is required");
        }
        Integer suppliers = values.getAsInteger(ItemContract.ItemEntry.COLUMN_SUPPLIERS);
        if(suppliers == null || suppliers < 0){
            throw new IllegalArgumentException("Suppliers is required");
        }

        SQLiteDatabase db = database.getWritableDatabase();

        long id = db.insert(ItemContract.ItemEntry.TABLE_NAME,null,values);
        if(id == -1){
            Toast.makeText(getContext(),"Unable to insert",Toast.LENGTH_SHORT).show();
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int uriMatch = sUriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatch){
            case ITEM:
                rowsDeleted = db.delete(ItemContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;
            case ITEM_ID:
                selection = ItemContract.ItemEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ItemContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unable to delete" + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case ITEM:
                return updateItem(uri,values,selection,selectionArgs);
            case ITEM_ID:
                selection = ItemContract.ItemEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Unable to process"+ uri );
        }
    }
    private int updateItem(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        if(values.containsKey(ItemContract.ItemEntry.COLUMN_NAME)){
            String name = values.getAsString(ItemContract.ItemEntry.COLUMN_NAME);
            if(name == null){
                throw new IllegalArgumentException("Name is required");
            }
        }
        if (values.containsKey(ItemContract.ItemEntry.COLUMN_PRICE)){
            Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRICE);
            if(price == null || price < 0){
                throw new IllegalArgumentException("Price is required");
            }
        }
        if(values.containsKey(ItemContract.ItemEntry.COLUMN_QUANTITY)){
            Integer quantity = values.getAsInteger(ItemContract.ItemEntry.COLUMN_QUANTITY);
            if(quantity == null || quantity < 0){
                throw new IllegalArgumentException("Quantity is required");
            }
        }
        if(values.containsKey(ItemContract.ItemEntry.COLUMN_SUPPLIERS)){
            Integer suppliers = values.getAsInteger(ItemContract.ItemEntry.COLUMN_SUPPLIERS);
            if(suppliers == null || suppliers < 0){
                throw new IllegalArgumentException("Suppliers is required");
            }
        }
        SQLiteDatabase db = database.getWritableDatabase();

        int id = db.update(ItemContract.ItemEntry.TABLE_NAME,values,selection,selectionArgs);
        if(id != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return id;
    }
}
