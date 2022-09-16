package org.phramusca.cookandfreeze.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Recipient implements Cloneable{

    private final String uuid;
    private String title = "";
    private String content = "";
    private Date date = new Date(0);

    public Recipient(String uuid) {
        this.uuid = uuid;
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
