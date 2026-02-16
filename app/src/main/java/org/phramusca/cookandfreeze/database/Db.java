package org.phramusca.cookandfreeze.database;

import static org.phramusca.cookandfreeze.database.DbSchema.COL_CONTENT;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_DATE;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_INVENTORY_DATE;
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

    private static final String[] COLUMNS_RECIPIENT = new String[]{COL_CONTENT, COL_TITLE, COL_UUID, COL_DATE, COL_INVENTORY_DATE};

    public synchronized void insertOrUpdateRecipient(String title, String uuid, String content, Date date) {
        insertOrUpdateRecipient(title, uuid, content, date, null);
    }

    public synchronized void insertOrUpdateRecipient(String title, String uuid, String content, Date date, Date inventoryDate) {
        String log = "insertOrUpdateRecipient(" + title + ", " + uuid + ", " + content + ")"; //NON-NLS
        try {
            Log.d(TAG, log);
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, title);
            values.put(COL_UUID, uuid);
            values.put(COL_CONTENT, content);
            values.put(COL_DATE, HelperDateTime.formatUTCtoSqlUTC(date));
            // À la création (inventoryDate null), date d'inventaire = date d'ajout
            Date invDate = inventoryDate != null ? inventoryDate : date;
            values.put(COL_INVENTORY_DATE, HelperDateTime.formatUTCtoSqlUTC(invDate));
            db.insertWithOnConflict(
                    TABLE_RECIPIENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, log, ex);
        }
    }

    public synchronized void updateInventoryDate(String uuid, Date inventoryDate) {
        try {
            ContentValues values = new ContentValues();
            values.put(COL_INVENTORY_DATE, HelperDateTime.formatUTCtoSqlUTC(inventoryDate));
            db.update(TABLE_RECIPIENTS, values, COL_UUID + "=?", new String[]{uuid});
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateInventoryDate()", ex);
        }
    }

    public synchronized void deleteRecipient(String uuid) {
        try {
            db.delete(TABLE_RECIPIENTS, COL_UUID + "=?", new String[]{uuid});
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteRecipient()", ex);
        }
    }

    @SuppressLint("Range")
    public Recipient getRecipient(String uuid) {
        Recipient recipient = null;
        try {
            Cursor cursor = db.query(TABLE_RECIPIENTS, COLUMNS_RECIPIENT,
                    COL_UUID + "=?", new String[]{uuid}, null, null, COL_TITLE);
            if (cursor != null && cursor.moveToFirst()) {
                Date date = HelperDateTime.parseSqlUtc(
                        cursor.getString(cursor.getColumnIndex(DbSchema.COL_DATE)));
                String invDateStr = cursor.getString(cursor.getColumnIndex(COL_INVENTORY_DATE));
                Date invDate = invDateStr != null ? HelperDateTime.parseSqlUtc(invDateStr) : null;
                recipient = new Recipient(
                        uuid,
                        cursor.getString(cursor.getColumnIndex(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(DbSchema.COL_CONTENT)),
                        date,
                        invDate);
            }
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getRecipient()", ex); //NON-NLS
        }
        return recipient;
    }

    public Cursor getRecipients(String search) {
        try {
            return db.query(TABLE_RECIPIENTS, COLUMNS_RECIPIENT,
                    COL_CONTENT + " LIKE ?", new String[]{"%"+search+"%"}, null, null, COL_TITLE);
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getRecipients()", ex); //NON-NLS
        }
        return null;
    }
}
