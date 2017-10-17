package com.drawshirt.mediamobile.local;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.drawshirt.mediamobile.utils.PublicTools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Locale;



public class VideoObject {

    protected ContentResolver mContentResolver;
    protected int mCursorRow;
    protected long mId, mMiniThumbMagic;
    protected VideoList mContainer;
    private int mThumbnailState;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    java.util.Random mRandom = new java.util.Random(System.currentTimeMillis());

    protected VideoObject(long id, ContentResolver cr, VideoList container, int row) {
        mId = id;
        mContentResolver = cr;
        mContainer = container;
        mCursorRow = row;
        mThumbnailState = PublicTools.THUMBNAIL_EMPTY;
        mMiniThumbMagic = makeThumbMagic();
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }
    
    public String getTitle() {
        String name = null;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                name = c.getString(mContainer.INDEX_TITLE);
            }
        }
        return (name != null && name.length() > 0) ? name : String.valueOf(mId);
    }
    public String getMediapath() {
        String path = null;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                path = c.getString(mContainer.INDEX_DATA);
            }
        }
        return path;
    }
    public long getBucketId() {
        long bucket_id = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                bucket_id = c.getLong(mContainer.INDEX_BUCKET_ID);
            }
        }
        return bucket_id;
    }
    public long getDateModified() {
        long dateModified = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                dateModified = c.getLong(mContainer.INDEX_DATE_MODIFIED);
            }
        }
        return dateModified;
    }
    public String getDuration() {
        long duration = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                duration = c.getLong(mContainer.INDEX_DURATION);
            }
        }

        int totalSeconds = (int) (duration / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60);

        mFormatBuilder.setLength(0);
        if (minutes > 0) {
            return mFormatter.format("%d'%02d\"", minutes, seconds).toString();
        } else {
            if (seconds == 0)
                seconds = 1;

            return mFormatter.format("%02d\"", seconds).toString();
        }
    }
    public int getMediaId() {
        int id = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                id = c.getInt(mContainer.INDEX_ID);
            }
        }

        return id;
    }
    public String getSize() {
        long size = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                size = c.getLong(mContainer.INDEX_SIZE);
            }
        }

        double mSize = ((double) size / 1024.0);
        String flag;

        Log.v("test", "size : " + mSize);
        if (mSize > 1024.0) {
            mSize = mSize / 1024.0;
            flag = "MB";
        } else {
            flag = "KB";
        }

        NumberFormat formater = DecimalFormat.getInstance();
        formater.setMaximumFractionDigits(1);
        return formater.format(mSize) + flag;
    }

    /**
     * get video thumbnail from .db
     * 2010.11.10 change by W.Y
     * */
    /**hejn, optimizing thumbnail list, 20101210 begin*/
    public Bitmap miniThumbBitmap(boolean decodeOnly, Hashtable<Integer, Bitmap> ht, Bitmap defaultBitmap) {
        Bitmap mThumbnail = null;
//        2010.11.15 delete by W.Y
//        Cursor mCursor = getCursor();
//        long thumbnailId = 0;
//        String id = null;
        if(ht != null) {
            mThumbnail = ht.get(new Integer(getMediaId()));
        }
        
        if(mThumbnail == null) {
            mThumbnail = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, getMediaId(), MediaStore.Video.Thumbnails.MICRO_KIND, null);

            Log.v("VideoObject", "Can't get thumbnail from the hashtable, new bitmap is  "+ mThumbnail);
            if(mThumbnail == null) {
                mThumbnail = defaultBitmap;
            }
            if(ht != null) {
                ht.put(new Integer(getMediaId()), mThumbnail);
            }
        }

        if(mThumbnail != null) {
            mThumbnailState = PublicTools.THUMBNAIL_PREPARED;
        } else {
            mThumbnailState = PublicTools.THUMBNAIL_CORRUPTED;
        }
        return mThumbnail;
    }
    /**hejn, optimizing thumbnail list, 20101210 end*/
    // help functions
    private long makeThumbMagic() {
        Cursor c = getCursor();
        synchronized (c) {
            String path = c.getString(mContainer.INDEX_DATA);
            long lastModify = c.getLong(mContainer.INDEX_DATE_MODIFIED);
            if (lastModify == 0) {
                lastModify = mRandom.nextLong();
            }
            return (path.hashCode() + lastModify);
        }
    }
   
    public int getRow() {
        return mCursorRow;
    }
    Cursor getCursor() {
        return mContainer.getCursor();
    }
    public VideoList getContainer() {
        return mContainer;
    }
    public long fullSizeImageId() {
        return mId;
    }
    public Uri fullSizeImageUri() {
        return mContainer.contentUri(mId);
    }
    public void onRemove() {
        mContainer.mCache.remove(mId);
    }
    public int getThumbnailState() {
        return mThumbnailState;
    }
}
