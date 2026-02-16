package org.phramusca.cookandfreeze.ui.recipient;

import static org.phramusca.cookandfreeze.database.DbSchema.COL_CONTENT;
import static org.phramusca.cookandfreeze.database.DbSchema.COL_INVENTORY_DATE;
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
    private final Date inventoryDate;

    public AdapterListItemRecipient(String uuid, String title, String content, Date date, Date inventoryDate) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.date = date;
        this.inventoryDate = inventoryDate;
    }

    @SuppressLint("Range")
    public static AdapterListItemRecipient fromCursor(Cursor c) {
        Date date = HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(DbSchema.COL_DATE)));
        int invIdx = c.getColumnIndex(COL_INVENTORY_DATE);
        String invStr = invIdx >= 0 ? c.getString(invIdx) : null;
        Date invDate = invStr != null ? HelperDateTime.parseSqlUtc(invStr) : null;
        return new AdapterListItemRecipient(
                c.getString(c.getColumnIndex(COL_UUID)),
                c.getString(c.getColumnIndex(COL_TITLE)),
                c.getString(c.getColumnIndex(COL_CONTENT)),
                date,
                invDate);
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

    public Date getInventoryDate() {
        return inventoryDate;
    }

    public Recipient toRecipient() {
        return new Recipient(uuid, title, content, date, inventoryDate);
    }

    public static AdapterListItemRecipient fromRecipient(Recipient r) {
        return new AdapterListItemRecipient(
                r.getUuid(),
                r.getTitle(),
                r.getContent(),
                r.getDate(),
                r.getInventoryDate());
    }
}