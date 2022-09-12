package org.phramusca.cookandfreeze.database;

import static org.phramusca.cookandfreeze.database.DbSchema.COL_CONTENT;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_NUMBER;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_UUID;
import static org.phramusca.cookandfreeze.database.DbSchema.TABLE_RECIPIENTS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class Db {
    SQLiteDatabase db;
    private final DbSchema dbSchema;
    private static final String TAG = Db.class.getName();

    Db(Context context) {
        dbSchema = new DbSchema(context);
    }

    public synchronized void open() {
        db = dbSchema.getWritableDatabase();
    }

    public synchronized void close() {
        db.close();
    }

    public synchronized void insertOrUpdateRecipient(int number, String uuid, String content) {
        String log = "insertOrUpdateRecipient(" + number + ", " + uuid + ", " + content + ")"; //NON-NLS
        try {
            Log.d(TAG, log);
            ContentValues values = new ContentValues();
            values.put(COL_NUMBER, number);
            values.put(COL_UUID, uuid);
            values.put(COL_CONTENT, content);
            db.insertWithOnConflict(
                    TABLE_RECIPIENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, log, ex);
        }
    }

    public Cursor getRecipient(String uuid) {
        try {
            return db.query(TABLE_RECIPIENTS, new String[]{COL_CONTENT, COL_NUMBER, COL_UUID},
                    COL_UUID + "=?", new String[]{uuid}, null, null, COL_NUMBER);
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getRecipient()", ex); //NON-NLS
        }
        return null;
    }

    public Cursor getRecipients() {
        try {
            return db.query(TABLE_RECIPIENTS, new String[]{COL_CONTENT, COL_NUMBER, COL_UUID},
                    null, null, null, null, COL_NUMBER);
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getRecipients()", ex); //NON-NLS
        }
        return null;
    }

}
