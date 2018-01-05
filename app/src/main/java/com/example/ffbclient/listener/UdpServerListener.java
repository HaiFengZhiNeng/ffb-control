package com.example.ffbclient.listener;

import java.net.DatagramPacket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public interface UdpServerListener {

    void onReceive(DatagramPacket receivePacket);

    void onFail(Exception e);
}
