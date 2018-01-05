package com.example.ffbclient.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.ffbclient.common.Constants;
import com.example.ffbclient.listener.OnListenerUDPServer;
import com.example.ffbclient.listener.UdpReceiver;
import com.example.ffbclient.udp.NetClient;
import com.seabreeze.log.Print;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UdpService extends Service implements OnListenerUDPServer {

    private LocalBroadcastManager mLbmManager;

    private NetClient netClient;

    /**
     * 接受发送的广播
     */
    private UdpSendReceiver mUdpSendReceiver;

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLbmManager = LocalBroadcastManager.getInstance(this);

        netClient = NetClient.getInstance(this);

        registerUdp();

        mUdpSendReceiver = new UdpSendReceiver();
        IntentFilter filter = new IntentFilter(Constants.UDP_SEND_ACTION);
        mLbmManager.registerReceiver(mUdpSendReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpSendReceiver);
    }


    private void registerUdp() {
        netClient.registerUdpClient(new UdpReceiver(this));
    }



    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示toast
     *
     * @param resStr
     * @return Toast对象，便于控制toast的显示与关闭
     */
    public void showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(UdpService.this, resStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    private void sendLocal(String content){
        Intent intent = new Intent(Constants.UDP_ACCEPT_ACTION);
        intent.putExtra("content", content);
        mLbmManager.sendBroadcast(intent);
    }

    @Override
    public void receiver(String receiver) {
        sendLocal(receiver);
    }

    @Override
    public void acquireIp(boolean isAcquire) {
        Print.e(isAcquire);
    }

    private class UdpSendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String information = intent.getStringExtra("controlSend");
            if(information != null){
                netClient.sendTextMessageByUdp(information);
            }
        }
    }

}
