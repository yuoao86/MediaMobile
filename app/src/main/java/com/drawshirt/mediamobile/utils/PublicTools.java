package com.drawshirt.mediamobile.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.drawshirt.mediamobile.R;

import java.io.File;

public class PublicTools {
    public static final int THUMBNAIL_PREPARED = 1;
    public static final int THUMBNAIL_EMPTY = 0;
    public static final int THUMBNAIL_CORRUPTED = -1;
    public static final int MINI_INTERVAL = 50;
    public static final int SHORT_INTERVAL = 150;
    public static final int MIDDLE_INTERVAL = 300;
    public static final int LONG_INTERVAL = 600;
    public static final int LONG_LONG_INTERVAL = 6000;
    private static final int FILENAMELENGTH = 80;

    public static long getBucketId(String path) {
        return path.toLowerCase().hashCode();
    }

    public static String cutString(String origin, int length) {
        char[] c = origin.toCharArray();
        int len = 0;
        int strEnd = 0;
        for (int i = 0; i < c.length; i++) {
            strEnd++;
            len = (c[i] / 0x80 == 0) ? (len + 1) : (len + 2);
            if (len > length || (len == length && i != (c.length - 1))) {
                origin = origin.substring(0, strEnd) + "...";
                break;
            }
        }
        return origin;
    }

    public static String replaceFilename(String filepath, String name) {
        String newPath = "";
        int lastSlash = filepath.lastIndexOf('/');
        if (lastSlash >= 0) {
            lastSlash++;
            if (lastSlash < filepath.length()) {
                newPath = filepath.substring(0, lastSlash);
            }
        }
        newPath = newPath + name;
        int lastDot = filepath.lastIndexOf('.');
        if (lastDot > 0) {
            newPath = newPath
                    + filepath.substring(lastDot, filepath.length());
        }
        return newPath;
    }

    public static boolean isFilenameIllegal(String filename) {
        return (filename.length() <= FILENAMELENGTH);
    }

    public static boolean isLandscape() {
        // Log.v(TAG,"isLandscape : "+ mDisplay.getOrientation());
        //getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
        return (UIUtils.getContext().getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE);

    }

    public static boolean isFileExist(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    public static void sleep(int interval) {
        try {
            Thread.sleep(interval);
        } catch (Exception e) {
        }
    }

    public static AlertDialog hint(Context context, int StringId) {
        return new AlertDialog.Builder(context).setMessage(
                context.getString(StringId)).setNeutralButton(
                R.string.button_ok, null).show();
    }

    public static Cursor query(ContentResolver resolver, Uri uri,
                               String[] projection, String selection, String[] selectionArgs,
                               String sortOrder) {
        try {
            if (resolver == null) {
                return null;
            }
            return resolver.query(uri, projection, selection,
                    selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }
    }

    public static boolean isMediaScannerScanning(ContentResolver cr) {
        boolean result = false;
        Cursor cursor = query(cr, MediaStore.getMediaScannerUri(),
                new String[] { MediaStore.MEDIA_SCANNER_VOLUME }, null,
                null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = "external".equals(cursor.getString(0));
            }
            cursor.close();
        }

        return result;
    }

    public static boolean isVideoStreaming(Uri uri) {
        return ("http".equalsIgnoreCase(uri.getScheme()) || "rtsp"
                .equalsIgnoreCase(uri.getScheme()));
    }
}