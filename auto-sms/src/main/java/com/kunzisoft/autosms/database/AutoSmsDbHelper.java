package com.kunzisoft.autosms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kunzisoft.autosms.model.AutoSms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoSmsDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "AutoSmsDbHelper";

    private static AutoSmsDbHelper AutoSmsDbHelper;

    private static final String DATABASE_NAME = "SmsScheduler.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SMS = "sms";

    public static final String COLUMN_TIMESTAMP_CREATED = "datetimeCreated";
    public static final String COLUMN_TIMESTAMP_SCHEDULED = "datetimeScheduled";
    public static final String COLUMN_RECIPIENT_PHONE_NUMBER = "recipientPhoneNumber";
    public static final String COLUMN_RECIPIENT_LOOKUP = "recipientLookup";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_SUBSCRIPTION_ID = "subscriptionId";
    public static final String COLUMN_RECURRING_MODE = "recurringMode";

    public AutoSmsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public AutoSmsDbHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    static public AutoSmsDbHelper getDbHelper(Context context) {
        if (null == AutoSmsDbHelper) {
            AutoSmsDbHelper = new AutoSmsDbHelper(context);
        }
        return AutoSmsDbHelper;
    }

    static public void closeDbHelper() {
        if (AutoSmsDbHelper != null) {
            AutoSmsDbHelper.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SMS +
                "(" +
                COLUMN_TIMESTAMP_CREATED + " BIGINTEGER PRIMARY KEY," +
                COLUMN_TIMESTAMP_SCHEDULED + " BIGINTEGER," +
                COLUMN_RECIPIENT_PHONE_NUMBER + " TEXT," +
                COLUMN_RECIPIENT_LOOKUP + " TEXT," +
                COLUMN_MESSAGE + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_RESULT + " TEXT," +
                COLUMN_SUBSCRIPTION_ID + " INTEGER," +
                COLUMN_RECURRING_MODE + " TEXT" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion <= oldVersion) {
            Log.i(getClass().getName(), "newVersion <= oldVersion");
            return;
        }
    }

    private ContentValues constructContentValues(AutoSms sms) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP_SCHEDULED, sms.getDateScheduled().getTime());
        values.put(COLUMN_RECIPIENT_LOOKUP, sms.getRecipientLookup());
        values.put(COLUMN_RECIPIENT_PHONE_NUMBER, sms.getRecipientPhoneNumber());
        values.put(COLUMN_MESSAGE, sms.getMessage());
        values.put(COLUMN_STATUS, sms.getStatus().name());
        values.put(COLUMN_RESULT, sms.getResult());
        values.put(COLUMN_SUBSCRIPTION_ID, sms.getSubscriptionId());
        values.put(COLUMN_RECURRING_MODE, sms.getRecurringMode());
        return values;
    }

    public void insert(AutoSms sms) {
        ContentValues values = constructContentValues(sms);
        long timestampCreated = System.currentTimeMillis();
        sms.setDateCreated(new Date(timestampCreated));
        values.put(COLUMN_TIMESTAMP_CREATED, timestampCreated);
        AutoSmsDbHelper.getWritableDatabase().insert(TABLE_SMS, null, values);
        Log.d(TAG, "Add auto-sms " + sms);
    }

    public void update(AutoSms sms) {
        ContentValues values = constructContentValues(sms);
        String whereClause = COLUMN_TIMESTAMP_CREATED + "=?";
        String[] whereArgs = new String[] {String.valueOf(sms.getDateCreated().getTime())};
        AutoSmsDbHelper.getWritableDatabase().update(
                TABLE_SMS,
                values,
                whereClause,
                whereArgs);
        Log.d(TAG, "Update auto-sms " + sms);
    }

    public AutoSms getAutoSmsById(long timestampCreated) {
        Cursor cursor = AutoSmsDbHelper.getReadableDatabase().query(
                false,
                TABLE_SMS,
                new String[]{"*", COLUMN_TIMESTAMP_CREATED + " AS _id"},
                COLUMN_TIMESTAMP_CREATED + "=?",
                new String[]{Long.toString(timestampCreated)},
                null,
                null,
                null,
                "1"
        );
        if (cursor != null) {
            List<AutoSms> results = getListAutoSms(cursor);
            cursor.close();
            if (results.size() > 0) {
                return results.get(0);
            }
        }
        return null;
    }

    public List<AutoSms> getListAutoSmsByLookupKeyAndStatus(String lookupKey, AutoSms.Status status) {
        Cursor cursor =  AutoSmsDbHelper.getReadableDatabase().query(
                TABLE_SMS,
                new String[] { "*", COLUMN_TIMESTAMP_CREATED + " AS _id" },
                COLUMN_STATUS + "=?" + " AND " + COLUMN_RECIPIENT_LOOKUP + "=?",
                new String[] {status.name(), lookupKey},
                null,
                null,
                COLUMN_TIMESTAMP_CREATED + " DESC");
        if (cursor != null) {
            List<AutoSms> results = getListAutoSms(cursor);
            cursor.close();
            return results;
        }
        return null;
    }

    public List<AutoSms> getListAutoSmsByStatus(AutoSms.Status status) {
        Cursor cursor =  AutoSmsDbHelper.getReadableDatabase().query(
                TABLE_SMS,
                new String[] { "*", COLUMN_TIMESTAMP_CREATED + " AS _id" },
                COLUMN_STATUS + "=?",
                new String[] {status.name()},
                null,
                null,
                COLUMN_TIMESTAMP_CREATED + " DESC");
        if (cursor != null) {
            List<AutoSms> results = getListAutoSms(cursor);
            cursor.close();
            return results;
        }
        return null;
    }

    public void deleteAllByLookupKey(String lookupKey) {
        String selection = COLUMN_RECIPIENT_LOOKUP + "=?";
        String[] selectionArgs = new String[] {lookupKey};
        AutoSmsDbHelper.getReadableDatabase().delete(TABLE_SMS, selection, selectionArgs);
    }

    public void deleteById(Long timestampCreated) {
        String selection = COLUMN_TIMESTAMP_CREATED + "=?";
        String[] selectionArgs = new String[] {timestampCreated.toString()};
        AutoSmsDbHelper.getReadableDatabase().delete(TABLE_SMS, selection, selectionArgs);
    }

    private List<AutoSms> getListAutoSms(Cursor cursor) {
        ArrayList<AutoSms> result = new ArrayList<>();
        int indexTimestampCreated = cursor.getColumnIndex(COLUMN_TIMESTAMP_CREATED);
        int indexTimestampScheduled = cursor.getColumnIndex(COLUMN_TIMESTAMP_SCHEDULED);
        int indexRecipientNumber = cursor.getColumnIndex(COLUMN_RECIPIENT_PHONE_NUMBER);
        int indexRecipientLookup = cursor.getColumnIndex(COLUMN_RECIPIENT_LOOKUP);
        int indexMessage = cursor.getColumnIndex(COLUMN_MESSAGE);
        int indexStatus = cursor.getColumnIndex(COLUMN_STATUS);
        int indexResult = cursor.getColumnIndex(COLUMN_RESULT);
        int indexSubscriptionId = cursor.getColumnIndex(COLUMN_SUBSCRIPTION_ID);
        int indexRecurringMode = cursor.getColumnIndex(COLUMN_RECURRING_MODE);
        AutoSms object;
        while (cursor.moveToNext()) {
            object = new AutoSms();
            object.setDateCreated(new Date(cursor.getLong(indexTimestampCreated)));
            object.setDateScheduled(new Date(cursor.getLong(indexTimestampScheduled)));
            object.setRecipientPhoneNumber(cursor.getString(indexRecipientNumber));
            object.setRecipientLookup(cursor.getString(indexRecipientLookup));
            object.setMessage(cursor.getString(indexMessage));
            object.setStatus(AutoSms.Status.valueOf(cursor.getString(indexStatus)));
            object.setResult(cursor.getString(indexResult));
            object.setSubscriptionId(cursor.getInt(indexSubscriptionId));
            object.setRecurringMode(cursor.getString(indexRecurringMode));
            result.add(object);
        }
        return result;
    }
}
