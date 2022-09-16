package org.phramusca.cookandfreeze.ui.recipient;

import static org.phramusca.cookandfreeze.database.DbSchema.COL_CONTENT;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_TITLE;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_UUID;

import android.annotation.SuppressLint;
import android.database.Cursor;

import org.phramusca.cookandfreeze.database.DbSchema;
import org.phramusca.cookandfreeze.helpers.HelperDateTime;
import org.phramusca.cookandfreeze.models.Recipient;

import java.util.Date;

public class AdapterListItemRecipient {
    private final String uuid;
    private final String title;
    private final String content;
    private final Date date;

    public AdapterListItemRecipient(String uuid, String title, String content, Date date) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    @SuppressLint("Range")
    public static AdapterListItemRecipient fromCursor(Cursor c) {
        Date date = HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(DbSchema.COL_DATE)));
        return new AdapterListItemRecipient(
                c.getString(c.getColumnIndex(COL_UUID)),
                c.getString(c.getColumnIndex(COL_TITLE)),
                c.getString(c.getColumnIndex(COL_CONTENT)),
                date);
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public Recipient toRecipient() {
        Recipient recipient = new Recipient(getUuid());
        recipient.setDate(date);
        recipient.setContent(getContent());
        recipient.setTitle(getTitle());
        return recipient;
    }
}