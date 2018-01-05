package com.example.ffbclient.udp;

import com.example.ffbclient.common.Constants;
import com.example.ffbclient.listener.UdpServerListener;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyuanyuan on 2017/10/20.
 */

public class SocketManager {

    public final static int DEFAULT_UDPSERVER_PORT = 8890;

    /**
     * 此类的对象，用与单例模式
     */
    private static SocketManager mInstance;

    /**
     * 得到 SocketManager对象的方法
     *
     * @return SocketManager对象
     */
    public static SocketManager getInstance() {
        if (mInstance == null) {
            synchronized (SocketManager.class) {
                if (mInstance == null)
                    mInstance = new SocketManager();
            }
        }
        return mInstance;
    }

    private int mPort = DEFAULT_UDPSERVER_PORT;

    private DatagramSocket mDatagramSocket;

    private ThreadPoolExecutor executorService;

    /**
     * 标记是否成功获取ip
     */
    public boolean isGetTcpIp = false;

    /**
     * 无参数的构造函数
     */
    private SocketManager() {
        if(Constants.IP == null){
            throw new RuntimeException("ip is null");
        }
    }

    /**
     * 设置连接者ip等
     */
    public void setUdpIp() {
        isGetTcpIp = true;
    }

    /**
     * 注册一个UDP端口监听服务
     *
     * @param udpServerListener UDP端口监听回调
     */
    public void registerUdpServer(UdpServerListener udpServerListener) {
        if (mDatagramSocket == null) {
            Print.e("mDatagramSocket is null try create DatagramSocket object");
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                mDatagramSocket = new DatagramSocket(inetSocketAddress);
            } catch (SocketException e) {
                e.printStackTrace();
                mPort++;
                try {
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                    mDatagramSocket = new DatagramSocket(inetSocketAddress);
                } catch (SocketException e1) {
                    e1.printStackTrace();
                    mPort++;
                    try {
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                        mDatagramSocket = new DatagramSocket(inetSocketAddress);
                    } catch (SocketException e2) {
                        e2.printStackTrace();
                        mPort++;
                        try {
                            InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                            mDatagramSocket = new DatagramSocket(inetSocketAddress);
                        } catch (SocketException e3) {
                            e3.printStackTrace();
                            try {
                                InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                                mDatagramSocket = new DatagramSocket(inetSocketAddress);
                            } catch (SocketException e4) {
                                e4.printStackTrace();
                                mPort++;
                                try {
                                    InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                                    mDatagramSocket = new DatagramSocket(inetSocketAddress);
                                } catch (SocketException e5) {
                                    e5.printStackTrace();
                                    try {
                                        InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                                        mDatagramSocket = new DatagramSocket(inetSocketAddress);
                                    } catch (SocketException e6) {
                                        e6.printStackTrace();
                                        mPort++;
                                        try {
                                            InetSocketAddress inetSocketAddress = new InetSocketAddress(Constants.IP, mPort);
                                            mDatagramSocket = new DatagramSocket(inetSocketAddress);
                                        } catch (SocketException e7) {
                                            e7.printStackTrace();
                                            udpServerListener.onFail(e7);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mDatagramSocket == null)
            throw new RuntimeException("DatagramSocket is null");
        getExecutorService().execute(new UDPReceiveRunnable(mDatagramSocket, udpServerListener));
    }


    /**
     * 注册一个UDP端口监听服务
     *
     * @param udpServerListener UDP端口监听回调
     */
    public void registerUdpClient(UdpServerListener udpServerListener) {
        if (mDatagramSocket == null) {
            Print.e("mDatagramSocket is null try create DatagramSocket object");
            try {
                mDatagramSocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        if (mDatagramSocket == null)
            throw new RuntimeException("DatagramSocket is null");
        getExecutorService().execute(new UDPClientRunnable(mDatagramSocket, udpServerListener));
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 10, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return executorService;
    }

    /**
     * 通过UDP发送文本
     *
     * @param msg      发送的字符串
     */
    public void sendTextByUDP(String msg) {
        try {
            InetAddress host = InetAddress.getByName(Constants.CONNECT_IP);
            final DatagramPacket dpSend = new DatagramPacket(msg.getBytes(), msg.getBytes().length, host, Constants.CONNECT_PORT);

            if (mDatagramSocket == null)
                throw new RuntimeException("DatagramSocket is null");

            getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDatagramSocket.send(dpSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
