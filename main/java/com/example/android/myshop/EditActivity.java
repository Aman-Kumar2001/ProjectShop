package com.example.android.myshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.myshop.data.ItemContract;
import com.example.android.myshop.data.ItemDatabase;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean mPetHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    private EditText mNameEdit;
    private EditText mPriceEdit;
    private EditText mQuantityEdit;
    private EditText mSuppliersEdit;
    private Spinner mQuantitySpinner;

    private int mQuantity = 0;
    private Uri mUri;

    private static int LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mUri = intent.getData();
        if(mUri == null){
            this.setTitle("Add new item");

            invalidateOptionsMenu();
        }else{
            this.setTitle("Edit item");

            getLoaderManager().initLoader(LOADER,null,this);
        }

        mNameEdit = (EditText)findViewById(R.id.edit_name);
        mPriceEdit = (EditText)findViewById(R.id.edit_price);
        mQuantityEdit = (EditText)findViewById(R.id.edit_quantity);
        mSuppliersEdit = (EditText)findViewById(R.id.edit_suppliers);

        mQuantitySpinner = (Spinner)findViewById(R.id.spinner_quantity);

        setUpSpinner();
    }

    private void setUpSpinner(){
        ArrayAdapter quantitySpinner = ArrayAdapter.createFromResource(this,R.array.array_quantity_options,android.R.layout.simple_spinner_item);

        mQuantitySpinner.setSelection(android.R.layout.simple_dropdown_item_1line);
        mQuantitySpinner.setAdapter(quantitySpinner);

        mQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals("kg")){
                        mQuantity = ItemContract.ItemEntry.QUANTITY_KG;
                    }else if(selection.equals("lt")){
                        mQuantity = ItemContract.ItemEntry.QUANTITY_LT;
                    }else{
                        mQuantity = ItemContract.ItemEntry.QUANTITY_PCS;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantity = ItemContract.ItemEntry.QUANTITY_PCS;
            }
        });
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_edit);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void saveData(){
        String name = mNameEdit.getText().toString().trim();
        String price = mPriceEdit.getText().toString().trim();
        String quantity = mQuantityEdit.getText().toString().trim();
        String suppliers = mSuppliersEdit.getText().toString().trim();

        int quant = Integer.parseInt(quantity);
        int cost = Integer.parseInt(price);
        int supp = Integer.parseInt(suppliers);

        if(mUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(price) && TextUtils.isEmpty(quantity) && TextUtils.isEmpty(suppliers) && mQuantity == ItemContract.ItemEntry.QUANTITY_PCS){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME,name);
        values.put(ItemContract.ItemEntry.COLUMN_PRICE,cost);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY,quant);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY_UNIT,mQuantity);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIERS,supp);

        if(mUri == null){
            Uri k = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI,values);
            if(k != null){
                Toast.makeText(this,"Added Successfully",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Adding failed",Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowsUpdated = getContentResolver().update(mUri,values,null,null);
            if(rowsUpdated == 0){
                Toast.makeText(this,"Update Failed",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Updated Successfully",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void deleteItem(){
        String selection = ItemContract.ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mUri))};
        int rowDeleted = getContentResolver().delete(mUri,selection,selectionArgs);
        if(rowDeleted == 0){
            Toast.makeText(this,"Delete Failed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Deleted successfully",Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    private void deleteItemDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
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
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_new_item:
                saveData();
                finish();
                return true;

            case R.id.delete_edit:
                deleteItemDialog();
                return true;

            case R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditActivity.this);
                    }
                };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_QUANTITY_UNIT,
                ItemContract.ItemEntry.COLUMN_SUPPLIERS
        };

        return new CursorLoader(this,mUri,projection,null,null,null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null || data.getCount() < 1){
            return;
        }
        if(data.moveToFirst()){
            String name = data.getString(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
            int price = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
            int quantity = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));
            int supply = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIERS));
            int unit = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY_UNIT));

            mNameEdit.setText(name);
            mPriceEdit.setText(Integer.toString(price));
            mQuantityEdit.setText(Integer.toString(quantity));
            mSuppliersEdit.setText(Integer.toString(supply));

            switch (unit){
                case ItemContract.ItemEntry.QUANTITY_KG:
                    mQuantitySpinner.setSelection(1);
                case ItemContract.ItemEntry.QUANTITY_LT:
                    mQuantitySpinner.setSelection(2);
                default:mQuantitySpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEdit.setText("");
        mPriceEdit.setText("");
        mQuantityEdit.setText("");
        mSuppliersEdit.setText("");
        mQuantitySpinner.setSelection(0);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you surely want to leave?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}