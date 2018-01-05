package com.example.ffbclient.udp;

import com.example.ffbclient.listener.UdpServerListener;
import com.seabreeze.log.Print;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UDPClientRunnable implements Runnable {

    private DatagramSocket mServer;
    private UdpServerListener mUdpServerListener;

    private boolean udpLife = true;     //udp生命线程

    private DatagramPacket dpRcv;
    private byte[] msgRcv = new byte[1024];

    public UDPClientRunnable(DatagramSocket datagramSocket, UdpServerListener udpServerListener) {
        this.mServer = datagramSocket;
        this.mUdpServerListener = udpServerListener;
    }


    @Override
    public void run() {
        try {

            dpRcv = new DatagramPacket(msgRcv, msgRcv.length);

            while (udpLife) {
                Print.e("SocketInfo" + "UDP监听中");
                mServer.receive(dpRcv);

                if (mUdpServerListener != null) {
                    mUdpServerListener.onReceive(dpRcv);
                }
            }

        } catch (Exception e) {
            Print.e("RecveviceThread start fail");
            e.printStackTrace();
            if (mUdpServerListener != null) {
                mUdpServerListener.onFail(e);
            }
            mServer.close();
        }
        Print.e("Thread.interrupted");
    }
}
