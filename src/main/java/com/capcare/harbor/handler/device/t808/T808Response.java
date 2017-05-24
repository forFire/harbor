package com.capcare.harbor.handler.device.t808;

import com.thoughtworks.xstream.converters.basic.ByteConverter;

import module.util.BytesConvert;

public class T808Response {

    private byte [] head = new T808 ().getBegin ();
    private byte [] messageId;                     // 终端消息ID
    private byte [] datalength;
    private byte [] messageSequnce;                // 终端消息的流水号
    private byte [] sn   = new byte [8];
    private byte    lanage;
    private String  authkey;
    private byte [] resultData;
    private byte [] phone;
    private boolean subpackage;
    private byte [] packageCount;
    private byte [] packageSequence;

    public T808Response () {

        this.subpackage = false;
    }

    public byte [] getHead () {

        return head;
    }

    public void setHead (byte [] head) {

        this.head = head;
    }

    public byte [] getDatalength () {

        return datalength;
    }

    public void setDatalength (byte [] datalength) {

        this.datalength = datalength;
    }

    public byte [] getSn () {

        return sn;
    }

    public void setSn (byte [] sn) {

        for (int i = 0; i < 8; i++) {
            this.sn [i] = sn [i];
        }
    }

    public byte getLanage () {

        return lanage;
    }

    public void setLanage (byte lanage) {

        this.lanage = lanage;
    }

    public byte [] getMessageId () {

        return messageId;
    }

    public void setMessageId (byte [] messageId) {

        this.messageId = messageId;
    }

    public byte [] getMessageSequnce () {

        return messageSequnce;
    }

    public void setMessageSequnce (byte [] messageSequnce) {

        this.messageSequnce = messageSequnce;
    }

    public String getAuthkey () {

        return authkey;
    }

    public void setAuthkey (String authkey) {

        this.authkey = authkey;
    }

    public byte [] getResultData () {

        return resultData;
    }

    public void setResultData (byte [] resultData) {

        this.resultData = resultData;
        this.setDatalength (BytesConvert.int2Bytes (resultData.length, 2));
    }

    public byte [] getPhone () {

        return phone;
    }

    public void setPhone (byte [] phone) {

        this.phone = phone;
    }

    public boolean isSubpackage () {

        return subpackage;
    }

    public void setSubpackage (boolean subpackage) {

        this.subpackage = subpackage;
    }

    public byte [] getPackageCount () {

        return packageCount;
    }

    public void setPackageCount (byte [] packageCount) {

        this.packageCount = packageCount;
    }

    public byte [] getPackageSequence () {

        return packageSequence;
    }

    public void setPackageSequence (byte [] packageSequence) {

        this.packageSequence = packageSequence;
    }

    public byte [] tobytes () {

        /***
            2+2+6+2+BytesConvert.bytes2Int (datalength)+1
                                            标志位+消息ID+消息体属性+中端手机号+消息流水号+消息体+校验码
        **/
        if (messageId == null)
            return null;
        byte [] ret = null;
        if (this.isSubpackage ()) {
            int messageAttrabute = 8192 + BytesConvert.bytes2Int (this.datalength);
            ret = BytesConvert.concatAll (this.messageId, BytesConvert.int2Bytes (messageAttrabute, 2), this.phone,
                    this.messageSequnce, this.packageCount, this.packageSequence, this.resultData, new byte [] {
                        0
                    });
        }
        else {
            ret = BytesConvert.concatAll (this.messageId, this.datalength, this.phone, this.messageSequnce,
                    this.resultData, new byte [] {
                        0
                    });
        }

        return T808CodeUtils.toRsBytes (ret);
    }
}
