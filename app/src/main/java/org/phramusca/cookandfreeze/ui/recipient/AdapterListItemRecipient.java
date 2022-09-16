package org.phramusca.cookandfreeze.ui.recipient;

import static org.phramusca.cookandfreeze.database.DbSchema.COL_CONTENT;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_NUMBER;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_UUID;

import android.annotation.SuppressLint;
import android.database.Cursor;

import org.phramusca.cookandfreeze.database.DbSchema;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;

import java.util.Date;

public class AdapterListItemRecipient {
    private final String uuid;
    private final int number;
    private final String content;
    private final String date; //TODO: Only change when requested

    public AdapterListItemRecipient(String uuid, int number, String content, String date) {
        this.uuid = uuid;
        this.number = number;
        this.content = content;
        this.date = date;
    }

    @SuppressLint("Range")
    public static AdapterListItemRecipient fromCursor(Cursor c) {
        Date date = HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(DbSchema.COL_DATE)));
        String dateDisplay = HelperDateTime.formatUTC(date,
                HelperDateTime.DateTimeFormat.HUMAN, true);
        return new AdapterListItemRecipient(
                c.getString(c.getColumnIndex(COL_UUID)),
                c.getInt(c.getColumnIndex(COL_NUMBER)),
                c.getString(c.getColumnIndex(COL_CONTENT)),
                dateDisplay);
    }

    public String getUuid() {
        return uuid;
    }

    public int getNumber() {
        return number;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}