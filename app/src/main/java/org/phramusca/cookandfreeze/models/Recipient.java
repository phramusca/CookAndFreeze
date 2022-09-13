package org.phramusca.cookandfreeze.models;

import java.util.Date;
import java.util.Objects;

public class Recipient {

    private final String uuid;
    private int number = -1;
    private String content = "";
    private Date date = new Date(0);

    public Recipient(String uuid) {
        this.uuid = uuid;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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
}
