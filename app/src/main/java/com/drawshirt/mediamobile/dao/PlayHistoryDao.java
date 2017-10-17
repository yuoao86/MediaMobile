package com.drawshirt.mediamobile.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.db.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * 	
 * @author yanggf
 *
 */
public class PlayHistoryDao {
	
	/**
	 * SQLite help class
	 */
	private SQLiteHelper helper;
	
	public PlayHistoryDao(Context ctx) {
		helper = new SQLiteHelper(ctx);
	}
	
	
	/**
	 * Insert the playback history information
	 */
	public void insert(PlayHistoryInfo info){
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			ContentValues value = new ContentValues();
			value.put(HistoryTable.COL_TITLE, info.getTitle());
			value.put(HistoryTable.COL_IMAGE_URL, info.getImage_url());
			value.put(HistoryTable.COL_SOURCE_URL, info.getSource_url());
			value.put(HistoryTable.COL_POSITION, info.getPosition());
			value.put(HistoryTable.COL_ISNET, info.getIs_net());
			value.put(HistoryTable.COL_PLAY_TIME, info.getPlay_time());
			db.replace(HistoryTable.TAB_NAME, null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db != null)
			db.close();
		}
		
	}
	
	/**
	 * Query the database have to play in the history of the total number of records
	 */
	public int findTota(){
		SQLiteDatabase db = null;
		Cursor cur = null;
		int count = 0 ;
		try {
			db = helper.getWritableDatabase();
			String sql = "select count(*) as c from "+HistoryTable.TAB_NAME ;
		    cur = db.rawQuery(sql, null);
			if(cur.moveToNext()){
				count = cur.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return count;
		}finally{
			if(cur != null)
			cur.close();
			if(db != null)
			db.close();
			
		}
		return count;
	}
	
	
	/**
	 * Query the database, all player history in mind and return a collection of
     * Desc descending order, other default ascending
	 */
	
	public List<PlayHistoryInfo> findByOrder(String order) {
		SQLiteDatabase db = null;
		Cursor cur = null;
		List<PlayHistoryInfo> list = null;
		try {
			 list = new ArrayList<PlayHistoryInfo>();
			 db = helper.getWritableDatabase();
			 String sql = null;
			 if(order!=null){
				 if("desc".equals(order)){
					 sql = "select * from " + HistoryTable.TAB_NAME + " order by " + HistoryTable.COL_PLAY_TIME + " desc" ;
					 //sql ="select _mid,_mediatype,_medianame,_hashid,_taskname,_fsp,_playedtimestring,_playedtime,_position,_movieposition,_movieplayedtime ,_size,_percent,_purl from playhistoryinfos order by _playedtime desc";
				 }else{
					 sql = "select * from " + HistoryTable.TAB_NAME + " order by " + HistoryTable.COL_PLAY_TIME ;
					 //sql ="select _mid,_mediatype,_medianame,_hashid,_taskname,_fsp,_playedtimestring,_playedtime,_position,_movieposition,_movieplayedtime,_size ,_percent ,_purl  from playhistoryinfos order by _playedtime";
				 }
			 }else{
				 sql = "select * from " + HistoryTable.TAB_NAME + " order by " + HistoryTable.COL_PLAY_TIME ;
				 //sql ="select _mid,_mediatype,_medianame,_hashid,_taskname,_fsp,_playedtimestring,_playedtime,_position,_movieposition,_movieplayedtime,_size ,_percent ,_purl  from playhistoryinfos order by _playedtime";
			 }
			 
			 cur = db.rawQuery(sql, new String[]{});
			
			if (cur != null) {  
				cur.moveToFirst();  
		        for (int i=0; i<cur.getCount(); i++) {  
		        	PlayHistoryInfo info = new PlayHistoryInfo();
					info.setTitle(cur.getString(cur.getColumnIndex(HistoryTable.COL_TITLE)));
					info.setImage_url(cur.getString(cur.getColumnIndex(HistoryTable.COL_IMAGE_URL)));
					info.setSource_url(cur.getString(cur.getColumnIndex(HistoryTable.COL_SOURCE_URL)));
					info.setPosition(cur.getString(cur.getColumnIndex(HistoryTable.COL_POSITION)));
					info.setIs_net(cur.getInt(cur.getColumnIndex(HistoryTable.COL_ISNET)));
					info.setPlay_time(cur.getLong(cur.getColumnIndex(HistoryTable.COL_PLAY_TIME)));
					list.add(info);
		            cur.moveToNext();  
		        }  
		      
		    }  
			
		} catch (Exception e) {
			e.printStackTrace();
			return list ;
		}finally{
			if(cur!=null)
			cur.close();
			if(db!=null)
			db.close();
		}
		return list ;
	}

	/**
	 * When more data than the auto bar, clear the table in the auto after all
	 */

	public void autoDelete(int auto){
		SQLiteDatabase db = null;
		int autos = auto;

		try {
			db = helper.getWritableDatabase();
			String sql="delete from "+HistoryTable.TAB_NAME+" where (select count("+HistoryTable.COL_TITLE+") from "+HistoryTable.TAB_NAME+
					")> "+auto+" and "+HistoryTable.COL_TITLE+" in (select "+HistoryTable.COL_TITLE+" from "+HistoryTable.TAB_NAME+
					" order by "+HistoryTable.COL_PLAY_TIME+" desc limit (select count("+HistoryTable.COL_TITLE+") from "+HistoryTable.TAB_NAME+") offset "+auto+" )";
			db.execSQL(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null)
				db.close();

		}

	}
	
	
	/**
	 * Clear all data in the table
	 */
	public void deleteAllData(){
		SQLiteDatabase db = null;
		try {
			db = helper.getWritableDatabase();
			String sql = "delete from "+HistoryTable.TAB_NAME ;
			db.execSQL(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null)
			 db.close();
			
		}
	}
}
