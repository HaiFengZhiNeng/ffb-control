package com.example.ffbclient.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.example.ffbclient.R;
import com.example.ffbclient.common.BaseActivity;
import com.example.ffbclient.common.BaseHandler;
import com.example.ffbclient.common.Constants;
import com.example.ffbclient.im.MyTLSService;
import com.example.ffbclient.im.PushUtil;
import com.example.ffbclient.im.event.FriendshipEvent;
import com.example.ffbclient.im.event.GroupEvent;
import com.example.ffbclient.im.event.MessageEvent;
import com.example.ffbclient.im.event.RefreshEvent;
import com.example.ffbclient.im.init.InitBusiness;
import com.example.ffbclient.im.init.LoginBusiness;
import com.example.ffbclient.im.init.TlsBusiness;
import com.example.ffbclient.model.UserInfo;
import com.example.ffbclient.presenter.SplashPresenter;
import com.example.ffbclient.presenter.ipresenter.SplashView;
import com.example.ffbclient.utils.PermissionsChecker;
import com.example.ffbclient.utils.VersonUtil;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zhangyuanyuan on 2017/9/25.
 */

public class SplashActivity extends BaseActivity implements SplashView, BaseHandler.HandleMessage, TIMCallBack {

    private SplashPresenter presenter;
    private PermissionsChecker mChecker;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //权限
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    //登陆
    public static final int LOGIN_RESULT_CODE = 570;
    public static final int LOGIN_RESULTCODE = 571;
    public static final int BACK_RESULTCODE = 572;
    //handler
    private static final int REFRESH_COMPLETE = 0X153;

    //标志位确定SDK是否初始化，避免客户SDK未初始化的情况，实现可重入的init操作
    private static boolean isSDKInit = false;

    private Handler handler = new BaseHandler<>(SplashActivity.this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        TextView mainTitle = (TextView) findViewById(R.id.mainTitle);
        TextView subTitle = (TextView) findViewById(R.id.subTitle);
        TextView versionNum = (TextView) findViewById(R.id.versionNum);
        ImageView ivSplash = (ImageView) findViewById(R.id.iv_splash);

        mChecker = new PermissionsChecker(this);
        presenter = new SplashPresenter(this);

        Glide.with(this)
                .load(R.mipmap.splash_bg)
                .skipMemoryCache(true)
                .animate(animationObject)
                .into(ivSplash);
//        mainTitle.setText(getString(R.string.app_name));
//        subTitle.setText("");
//        versionNum.setText("v " + VersonUtil.getVersionName(this));

        @SuppressLint("WrongConstant") WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Constants.displayWidth = wm.getDefaultDisplay().getWidth();
        Constants.displayHeight = wm.getDefaultDisplay().getHeight();

    }

    ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
            view.setAlpha(0f);

            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeAnim.setDuration(1500);
            fadeAnim.start();
        }
    };

    @Override
    protected void initData() {
        if (mChecker.lacksPermissions(PERMISSIONS)) {

            if (mChecker.lacksPermissions(PERMISSIONS)) {
                //请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

            } else {
                initIM(); // 全部权限都已获取
            }
        } else {
            initIM();
        }
    }

    @Override
    protected void setListener() {

    }

    private void initIM() {
        init();

        presenter = new SplashPresenter(this);
        presenter.start();
    }

    private void init() {
        if (isSDKInit)
            return;
        //初始化IMSDK
        InitBusiness.start(getApplicationContext());
        //初始化TLS
        TlsBusiness.init(getApplicationContext());

        ILiveSDK.getInstance().initSdk(getApplicationContext(), Constants.IMSDK_APPID, Constants.IMSDK_ACCOUNT_TYPE);

        isSDKInit = true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            initIM();
        } else {

            showMissingPermissionDialog();
        }
    }

     //含有全部的权限

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RESULT_CODE) {
            if (resultCode == LOGIN_RESULTCODE) {//判断登陆是否成功
                if (0 == MyTLSService.getInstance().getLastErrno()) {
                    navToHome();
                } else if (resultCode == RESULT_CANCELED) {
                    handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
                }
            } else if (resultCode == BACK_RESULTCODE) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        }
    }


    @Override
    public void navToHome() {
//        TIMUserConfig userConfig = TIMUserConfigSetting.setTIMUserConfig(this);
//        TIMManager.getInstance().setUserConfig(userConfig);
        RefreshEvent.getInstance().init();
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        RefreshEvent.getInstance().init();

        LoginBusiness.loginIm(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserSig(), this);
    }


    @Override
    public void navToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);
    }

    @Override
    public boolean isUserLogin() {
        String id = UserInfo.getInstance().getIdentifier();

        boolean isNeed = MyTLSService.getInstance().needLogin(id);

        boolean islogin = id != null && (!isNeed);
        return islogin;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case REFRESH_COMPLETE:
                finish();
                break;
        }
    }


    @Override
    public void onError(int i, String s) {
        Print.e("login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                showKickOutDialog(this);
                break;
            case 6200:
                Print.e("登陆失败， 当前没有网络");
                navToLogin();
                break;
            default:
                Print.e("登陆失败， 请稍后重试");
                navToLogin();
                break;
        }
    }

    @Override
    public void onSuccess() {
        loginSDK();

    }

    private void loginSDK(){
        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
            @Override
            public void onForceOffline(int error, String message) {
                Print.e("onForceOffline : " + error + " message : " + message);
            }
        });
        ILiveLoginManager.getInstance().iLiveLogin(UserInfo.getInstance().getIdentifier(), UserInfo.getInstance().getUserSig(),
                new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Print.e("onSuccess");
                //初始化程序后台后消息推送
                PushUtil.getInstance();
                //初始化消息监听
                MessageEvent.getInstance();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Print.e("login failed:" + module+"|"+errCode+"|"+errMsg);
            }
        });
    }


    /**
     * 显示被踢下线通知
     *
     * @param context activity
     */
    private void showKickOutDialog(final Context context) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        pDialog.setTitleText("您的帐号已在其它地方登陆");
        pDialog.setCancelable(false);
        pDialog.setCancelText("退出").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
                LoginBusiness.logout(new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        if (SplashActivity.this != null){
                            Print.e("退出登录失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onSuccess() {
                        sendBroadcast(new Intent(Constants.NET_LOONGGG_EXITAPP));
                    }
                });
            }
        });
        pDialog.setConfirmText("重新登陆").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pDialog.dismiss();
                navToHome();
            }
        });
        pDialog.show();
    }


}
