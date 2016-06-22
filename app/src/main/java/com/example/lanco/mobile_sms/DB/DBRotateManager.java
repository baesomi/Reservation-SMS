package com.example.lanco.mobile_sms.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lanco on 2016-06-22.
 */
public class DBRotateManager {
    static final String KEY_ROWID = "_id";
    static final String KEY_REPORT = "rreportdate";
    static final String KEY_PERIOD = "period";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyRotate";
    static final String DATABASE_TABLE = "rotate";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE = "create table " + DATABASE_TABLE +
            " (_id integer primary key autoincrement, rreportdate biginteger not null, period integer not null);";
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBRotateManager(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS rotate");
            onCreate(db);
        }
    }

    // ---opens the database---
    public DBRotateManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    // ---closes the database---
    public void close() {
        DBHelper.close();
    }

    // ---insert a contact into the database---
    public long insertContact(long r, int p) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_REPORT, r);
        initialValues.put(KEY_PERIOD, p);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // ---deletes a particular contact---
    public boolean deleteContact(long reportdate) {
        return db.delete(DATABASE_TABLE, KEY_REPORT + "=" + reportdate, null) > 0;
    }

    // ---retrieves all the contacts---
    public Cursor getAllContacts() {
        return db.query(DATABASE_TABLE, null, null, null, null, null, null);
    }

    // ---retrieves a particular contact---
    public Cursor getContact(long r) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, null,
                KEY_REPORT + "=" + r, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // ---updates a contact---
    public boolean updateContact(long rowId, long r, int p) {
        ContentValues args = new ContentValues();
        args.put(KEY_REPORT, r);
        args.put(KEY_PERIOD, p);
        return db.update(DATABASE_TABLE, args, KEY_REPORT + "=" + r, null) > 0;
    }
}
