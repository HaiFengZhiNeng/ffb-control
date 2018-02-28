package com.example.ffbclient.common;

/**
 * Created by android on 2018/1/5.
 */

public class UserManage {


    public static final String roomAVId = "@TGS#2GOCMM6EN";

    public static String robotName001 = "fanf102";
    public static String robotName002 = "fanf105";

    private volatile static UserManage userManage;

    private UserManage() {
    }


    public static UserManage getInstance() {
        if (null == userManage) {
            synchronized (UserManage.class) {
                if (null == userManage) {
                    userManage = new UserManage();
                }
            }
        }
        return userManage;
    }

    private String robotName;

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

}
