package com.motorola.leaf.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TasksProvider extends ContentProvider
{
    private static final String AUTHORITY = "com.motorola.leaf.providers.TasksProvider";
    private static final String TASKS_BASE_PATH = "child_tasks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TASKS_BASE_PATH);

    private static final String DATABASE_NAME = "leaf";
    private static final String TASKS_TABLE_NAME = TASKS_BASE_PATH;
    public static final String COLUMN_CHILD_ID = "child_id";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_TASK_DESC = "task_desc";
    public static final String COLUMN_TASK_STATUS = "task_status";
    public static final String COLUMN_TASK_POINTS = "task_points";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK_CHANGED = "task_changed";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_DB_TABLE =
            " CREATE TABLE " + TASKS_TABLE_NAME + " (" +
                    COLUMN_TASK_CHANGED + " INTEGER, " +

                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CHILD_ID + " TEXT NOT NULL, " +
                    COLUMN_TASK_ID + " TEXT NOT NULL, " +
                    COLUMN_TASK_DESC + " TEXT NOT NULL, " +
                    COLUMN_TASK_STATUS + " INTEGER NOT NULL, " +
                    COLUMN_TASK_POINTS + " INTEGER NOT NULL" + ");";

    private static class TasksDatabaseHelper extends SQLiteOpenHelper
    {

        public TasksDatabaseHelper(Context context)
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
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        TasksDatabaseHelper helper = new TasksDatabaseHelper(context);
        db = helper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TASKS_TABLE_NAME);
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        long rowId = db.insert(TASKS_TABLE_NAME, "", contentValues);

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
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        return db.update(TASKS_TABLE_NAME, contentValues, s, strings);
    }
}
