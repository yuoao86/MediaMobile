package com.drawshirt.mediamobile.dao;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class HistoryTable {
    public static final String TAB_NAME = "tab_history";
    public static final String COL_TITLE = "col_title";
    public static final String COL_IMAGE_URL = "col_image_url";
    public static final String COL_SOURCE_URL = "col_source_url";
    public static final String COL_PLAY_TIME = "col_play_time";
    public static final String COL_POSITION = "col_position";
    public static final String COL_ISNET = "col_isnet";

    public static final String CREATE_TAB = "create table "
            + TAB_NAME + " ("
            + COL_TITLE + " text,"
            + COL_IMAGE_URL + " text,"
            + COL_SOURCE_URL + " text,"
            + COL_POSITION + " text,"
            + COL_ISNET + " integer,"
            + COL_PLAY_TIME + " long);";


}
