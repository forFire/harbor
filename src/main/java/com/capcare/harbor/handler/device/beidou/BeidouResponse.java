package com.capcare.harbor.handler.device.beidou;

import java.util.ArrayList;
import java.util.List;

import com.capcare.harbor.handler.device.beidou.encoderManage.BeidouEncoderType;

public class BeidouResponse {

    private final String      head  = "";
    private BeidouEncoderType type;

    private List <String>     datas = new ArrayList <String> ();

    public void setType (BeidouEncoderType type) {

        this.type = type;
    }

    public BeidouResponse addData (String data) {

        this.datas.add (data);
        return this;

    }

    public String toData () {

        StringBuffer sbf = new StringBuffer ();
        sbf.append (head).append ("").append (type.getType ()).append ("");
        return sbf.toString ();
    }

}
