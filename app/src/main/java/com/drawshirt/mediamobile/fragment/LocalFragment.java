package com.drawshirt.mediamobile.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.bean.MediaItem;
import com.drawshirt.mediamobile.common.BaseFragment;
import com.drawshirt.mediamobile.local.DisplayListAdapter;
import com.drawshirt.mediamobile.local.DisplayListAdapter.ViewHolder;
import com.drawshirt.mediamobile.local.ImageManager;
import com.drawshirt.mediamobile.local.VideoList;
import com.drawshirt.mediamobile.local.VideoObject;
import com.drawshirt.mediamobile.utils.PublicTools;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.view.MainTitlebar;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import butterknife.Bind;


/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class LocalFragment extends BaseFragment {

    @Bind(R.id.main_title)
    MainTitlebar mMainTitle;
    @Bind(R.id.channel_video_new)
    TextView mChannelVideoNew;
    @Bind(R.id.channel_video_hot)
    TextView mChannelVideoHot;
    @Bind(R.id.nodata_img)
    ImageView mNodataImg;
    @Bind(R.id.playListView)
    TextView mPlayListView;
    @Bind(R.id.havanodata_layout)
    LinearLayout mHavanodataLayout;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.icon_nocard)
    ImageView mIconNocard;
    @Bind(R.id.layout)
    LinearLayout mLayout;

    private static final String TAG = "LocalFragment";
    private static final int APPSTATE_FIRST_START = 0;
    private static final int APPSTATE_INITIALIZED = 1;
    private static final int APPSTATE_FINISHED = 2;

    private static final int PROCESS_DIALOG_START_KEY = 0;
    private static final int PROCESS_MEDIA_SCANNING_KEY = 1;

    private static final String CALLER_VIDEOPLAYER = "VIDEOPLAYER";
    private static final String CALLER_MMS = "MMS";
    private static final String CALLER_CAMERA = "CAMERA";
    private static final String CALLER_WATCHMSG = "WATCHMSG";

    private static final int LIST_STATE_IDLE = 0;
    private static final int LIST_STATE_BUSY = 1;
    private static final int LIST_STATE_REQUEST_REFRESH = 2;

    /**
     * change camera video sql path 2010.11.03 add by W.Y
     */
    // private static final long CAMERAFOLDER_USERDATA_BUCKET_ID = 1712717414;
    private static final String CAMERAFOLDER_SDCARD_PATH = "/mnt/sdcard/Camera/Videos";

    private int mAppState;
    private boolean mRequest_stop_thread;
    private boolean mIsVideoPlaying;
    private boolean mFinishScanning;
    private int mCurrentListState;
    private String mCaller;
    /**
     * hejn, optimizing thumbnail list, 20101210 begin
     */
    private Hashtable<Integer, Bitmap> mThumbHash = new Hashtable<Integer, Bitmap>();
    private Bitmap mDefaultBitmap;

    private static Display mDisplay;
    private VideoList mAllImages;

    private AlertDialog mListOperationDialog;
    private AlertDialog mCurrrentActiveDialog;
    private Thread mLoadingThread;

    public class ListLastPosition {
        public int normalVideo = 0;
        public int cameraVideo = 0;
    }

    private ListLastPosition listLastPosition = new ListLastPosition();

    public class VideoWorkItem {
        public VideoObject object;
        public long dataModified = 0;
        public String datapath;
        public String name;
        public String duration;
        public String size;
        public boolean isHighlight = false;
        public int lastPos = 0;
    }

    private List<VideoWorkItem> mAllVideoList = new ArrayList<VideoWorkItem>();
    private List<VideoWorkItem> mNormalVideoList = new ArrayList<VideoWorkItem>();
    private List<VideoWorkItem> mCameraList = new ArrayList<VideoWorkItem>();
    private List<VideoWorkItem> mActiveList;
    private ArrayList<MediaItem> mCurrentPlayList;
    private VideoWorkItem mLastPlayedItem;

    private enum ListEnum {
        NormalVideo, CameraVideo
    }

    private DisplayListAdapter mListAdapter;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    public void initTitle() {
        mMainTitle.show("本地影音");
    }

    @Override
    public void initData(String content) {
        initialize();
        if (isSDcardEjected()) {
            // showDialog(PROCESS_DIALOG_START_KEY);
            mLoadingThread = createLoadingThread();
            mLoadingThread.start();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_local;
    }


    private void initialize() {
        Log.v(TAG, "VideoPlayerActivity  initialize");

        mAppState = APPSTATE_FIRST_START;
        mCaller = CALLER_VIDEOPLAYER;
        mIsVideoPlaying = false;
        mFinishScanning = false;

        //        mList.setOnItemClickListener(this);
        //        mList.setOnItemLongClickListener(this);
        //        mList.setOnScrollListener(this);

        // mManagePreference.initialize(VideoPlayerActivity.this);
        mDisplay = getActivity().getWindow().getWindowManager().getDefaultDisplay();

        String caller = getActivity().getIntent().getStringExtra("Caller");
        if (caller != null) {
            mCaller = caller;
        }

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        iFilter.addDataScheme("file");
        getActivity().registerReceiver(mBroadcastReceiver, iFilter);
        /** hejn, optimizing thumbnail list, 20101210 begin */
        mThumbHash.clear();
        mDefaultBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.bg_default);
        /** hejn, optimizing thumbnail list, 20101210 end */
        // createTab();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        boolean mountState = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAppState == APPSTATE_FINISHED) {
                return;
            }
            String action = intent.getAction();
            Log.v(TAG, "BroadcastReceiver action : " + action);
            // action.equals(Intent.ACTION_MEDIA_MOUNTED)

            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                if (!mountState) {
                    Log.v(TAG, "BroadcastReceiver sdcard ejected/mounted");
                    if (mAppState == APPSTATE_INITIALIZED) {
                        uninitialize();
                    }
                    mountState = true;
                }
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                Log.v(TAG, "BroadcastReceiver start scan media");
                // if (mountState) {
                // showDialog(PROCESS_DIALOG_SCAN_KEY);
                // }
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                if (isSDcardEjected() && mAppState != APPSTATE_FINISHED) {
                    Log.v(TAG, "BroadcastReceiver stop scan media");
                    if (mAppState == APPSTATE_FIRST_START) {
                        getActivity().showDialog(PROCESS_DIALOG_START_KEY);
                        createLoadingThread().start();
                    } else {
                        getActivity().removeDialog(PROCESS_MEDIA_SCANNING_KEY);
                        refreshLastest(true);
                    }
                    mountState = false;
                    mFinishScanning = true;
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.v(TAG, "LoadDataThread  handleMessage APPSTATE_FIRST_START");
            mAppState = APPSTATE_INITIALIZED;

            if (mCaller.equals(CALLER_CAMERA)) {
                mActiveList = mCameraList;
                // setTitle(getString(R.string.cameravideo_list));
                // tabHost.setCurrentTab(TAB_INDEX_CAMERA_VIDEO);
            } else {
                mActiveList = mNormalVideoList;
                // setTitle(getString(R.string.allvideo_list));
                // tabHost.setCurrentTab(TAB_INDEX_NORMAL_VIDEO);
                refreshLastest(false);
            }
            getActivity().removeDialog(PROCESS_DIALOG_START_KEY);
            checkListScanning();
        }
    };

    public void checkListScanning() {
        if (PublicTools.isMediaScannerScanning(getActivity().getContentResolver())
                && !mFinishScanning) {
            getActivity().showDialog(PROCESS_MEDIA_SCANNING_KEY);
        }
    }

    private Thread createLoadingThread() {

        return new Thread(new Runnable() {
            private static final int STATE_STOP = 0;
            private static final int STATE_IDLE = 1;
            private static final int STATE_TERMINATE = 2;
            private int workStatus;
            private int currentPos;
            private int maxPos;
            private Object[] items;

            public void run() {
                Log.v(TAG, "LoadDataThread  run");
                mRequest_stop_thread = false;

                getVideoData();
                mHandler.sendMessage(mHandler.obtainMessage());

                init();
                loadThumbnails();
            }

            private void init() {
                mCurrentListState = LIST_STATE_IDLE;
                workStatus = STATE_STOP;

                items = mAllVideoList.toArray();
                maxPos = items.length;
                currentPos = 0;

                Log.v("LoadDataThread", "maxPos : " + maxPos);
            }

            private void loadThumbnails() {
                while (workStatus != STATE_TERMINATE) {
                    switch (workStatus) {
                        case STATE_STOP:
                            workStatus = onStop();
                            break;
                        case STATE_IDLE:
                            workStatus = onIdle();
                            break;
                        default:
                            break;
                    }
                }
                Log.v("LoadDataThread", "STATE_TERMINATE!!!");
            }

            private int onIdle() {
                Log.v(TAG, "createLoadingThread : onIdle");

                while (true) {
                    if (mRequest_stop_thread || (currentPos == maxPos)) {
                        return STATE_TERMINATE;
                    }
                    if (mCurrentListState == LIST_STATE_REQUEST_REFRESH) {
                        mCurrentListState = LIST_STATE_IDLE;
                        return STATE_STOP;
                    }

                    PublicTools.sleep(PublicTools.LONG_INTERVAL);
                }
            }

            private int onStop() {
                if (mRequest_stop_thread) {
                    return STATE_TERMINATE;
                }
                if (mActiveList == null || mList == null) {
                    PublicTools.sleep(PublicTools.SHORT_INTERVAL);
                    return STATE_STOP;
                }
                if (mActiveList.isEmpty()) {
                    return STATE_IDLE;
                }
                if (-1 == mList.getLastVisiblePosition()) {
                    PublicTools.sleep(PublicTools.SHORT_INTERVAL);
                    return STATE_STOP;
                }

                Log.v(TAG, "createLoadingThread : onStop");

                Object[] viewHolders = mListAdapter.getHolderObjects();
                int count = viewHolders.length;
                for (int i = 0; i < count; i++) {
                    if (mCurrentListState == LIST_STATE_BUSY) {
                        return STATE_IDLE;
                    } else if (mCurrentListState == LIST_STATE_REQUEST_REFRESH) {
                        mCurrentListState = LIST_STATE_IDLE;
                        return STATE_STOP;
                    }
                    RefreshThumbnail((ViewHolder) viewHolders[i]);
                    PublicTools.sleep(PublicTools.MINI_INTERVAL);
                }

                PublicTools.sleep(PublicTools.MIDDLE_INTERVAL);

                if (count < mListAdapter.getHolderObjects().length) {
                    return STATE_STOP;
                }
                if (mCurrentListState == LIST_STATE_IDLE) {
                    return STATE_IDLE;
                } else {
                    mCurrentListState = LIST_STATE_IDLE;
                    return STATE_STOP;
                }
            }

            private void RefreshThumbnail(ViewHolder holder) {
                if (holder == null) {
                    return;
                }
                if (!holder.mUseDefault
                        || holder.mItem == null
                        || PublicTools.THUMBNAIL_CORRUPTED == holder.mItem.object
                        .getThumbnailState()) {
                    return;
                }
                /** hejn, optimizing thumbnail list, 20101210 begin */
                holder.mBitmap = holder.mItem.object.miniThumbBitmap(false,
                        mThumbHash, mDefaultBitmap);
                /** hejn, optimizing thumbnail list, 20101210 end */
                if (PublicTools.THUMBNAIL_PREPARED == holder.mItem.object
                        .getThumbnailState()) {
                    mListAdapter.sendRefreshMessage(holder);
                    holder.mUseDefault = false;
                } else {
                    holder.mUseDefault = true;
                }
            }
        });
    }

    private boolean isSDcardEjected() {
        boolean isSdcard_ok = false;
        String status = Environment.getExternalStorageState();
        Log.v(TAG, "status : " + status
                + status.equals(Environment.MEDIA_REMOVED));

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            isSdcard_ok = true;
            return true;
        }


        if (!isSdcard_ok) {
            if (mIconNocard != null)
                mIconNocard.setVisibility(View.VISIBLE);
            if (status.equals(Environment.MEDIA_UNMOUNTED)) {
                UIUtils.toast(getString(R.string.sd_unmounted), false);

            } else if (status.equals(Environment.MEDIA_SHARED)) {
                UIUtils.toast(getString(R.string.sd_shared), false);

            } else if (status.equals(Environment.MEDIA_REMOVED)) {
                UIUtils.toast(getString(R.string.sd_removed), false);

            } else {
                UIUtils.toast(getString(R.string.sd_noinsert), false);

            }
        } else {
            mIconNocard.setVisibility(View.GONE);
        }

        return isSdcard_ok;
    }

    // help functions
    private void uninitialize() {
        Log.v(TAG, "uninitialize");
        UIUtils.toast(getString(R.string.sd_shared), false);

        if (mAllImages != null) {
            mAllImages.onDestory();
        }
        if (mCurrrentActiveDialog != null) {
            if (mCurrrentActiveDialog.isShowing()) {
                mCurrrentActiveDialog.dismiss();
            }
        }
        listLastPosition.cameraVideo = 0;
        listLastPosition.normalVideo = 0;
        mAllImages = null;
        mAllVideoList.clear();
        mNormalVideoList.clear();
        mCameraList.clear();
        if (mCurrentPlayList != null)
            mCurrentPlayList.clear();
        if (mLastPlayedItem != null) {
            mLastPlayedItem.object = null;
            mLastPlayedItem.isHighlight = false;
            mLastPlayedItem.lastPos = 0;
        }
        refreshLastest(false);
    }

    public void refreshLastest(boolean isRefreshData) {
        if (isRefreshData) {
            getVideoData();
        }
        if (mActiveList == mNormalVideoList) {
            refreshList(ListEnum.NormalVideo);
        } else if (mActiveList == mCameraList) {
            refreshList(ListEnum.CameraVideo);
        }
        if (isRefreshData) {
            //modify by yangguangfu
            //			Toast.makeText(this, getString(R.string.list_refresh), 1500).show();
        }
    }

    private void refreshList(ListEnum list) {
        int lastPos = mList.getFirstVisiblePosition();

        if (mActiveList == mNormalVideoList) {
            listLastPosition.normalVideo = lastPos;
        } else if (mActiveList == mCameraList) {
            listLastPosition.cameraVideo = lastPos;
        }
        if (list.equals(ListEnum.NormalVideo)) {
            mActiveList = mNormalVideoList;
            lastPos = listLastPosition.normalVideo;
        } else if (list.equals(ListEnum.CameraVideo)) {
            mActiveList = mCameraList;
            lastPos = listLastPosition.cameraVideo;
        }

        if (mListAdapter != null) {
            mListAdapter.destory();
        }

        mListAdapter = new DisplayListAdapter(getActivity());
        /** hejn, optimizing thumbnail list, 20101210 begin */
        mListAdapter.setThumbHashtable(mThumbHash, mDefaultBitmap);
        /** hejn, optimizing thumbnail list, 20101210 end */


        mListAdapter.setListItems(mActiveList);

        mList.setAdapter(mListAdapter);
        mList.setSelection(lastPos);

        mCurrentListState = LIST_STATE_REQUEST_REFRESH;
    }

    public void getVideoData() {
        Log.v(TAG, "getVideoData()");

        mAllVideoList.clear();
        mNormalVideoList.clear();
        mCameraList.clear();

        mAllImages = allImages(); // Video List

        if (mAllImages != null) {
            int totalNum = mAllImages.getCount();
            for (int i = 0; i < totalNum; i++) {
                VideoObject image = mAllImages.getImageAt(i);

                VideoWorkItem videoDisplayObj = new VideoWorkItem();
                videoDisplayObj.object = image;
                videoDisplayObj.name = image.getTitle();
                videoDisplayObj.duration = getString(R.string.duration_tag)
                        + " " + image.getDuration();
                videoDisplayObj.size = image.getSize();
                videoDisplayObj.datapath = image.getMediapath();

                long bucketId = image.getBucketId();

                if (PublicTools.getBucketId(CAMERAFOLDER_SDCARD_PATH) == bucketId) {
                    videoDisplayObj.dataModified = image.getDateModified();
                    mCameraList.add(videoDisplayObj);
                } else {
                    mNormalVideoList.add(videoDisplayObj);
                }


                mAllVideoList.add(videoDisplayObj);
            }
            mRefreshHandler.sendEmptyMessage(REFRESH);
            // sortList(mNormalVideoList, SORT_LIST_BY_TITLE);
            // sortList(mCameraList, SORT_LIST_BY_DATE);

            Log.v(TAG, "LoadDataThread  totalNum : " + totalNum);
        }
    }

    private VideoList allImages() {
        mAllImages = null;
        return ImageManager.instance().allImages(getActivity(), getActivity().getContentResolver(),
                ImageManager.INCLUDE_VIDEOS, ImageManager.SORT_ASCENDING);
    }

    private static final int REFRESH = 1;
    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REFRESH) {
                Log.d(TAG, "handleMessage()===============receive REFRESH message+++++++++++");
                // if (mSongsAdapter != null) {
                // mSongsAdapter.notifyDataSetChanged();
                // }
                // refresh();
                showEmptyView();
            }
        }
    };

    public void showEmptyView() {
        if (mAllImages != null) {
            int totalNum = mAllImages.getCount();
            Log.d(TAG, "totalNum===" + totalNum);
            if (totalNum == 0) {
                setEmptyView(true);
            } else {
                setEmptyView(false);
            }
        }

    }

    public String getEmptyString() {
        return getResources().getString(R.string.no_vide_file);
    }

    public void setEmptyView(boolean show) {
        if (show) {
            mPlayListView.setText(getEmptyString());
            mPlayListView.setVisibility(View.VISIBLE);
        } else {
            mPlayListView.setText(getEmptyString());
            mPlayListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "call onDestroy");

        mRequest_stop_thread = true;
        mAppState = APPSTATE_FINISHED;

        if (mListAdapter != null) {
            mListAdapter.destory();
        }
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        if (mAllImages != null) {
            mAllImages.onDestory();
        }
        /** hejn, optimizing thumbnail list, 20101210 begin */
        Enumeration<Bitmap> e = mThumbHash.elements();
        while (e.hasMoreElements()) {
            Bitmap tmp = e.nextElement();
            if (!tmp.isRecycled()) {
                tmp.recycle();
            }
        }
        mThumbHash.clear();
        /** hejn, optimizing thumbnail list, 20101210 end */
        if (mLoadingThread != null) {
            // mLoadingThread.stop();
        }
        super.onDestroy();
    }
}
