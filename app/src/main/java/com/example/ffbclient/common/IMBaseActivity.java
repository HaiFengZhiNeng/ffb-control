package com.example.ffbclient.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.example.ffbclient.activity.SplashActivity;
import com.example.ffbclient.im.event.MessageEvent;
import com.example.ffbclient.im.init.TlsBusiness;
import com.example.ffbclient.model.UserInfo;
import com.example.ffbclient.utils.AsimpleCache.UserInfoCache;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public abstract class IMBaseActivity extends BaseActivity {


    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier();
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(Constants.EXIT_APP));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
    }



    public class ExitBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.EXIT_APP)) {
                //在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
                onReceiveExitMsg();
            }
        }
    }

    private void onReceiveExitMsg() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(IMBaseActivity.this, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText("您的帐号已在其它地方登陆");
        pDialog.setCancelable(false);
        pDialog.setCancelText("退出").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
                LocalBroadcastManager.getInstance(IMBaseActivity.this).sendBroadcast(new Intent(Constants.NET_LOONGGG_EXITAPP));
            }
        });
        pDialog.setConfirmText("重新登陆").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
                logout();
            }
        });
        pDialog.show();
    }


    public void logout(){

        TlsBusiness.logout(UserInfo.getInstance().getIdentifier());
        UserInfoCache.clearCache(this);
        UserInfo.getInstance().setIdentifier(null);
        MessageEvent.getInstance().clear();
//        FriendshipInfo.getInstance().clear();
//        GroupInfo.getInstance().clear();
        Intent intent = new Intent(IMBaseActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 设置全屏
     */
    public void fullScreen() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }



}
