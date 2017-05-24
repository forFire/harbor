package com.capcare.harbor.service.logic.bean;

/**
 * @author wangkun
 *  
 * registRs 0成功 1车辆已被注册 2数据库中午该车辆   3设备已被注册 4数据库中午该设备
 */
public class DeviceRegistRsBean {

    private byte   registRs;
    
    private String registId;

    public byte getRegistRs () {

        return registRs;
    }

    public void setRegistRs (byte registRs) {

        this.registRs = registRs;
    }

    public String getRegistId () {

        return registId;
    }

    public void setRegistId (String registId) {

        this.registId = registId;
    }

}
