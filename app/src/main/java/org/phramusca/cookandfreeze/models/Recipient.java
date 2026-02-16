package org.phramusca.cookandfreeze.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Recipient implements Cloneable{

    private final String uuid;
    private String title;
    private String content;
    private Date date;
    private Date inventoryDate;

    public Recipient(String uuid, String title, String content, Date date) {
        this(uuid, title, content, date, null);
    }

    public Recipient(String uuid, String title, String content, Date date, Date inventoryDate) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.date = date;
        this.inventoryDate = inventoryDate;
    }

    public Recipient(String uuid) {
        this(uuid, "", "", new Date(), null);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(Date inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipient recipient = (Recipient) o;
        return uuid.equals(recipient.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
