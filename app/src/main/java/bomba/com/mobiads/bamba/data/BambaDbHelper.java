package bomba.com.mobiads.bamba.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import bomba.com.mobiads.bamba.data.BambaContract.*;

/**
 * Created by WAKY on 2/19/2017.
 */
public class BambaDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "bamba_test.db";


    public BambaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TONES_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TonesEntry.TABLE_NAME + " ( " +
                        TonesEntry._ID + " INTEGER PRIMARY KEY, " +
                        TonesEntry.COLUMN_PHONE + " varchar(255) NOT NULL, " +
                        TonesEntry.COLUMN_NAME + " varchar(255) NOT NULL, " +
                        TonesEntry.COLUMN_PATH + " varchar(255), " +
                        TonesEntry.COLUMN_STATUS + " varchar(50), " +
                        TonesEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
        sqLiteDatabase.execSQL(SQL_CREATE_TONES_TABLE);

        Log.i("WOURA", "Tables created!");
        Log.i("WOURA", "PROJECTS SQL: " + SQL_CREATE_TONES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TonesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
