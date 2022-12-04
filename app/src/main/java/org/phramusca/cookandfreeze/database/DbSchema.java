package org.phramusca.cookandfreeze.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import org.phramusca.cookandfreeze.R;
import org.phramusca.cookandfreeze.helpers.HelperFile;

public class DbSchema extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;

    public static final String TABLE_RECIPIENTS = "recipients"; //NON-NLS

    public static final String COL_TITLE = "title";
    public static final String COL_UUID = "uuid";
    public static final String COL_CONTENT = "content";
    public static final String COL_DATE = "date";

    private static final String CREATE_TABLE_RECIPIENTS =
            "CREATE TABLE " + TABLE_RECIPIENTS + " (" //NON-NLS //NON-NLS
            + COL_UUID + " TEXT PRIMARY KEY, "
            + COL_TITLE + " TEXT NOT NULL, " //NON-NLS
            + COL_DATE + " TEXT NOT NULL, " //NON-NLS
            + COL_CONTENT + " TEXT NOT NULL); "; //NON-NLS

    DbSchema(final Context context) {
        super(context, HelperFile.getFile(context.getResources().getString(R.string.app_name)+".db").getAbsolutePath(),
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
