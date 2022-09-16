package org.phramusca.cookandfreeze.database;

import static org.phramusca.cookandfreeze.database.DbSchema.COL_CONTENT;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_DATE;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_TITLE;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_UUID;
import static org.phramusca.cookandfreeze.database.DbSchema.TABLE_RECIPIENTS;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.phramusca.cookandfreeze.models.Recipient;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;

import java.util.Date;

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

    public synchronized void insertOrUpdateRecipient(String title, String uuid, String content) {
        String log = "insertOrUpdateRecipient(" + title + ", " + uuid + ", " + content + ")"; //NON-NLS
        try {
            Log.d(TAG, log);
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, title);
            values.put(COL_UUID, uuid);
            values.put(COL_CONTENT, content);
            values.put(COL_DATE, HelperDateTime.getCurrentUtcSql());
            db.insertWithOnConflict(
                    TABLE_RECIPIENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, log, ex);
        }
    }

    @SuppressLint("Range")
    public Recipient getRecipient(String uuid) {
        Recipient recipient = new Recipient(uuid);
        try {
            Cursor cursor = db.query(TABLE_RECIPIENTS, new String[]{COL_CONTENT, COL_TITLE, COL_UUID, COL_DATE},
                    COL_UUID + "=?", new String[]{uuid}, null, null, COL_TITLE);
            if(cursor!=null && cursor.moveToFirst()) {
                recipient.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
                recipient.setContent(cursor.getString(cursor.getColumnIndex(DbSchema.COL_CONTENT)));
                Date date = HelperDateTime.parseSqlUtc(
                        cursor.getString(cursor.getColumnIndex(DbSchema.COL_DATE)));
                recipient.setDate(date);
            }
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getRecipient()", ex); //NON-NLS
        }
        return recipient;
    }

    public Cursor getRecipients(String search) {
        try {
            return db.query(TABLE_RECIPIENTS, new String[]{COL_CONTENT, COL_TITLE, COL_UUID, COL_DATE},
                    COL_CONTENT + " LIKE ?", new String[]{"%"+search+"%"}, null, null, COL_TITLE);
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getRecipients()", ex); //NON-NLS
        }
        return null;
    }
}
