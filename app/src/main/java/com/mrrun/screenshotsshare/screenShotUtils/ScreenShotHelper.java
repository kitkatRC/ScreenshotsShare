package com.mrrun.screenshotsshare.screenShotUtils;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.mrrun.screenshotsshare.AppContext;

public class ScreenShotHelper {
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap", "snap", "截屏"
    };

    /**
     * 读取媒体数据库时需要读取的列
     */
    private static final String[] MEDIA_PROJECTIONS = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED,
    };
    /**
     * 内部存储器内容观察者
     */
    private ContentObserver mInternalObserver;
    /**
     * 外部存储器内容观察者
     */
    private ContentObserver mExternalObserver;
    private ContentResolver mResolver;
    private OnScreenShotListener listener;
    private String lastData;
    private Runnable shotCallBack = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                final String path = lastData;
                if (path != null && path.length() > 0) {
                    listener.onShot(path);
                }
            }
        }
    };

    private ScreenShotHelper() {
        // 初始化
        mInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, null);
        mExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);

        mResolver = AppContext.get().getContentResolver();
        // 添加监听
        mResolver.registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false,
                mInternalObserver
        );
        mResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                mExternalObserver
        );
    }

    private static class Instance {
        static ScreenShotHelper mInstance = new ScreenShotHelper();
    }

    public static ScreenShotHelper get() {
        return Instance.mInstance;
    }

    public void setScreenShotListener(OnScreenShotListener listener) {
        this.listener = listener;
    }

    public void removeScreenShotListener(OnScreenShotListener listener) {
        if (this.listener == listener) {
            synchronized (ScreenShotHelper.class) {
                if (this.listener == listener) {
                    this.listener = null;
                }
            }
        }
    }

    public void stopListener() {
        mResolver.unregisterContentObserver(mInternalObserver);
        mResolver.unregisterContentObserver(mExternalObserver);
    }

    private void handleMediaContentChange(Uri contentUri) {
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = mResolver.query(
                    contentUri,
                    MEDIA_PROJECTIONS,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            );
            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                return;
            }
            // 获取各列的索引
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int dateAddIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED);
            // 获取行数据
            final String data = cursor.getString(dataIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);
            long dateAdded = cursor.getLong(dateAddIndex);
            if (data.length() > 0) {
                if (TextUtils.equals(lastData, data)) {
                    //更改资源文件名也会触发，并且传递过来的是之前的截屏文件，所以只对分钟以内的有效
                    if (System.currentTimeMillis() - dateTaken < 3 * 3600) {
                        MainHandler.get().removeCallbacks(shotCallBack);
                        MainHandler.get().postDelayed(shotCallBack, 500);
                    }
                } else if (dateTaken == 0 || dateTaken == dateAdded * 1000) {
                    MainHandler.get().removeCallbacks(shotCallBack);
                    if (listener != null) {
                        listener.onShot(null);
                    }
                } else if (checkScreenShot(data)) {
                    MainHandler.get().removeCallbacks(shotCallBack);
                    lastData = data;
                    MainHandler.get().postDelayed(shotCallBack, 500);
                }
            }
        } catch (Exception e) {
            //
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 根据包含关键字判断是否是截屏
     */
    private boolean checkScreenShot(String data) {
        if (data == null || data.length() < 2) {
            return false;
        }
        data = data.toLowerCase();
        for (String keyWork : KEYWORDS) {
            if (data.contains(keyWork)) {
                return true;
            }
        }
        return false;
    }

    private class MediaContentObserver extends ContentObserver {
        private Uri mContentUri;

        MediaContentObserver(Uri contentUri, Handler handler) {
            super(handler);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (listener != null) {
                handleMediaContentChange(mContentUri);
            }
        }
    }

    public interface OnScreenShotListener {
        void onShot(@Nullable String data);
    }
}
