package com.example.ffbclient.presenter.ipresenter;

import android.widget.RelativeLayout;

import com.example.ffbclient.common.RobotType;
import com.example.ffbclient.common.presenter.BasePresenter;
import com.example.ffbclient.common.presenter.BaseView;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/9/22.
 */

public abstract class IMainPresenter implements BasePresenter {

    private IMainView mBaseView;

    public IMainPresenter(IMainView baseView) {
        mBaseView = baseView;
    }

    public abstract void reLink();

    public abstract void sendAutoAction();

    public abstract void showInterfaceDialog();

    public abstract void sendSpeech();

    public abstract void showSceneDialog();

    public abstract void sendAIUIText();

    public abstract void changeSpeech();

    public abstract void doRepear();

    public abstract void audioPermission();

    public abstract void localViewVisible();

    public abstract void onAccept();

    public abstract void onHangUp();

    public abstract void showControl(RelativeLayout mControlRelative);

    public abstract void showInput(RelativeLayout mEdit);

    public abstract void showPersonSelectDialog();


    public interface IMainView extends BaseView {


        void SendRobot(String order, RobotType robotType);

        void setGlViewVisable(boolean b);

        void setBottomVisible(boolean b);

        void setSportVisiable(boolean b);

        void setVoicetVisiable(boolean b);

        String getEditText();

        void setEditText(String text);

        void setVoiceText(String text);

        void setVoiceBack(boolean b);

        void setLayoutVisible(boolean b);

        void setRepeatShow(boolean b);

        void setAsrLayoutVisible(boolean b);

        void setAnimationVisible(boolean b);

        void setLocalViewVisiable(boolean b);

        void setRefuseVisable(boolean b);

        void setChronometerVisible(boolean b);

        void setInputVisiable(boolean b);
    }
}
