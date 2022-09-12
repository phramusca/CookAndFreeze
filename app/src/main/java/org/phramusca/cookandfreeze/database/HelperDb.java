package org.phramusca.cookandfreeze.database;

import android.content.Context;

public final class HelperDb {

    public static Db db;

    private HelperDb() {
    }

    public static void open(Context context) {
        if (db == null || !db.db.isOpen()) {
            db = new Db(context);
            db.open();
        }
    }

    public static void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }
}