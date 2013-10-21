package com.motorola.root.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ChildrenProvider extends ContentProvider
{
    private static final String AUTHORITY = "com.motorola.root.providers.ChildrenProvider";
    private static final String CHILDREN_BASE_PATH = "children";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + CHILDREN_BASE_PATH);

    private static final String DATABASE_NAME = "root";
    private static final String CHILDREN_TABLE_NAME = CHILDREN_BASE_PATH;
    public static final String COLUMN_CHILD_CHANGED = "child_changed";
    public static final String COLUMN_CHILD_ID = "child_id";
    public static final String COLUMN_PARENT_ID = "parent_id";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_DB_TABLE =
            " CREATE TABLE " + CHILDREN_TABLE_NAME + " (" +
                    COLUMN_CHILD_CHANGED + " INTEGER NOT NULL, " +
                    COLUMN_PARENT_ID + " TEXT NOT NULL, " +
                    COLUMN_CHILD_ID + " TEXT NOT NULL);";

    private static class ChildrenDatabaseHelper extends SQLiteOpenHelper
    {

        public ChildrenDatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
        {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CHILDREN_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate()
    {
        Context context = getContext();
        ChildrenDatabaseHelper helper = new ChildrenDatabaseHelper(context);
        db = helper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CHILDREN_TABLE_NAME);
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        long rowId = db.insert(CHILDREN_TABLE_NAME, "", contentValues);

        if(rowId > 0)
        {
            Uri returnUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(returnUri, null);
            return returnUri;
        }
        else
        {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings)
    {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings)
    {
        return db.update(CHILDREN_TABLE_NAME, contentValues, s, strings);
    }
}
