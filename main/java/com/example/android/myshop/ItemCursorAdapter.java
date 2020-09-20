package com.example.android.myshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.myshop.data.ItemContract;

import java.util.zip.Inflater;

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context,Cursor c){
        super(context,c,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView)view.findViewById(R.id.item_name);
        TextView price = (TextView)view.findViewById(R.id.item_price);
        TextView stock = (TextView)view.findViewById(R.id.item_stock);

        String itemName = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
        int itemPrice = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
        int itemStock = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));

        name.setText(itemName);
        price.setText(Integer.toString(itemPrice));
        String inStock = "In stock";
        String outStock = "Out of Stock";

        if(itemStock > 0){
            stock.setText(inStock);
            stock.setTextColor(Color.GREEN);
        }else{
            stock.setText(outStock);
            stock.setTextColor(Color.RED);
        }

    }
}
