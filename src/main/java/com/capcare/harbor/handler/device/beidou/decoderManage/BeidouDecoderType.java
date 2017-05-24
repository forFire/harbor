package com.capcare.harbor.handler.device.beidou.decoderManage;

import module.util.Tools;

public enum BeidouDecoderType {

    receive_messages ("S"),   //中心接收短报文
    receive_locations ("D"),  //中心接受位置文
    receive_search("C"),	  //中心接受周边查询
    command_users ("U"),      //指挥机信息
    command_status ("B"),     //指挥机状态信息
    command_GPS ("G")		  //指挥机GPS数据
    ;

    private String type;

    BeidouDecoderType (String type) {

        this.type = type;
    }

    private String getType () {

        return type;
    }

    public static BeidouDecoderType toType (String typeStr) {

        BeidouDecoderType decoderType = null;
        if (Tools.checkEmpty (typeStr)) {
            return decoderType;
        }

        for (BeidouDecoderType detailType : BeidouDecoderType.values ()) {
            if (detailType.getType ().equals (typeStr)) {
                decoderType = detailType;
                break;
            }
        }
        return decoderType;
    }
}
