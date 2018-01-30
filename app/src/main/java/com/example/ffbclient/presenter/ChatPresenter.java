package com.example.ffbclient.presenter;

import android.util.Log;

import com.example.ffbclient.common.RobotType;
import com.example.ffbclient.common.UserManage;
import com.example.ffbclient.im.event.MessageEvent;
import com.example.ffbclient.im.event.RefreshEvent;
import com.example.ffbclient.model.RobotBean;
import com.example.ffbclient.model.UserInfo;
import com.example.ffbclient.presenter.ipresenter.IChatPresenter;
import com.example.ffbclient.utils.BitmapUtils;
import com.example.ffbclient.utils.GsonUtil;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFileElem;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageLocator;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by zhangyuanyuan on 2017/9/26.
 */

public class ChatPresenter extends IChatPresenter implements Observer {

    /**
     * TIMConversationType.C2C,    //会话类型：单聊
     * TIMConversationType.Group,      //会话类型：群组
     */

    private IChatView mChatView;

    private TIMConversation conversation;

    public ChatPresenter(IChatView baseView, TIMConversationType type, String peer) {
        super(baseView);
        mChatView = baseView;
        //peer 参与会话的对方, C2C会话为对方帐号identifier, 群组会话为群组Id
        conversation = TIMManager.getInstance().getConversation(type, peer);
    }


    @Override
    public void start() {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
//        createGroup(User.roomId);
    }

    @Override
    public void finish() {
        MessageEvent.getInstance().deleteObserver(this);
        RefreshEvent.getInstance().deleteObserver(this);
//        deleteGroup(User.roomId);
    }


    @Override
    public void switchChatMode(TIMConversationType type, String peer) {
        conversation = TIMManager.getInstance().getConversation(type, peer);
    }

    @Override
    public TIMConversation getConversation() {
        return conversation;
    }

    @Override
    public void createGroup(String roomId) {
        ArrayList<String> list = new ArrayList<>();
        list.add(UserManage.getInstance().getRobotName());
//        TIMGroupManager.getInstance().createGroup("Private", list, roomId, new TIMValueCallBack<String>() {
//            @Override
//            public void onError(int code, String s) {
//                Print.e( "create group failed: " + code + " desc");
//            }
//
//            @Override
//            public void onSuccess(String s) {//@TGS#1BYSOL6ED
//                Print.e( "create group succ: " + s);
//            }
//        });
        TIMGroupManager.getInstance().createAVChatroomGroup(roomId, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                Print.e("create av group failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(String s) {
                Print.e("create av group succ, groupId : " + s);
                Print.e("create av group succ, groupId : " + s);
                Print.e("create av group succ, groupId : " + s);
                Print.e("create av group succ, groupId : " + s);
                Print.e("create av group succ, groupId : " + s);
                Print.e("create av group succ, groupId : " + s);
            }
        });
    }

    @Override
    public void createGroupParam(String roomId) {
        TIMGroupManager.CreateGroupParam param = TIMGroupManager.getInstanceById(UserInfo.getInstance().getIdentifier()).new CreateGroupParam();
        param.setGroupType("Private");
        param.setGroupName(roomId);
        param.setIntroduction("hello haifeng");
        param.setNotification("welcome to hello group");

        //添加群成员
        List<TIMGroupMemberInfo> infos = new ArrayList<TIMGroupMemberInfo>();
        TIMGroupMemberInfo member = new TIMGroupMemberInfo();
        member.setUser(UserManage.getInstance().getRobotName());
        member.setRoleType(TIMGroupMemberRoleType.NotMember);
        infos.add(member);
        param.setMembers(infos);

        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                Print.e("create group failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(String s) {
                Print.e("create group succ, groupId:" + s);
            }
        });
    }

    @Override
    public void inviteGroup(String roomId) {
        //创建待加入群组的用户列表
        ArrayList<String> list = new ArrayList<>();
        list.add(UserManage.getInstance().getRobotName());
        TIMGroupManager.getInstance().inviteGroupMember(roomId, list, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> results) {
                for (TIMGroupMemberResult r : results) {
                    Print.e("result: " + r.getResult()  //操作结果:  0:添加失败；1：添加成功；2：原本是群成员
                            + " user: " + r.getUser());    //用户帐号
                }
            }
        });

    }

    @Override
    public void applyJoinGroup(String roomId) {
        TIMGroupManager.getInstance().applyJoinGroup(roomId, "anything", new TIMCallBack() {
            @java.lang.Override
            public void onError(int code, String desc) {
                //接口返回了错误码code和错误描述desc，可用于原因
                //错误码code列表请参见错误码表
                Print.e("disconnected");
            }

            @java.lang.Override
            public void onSuccess() {
                Print.e("join group");
            }
        });
    }

    @Override
    public void quitGroup(String roomId) {
        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
                Print.e("quit group succ");
            }
        });
    }

    @Override
    public void deleteGroup(String roomId) {
        TIMGroupManager.getInstance().deleteGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Print.e(String.format("delete group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Print.e("delete group success");
            }
        });
    }


    @Override
    public void sendTextMessage(String txt) {
        TIMMessage msg = new TIMMessage();

        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(txt);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Print.d("addElement failed");
            return;
        }
        sendMessage(msg);
    }

    @Override
    public void sendImageMessage(String imagePath) {
        TIMMessage msg = new TIMMessage();

        //添加图片
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(imagePath);

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Print.d("addElement failed");
            return;
        }
        sendMessage(msg);
    }

    @Override
    public void sendCustomMessage(RobotBean bean) {
        TIMMessage msg = new TIMMessage();

        String userStr = GsonUtil.GsonString(bean);

        TIMCustomElem elem = new TIMCustomElem();

        elem.setData(userStr.getBytes());      //自定义byte[]
        elem.setDesc(userStr); //自定义描述信息

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Print.d("addElement failed");
            return;
        }

        sendMessage(msg);
    }


    @Override
    public void sendMessage(final TIMMessage message) {
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                mChatView.onSendMessageFail(code, desc, message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {
                //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
                MessageEvent.getInstance().onNewMessage(null);
                mChatView.onSendMessageSuccess(msg);
            }
        });
        //message对象为发送中状态
//        MessageEvent.getInstance().onNewMessage(message);
    }

    @Override
    public void analysisTextMessage(TIMTextElem timTextElem) {
        String txt = timTextElem.getText();
        mChatView.parseMsgcomplete(txt);
    }

    @Override
    public void analysisImageMessage(TIMImageElem timImageElem) {
        ArrayList<TIMImage> timImages = timImageElem.getImageList();

        for (TIMImage image : timImages) {
            Print.d("image type: " + image.getType() + " image size " + image.getSize() +
                    " image height " + image.getHeight() + " image width " + image.getWidth());
            image.getImage("", new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    //错误码code和错误描述desc，可用于定位请求失败原因
                    //错误码code含义请参见错误码表
                    Print.d("getImage failed. code: " + code + " errmsg: " + desc);
                }

                @Override
                public void onSuccess() {
                    Print.d("getImage success. 成功，参数为图片数据");
                }
            });
        }

    }

    public void analysisCustomMessage(TIMCustomElem timCustomElem) {
        byte[] datas = timCustomElem.getData();
        String str = String.valueOf(datas);

        String desc = timCustomElem.getDesc();
        Print.e(" 自定义消息 : " + str + "  desc : " + desc);
        mChatView.parseCustomMsgcomplete(desc);
    }

    public void analysisFileMessage(final TIMFileElem fileElem) {
        fileElem.getToFile(BitmapUtils.projectPath + fileElem.getFileName(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess() {
                RobotBean robotBean = new RobotBean();
                mChatView.parseCustomMsgcomplete(GsonUtil.GsonString(robotBean));
            }
        });
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage || data == null) {
                TIMMessage msg = (TIMMessage) data;
                if (msg == null || msg.getConversation().getPeer().equals(conversation.getPeer())
                        && msg.getConversation().getType() == conversation.getType()) {
                    if (msg == null) {
                        return;
                    }
                    TIMElem elem = msg.getElement(0);
                    if (elem.getType() == TIMElemType.Text) {
                        TIMTextElem timTextElem = (TIMTextElem) elem;
                        analysisTextMessage(timTextElem);
                    } else if (elem.getType() == TIMElemType.Image) {
                        TIMImageElem timImageElem = (TIMImageElem) elem;
                        analysisImageMessage(timImageElem);
                    } else if (elem.getType() == TIMElemType.Custom) {
                        TIMCustomElem timCustomElem = (TIMCustomElem) elem;
                        analysisCustomMessage(timCustomElem);
                    } else if (elem.getType() == TIMElemType.File) {
                        TIMFileElem timFileElem = (TIMFileElem) elem;
                        analysisFileMessage(timFileElem);
                    }

                }
            } else if (data instanceof TIMMessageLocator) {
                //撤回的消息
            }

        } else if (observable instanceof RefreshEvent) {
            Print.i("RefreshEvent");
        }
    }


}
