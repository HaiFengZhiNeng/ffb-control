package com.example.ffbclient.listener;

import com.example.ffbclient.udp.SocketManager;
import com.seabreeze.log.Print;

import static android.content.ContentValues.TAG;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UdpReceiver extends UdpRegisterRequestListener {

    private OnListenerUDPServer onListenerUDPServer;

    public UdpReceiver(OnListenerUDPServer onListenerUDPServer) {
        this.onListenerUDPServer = onListenerUDPServer;
    }


    @Override
    public void onFail(Exception e) {
        super.onFail(e);
        e.printStackTrace();
    }

    @Override
    public void onReceive(String ip, int port, String result) {
        super.onReceive(ip, port, result);
        if (!SocketManager.getInstance().isGetTcpIp) {
                Print.e(TAG, "通过UDP获取到的ip--->" + ip + "   port-->" + port);
                SocketManager.getInstance().setUdpIp();
                if (onListenerUDPServer != null)
                    onListenerUDPServer.acquireIp(true);
        } else {
            if (onListenerUDPServer != null)
                onListenerUDPServer.receiver(result);
        }
    }

}
