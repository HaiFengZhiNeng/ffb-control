package com.example.ffbclient.model;

import com.example.ffbclient.common.RobotType;

/**
 * Created by zhangyuanyuan on 2017/10/10.
 */

public class RobotBean {

    private RobotType type;
    private String order;

    public RobotType getType() {
        return type;
    }

    public void setType(RobotType type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
