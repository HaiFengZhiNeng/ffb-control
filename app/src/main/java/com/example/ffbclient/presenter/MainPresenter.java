package com.example.ffbclient.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.example.ffbclient.R;
import com.example.ffbclient.asr.youdao.TranslateData;
import com.example.ffbclient.asr.youdao.TranslateLanguage;
import com.example.ffbclient.common.RobotType;
import com.example.ffbclient.dao.DataBaseDao;
import com.example.ffbclient.listener.MyAiuiListener;
import com.example.ffbclient.listener.MyRecognizerListener;
import com.example.ffbclient.listener.MySynthesizerListener;
import com.example.ffbclient.model.InterfaceBean;
import com.example.ffbclient.presenter.ipresenter.IMainPresenter;
import com.example.ffbclient.ui.InterfaceDialog;
import com.example.ffbclient.ui.NavController;
import com.example.ffbclient.ui.PersonSelectDialog;
import com.example.ffbclient.ui.SceneDialog;
import com.example.ffbclient.ui.anim.MyAnimation;
import com.example.ffbclient.utils.JsonParser;
import com.example.ffbclient.utils.PreferencesUtils;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.seabreeze.log.Print;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydonlinetranslate.TranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by zhangyuanyuan on 2017/10/10.
 */

public class MainPresenter extends IMainPresenter implements ILVBCallMemberListener, NavController.OnNavAndSpeedListener,
        MySynthesizerListener.SynListener, MyAiuiListener.AiListener, MyRecognizerListener.RecognListener {

    private IMainView mView;

    private int mCallId;


    //    private CustomControl customControl;
    //    private NlpControl nlpControl;

    public static final int RESULT_CODE_STARTAUDIO = 100;

//    private boolean needTransalte = false;

    //是否复读
    private boolean isRepeat = false;


    // 解析得到语义结果
    String finalText = "";
    private boolean isAutoAction = false;
    private boolean isSpeech = false;

//    byte[] controlBytes = new byte[7];
//    byte[] interfaceBytes = null;

    //    private boolean isInterface = false;
//    private ArrayList<InterfaceBean> been = null;
//
//    private StringBuffer stringBuffer = new StringBuffer();
//    byte[] getDataBytes = null;
//
//    private boolean isGetData = false;
    private InterfaceDialog interfaceDialog = null;//显示界面控制的Dialog
    private SceneDialog sceneDialog = null;//显示DIY音频的 Dialog

    private PersonSelectDialog selectDialog = null;// 选择发音人Dialog

    private Handler mHandler = new Handler();

    //标记当前是否为语音输入
    private boolean isSpeechInput = false;

    private boolean isVisiable = true;

    private boolean isAreLevelShowing = true;
    private boolean isShowInput = true;

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    //粤语听写成文本
//    private boolean cantonese = false;
    //判断是否是经过语义理解而来的
//    private boolean translateSource = false;
    //点一次按钮，翻译完成后走语义理解，第二次回来不进语义理解，不然陷入死循环
//    private boolean onlyOne = false;
    //英语复读标识
//    private boolean repeatEnglish = false;
//    private boolean fromQuestion;

    //默认发音人
    private String spokesman = "xiaoyan";
    //是否正在说话
    private boolean isTalking;

    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    //语音合成
    private SpeechSynthesizer mTts;
    //AIUI
    private AIUIAgent mAIUIAgent;
    //语音听写
    private SpeechRecognizer mIat;

    private MySynthesizerListener synthesizerListener;
    private MyAiuiListener aiuiListener;
    private MyRecognizerListener recognizerListener;
    private LanguageType languageType;
    private QueryType queryType;
    private int mAIUIState;
//    private boolean isQuerying;

    private String code;
    private boolean isTouch;
    private int mNav;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;

                case 2:
                    if (isTouch) {

                        if (!TextUtils.isEmpty(code))
                            mView.SendRobot(code, RobotType.Motion);

                        myHandler.sendEmptyMessageDelayed(2, 200);
                    }
                    //  myHandler.sendEmptyMessage(2);
                    break;
                case 3:


                    if (!TextUtils.isEmpty(code))
                        mView.SendRobot(code, RobotType.Motion);

                    myHandler.sendEmptyMessageDelayed(2, 1000);
                    code = "";
                    myHandler.removeMessages(2);
                    myHandler.removeMessages(4);
                    isTouch = false;
                    break;
                case 4:
                    if (!TextUtils.isEmpty(code))

                    mView.SendRobot(code, RobotType.Motion);
                    myHandler.sendEmptyMessageDelayed(4, 200);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public MainPresenter(IMainView baseView) {
        super(baseView);
        mView = baseView;


//        nlpControl = new NlpControl(mView.getContext());
//        nlpControl.setmAIUIListener(mAIUIListener);
//        nlpControl.init();
        synthesizerListener = new MySynthesizerListener(this);
        aiuiListener = new MyAiuiListener((Activity) mView.getContext(), this);
        recognizerListener = new MyRecognizerListener(this);

        initTts();
        initAiui();
        initIat();

        String language = getLanguage();
        setLanguageType(language);
        queryType = QueryType.noQuery;
//        customControl = new CustomControl(mView.getContext());
//        customControl.setmAIUIListener(mAIUIListener);

    }

    private void setLanguageType(String language) {
        if ("en_us".equals(language)) {
            languageType = LanguageType.English;
        } else if ("cantonese".equals(language)) {
            languageType = LanguageType.Cantonese;
        } else {
            languageType = LanguageType.Chinese;
        }
    }

    public void initTts() {
        mTts = SpeechSynthesizer.createSynthesizer(mView.getContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    mView.showMsg("初始化失败,错误码：" + code);
                }
            }
        });
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTts.setParameter(SpeechConstant.VOICE_NAME, spokesman);
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    public void initAiui() {
        String params = "";
        AssetManager assetManager = mView.getContext().getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAIUIAgent = AIUIAgent.createAgent(mView.getContext(), params, aiuiListener);
        AIUIMessage startMsg = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null);
        mAIUIAgent.sendMessage(startMsg);

//        if (AIUIConstant.STATE_WORKING != this.mAIUIState) {
//            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
//            mAIUIAgent.sendMessage(wakeupMsg);
//        }
//
////         打开AIUI内部录音机，开始录音
//        String paramss = "sample_rate=16000,data_type=audio";
//        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, paramss, null);
//        mAIUIAgent.sendMessage(writeMsg);
    }

    public void initIat() {
        mIat = SpeechRecognizer.createRecognizer(mView.getContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    mView.showMsg("初始化失败，错误码：" + code);
                }
            }
        });
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String lag = PreferencesUtils.getString(mView.getContext(), "iat_language_preference", "mandarin");
        if (lag.equals("en_us")) {
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);
        } else {
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }
        mIat.setParameter(SpeechConstant.VAD_BOS, "99000");
        mIat.setParameter(SpeechConstant.VAD_EOS, PreferencesUtils.getString(mView.getContext(), "iat_vadeos_preference", "1000"));
        mIat.setParameter(SpeechConstant.ASR_PTT, PreferencesUtils.getString(mView.getContext(), "iat_punc_preference", "1"));
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
//        FlowerCollector.onEvent(mView.getContext(), "iat_recognize");
        mIat.startListening(recognizerListener);
    }

    @Override
    public void reLink() {
        mView.showMsg("当前未连接机器人");
    }

    @Override
    public void makeCall() {
        mView.setGlViewVisable(true);
        mView.setBottomVisible(false);

        ILVCallOption option = new ILVCallOption(ILiveLoginManager.getInstance().getMyUserId())
                .callTips("CallSDK Demo")
                .setMemberListener(this)
                .setCallType(ILVCallConstants.CALL_TYPE_VIDEO);
        if (0 == mCallId) { // 发起呼叫
            ArrayList<String> nums = new ArrayList<String>();
            nums.add("zhangT");

            if (nums.size() > 1) {
                mCallId = ILVCallManager.getInstance().makeMutiCall(nums, option);
            } else {
                mCallId = ILVCallManager.getInstance().makeCall(nums.get(0), option);
            }
        } else {  // 接听呼叫
            ILVCallManager.getInstance().acceptCall(mCallId, option);
        }
    }

    @Override
    public void sendAutoAction() {
        //    mView.SendRobot("自由运动", RobotType.AutoAction);
        String data;
        if (isAutoAction) {
            //执行开
            mView.setSportVisiable(true);
            data = "自由运动(关)";
            mHandler.removeCallbacks(runnable);
            //     controlBytes[3] &= (byte) ~(1 << 1);
            mView.SendRobot("A5038005AA", RobotType.Motion);

        } else {
            //执行关
            //   controlBytes[3] |= (byte) (1 << 1);
            mView.SendRobot("A503800AAA", RobotType.Motion);
            data = "自由运动(开)";
            mView.setSportVisiable(false);
            mHandler.postDelayed(runnable, 200);
        }
//        mView.setAutoText(data);
        isAutoAction = !isAutoAction;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mView.SendRobot("A503800AAA", RobotType.Motion);
            mHandler.postDelayed(runnable, 600);
            Print.e("自由运动(开)");
        }
    };

    @Override
    public void showInterfaceDialog() {
        if (interfaceDialog == null) {
            interfaceDialog = new InterfaceDialog(mView.getContext(), "");
        }
        interfaceDialog.setClickListener(clickListenerInterface);
        interfaceDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取data数据
                DataBaseDao dataBaseDao = new DataBaseDao(mView.getContext());
                ArrayList<InterfaceBean> beans = dataBaseDao.queryAll();
                Print.e("本地数据库数据 : " + beans.toString());
                if (beans != null && beans.size() > 0)
                    interfaceDialog.setData(beans);
            }
        }).start();
    }

    @Override
    public void sendSpeech() {
        String data = "";
        if (isSpeech) {//执行开
            data = "语音识别(开)";
            mView.setVoicetVisiable(true);
//            controlBytes[3] |= (byte) (1);
            mView.SendRobot("语音开", RobotType.VoiceSwitch);
        } else {//执行关
            data = "语音识别(关)";
            mView.setVoicetVisiable(false);
//            controlBytes[3] &= (byte) 0xfe;
            mView.SendRobot("语音关", RobotType.VoiceSwitch);
        }
//        mView.setSpeechText(data);
        isSpeech = !isSpeech;
    }

    @Override
    public void showSceneDialog() {
        if (sceneDialog == null) {
            sceneDialog = new SceneDialog(mView.getContext(), "");
        }
        sceneDialog.setClickListener(clickListenerInterface);
        sceneDialog.show();
    }

    @Override
    public void sendAIUIText() {
        String text = mView.getEditText();
        if (TextUtils.isEmpty(text))
            return;
        //是否复读
        if (!isRepeat) {
            if (languageType == LanguageType.English) {
                queryType = QueryType.isQuerying;
                query(text, TranslateLanguage.LanguageType.EN, TranslateLanguage.LanguageType.ZH);
            } else {
                if (mAIUIAgent == null) {
                    initAiui();
                }
                aiuiWriteText(text);
            }
        } else {
            sendTextToByte("text", text);
        }

        mView.setEditText("");//清空文本框内容
    }

    /**
     * 切换语音识别和文本输入
     */
    @Override
    public void changeSpeech() {
        if (isSpeechInput) {
            //隐藏
            mView.setLayoutVisible(true);
        } else {
            //显示
            mView.setLayoutVisible(false);
        }

        isSpeechInput = !isSpeechInput;

    }

    @Override
    public void doRepear() {
        finalText = "";
        if (isRepeat) {
            mView.setRepeatShow(false);
        } else {
            mView.setRepeatShow(true);
        }
        isRepeat = !isRepeat;
    }

    @Override
    public void audioPermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(mView.getContext(), android.Manifest.permission.RECORD_AUDIO)) {
            startAsr();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //提示用户开户权限音频
                String[] perms = {"android.permission.RECORD_AUDIO"};
                ActivityCompat.requestPermissions((Activity) mView, perms, RESULT_CODE_STARTAUDIO);
            }
        }
    }

    @Override
    public void localViewVisible() {
        if (isVisiable) {
            mView.setLocalViewVisiable(true);
        } else {
            mView.setLocalViewVisiable(false);
        }
        isVisiable = !isVisiable;
    }

    @Override
    public void onRefuse() {
        //拒绝呼入
//        if (!TextUtils.isEmpty(mCurrentCallId)) {
//            ECDevice.getECVoIPCallManager().rejectCall(mCurrentCallId, 6666);
//        }
        ILVCallManager.getInstance().endCall(mCallId);
        mView.setGlViewVisable(false);
        mView.setRefuseVisable(true);
    }

    @Override
    public void onAccept() {
        //接听
//        if (!TextUtils.isEmpty(mCallId)) {
//            ECDevice.getECVoIPCallManager().acceptCall(mCallId);
//            mView.setBottomVisible(false);
//        }
    }

    @Override
    public void onHangUp() {
        //挂断
//        mCallId = ((Activity) mView).getIntent().getStringExtra(ECDevice.CALLID);
//        if (!TextUtils.isEmpty(mCallId)) {
//            ECDevice.getECVoIPCallManager().releaseCall(mCallId);
////            exit();
//        }
        mView.setGlViewVisable(false);
        mView.setRefuseVisable(true);
        mView.setChronometerVisible(true);
    }

    /**
     * 控制盘的显示与隐藏
     */
    @Override
    public void showControl(RelativeLayout relate_level) {
        if (!isAreLevelShowing) {
            MyAnimation.startAnimationsIn(relate_level, 800, mView.getContext());
        } else {
            if (isAreLevelShowing) {
                MyAnimation.startAnimationsOut(relate_level, 800, 500, mView.getContext());
            } else {
                MyAnimation.startAnimationsOut(relate_level, 800, 0, mView.getContext());
            }
        }
        isAreLevelShowing = !isAreLevelShowing;
    }

    /**
     * 文本编辑 隐藏显示
     */
    @Override
    public void showInput(RelativeLayout relativeLayout) {
        if (isShowInput) {
            TranslateAnimation translateAnimation = new TranslateAnimation(0.1f, 0.0f, 0.1f, 0.0f);
            translateAnimation.setDuration(500);
            relativeLayout.startAnimation(translateAnimation);
            mView.setInputVisiable(true);
        } else {
            TranslateAnimation translateAnimation = new TranslateAnimation(0.1f, 900.0f, 0.1f, 0.0f);
            translateAnimation.setDuration(500);
            relativeLayout.startAnimation(translateAnimation);
            mView.setInputVisiable(false);
        }
        isShowInput = !isShowInput;
    }

    /**
     * 选择发音人
     */
    @Override
    public void showPersonSelectDialog() {
        if (selectDialog == null) {
            selectDialog = new PersonSelectDialog(mView.getContext(), "");
        }
        selectDialog.setClickListener(clickListenerInterface);
        selectDialog.show();
    }

    /**
     * 发送语音
     */
    void startAsr() {
        //点击说话
        if (!isTalking) {
            mView.setAsrLayoutVisible(false);
            mView.setAnimationVisible(false);//音量动画 关闭
            mView.setVoiceText("结束说话");
            mView.setVoiceBack(true);

//            startRecognizerListener();
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }


            if (languageType == LanguageType.English) {
                stopAiuiVoiceListener();
                startRecognizerListener();
            } else if (languageType == LanguageType.Cantonese) {
                stopAiuiVoiceListener();
                startRecognizerListener();
            } else {
                if (isRepeat) {
                    stopAiuiVoiceListener();
                    startRecognizerListener();
                } else {
                    stopRecognizerListener();
                    startAiuiVoiceListener();
                }
            }

        } else {
            mView.setVoiceText("点击开始");
            mView.setVoiceBack(false);
        }
        isTalking = !isTalking;

    }

    private void startAiuiVoiceListener() {
        AIUIMessage aiuiMessage1 = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null);
        mAIUIAgent.sendMessage(aiuiMessage1);
        if (AIUIConstant.STATE_WORKING != this.mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(writeMsg);
    }

    @NonNull
    private String getLanguage() {
        SharedPreferences sp = mView.getContext().getSharedPreferences("com.iflytek.setting", Activity.MODE_PRIVATE);
        return sp.getString("iat_language_preference", "mandarin");
    }


    public void stopAiuiVoiceListener() {
        if (mAIUIAgent != null) {
            String paramss = "sample_rate=16000,data_type=audio";
            AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, paramss, null);
            mAIUIAgent.sendMessage(writeMsg);
            AIUIMessage aiuiMessage = new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, null, null);
            mAIUIAgent.sendMessage(aiuiMessage);
        }
    }

    public void startRecognizerListener() {
        stopAiuiVoiceListener();
        mIat.startListening(recognizerListener);
    }

    public void stopRecognizerListener() {
        mIat.startListening(null);
        mIat.stopListening();
    }


    /**
     * 发送信息
     */
    private void sendTextToByte(String type, String text) {
        if (type.equals("text")) {//文本
            mView.SendRobot(text, RobotType.Text);
            mView.setVoiceText("点击开始");
            mView.setVoiceBack(false);
            isTalking = !isTalking;
        } else if (type.equals("controll")) {//前后左右控制

        }


    }


    public void query(final String source, TranslateLanguage.LanguageType fromType, TranslateLanguage.LanguageType toType) {
        String from = "", to = "", input = "";
        // 源语言或者目标语言其中之一必须为中文,目前只支持中文与其他几个语种的互译
        if (fromType == TranslateLanguage.LanguageType.EN && toType == TranslateLanguage.LanguageType.ZH) {
            //英译中
            from = "英文";
            to = "中文";
        } else if (fromType == TranslateLanguage.LanguageType.ZH && toType == TranslateLanguage.LanguageType.EN) {
            //中译英
            from = "中文";
            to = "英文";
        } else {
            return;
        }

        input = source;
        Language langFrom = LanguageUtils.getLangByName(from);
        Language langTo = LanguageUtils.getLangByName(to);

        TranslateParameters tps = new TranslateParameters.Builder()
                .source("youdao").from(langFrom).to(langTo).timeout(3000).build();// appkey可以省略

        Translator translator = Translator.getInstance(tps);

        translator.lookup(input, new TranslateListener() {

            @Override
            public void onResult(Translate result, String input) {
                TranslateData td = new TranslateData(System.currentTimeMillis(), result);

                if (languageType == LanguageType.English) {
                    if (isRepeat) {
                        sendTextToByte("text", td.translates());
                        if (queryType == QueryType.isQuerying) {
                            queryType = QueryType.finishQuery;
                        }
                    } else {
                        if (queryType == QueryType.isQuerying) {
                            aiuiWriteText(td.translates());
                        } else if (queryType == QueryType.finishQuery) {
                            queryType = QueryType.noQuery;
                            sendTextToByte("text", td.translates());
                        }
                    }
                } else if (languageType == LanguageType.Cantonese) {
                    if (isRepeat) {
                        Print.e("");
                    } else {
                        Print.e("");
                    }
                } else {
                    if (isRepeat) {
                        Print.e("");
                        sendTextToByte("text", td.translates());
                    } else {
                        Print.e("");
                        aiuiWriteText(td.translates());
                    }
                }


            }

            @Override
            public void onError(TranslateErrorCode error) {
            }
        });
    }

    private void aiuiWriteText(String text) {

        if (mAIUIAgent != null) {
            AIUIMessage aiuiMessage1 = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null);
            mAIUIAgent.sendMessage(aiuiMessage1);
            AIUIMessage aiuiMessage = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(aiuiMessage);

            String params = "data_type=text";
            if (TextUtils.isEmpty(text)) {
                text = "合肥明天的天气怎么样？";
            }
            byte[] textData = text.getBytes();
            AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData);
            mAIUIAgent.sendMessage(msg);
        } else {
            initAiui();
            aiuiWriteText(text);
        }
    }

    public int getmCallId() {
        return mCallId;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void onCameraEvent(String id, boolean bEnable) {

    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {

    }

    private ClickListenerInterface clickListenerInterface = new ClickListenerInterface() {
        @Override
        public void sendInterface(int id) {
            mView.SendRobot("点击了界面控制的单项", RobotType.Text);
        }

        @Override
        public void sendScene(String anwer) {
            mView.SendRobot(anwer, RobotType.Anwser);
        }

        @Override
        public void sendRefreshData() {
        }

        @Override
        public void sendPersonSelect(String text) {
            spokesman = text;
            mView.SendRobot(spokesman, RobotType.SmartChat);
            SharedPreferences sp = mView.getContext().getSharedPreferences("com.iflytek.setting", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            if (text.equals("henry") || text.equals("aiscatherine") || text.equals("aisjohn") || text.equals("vimary") || text.equals("aistom")) {
                editor.putString("iat_language_preference", "en_us");
                languageType = LanguageType.English;
            } else if (text.equals("dalong") || text.equals("xiaomei")) {//粤语
                editor.putString("iat_language_preference", "cantonese");
                languageType = LanguageType.Cantonese;
            } else {
                editor.putString("iat_language_preference", "mandarin");
                languageType = LanguageType.Chinese;
            }
            editor.commit();
            Print.i(mView.getContext().getSharedPreferences("com.iflytek.setting", Activity.MODE_PRIVATE).getString("iat_language_preference", "wu"));
        }
    };


    /**
     * 将AIUI监听中返回的结果提出来
     *
     * @param jsonObject
     * @throws JSONException
     */
    @SuppressLint("WrongConstant")
    public void speechAnalysis(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("answer")) {
//            //被语音语义识别，返回结果
            JSONObject answerObj = jsonObject.getJSONObject("answer");
            if (!isRepeat) {
                finalText = answerObj.optString("text");
                if (languageType == LanguageType.English) {
                    if (queryType == QueryType.isQuerying) {
                        queryType = QueryType.finishQuery;
                        query(finalText, TranslateLanguage.LanguageType.ZH, TranslateLanguage.LanguageType.EN);
                    } else {
                        sendTextToByte("text", finalText);
                        mView.showMsg("已发送" + finalText);
                    }
                } else if (languageType == LanguageType.Cantonese) {
                    sendTextToByte("text", finalText);
                    mView.showMsg("已发送" + finalText);
                } else {
                    sendTextToByte("text", finalText);
                    mView.showMsg("已发送" + finalText);
                }
            } else {//有结果——复读
                JSONObject question = answerObj.optJSONObject("question");
                String repeatText = question.optString("question");
                if (languageType == LanguageType.English) {
                    if (queryType == QueryType.isQuerying) {
                        queryType = QueryType.finishQuery;
                        query(repeatText, TranslateLanguage.LanguageType.ZH, TranslateLanguage.LanguageType.EN);
                    } else {
                        sendTextToByte("text", finalText);
                        mView.showMsg("已发送" + finalText);
                    }
                } else if (languageType == LanguageType.Cantonese) {
                    sendTextToByte("text", repeatText);
                } else {
                    sendTextToByte("text", repeatText);
                }
            }
        } else if (jsonObject.has("rc") && "4".equals(jsonObject.getString("rc"))) {
//            //不能返回结果
            if (!isRepeat) {//无结果——不复读
//                //随机在结果集中找出一个结果输出
                String[] arrResult = mView.getContext().getResources().getStringArray(R.array.no_result);
                finalText = arrResult[new Random().nextInt(arrResult.length)];
                if (languageType == LanguageType.English) {
                    if (queryType == QueryType.isQuerying) {
                        queryType = QueryType.finishQuery;
                        query(finalText, TranslateLanguage.LanguageType.ZH, TranslateLanguage.LanguageType.EN);
                    } else {
                        sendTextToByte("text", finalText);
                        mView.showMsg("已发送" + finalText);
                    }
                } else if (languageType == LanguageType.Cantonese) {
                    sendTextToByte("text", finalText);
                    mView.showMsg("已发送" + finalText);
                } else {
                    sendTextToByte("text", finalText);
                    mView.showMsg("已发送" + finalText);
                }
            } else {//无结果——复读
                String repeatStr = jsonObject.optString("text");
                sendTextToByte("text", repeatStr);
            }
        }
    }


    @Override
    public void onNavAndSpeed(int nav) {
        if (mNav != nav) {
            isTouch = false;
            myHandler.removeMessages(2);
        }
        if (!isTouch) {

            isTouch = true;
            mNav = nav;
            if (nav == 5) {
                code = "A5038006AA";//右
                myHandler.sendEmptyMessage(2);
            } else if (nav == 3) {
                code = "A5038008AA";//下
                myHandler.sendEmptyMessage(2);
            } else if (nav == 7) {
                code = "A5038002AA";//上
                myHandler.sendEmptyMessage(2);
            } else if (nav == 1) {
                code = "A5038004AA";//左
                myHandler.sendEmptyMessage(2);
            }


            else if (nav == 4) {
                code = "A5038009AA";//右下
                myHandler.sendEmptyMessage(2);
            } else if (nav == 6) {
                code = "A5038003AA";//右上
                myHandler.sendEmptyMessage(2);
            } else if (nav == 2) {
                code = "A5038007AA";//左下
                myHandler.sendEmptyMessage(2);
            } else if (nav == 0) {
                code = "A5038001AA";//左上
                myHandler.sendEmptyMessage(2);
            }



            else {
                code = "5";//停
                myHandler.sendEmptyMessage(3);
            }
        }
    }

    @Override
    public void onTouchDown() {
        isTouch = false;
        myHandler.removeMessages(2);
//        sendTextToByte("Motion", "A5038005AA");
//        mView.SendRobot("A5038002AA", RobotType.Motion);
    }

    /**
     * 监听说话完成
     */
    @Override
    public void onCompleted() {
        isTalking = false;
    }

    /**
     * 监听开始说话
     */
    @Override
    public void onSpeakBegin() {
        isTalking = true;
    }

    /**
     * 听到的结果
     *
     * @param results
     */
    @Override
    public void onResult(RecognizerResult results) {

        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }


        if (languageType == LanguageType.English) {
            if (isRepeat) {
                String repeatText = resultBuffer.toString();
                sendTextToByte("text", repeatText);
            } else {
                Print.e("");
                queryType = QueryType.isQuerying;
                query(resultBuffer.toString(), TranslateLanguage.LanguageType.EN, TranslateLanguage.LanguageType.ZH);
            }
        } else if (languageType == LanguageType.Cantonese) {
            if (isRepeat) {
                sendTextToByte("text", resultBuffer.toString());
            } else {
                aiuiWriteText(resultBuffer.toString());
            }
        } else {
            if (isRepeat) {
                sendTextToByte("text", resultBuffer.toString());
            } else {
                aiuiWriteText(resultBuffer.toString());
            }
        }

    }

    /**
     * 错误20006
     */
    @Override
    public void onErrInfo() {

    }

    /**
     * 错误10118
     */
    @Override
    public void onRecognDown() {

    }

    /**
     * @param jsonObject
     */
    @Override
    public void aiuiResult(JSONObject jsonObject) {
        try {

            speechAnalysis(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * aiui Error
     */
    @Override
    public void onError() {
//        mAIUIAgent = null;
    }

    /**
     * if (AIUIConstant.STATE_READY == mAIUIState)
     */
    @Override
    public void onAIUIDowm() {
//        mAIUIAgent = null;
    }

    @Override
    public void onAIUIState(int aIUIConstant) {
        mAIUIState = aIUIConstant;
    }

    public interface ClickListenerInterface {
        void sendInterface(int id);

        void sendScene(String anwer);

        void sendRefreshData();

        void sendPersonSelect(String text);
    }

    enum LanguageType {
        Chinese, English, Cantonese
    }

    enum QueryType {
        noQuery, isQuerying, finishQuery
    }

}
