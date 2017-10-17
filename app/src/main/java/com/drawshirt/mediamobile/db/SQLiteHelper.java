package com.drawshirt.mediamobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.drawshirt.mediamobile.dao.HistoryTable;
import com.drawshirt.mediamobile.utils.Utils;

/**
 * @author yanggf
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	

	public SQLiteHelper(Context context) {
		super(context, Utils.DB_NAME, null, Utils.DB_VERSION);

	}

	public SQLiteHelper(Context context, String name, CursorFactory factory,
                        int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(HistoryTable.CREATE_TAB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
		db.execSQL("DROP TABLE IF EXISTS "+HistoryTable.TAB_NAME);
		db.execSQL(HistoryTable.CREATE_TAB);
	}

}
