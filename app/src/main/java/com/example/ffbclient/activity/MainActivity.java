package com.example.ffbclient.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ffbclient.FfbClientApp;
import com.example.ffbclient.R;
import com.example.ffbclient.common.Constants;
import com.example.ffbclient.common.IMBaseActivity;
import com.example.ffbclient.common.RobotType;
import com.example.ffbclient.common.UserManage;
import com.example.ffbclient.model.RobotBean;
import com.example.ffbclient.presenter.ChatPresenter;
import com.example.ffbclient.presenter.MainPresenter;
import com.example.ffbclient.presenter.ipresenter.IChatPresenter;
import com.example.ffbclient.presenter.ipresenter.IMainPresenter;
import com.example.ffbclient.service.UDPAcceptReceiver;
import com.example.ffbclient.service.UdpService;
import com.example.ffbclient.ui.AnimationView;
import com.example.ffbclient.ui.NavController;
import com.example.ffbclient.utils.BitmapUtils;
import com.example.ffbclient.utils.FileUtil;
import com.example.ffbclient.utils.GsonUtil;
import com.example.ffbclient.utils.PhoneUtil;
import com.example.ffbclient.utils.PreferencesUtils;
import com.seabreeze.log.Print;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by zhangyuanyuan on 2017/9/30.
 */

public class MainActivity extends IMBaseActivity implements IMainPresenter.IMainView, ILVCallListener, ILVIncomingListener,
        ILVCallNotificationListener, IChatPresenter.IChatView, UDPAcceptReceiver.UDPAcceptInterface, ILVBCallMemberListener {


    @BindView(R.id.nav_control)
    NavController navControl;
    @BindView(R.id.voice_animation)
    AnimationView mVoiceView;
    @BindView(R.id.voice_animation_bg)
    LinearLayout mAnimationLayout;
    @BindView(R.id.link_imageView)
    ImageView mLink;
    @BindView(R.id.relink_imageView)
    ImageView mRrelink;
    @BindView(R.id.openSurface_imageView)
    ImageView mOpenSurface;
    @BindView(R.id.localView_imageView)
    ImageView mLocalView;
    @BindView(R.id.connect_state)
    TextView mConnetState;
    @BindView(R.id.av_root_view)
    AVRootView avRootView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.glView_frameLayout)
    FrameLayout mGlView_frameLayout;
    @BindView(R.id.chronometer)
    Chronometer mChronometer;
    @BindView(R.id.btn_cancel_video)
    Button mCancelVideo;
    @BindView(R.id.ll_release)
    LinearLayout mReleaseLayout;
    @BindView(R.id.video_btn_accept)
    Button mAcceptVideo;
    @BindView(R.id.video_btn_relese)
    Button mReleaseVideo;
    @BindView(R.id.ll_begin)
    LinearLayout mAcceptLayout;
    @BindView(R.id.sport_imageView)
    ImageView mSport;
    @BindView(R.id.voice_imageView)
    ImageView mVoice;
    @BindView(R.id.setting_imageView)
    ImageView mSetting;
    @BindView(R.id.like_imageView)
    ImageView mLike;
    @BindView(R.id.repeat_imageView)
    ImageView mRepeat;
    @BindView(R.id.control_relative)
    RelativeLayout mControlRelative;
    @BindView(R.id.controlSetting_imageView)
    ImageView mControlSetting;
    @BindView(R.id.change_text_voice)
    ImageView mChangeTxtOrVoice;
    @BindView(R.id.voice_btn)
    Button mAsrBtn;
    @BindView(R.id.edit_text)
    EditText mEditText;
    @BindView(R.id.send_text)
    TextView mSendEdit;
    @BindView(R.id.voicePeople_imageview)
    ImageView mVoicePeople;
    @BindView(R.id.hideInput_iv)
    ImageView mHideInput;
    @BindView(R.id.edit_text_layout)
    LinearLayout mEditLayout;
    @BindView(R.id.edit_layout)
    RelativeLayout mEdit;
    @BindView(R.id.message_relative)
    RelativeLayout mMessage;
    @BindView(R.id.btn_switch_chat_mode)
    Button btnSWithChatMode;

    private MainPresenter mPresenter;
    private ChatPresenter mChatPresenter;

    private LocalBroadcastManager mLbmManager;
    private boolean isAccept;
    //udp
    private UDPAcceptReceiver mUdpAcceptReceiver;

    private SweetAlertDialog mDialog;

    private boolean quit = false; //设置退出标识

    public static List<String> localVoices;

    @Override
    protected void onResume() {
        ILVCallManager.getInstance().onResume();
        super.onResume();
        isAccept = true;
        mUdpAcceptReceiver = new UDPAcceptReceiver(this);
        IntentFilter intentFilter = new IntentFilter(Constants.UDP_ACCEPT_ACTION);
        mLbmManager.registerReceiver(mUdpAcceptReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        ILVCallManager.getInstance().onPause();
        super.onPause();
        isAccept = false;
    }

    @Override
    protected void onDestroy() {
        endCall();
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onDestory();
        stopService(new Intent(this, UdpService.class));
        mChatPresenter.finish();
        super.onDestroy();
        mLbmManager.unregisterReceiver(mUdpAcceptReceiver);
    }

    private void endCall() {
        if(mCallId != -1) {
            ILVCallManager.getInstance().endCall(mCallId);
            mCallId = -1;
        }
    }

    @Override
    protected void setBeforeLayout() {
        fullScreen();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mPresenter = new MainPresenter(this);
        mChatPresenter = new ChatPresenter(this, TIMConversationType.C2C, UserManage.getInstance().getRobotName());
        mChatPresenter.start();

        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setNotificationListener(this)
                .setAutoBusy(true));
        ILVCallManager.getInstance().addIncomingListener(this);//添加来电回调

        ILVCallManager.getInstance().addCallListener(this);

        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
            @Override
            public void onForceOffline(int error, String message) {
                setGlViewVisable(false);
                setRefuseVisable(false);
                isCalling = false;
            }
        });

        mChronometer.stop();
        mChronometer.setVisibility(View.GONE);

        mLbmManager = LocalBroadcastManager.getInstance(this);
        SendRobot(Constants.IP, RobotType.GETIP);
        mConnetState.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {
        if (localVoices != null) {
            localVoices.clear();
        }
    }

    @Override
    protected void setListener() {
        navControl.setOnNavAndSpeedListener(mPresenter);
    }

    @Override
    public void onBackPressed() {
        //结束呼叫
        endCall();
        if (!quit) { //询问退出程序
            showToast("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() { //启动定时任务
                @Override
                public void run() {
                    quit = false; //重置退出标识
                }
            }, 2000);
            quit = true;
        } else { //确认退出程序
            super.onBackPressed();
            finish();
            //退出时杀掉所有进程
            Process.killProcess(Process.myPid());
        }
    }

    @OnClick({R.id.sport_imageView, R.id.setting_imageView, R.id.voice_imageView, R.id.like_imageView, R.id.send_text,
            R.id.change_text_voice, R.id.repeat_imageView, R.id.relink_imageView, R.id.voice_btn, R.id.localView_imageView,
            R.id.btn_cancel_video, R.id.video_btn_accept, R.id.video_btn_relese, R.id.openSurface_imageView,
            R.id.controlSetting_imageView, R.id.message_relative, R.id.hideInput_iv, R.id.voicePeople_imageview,
            R.id.btn_switch_chat_mode})
    void onClick(View view) {
        fullScreen();
        switch (view.getId()) {
            case R.id.sport_imageView:
                mPresenter.sendAutoAction();
                break;
            case R.id.setting_imageView:
                mPresenter.showInterfaceDialog();
                break;
            case R.id.voice_imageView:
                mPresenter.sendSpeech();
                break;
            case R.id.like_imageView:
                if (localVoices == null || localVoices.size() == 0) {
                    SendRobot("GetLocalVoice", RobotType.LocalVoice);
                } else {
                    mPresenter.showSceneDialog();
                }
                break;
            case R.id.send_text:
                mPresenter.sendAIUIText();
                break;
            case R.id.change_text_voice:
                mPresenter.changeSpeech();
                break;
            case R.id.repeat_imageView:
                mPresenter.doRepear();
                break;
            case R.id.relink_imageView:
                mPresenter.reLink();
                break;
            case R.id.voice_btn:
                mPresenter.audioPermission();
                break;
            case R.id.localView_imageView:
                mPresenter.localViewVisible();
                break;
            case R.id.btn_cancel_video:
                endCall();
                setGlViewVisable(false);
                setRefuseVisable(false);
                break;
            case R.id.video_btn_accept:
//                mPresenter.attachGlView(mLocalVideoView, mRemoteVideoView);
                mPresenter.onAccept();
                break;
            case R.id.video_btn_relese:
                mPresenter.onHangUp();
                break;
            case R.id.openSurface_imageView:
                if (isCalling) {
                    return;
                }
                isCalling = true;
                setGlViewVisable(true);
                setRefuseVisable(true);
                ILVCallOption option = new ILVCallOption(ILiveLoginManager.getInstance().getMyUserId())
                        .callTips("CallSDK Demo")
                        .setMemberListener(this)
                        .setCallType(ILVCallConstants.CALL_TYPE_VIDEO);
                if (-1 == mCallId) { // 发起呼叫
                    ArrayList<String> nums = new ArrayList<String>();
                    nums.add(UserManage.getInstance().getRobotName());

                    if (nums.size() > 1) {
                        mCallId = ILVCallManager.getInstance().makeMutiCall(nums, option);
                    } else {
                        mCallId = ILVCallManager.getInstance().makeCall(nums.get(0), option);
                    }
                    ILVCallManager.getInstance().initAvView(avRootView);
                } else {  // 接听呼叫
//                    ILVCallManager.getInstance().acceptCall(mCallId, option);
                }
                break;
            case R.id.controlSetting_imageView:
                mPresenter.showControl(mControlRelative);
                mSetting.setVisibility(View.GONE);
                break;
            case R.id.message_relative:
                mPresenter.showInput(mEdit);
                break;
            case R.id.hideInput_iv:
                mPresenter.showInput(mEdit);
                break;
            case R.id.voicePeople_imageview:
                mPresenter.showPersonSelectDialog();
                break;
            case R.id.btn_switch_chat_mode:
                if (mChatPresenter.getConversation().getType() == TIMConversationType.C2C) {
                    btnSWithChatMode.setText("单聊");
                    mChatPresenter.switchChatMode(TIMConversationType.Group, UserManage.roomAVId);
                } else {
                    mChatPresenter.switchChatMode(TIMConversationType.C2C, UserManage.getInstance().getRobotName());
                    btnSWithChatMode.setText("群聊");
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void SendRobot(String order, RobotType robotType) {
        if (robotType == RobotType.Motion) {
            if (FfbClientApp.getInstance().isUdpConnect()) {
                Print.e("motion");
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra("controlSend", order);
                mLbmManager.sendBroadcast(intent);
            } else {
                RobotBean robotBean = new RobotBean();
                robotBean.setType(robotType);
                robotBean.setOrder(order);
                mChatPresenter.sendCustomMessage(robotBean);
            }
        } else if (robotType == RobotType.GETIP) {
            RobotBean robotBean = new RobotBean();
            robotBean.setType(robotType);
            robotBean.setOrder(order);
            mChatPresenter.sendCustomMessage(robotBean);
            Print.e("发送contorl ip");
        } else {
            RobotBean robotBean = new RobotBean();
            robotBean.setType(robotType);
            robotBean.setOrder(order);
            mChatPresenter.sendCustomMessage(robotBean);
        }
    }

    public void setBottomVisible(boolean visible) {
        mReleaseLayout.setVisibility(visible ? View.GONE : View.VISIBLE);//结束(呼出)
        mAcceptLayout.setVisibility(visible ? View.VISIBLE : View.GONE);//接收(呼入)
    }

    @Override
    public void setSportVisiable(boolean b) {
        if (b) {
            mSport.setImageResource(R.mipmap.ic_control_sport);
        } else {
            mSport.setImageResource(R.mipmap.ic_control_sport_pressed);
        }
    }

    @Override
    public void setVoicetVisiable(boolean b) {
        if (b) {
            mVoice.setImageResource(R.mipmap.ic_control_voice_pressed);
        } else {
            mVoice.setImageResource(R.mipmap.ic_control_voice);
        }
    }

    @Override
    public String getEditText() {
        return mEditText.getText().toString();
    }

    @Override
    public void setEditText(String text) {
        mEditText.setText(text);
    }

    @Override
    public void setVoiceText(String text) {
        mAsrBtn.setText(text);
    }

    @Override
    public void setVoiceBack(boolean b) {
        if (b) {
            mAsrBtn.setBackgroundResource(R.mipmap.ic_voice_hangup);
        } else {
            mAsrBtn.setBackgroundResource(R.mipmap.ic_voice_bg);
        }
    }

    @Override
    public void setLayoutVisible(boolean b) {
        if (b) {
            mEditLayout.setVisibility(View.VISIBLE);
            mAsrBtn.setVisibility(View.INVISIBLE);
//            mAsrResultLayout.setVisibility(View.GONE);
            mChangeTxtOrVoice.setImageResource(R.mipmap.ic_input_voice);

        } else {
            mEditLayout.setVisibility(View.GONE);
            mAsrBtn.setVisibility(View.VISIBLE);
            mChangeTxtOrVoice.setImageResource(R.mipmap.ic_input);
        }
    }

    @Override
    public void setRepeatShow(boolean b) {
        if (b) {
            mRepeat.setImageResource(R.mipmap.ic_repeat_pressed);
        } else {
            mRepeat.setImageResource(R.mipmap.ic_repeat);
        }
    }

    @Override
    public void setAsrLayoutVisible(boolean b) {
//        mAsrResultLayout.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setAnimationVisible(boolean b) {
        mAnimationLayout.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLocalViewVisiable(boolean b) {
//        mLocalVideoView.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setRefuseVisable(boolean b) {
        mReleaseLayout.setVisibility(b ? View.VISIBLE : View.GONE);//结束(呼出)
    }

    @Override
    public void setChronometerVisible(boolean b) {
        mChronometer.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setInputVisiable(boolean b) {
        mMessage.setVisibility(b ? View.GONE : View.VISIBLE);
        mEdit.setVisibility(b ? View.VISIBLE : View.GONE);
    }


    public void setGlViewVisable(boolean visable) {
        mGlView_frameLayout.setVisibility(visable ? View.VISIBLE : View.GONE);
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showMsg(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCallEstablish(int callId) {
        Print.e("onCallEstablish");
        initCallManager();
        avRootView.swapVideoView(0, 1);
        for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
            final int index = i;
            AVVideoView minorView = avRootView.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())) {
                minorView.setMirror(true);      // 本地镜像
            }
            minorView.setDragable(true);    // 小屏可拖动
            minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    avRootView.swapVideoView(0, index);     // 与大屏交换
                    return false;
                }
            });
        }
    }

    private int mCurCameraId = ILiveConstants.BACK_CAMERA;

    private void initCallManager() {
        //打开摄像头
        ILVCallManager.getInstance().enableCamera(mCurCameraId, true);
        //关闭摄像头
//        ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
//        avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        //切换摄像头
//        mCurCameraId = (ILiveConstants.FRONT_CAMERA==mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        ILVCallManager.getInstance().switchCamera(mCurCameraId);
        //打开麦克风
        ILVCallManager.getInstance().enableMic(true);
        //关闭麦克风
//        ILVCallManager.getInstance().enableMic(false);
        //切换到听筒
//        ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_HEADSET);
        //切换到扬声器
        ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        //设置美艳
        ILiveSDK.getInstance().getAvVideoCtrl().inputBeautyParam(0.0f);
    }

    /**
     * 会话结束回调
     *
     * @param callId
     * @param endResult 结束原因
     * @param endInfo   结束描述
     */
    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        setGlViewVisable(false);
        setRefuseVisable(false);
        isCalling = false;
        Print.e("XDBG_END", "onCallEnd->id: " + callId + "|" + endResult + "|" + endInfo);
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        Print.e("XDBG_END", "onCallEnd->id: " + iExceptionId + "|" + errCode + "|" + errMsg);
        isCalling = false;
    }


    @Override
    public void onRecvNotification(int callid, ILVCallNotification ilvCallNotification) {
        Print.i("onRecvNotification->notify id : " + ilvCallNotification.getNotifId() +
                " | " + ilvCallNotification.getUserInfo() + "/" + ilvCallNotification.getSender());
    }

    private boolean isCalling;
    private int mCallId = -1;

    @Override
    public void onNewIncomingCall(int callId, int callType, ILVIncomingNotification notification) {
        if (isCalling) {
            return;
        }
        isCalling = true;
        mCallId = callId;
        setGlViewVisable(true);
        setRefuseVisable(true);
        ILVCallOption ilvCallOption = new ILVCallOption(notification.getSender())
                .callTips("呼叫标题")
                .setMemberListener(this)
                .setCallType(callType);
        ILVCallManager.getInstance().initAvView(avRootView);
        ILVCallManager.getInstance().acceptCall(callId, ilvCallOption);
    }

    //设置成员事件回调(调用ILVCallManager中的摄像头及麦克风接口才会有事件)
    @Override
    public void onCameraEvent(String id, boolean bEnable) {
        Print.e("onCameraEvent");
    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {
        Print.e("onMicEvent");
    }

    @Override
    public void onSendMessageSuccess(TIMMessage message) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {

    }

    @Override
    public void parseMsgcomplete(String str) {

    }

    @Override
    public void parseCustomMsgcomplete(String customMsg) {
        RobotBean bean = GsonUtil.GsonToBean(customMsg, RobotBean.class);
        if (bean == null || bean.getType().equals("") || bean.getOrder().equals("")) {
            return;
        }
        RobotType robotType = bean.getType();
        if (robotType == RobotType.VoiceSwitch) {


        } else if (robotType == RobotType.SmartChat) {//发音人


        } else if (robotType == RobotType.AutoAction) {


        } else if (robotType == RobotType.Motion) {


        } else if (robotType == RobotType.GETIP) {
            try {
                String order = bean.getOrder();

                JSONObject object = new JSONObject(order);
                Constants.CONNECT_IP = object.getString("robotIp");
                Constants.CONNECT_PORT = object.getInt("robotPort");

                if (Constants.CONNECT_IP == null) {
                    throw new RuntimeException("connect ip null");
                }
                if (Constants.CONNECT_IP.equals("")) {
                    FfbClientApp.getInstance().setUdpConnect(false);
                    return;
                }
                mConnetState.setText("已连接");
                Intent intent = new Intent(this, UdpService.class);
                startService(intent);
                FfbClientApp.getInstance().setUdpConnect(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (robotType == RobotType.LocalVoice) {
            String localVoice = bean.getOrder();
            List<String> voices = GsonUtil.GsonToList(localVoice, String.class);
            localVoices = voices;
            mPresenter.showSceneDialog();
        }
    }

    @Override
    public void UDPAcceptMessage(String content) {
        if (isAccept) {
            showToast(content);
        }
    }
}
