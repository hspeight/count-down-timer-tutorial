package com.androidtutorialshub.countdowntimer.Utils;

/**
 * Created by hspeight on 31/03/2018.
 */

public class DBUtil {

    //Database version
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "timer.db";
    public static final String TABLE_NAME_TIMER = "timer";

    //Timer table column names
    public static final String TIM_ID = "id";
    public static final String TIM_TITLE = "title";
    public static final String TIM_DESC = "desc";
    public static final String TIM_MESSAGE = "message";
    public static final String TIM_STAMP = "timestamp";
    public static final String TIM_IMAGE = "image";
    public static final String TIM_STATUS = "status";
    public static final String TIM_TYPE = "type";

}
