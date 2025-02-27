package com.mrrun.screenshotsshare.screenShotUtils;

import android.os.Handler;
import android.os.Looper;

public class MainHandler extends Handler {
    private static volatile MainHandler instance;

    public static MainHandler get() {
        if (null == instance) {
            synchronized (MainHandler.class) {
                if (null == instance) {
                    instance = new MainHandler();
                }
            }
        }
        return instance;
    }

    private MainHandler() {
        super(Looper.getMainLooper());
    }
}
