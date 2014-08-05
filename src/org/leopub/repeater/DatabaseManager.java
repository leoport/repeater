package org.leopub.repeater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*
 * This class is used to control database. It uses the Singleton pattern.
 */
public class DatabaseManager {
    private static DatabaseManager sManager = null;

    private SQLiteDatabase mDatabase = null;

    public static DatabaseManager getInstance() {
        if (sManager == null) {
            sManager = new DatabaseManager(Configure.SQLITE_FILE_PATH);
        }
        return sManager;
    }

    private DatabaseManager(String SQLiteFilePath) {
        mDatabase = SQLiteDatabase.openOrCreateDatabase(SQLiteFilePath, null);
        String sql = "CREATE TABLE IF NOT EXISTS records (_id integer PRIMARY KEY AUTOINCREMENT, item_name VARCHAR(256), repeat_date BIGINT);";
        mDatabase.execSQL(sql);
    }

    public void addRecord(String itemName) {
        String sql = "INSERT INTO records VALUES(null, '" + itemName + "', " + (new Date()).getTime() + ");";
        mDatabase.execSQL(sql);
    }

    public List<Date> getRecords(String itemName) {
        String columns[] = {"repeat_date"};
        Cursor cursor = mDatabase.query("records", columns,
                "item_name='" + itemName + "'", null,
                null, null,
                "repeat_date DESC", null);
        List<Date> result = new ArrayList<Date>();
        while (cursor.moveToNext()) {
            result.add(new Date(cursor.getLong(0)));
        }
        return result;
    }
}
