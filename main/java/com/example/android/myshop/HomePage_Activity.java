package com.example.android.myshop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myshop.data.ItemContract;
import com.example.android.myshop.data.ItemDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ItemCursorAdapter itemCursorAdapter;
    private static int LOADER_URI = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(HomePage_Activity.this,EditActivity.class);
                startActivity(newActivity);
            }
        });

        ListView list = (ListView)findViewById(R.id.home_list_view);
        View emptyView = findViewById(R.id.empty_view);

        list.setEmptyView(emptyView);

        itemCursorAdapter = new ItemCursorAdapter(this,null);
        list.setAdapter(itemCursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomePage_Activity.this,EditActivity.class);
                Uri uriData = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI,id);

                intent.setData(uriData);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_URI,null,this);
    }


    private void insertDummy(){
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME,"Rice");
        values.put(ItemContract.ItemEntry.COLUMN_PRICE,100);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY,1);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY_UNIT, ItemContract.ItemEntry.QUANTITY_KG);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIERS,2);

        getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI,values);
    }
    private void deleteAll(){

        int rowDeleted = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI,null,null);
        if(rowDeleted == 0){
            Toast.makeText(this,"Delete Failed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Deleted successfully",Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteItemDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAll();
                    }
                })
                .setNegativeButton("No",null);

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.insert_new:
                insertDummy();
                return true;

            case R.id.delete_all:
                deleteItemDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_QUANTITY
        };
        return new CursorLoader(this,ItemContract.ItemEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        itemCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemCursorAdapter.swapCursor(null);
    }
}