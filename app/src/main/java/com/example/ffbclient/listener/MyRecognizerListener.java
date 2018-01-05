package com.example.ffbclient.listener;

import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

/**
 * Created by dell on 2017/9/20.
 */

public class MyRecognizerListener implements RecognizerListener {
    public MyRecognizerListener(RecognListener recognListener) {
        this.recognListener = recognListener;
    }

    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

    }

    @Override
    public void onBeginOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        if (!b) {
//            String text = JsonParser.parseIatResult(recognizerResult.getResultString());
            if(recognListener != null) {
                recognListener.onResult(recognizerResult);
            }
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        Log.e("SpeechError",speechError.getErrorCode()+"");
        Log.e("SpeechError",speechError.toString()+"");
        if (10118 == speechError.getErrorCode()){
            if (null != recognListener){
                recognListener.onRecognDown();
            }
        }else if (20006 == speechError.getErrorCode()) {
            if (null != recognListener) {
                recognListener.onErrInfo();
            }
        }else if(10114 == speechError.getErrorCode()){

        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    private RecognListener recognListener;

    public interface RecognListener{
        void onResult(RecognizerResult results);
        void onErrInfo();
        void onRecognDown();
    }
}
