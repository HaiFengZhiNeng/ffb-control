package com.example.ffbclient.common;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class BaseHandler<T extends BaseHandler.HandleMessage> extends Handler {

    private WeakReference<T> weakReference;

    public BaseHandler(T t) {
        weakReference = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (weakReference != null) {

            T t = weakReference.get();

            t.handleMessage(msg);
        }
    }


    public interface HandleMessage {
        void handleMessage(Message msg);
    }


}
