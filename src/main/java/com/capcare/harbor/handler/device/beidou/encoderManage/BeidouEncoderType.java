package com.capcare.harbor.handler.device.beidou.encoderManage;


public enum BeidouEncoderType {

    send_message ("S"),
    request_userDatas ("U"),
    request_status ("B"),
    request_GPS ("G")
    ;

    private String type;

    public String getType () {

        return type;
    }

    private BeidouEncoderType (String type) {

        this.type = type;
    }
    
//    public static BeidouDecoderType toType (String typeStr) {
//
//        BeidouDecoderType decoderType = null;
//        if (Tools.checkEmpty (typeStr)) {
//            return decoderType;
//        }
//
//        for (BeidouDecoderType detailType : BeidouDecoderType.values ()) {
//            if (detailType.getType ().equals (typeStr)) {
//                decoderType = detailType;
//                break;
//            }
//        }
//        return decoderType;
//    }

}
