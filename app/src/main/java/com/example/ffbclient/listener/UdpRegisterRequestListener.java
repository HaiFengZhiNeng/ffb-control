package com.example.ffbclient.listener;

import java.net.Socket;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class UdpRegisterRequestListener {

    public void onSucess(Socket mClient){};
    public void onFail(Exception e){};
    public void onResult(String result){};
    public void onReceive(String ip, int port, String result){};


}
