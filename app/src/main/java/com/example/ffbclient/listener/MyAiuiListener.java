package com.example.ffbclient.listener;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;

import org.json.JSONObject;

/**
 * Created by dell on 2017/9/20.
 */

public class MyAiuiListener implements AIUIListener {

    private Activity activity;

    public MyAiuiListener(Activity activity, AiListener aiListener) {
        this.activity = activity;
        this.aiListener = aiListener;
    }

    @Override
    public void onEvent(AIUIEvent event) {
        switch (event.eventType) {
            case AIUIConstant.EVENT_WAKEUP:
                Log.e("", "进入识别状态");
                break;
            case AIUIConstant.EVENT_SLEEP:
                Log.e("", "休眠事件。当出现交互超时，服务会进入休眠状态（待唤醒），或者发送了");
                break;
            case AIUIConstant.EVENT_RESULT:
                boolean isSend = false;
                try {
                    JSONObject bizParamJson = new JSONObject(event.info);
                    JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                    JSONObject params = data.getJSONObject("params");
                    JSONObject content = data.getJSONArray("content").getJSONObject(0);

                    if (content.has("cnt_id")) {
                        String cnt_id = content.getString("cnt_id");
                        JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));

                        String sub = params.optString("sub");
                        if ("nlp".equals(sub)) {

                            String resultStr = cntJson.optString("intent");
                            JSONObject jsonObject = new JSONObject(resultStr);
                            if (resultStr != null && resultStr.length() > 3) {
                                //语义分析
//                                speechAnalysis(jsonObject);
                                if(aiListener != null) {
                                    aiListener.aiuiResult(jsonObject);
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            break;
            case AIUIConstant.EVENT_ERROR:
               if(aiListener != null) {
                   aiListener.onError();
//                   initAiui();
               }
                Log.e("GG", "on event: " + event.eventType);
                Log.e("GG", "on event: " + event.info);
                break;
            case AIUIConstant.EVENT_VAD:
                if (AIUIConstant.VAD_BOS == event.arg1) {
                    Log.i("", "找到vad_bos");
                } else if (AIUIConstant.VAD_EOS == event.arg1) {
                    Log.i("","找到vad_eos");

                } else {
                }
                break;

            case AIUIConstant.EVENT_START_RECORD:
                Log.i("","开始录音");
                Toast.makeText(activity,"开始录音",Toast.LENGTH_SHORT).show();
                break;
            case AIUIConstant.EVENT_STOP_RECORD:
                Log.i("","停止录音");
                break;
            case AIUIConstant.EVENT_STATE:     // 状态事件
                int mAIUIState = event.arg1;
                if (AIUIConstant.STATE_IDLE == mAIUIState) {
                    Log.e("EVENT_STATE", "STATE_IDLE");
                } else if (AIUIConstant.STATE_READY == mAIUIState) {
                    Log.e("EVENT_STATE", "STATE_READY");
                    if(aiListener != null) {
                        aiListener.onAIUIDowm();
                    }
                } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                    Log.e("EVENT_STATE", "STATE_WORKING");
                }
                aiListener.onAIUIState(mAIUIState);
                break;
            case AIUIConstant.EVENT_CMD_RETURN: {
                if (AIUIConstant.CMD_UPLOAD_LEXICON == event.arg1) {
                    //某条CMD命令对应的返回事件。对于除CMD_GET_STATE外的有返回的命令，都会返回该事件，用arg1标识对应的CMD命令，arg2为返回值，0表示成功，info字段为描述信息
                }
            }
            break;
            default:
                break;
        }
    }

    private AiListener aiListener;

    public interface AiListener{
        void aiuiResult(JSONObject jsonObject);
        void  onError();
        void onAIUIDowm();
        void onAIUIState(int aIUIConstant);
    }
}
