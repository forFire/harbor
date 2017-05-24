package com.capcare.harbor.handler.device.t808;

import module.util.BytesConvert;

public class T808Message {

    private byte [] head        = new T808 ().getBegin (); // 标志位
    private byte [] messageId;                            // 消息ID
    private byte [] messageProperty;                      // 消息体属性
    private byte [] phone;                                // 终端手机号
    private byte [] messageSequnce;                       // 消息流水号
    private byte [] packages    = new byte [0];           // 消息总包数
    private byte [] packageSeq  = new byte [0];           // 包序号
    private byte [] messageBody = new byte [0];           // 消息体
    private Integer bodyLength  = 0;

    public String getHexString (byte [] b) {

        return BytesConvert.encodeHexStr (b);
    }

    public byte [] getBytes () {

        /***
        2+2+6+2+BytesConvert.bytes2Int (datalength)+1
                                        标志位+消息ID+消息体属性+中端手机号+消息流水号+消息体+校验码
        **/
        byte [] ret = BytesConvert.concatAll (this.messageId, BytesConvert.int2Bytes (this.bodyLength, 2), this.phone,
                this.messageSequnce, this.messageBody, new byte [] {
                    0
                });

        return T808CodeUtils.toRsBytes (ret);
    }

    public byte [] getHead () {

        return head;
    }

    public void setHead (byte [] head) {

        this.head = head;
    }

    public byte [] getMessageId () {

        return messageId;
    }

    public void setMessageId (byte [] messageId) {

        this.messageId = messageId;
    }

    public byte [] getMessageProperty () {

        return messageProperty;
    }

    public void setMessageProperty (byte [] messageProperty) {

        this.messageProperty = messageProperty;
    }

    public byte [] getPhone () {

        return phone;
    }

    public void setPhone (byte [] phone) {

        this.phone = phone;
    }

    public byte [] getMessageSequnce () {

        return messageSequnce;
    }

    public void setMessageSequnce (byte [] messageSequnce) {

        this.messageSequnce = messageSequnce;
    }

    public byte [] getPackages () {

        return packages;
    }

    public void setPackages (byte [] packages) {

        this.packages = packages;
    }

    public byte [] getPackageSeq () {

        return packageSeq;
    }

    public void setPackageSeq (byte [] packageSeq) {

        this.packageSeq = packageSeq;
    }

    public byte [] getMessageBody () {

        return messageBody;
    }

    public void setMessageBody (byte [] messageBody) {

        this.messageBody = messageBody;
        this.bodyLength = messageBody == null ? 0 : messageBody.length;
    }

    public Integer getBodyLength () {

        return bodyLength;
    }

    public void setBodyLength (Integer bodyLength) {

        this.bodyLength = bodyLength;
    }

}
