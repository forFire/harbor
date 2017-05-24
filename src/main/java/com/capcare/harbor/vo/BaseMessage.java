package com.capcare.harbor.vo;

public class BaseMessage {

    private Act         act;
    private LoginDevice loginDevice;

    private Object      response;    //请求信息，需要返回的

    private Object      businessData; //业务数据

    public static enum Act {
        LOGIN, HEART_BEAT, POSITION, ALARM, REPLY, CONFIG_REPLY, OBD, //
        OBD_ALARM, FEE_CHECK, AUTH, REG, LOGOFF, DEVICE_PARAM, //
        EVENT_REPLY, QUESTION_REPLY, MESSAGE_REPLY, DATA_RECORDER, //
        WAYBILL, DRIVER_INFO, MEDIAEVENT_UPLOAD, MEDIA_UPLOAD, //
        MEDIA_REPLY, DOWNLINK_DATA, GZIP, RSA, UPGRADE, //
        UPGRADE_DOWNLOAD,UPGRADE_FINISH,//
        SENT_MESSAGE,
        USERDATA,STATUS,GPS,//rd项目（指挥机信息，指挥机功况数据，GPS数据）
        FIREALARM,LEADER,LEADER_ZHUCE,LEADER_XINTIAO
        
    }

    public BaseMessage (Act act, Object businessData) {

        super ();
        this.act = act;
        this.businessData = businessData;
    }

    public Act getAct () {

        return act;
    }
    
    public LoginDevice getLoginDevice () {

        return loginDevice;
    }

    public void setLoginDevice (LoginDevice loginDevice) {

        this.loginDevice = loginDevice;
    }

    public Object getResponse () {

        return response;
    }

    public void setResponse (Object response) {

        this.response = response;
    }

    public Object getBusinessData () {

        return businessData;
    }

    public void setBusinessData (Object businessData) {

        this.businessData = businessData;
    }

    @Override
    public String toString () {

        return "[" + businessData + ", act=" + act.name () + ", loginDevice=" + loginDevice + ", response=" + response
                + "]";
    }

}
