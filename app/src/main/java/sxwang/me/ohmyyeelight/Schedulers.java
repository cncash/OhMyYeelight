package sxwang.me.ohmyyeelight;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Shaoxing on 03/05/2017.
 */

public class Schedulers {

    public static Schedulers getInstance() {
        return InstanceHolder.sInstance;
    }

    private static class InstanceHolder {
        static Schedulers sInstance = new Schedulers();
    }

    private Handler mHandler;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    private Schedulers() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void runOnUiThread(Runnable command) {
        mHandler.post(command);
    }

    public void runOnIoThread(Runnable command) {
        mExecutorService.submit(command);
    }

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

}
