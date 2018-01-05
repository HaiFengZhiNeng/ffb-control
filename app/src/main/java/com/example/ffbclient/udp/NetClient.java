package com.example.ffbclient.udp;

import android.content.Context;

import com.example.ffbclient.listener.UdpRegisterRequestListener;
import com.example.ffbclient.listener.UdpServerListener;

import java.net.DatagramPacket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class NetClient {

    private Context mContext;

    private SocketManager mSocketManager;

    private static NetClient client;

    public static NetClient getInstance(Context mContext) {
        if (client == null) {
            synchronized (NetClient.class) {
                if (client == null)
                    client = new NetClient(mContext.getApplicationContext());
            }
        }
        return client;
    }

    private NetClient(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * 监听本地的端口（Udp方式监听）
     *
     * @param listener 被连接的监听
     */
    public void registerUdpServer(final UdpRegisterRequestListener listener) {
        if (mSocketManager == null)
            mSocketManager = SocketManager.getInstance();

        mSocketManager.registerUdpServer(new UdpServerListener() {
            @Override
            public void onReceive(DatagramPacket recvPacket) {
                String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                if (listener != null)
                    listener.onReceive(recvPacket.getAddress().getHostAddress(), recvPacket.getPort(), recvStr);
            }

            @Override
            public void onFail(Exception e) {
                if (listener != null)
                    listener.onFail(e);
            }
        });
    }

    /**
     * 监听本地的端口（Udp方式监听）
     *
     * @param listener 被连接的监听
     */
    public void registerUdpClient(final UdpRegisterRequestListener listener) {
        if (mSocketManager == null)
            mSocketManager = SocketManager.getInstance();

        mSocketManager.registerUdpClient(new UdpServerListener() {
            @Override
            public void onReceive(DatagramPacket recvPacket) {
                String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                if (listener != null)
                    listener.onReceive(recvPacket.getAddress().getHostAddress(), recvPacket.getPort(), recvStr);
            }

            @Override
            public void onFail(Exception e) {
                if (listener != null)
                    listener.onFail(e);
            }
        });
    }

    public void sendTextMessageByUdp(String msg){
        if (mSocketManager == null)
            mSocketManager = SocketManager.getInstance();
        mSocketManager.sendTextByUDP(msg);
    }


}
