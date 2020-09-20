package com.example.android.myshop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ItemContract {

    private ItemContract(){}

    public static final String AUTHORITY = "com.example.android.myshop";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_ITEM = "myshop";

    public static final class ItemEntry implements BaseColumns{
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,PATH_ITEM);

        public final static String TABLE_NAME = "items";

        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_QUANTITY_UNIT = "units";
        public final static String COLUMN_SUPPLIERS = "suppliers";

        public static final int QUANTITY_KG = 1;
        public static final int QUANTITY_LT = 2;
        public static final int QUANTITY_PCS = 0;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ITEM;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ITEM;

    }
}
