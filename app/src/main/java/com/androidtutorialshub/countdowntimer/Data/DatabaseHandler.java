package com.androidtutorialshub.countdowntimer.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.androidtutorialshub.countdowntimer.Model.Timer;
import com.androidtutorialshub.countdowntimer.Model.Backup;
import com.androidtutorialshub.countdowntimer.Utils.DBUtil;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public String DEBUG_TAG = "!!DBH";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBUtil.DATABASE_NAME, factory, DBUtil.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(DBUtil.CREATE_TABLE_TIMER);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL(DBUtil.CREATE_TABLE_BACKUPS);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d(DEBUG_TAG, "upgrading db");

        try {
            db.execSQL("DROP TABLE IF EXISTS " + DBUtil.TABLE_NAME_TIMER);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL("DROP TABLE IF EXISTS " + DBUtil.TABLE_NAME_BACKUPS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Log.d(DEBUG_TAG, "DROP TABLE IF EXISTS " + DBUtil.TABLE_NAME_TIMER + ";");
        onCreate(db);
    }

    //Add Timer
    public void addTimer(Timer timer) {

        //Log.d(DEBUG_TAG, "Adding row");

        ContentValues value = new ContentValues();
        value.put(DBUtil.TIM_TITLE, timer.getTitle());
        value.put(DBUtil.TIM_DESC, timer.getDesc());
        value.put(DBUtil.TIM_MESSAGE, timer.getMessage());
        value.put(DBUtil.TIM_STAMP, timer.getTimestamp());
        value.put(DBUtil.TIM_IMAGE, timer.getImage());
        value.put(DBUtil.TIM_STATUS, timer.getStatus());
        value.put(DBUtil.TIM_TYPE, timer.getType());
        value.put(DBUtil.TIM_MODIFIED, timer.getModified());
        value.put(DBUtil.TIM_UNITS, timer.getTimeunits());
        value.put(DBUtil.TIM_SHAPE, timer.getImageshape());

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insert(DBUtil.TABLE_NAME_TIMER, null, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();

    }

    public Timer getTimer(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DBUtil.TABLE_NAME_TIMER, new String[]
                        {DBUtil.TIM_ID, DBUtil.TIM_TITLE, DBUtil.TIM_DESC, DBUtil.TIM_MESSAGE,
                                DBUtil.TIM_STAMP, DBUtil.TIM_IMAGE, DBUtil.TIM_STATUS,
                                DBUtil.TIM_TYPE, DBUtil.TIM_MODIFIED, DBUtil.TIM_UNITS, DBUtil.TIM_SHAPE},
                DBUtil.TIM_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        //Log.d(DEBUG_TAG, "cursor count is " + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new Timer(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getInt(8),
                    cursor.getInt(9),
                    cursor.getString(10));

        } else {
            return new Timer();
        }
    }

    public List <Timer> get_AllTimers() {

        SQLiteDatabase db = this.getReadableDatabase();
        List <Timer> timerList = new ArrayList <>();
        String selectAll = "SELECT * FROM " + DBUtil.TABLE_NAME_TIMER;
        Cursor cursor = db.rawQuery(selectAll, null);

        if (cursor.moveToFirst()) {
            do {
                Timer timer = new Timer();

                timer.setKey(Integer.parseInt(cursor.getString(0)));
                timer.setTitle(cursor.getString(1));
                timer.setDesc(cursor.getString(2));
                timer.setDesc(cursor.getString(3));
                timer.setTimestamp(cursor.getInt(4));
                timer.setImage(cursor.getString(5));
                timer.setDesc(cursor.getString(6));
                timer.setDesc(cursor.getString(7));
                timer.setModified(cursor.getInt(8));
                timer.setModified(cursor.getInt(9));
                timer.setDesc(cursor.getString(10));

                timerList.add(timer);

            } while (cursor.moveToNext());
        }

        return timerList;

    }

    // Get row count by status
    public int getRowCountByStatus(String status) {

        String whereClause;
        if (status.equals("A")) // all rows
            whereClause = "status > \"\"";
        else
            whereClause = "status=\"" + status + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, DBUtil.TABLE_NAME_TIMER, whereClause);

    }

    // Get row count by type
    public int getRowCountByType(String type) {

        String whereClause;
        whereClause = "type=\"" + type + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, DBUtil.TABLE_NAME_TIMER, whereClause);

    }

    //Delete (L)ive, (I)nactive or (A)ll rows
    public boolean deleteRowsByStatus(String status) {
        String selector;

        if (status.equals("A"))
            selector = " > \"\"";
        else
            selector = " =\"" + status + "\"";

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + DBUtil.TABLE_NAME_TIMER +
                        " WHERE " + DBUtil.TIM_STATUS + selector + "\";");
            //Log.d(DEBUG_TAG, "delete statement is " + "DELETE FROM " + DBUtil.TABLE_NAME_TIMER +
            //        " WHERE " + DBUtil.TIM_STATUS + selector + "\";");
            //result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return true;  // need to do a proper return
    }

    //Delete timer from database
    public boolean deleteTimerById(int id) {

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + DBUtil.TABLE_NAME_TIMER +
                    " WHERE " + DBUtil.TIM_ID + "=" + id + ";");
            Log.d(DEBUG_TAG, "delete timer statement is " + "DELETE FROM " + DBUtil.TABLE_NAME_TIMER +
                    " WHERE " + DBUtil.TIM_ID + "=" + id + ";");
            //result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return true;  // need to do a proper return
    }

    public void updateSampleStatus(String status) {
        // sets the status to (L)ive or (I)nactive for sample timers.
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("UPDATE " + DBUtil.TABLE_NAME_TIMER +   " SET " + DBUtil.TIM_STATUS + " = \"" + status + "\" WHERE " +
                    DBUtil.TIM_TYPE + " = \"S\";");
            //result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        //return result;
    }

    public String getTimerIds(String status) {
        String query;
        String dbString = "";
        SQLiteDatabase db = getReadableDatabase();
        if (status.equals("L")) { // get everything
            query = "SELECT id FROM " + DBUtil.TABLE_NAME_TIMER + " WHERE " + DBUtil.TIM_STATUS + " = \"" + status + "\";";
        } else { // dont get samples for (I)nactive
            query = "SELECT id FROM " + DBUtil.TABLE_NAME_TIMER + " WHERE " + DBUtil.TIM_STATUS +
                    " = \"" + status + "\" AND " + DBUtil.TIM_TYPE + " = \"R\";";

        }

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("id")) != null) {
                dbString += c.getString(c.getColumnIndex("id"));
                dbString += ":"; // Delimiter between record IDs
                c.moveToNext();
            }
        }
        //System.out.println("!!- "  + dbString);
        db.close();
        c.close();
        return dbString;
    }

    public void updateTimer(Timer timer) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBUtil.TIM_TITLE, timer.getTitle());
        cv.put(DBUtil.TIM_DESC, timer.getDesc());
        cv.put(DBUtil.TIM_MESSAGE, timer.getMessage());
        cv.put(DBUtil.TIM_IMAGE, timer.getImage());
        cv.put(DBUtil.TIM_STAMP, timer.getTimestamp());
        cv.put(DBUtil.TIM_STATUS, timer.getStatus());
        cv.put(DBUtil.TIM_TYPE, timer.getType());
        cv.put(DBUtil.TIM_MODIFIED, timer.getModified());
        cv.put(DBUtil.TIM_UNITS, timer.getTimeunits());
        cv.put(DBUtil.TIM_SHAPE, timer.getImageshape());

        db.update(DBUtil.TABLE_NAME_TIMER, cv, DBUtil.TIM_ID + "=?",
                        new String[] {String.valueOf(timer.getKey())});
        db.close();

    }

    //public boolean deleteRowsById (int id) {

    //}

    // Delete (R)eal or (S)ample rows
    //public boolean deleteRowsByType (String type) {

    //}

    //***************************************************/
    /************ Backups CRUD functions ***************/
    //***************************************************/

    //Add details for a backup
    public void addBackup(Backup backup) {

        //Log.d(DEBUG_TAG, "Adding backup row");

        ContentValues value = new ContentValues();
        value.put(DBUtil.BCK_FILENAME, backup.getFilename());
        value.put(DBUtil.BCK_NUMROWS, backup.getNumrows());
        value.put(DBUtil.BCK_TIMESTAMP, backup.getTimestamp());
        value.put(DBUtil.BCK_NOTES, backup.getNotes());

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insert(DBUtil.TABLE_NAME_BACKUPS, null, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();

    }
    public String getBackupIds() {

        StringBuilder dbString = new StringBuilder();


        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT bck_id FROM " + DBUtil.TABLE_NAME_BACKUPS + ";";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("bck_id")) != null) {
                dbString.append(c.getString(c.getColumnIndex("bck_id")));
                dbString.append(":"); // Delimiter between record IDs
                c.moveToNext();
            }
        }
        db.close();
        c.close();
        String retStr = dbString.toString();
        if(retStr.length() > 0)
            retStr = retStr.substring(0, retStr.length() - 1);

        //Log.d(DEBUG_TAG, "str is " + retStr);

        return retStr;
        //return dbString.toString();
    }

    public Backup getBackup(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DBUtil.TABLE_NAME_BACKUPS, new String[]
                        {DBUtil.BCK_ID, DBUtil.BCK_FILENAME, DBUtil.BCK_NUMROWS, DBUtil.BCK_TIMESTAMP,
                                DBUtil.BCK_NOTES},
                DBUtil.BCK_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        //Log.d(DEBUG_TAG, "cursor count is " + cursor.getCount());

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new Backup(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4));
        } else {
            cursor.close();
            return new Backup();
        }

    }

    //Delete backup row from database
    public boolean deleteBackupRow(int id) {
        String selector;

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + DBUtil.TABLE_NAME_BACKUPS +
                    " WHERE " + DBUtil.BCK_ID + "=" + id + ";");
            //Log.d(DEBUG_TAG, "delete statement is " + "DELETE FROM " + DBUtil.TABLE_NAME_BACKUPS +
            //        " WHERE " + DBUtil.BCK_ID + "=" + id + ";");
            //result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return true;  // need to do a proper return
    }

}