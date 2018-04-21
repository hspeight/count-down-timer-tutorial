package com.androidtutorialshub.countdowntimer.Utils;

/**
 * Created by hspeight on 31/03/2018.
 */

public class DBUtil {

    //Database version
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "timer.db";

    //Timer table & column names
    public static final String TABLE_NAME_TIMER = "timer";
    public static final String TIM_ID = "id";
    public static final String TIM_TITLE = "title";
    public static final String TIM_DESC = "desc";
    public static final String TIM_MESSAGE = "message";
    public static final String TIM_STAMP = "timestamp";
    public static final String TIM_IMAGE = "image";
    public static final String TIM_STATUS = "status";
    public static final String TIM_TYPE = "type";

    //Backups table column names
    public static final String TABLE_NAME_BACKUPS = "backups";
    public static final String BCK_ID = "bck_id";
    public static final String BCK_FILENAME = "bck_filename";
    public static final String BCK_NUMROWS = "bck_numrows";
    public static final String BCK_TIMESTAMP = "bck_timestamp";
    public static final String BCK_NOTES = "bck_notes";

    public static final String CREATE_TABLE_TIMER = "CREATE TABLE "
            + TABLE_NAME_TIMER + "(" + TIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TIM_TITLE
            + " TEXT," + TIM_DESC + " TEXT," + TIM_MESSAGE + " TEXT," + TIM_STAMP
            + " INTEGER," + TIM_IMAGE + " TEXT," + TIM_STATUS + " TEXT," + TIM_TYPE + " TEXT);";

    public static final String CREATE_TABLE_BACKUPS = "CREATE TABLE "
            + TABLE_NAME_BACKUPS  + "(" + BCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + BCK_FILENAME
            + " TEXT," + BCK_NUMROWS + " INTEGER," + BCK_TIMESTAMP + " INTEGER," + BCK_NOTES
            + " TEXT);";

}
