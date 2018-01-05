package com.example.ffbclient.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhangyuanyuan on 2017/11/7.
 */
@Entity
public class VoiceBean {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "type")
    private int type;
    @Property(nameInDb = "voiceType")
    private String voiceType;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    //    @Property(nameInDb = "sendType")
//    private String sendType;
    @Property(nameInDb = "voiceQuestion")
    private String voiceQuestion;
    @Property(nameInDb = "voiceAnswer")
    private String voiceAnswer;
    @Property(nameInDb = "imgUrl")
    private String imgUrl;
    @Property(nameInDb = "action")
    private String action;
    @Property(nameInDb = "expression")
    private String expression;
    @Property(nameInDb = "actionData")
    private String actionData;
    @Property(nameInDb = "expressionData")
    private String expressionData;
    @Generated(hash = 840091615)
    public VoiceBean(Long id, int type, String voiceType, long saveTime,
            String voiceQuestion, String voiceAnswer, String imgUrl, String action,
            String expression, String actionData, String expressionData) {
        this.id = id;
        this.type = type;
        this.voiceType = voiceType;
        this.saveTime = saveTime;
        this.voiceQuestion = voiceQuestion;
        this.voiceAnswer = voiceAnswer;
        this.imgUrl = imgUrl;
        this.action = action;
        this.expression = expression;
        this.actionData = actionData;
        this.expressionData = expressionData;
    }
    @Generated(hash = 1719036352)
    public VoiceBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getVoiceType() {
        return this.voiceType;
    }
    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }
    public long getSaveTime() {
        return this.saveTime;
    }
    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
    public String getVoiceQuestion() {
        return this.voiceQuestion;
    }
    public void setVoiceQuestion(String voiceQuestion) {
        this.voiceQuestion = voiceQuestion;
    }
    public String getVoiceAnswer() {
        return this.voiceAnswer;
    }
    public void setVoiceAnswer(String voiceAnswer) {
        this.voiceAnswer = voiceAnswer;
    }
    public String getImgUrl() {
        return this.imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getAction() {
        return this.action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getExpression() {
        return this.expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public String getActionData() {
        return this.actionData;
    }
    public void setActionData(String actionData) {
        this.actionData = actionData;
    }
    public String getExpressionData() {
        return this.expressionData;
    }
    public void setExpressionData(String expressionData) {
        this.expressionData = expressionData;
    }


}
