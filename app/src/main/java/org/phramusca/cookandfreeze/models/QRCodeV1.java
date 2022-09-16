package org.phramusca.cookandfreeze.models;

public class QRCodeV1 {
    public double version;
    public String title;
    public String uuid;

    public Recipient toRecipient() {
        Recipient recipient = new Recipient(uuid);
        recipient.setTitle(title);
        return recipient;
    }
}
