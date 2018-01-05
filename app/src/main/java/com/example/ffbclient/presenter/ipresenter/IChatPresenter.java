package com.example.ffbclient.presenter.ipresenter;

import com.example.ffbclient.common.presenter.BasePresenter;
import com.example.ffbclient.common.presenter.BaseView;
import com.example.ffbclient.model.RobotBean;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMImageElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public abstract class IChatPresenter implements BasePresenter {



    private IChatView mBaseView;

    public IChatPresenter(IChatView baseView) {
        mBaseView = baseView;
    }

    public abstract void switchChatMode(TIMConversationType type, String peer);

    public abstract TIMConversation getConversation();

    /**
     * 创建群
     */
    public abstract void createGroup(String roomId);
    public abstract void createGroupParam(String roomId);

    /**
     * 邀请加入
     */
    public abstract void inviteGroup(String roomId);

    /**
     * 申请加入
     */
    public abstract void applyJoinGroup(String roomId);

    /**
     * 退出
     */
    public abstract void quitGroup(String roomId);

    /**
     * 删除群
     */
    public abstract void deleteGroup(String roomId);


    /**
     * 发送文本消息
     * @param txt
     */
    public abstract void sendTextMessage(String txt);

    public abstract void sendImageMessage(String imagePath);

    public abstract void sendCustomMessage(RobotBean bean);
    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
    public abstract void sendMessage(TIMMessage message);


    public abstract void analysisTextMessage(TIMTextElem timTextElem);

    public abstract void analysisImageMessage(TIMImageElem timImageElem);

    public abstract void analysisCustomMessage(TIMCustomElem timCustomElem);

    /**
     * 聊天界面的接口
     */
    public interface IChatView extends BaseView {

        /**
         * 发送消息成功
         *
         * @param message 返回的消息
         */
        void onSendMessageSuccess(TIMMessage message);

        /**
         * 发送消息失败
         *
         * @param code 返回码
         * @param desc 返回描述
         * @param message 发送的消息
         */
        void onSendMessageFail(int code, String desc, TIMMessage message);

        void parseMsgcomplete(String str);

        void parseCustomMsgcomplete(String customMsg);


    }
}
