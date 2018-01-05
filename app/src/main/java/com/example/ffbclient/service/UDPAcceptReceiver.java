package com.example.ffbclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seabreeze.log.Print;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UDPAcceptReceiver extends BroadcastReceiver {

    private UDPAcceptInterface mUdpAcceptInterface;

    public UDPAcceptReceiver(UDPAcceptInterface udpAcceptInterface) {
        this.mUdpAcceptInterface = udpAcceptInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");
        if (content != null) {
            Print.e( "服务发过来的数据 :" + content);
            if(content != null){
                mUdpAcceptInterface.UDPAcceptMessage(content);
            }
        }
    }


    public interface UDPAcceptInterface {
        void UDPAcceptMessage(String content);
    }

}
