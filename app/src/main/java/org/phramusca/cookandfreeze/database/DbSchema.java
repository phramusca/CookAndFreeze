package org.phramusca.cookandfreeze.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import org.phramusca.cookandfreeze.R;

public class DbSchema extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public static final String TABLE_RECIPIENTS = "recipients"; //NON-NLS
    public static final String COL_NUMBER = "number";
    public static final String COL_UUID = "uuid";
    public static final String COL_CONTENT = "content";
    private static final String CREATE_TABLE_RECIPIENTS =
            "CREATE TABLE " + TABLE_RECIPIENTS + " (" //NON-NLS //NON-NLS
            + COL_UUID + " TEXT PRIMARY KEY, "
            + COL_NUMBER + " INTEGER NOT NULL, " //NON-NLS
            + COL_CONTENT + " TEXT NOT NULL); "; //NON-NLS

    DbSchema(final Context context) {
        super(context,Environment.getExternalStorageDirectory()
                    + "/"+context.getResources().getString(R.string.app_name)
                        +"/"+context.getResources().getString(R.string.app_name)+".db",
                null, DB_VERSION); //NON-NLS
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RECIPIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //NON-NLS
        db.execSQL("DROP TABLE " + TABLE_RECIPIENTS + ";"); //NON-NLS
        onCreate(db);
    }
}
