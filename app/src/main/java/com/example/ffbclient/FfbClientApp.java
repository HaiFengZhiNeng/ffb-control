package com.example.ffbclient;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.example.ffbclient.common.lifecycle.Foreground;
import com.example.ffbclient.dao.DBHelper;
import com.example.ffbclient.utils.AsimpleCache.UserInfoCache;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.seabreeze.log.Print;
import com.seabreeze.log.inner.ConsoleTree;
import com.seabreeze.log.inner.FileTree;
import com.seabreeze.log.inner.LogcatTree;
import com.youdao.sdk.app.YouDaoApplication;

/**
 * Created by zhangyuanyuan on 2017/9/30.
 */

public class FfbClientApp extends MultiDexApplication {


    private static FfbClientApp instance;

    private DBHelper dbHelper;

    private boolean udpConnect;

    public static FfbClientApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Foreground.init(this);

        initPrint();

        UserInfoCache.getUser(this);
        //599d6109
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=59b8fefd" + "," +
                SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
//        Setting.setShowLog(false);

        //创建应用，每个应用都会有一个Appid，绑定对应的翻译服务实例，即可使用
        YouDaoApplication.init(this, getResources().getString(R.string.app_youdao_id));

//        BaseManager.initOpenHelper(this);

        udpConnect = false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initPrint() {
        if(BuildConfig.DEBUG){
            Print.getLogConfig().configAllowLog(true)
                    .configShowBorders(false);
            Print.plant(new FileTree(this, "Log"));
            Print.plant(new ConsoleTree());
            Print.plant(new LogcatTree());
        }
    }

    /**
     * 获取数据库操作类
     *
     * @return 数据库操作类
     */

    public synchronized DBHelper getDataBase() {
        if (dbHelper == null)
            dbHelper = new DBHelper(this);
        return dbHelper;
    }

    public boolean isUdpConnect() {
        return udpConnect;
    }

    public void setUdpConnect(boolean udpConnect) {
        this.udpConnect = udpConnect;
    }
}
