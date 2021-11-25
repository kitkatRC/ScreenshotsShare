package com.mrrun.screenshotsshare;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mrrun.screenshotsshare.screenShotUtils.ScreenShotFileObserver;
import com.mrrun.screenshotsshare.screenShotUtils.ScreenShotFileObserverManager;
import com.mrrun.screenshotsshare.screenShotUtils.ScreenShotHelper;

public class AppContext extends Application {
    public static AppContext appContext;

    public static Application get() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        ScreenShotHelper.get().setScreenShotListener(new ScreenShotHelper.OnScreenShotListener() {
            @Override
            public void onShot(@Nullable String data) {
                Log.e("ScreenShotHelper", "ScreenShotHelper: $data");
            }
        });
//        ScreenShotFileObserverManager.registerScreenShotFileObserver(object ://oppo -real me--小米
//        ScreenShotFileObserver.ScreenShotLister {
//            override fun finshScreenShot(path: String?) {
//                var secondBitmap =
//                        BitmapUtil.drawableToBitmap(this@MainActivity.getDrawable(R.mipmap.ic_launcher)!!)
//                Log.e(TAG, "finshScreenShot path = $path")
//                Log.e(TAG, "secondBitmap = $secondBitmap")
////                var bitmap = BitmapUtil.concatBitmap(
////                    this@MainActivity,
////                    path,
////                    secondBitmap
////                )
////                val imageContentUri = getImageContentUri(this@MainActivity, path!!)
////                val bitmapFromUri = getBitmapFromUri(this@MainActivity, imageContentUri)
////                Log.e(TAG, "bitmap = $imageContentUri")
//                val file = File(path)
//                val fromFile = Uri.fromFile(file)
//                val function = {
//                        tv_path.text = path
//
////                    image.setImageURI(fromFile)
//                }
//                runOnUiThread(function)
//
//            }
//
//            override fun beganScreenShot(path: String?) {
//                val makeText = Toast.makeText(this@MainActivity, "正在截屏...", Toast.LENGTH_SHORT)
//                makeText
//                        .setGravity(Gravity.CENTER, 0, 0)
//                makeText.show()
//                Log.e(TAG, "beganScreenShot path = $path")
//            }
//        })
        ScreenShotFileObserverManager.INSTANCE.registerScreenShotFileObserver(new ScreenShotFileObserver.ScreenShotLister() {
            @Override
            public void finshScreenShot(@Nullable String path) {

            }

            @Override
            public void beganScreenShot(@Nullable String path) {
//                Toast makeText = Toast.makeText(getApplicationContext(), "正在截屏...", Toast.LENGTH_SHORT);
//                makeText.setGravity(Gravity.CENTER, 0, 0);
//                makeText.show();
                Log.e("ApplicationContext", "beganScreenShot: " + path);
            }
        });
    }
}
