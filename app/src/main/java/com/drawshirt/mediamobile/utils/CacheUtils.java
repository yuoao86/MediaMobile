package com.drawshirt.mediamobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.drawshirt.mediamobile.service.MusicPlayerService;




/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class CacheUtils {

    public static void putSharedMode(Context context,String key,int value){

        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,value).commit();

    }


    public static int getSharedMode(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }

    public static void putSharedData(Context context,String key,String value){

        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,value).commit();

    }


    public static String getSharedData(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

}
