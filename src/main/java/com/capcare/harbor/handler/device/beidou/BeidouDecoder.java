package com.capcare.harbor.handler.device.beidou;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import module.util.Tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderFactory;
import com.capcare.harbor.handler.device.beidou.decoderService.BeidouDecoderService;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;
import com.capcare.harbor.vo.BaseMessage;

@Component
@Scope("singleton")
public class BeidouDecoder {

    private Logger               log        = LoggerFactory.getLogger (getClass ());

    @Resource
    private BeidouMsgUtils       msgUtils;

    @Resource
    private BeidouDecoderFactory decoderFactory;

    private static final String  DECODE_STR = "BDDH";

    public List <BaseMessage> decode (IoSession session, IoBuffer in) {

        List <BaseMessage> msgs = new ArrayList <BaseMessage> ();

//        String inputStr = msgUtils.getInputToStr (in);
        String inputStr = msgUtils.getInputToStr (in,Charset.forName ("GB2312"));
        log.info("---------from beidou :{}", inputStr);
        List <String> messageStrs = this.getDecodeMessages (inputStr);
        msgs = this.toMessages (session, messageStrs);

        log.info ("msg size : {}", msgs.size ());
        return msgs;
    }

    private List <String> getDecodeMessages (String inputStr) {

        String [] strs = Tools.checkEmpty (inputStr) ? null : inputStr.split (DecoderKey.BEIDOU_BEGIN + DECODE_STR
                + DecoderKey.BEIDOU_BEGIN);
        List <String> rsls = new ArrayList <String> ();
        if (strs == null)
            return rsls;
        for (int num = 0; num < strs.length; num++) {
            //            if (num == strs.length - 1)
            //                break;
            if (Tools.checkEmpty (strs [num]))
                continue;
            rsls.add (strs [num]);
        }
        return rsls;
    }

    private List <BaseMessage> toMessages (IoSession session, List <String> messageStr) {

        List <BaseMessage> messages = new ArrayList <BaseMessage> ();
        for (String str : messageStr) {
            BeidouDecoderService message = decoderFactory.getDecoderService (str);
            if (message == null)
                continue;
            List <BaseMessage> baseMessages = message.getBaseMessage (str.split (DecoderKey.BEIDOU_BEGIN), session);
            if (baseMessages != null && !baseMessages.isEmpty ())
                messages.addAll (baseMessages);
            if(str.length()>0 && baseMessages.size()==0){
            	log.info("接收到的消息类型异常！" + "--该消息为：" + str);
            }
        }
        return messages;
    }
    
    public static void main(String[] args){
    	String str = "@BDDH@U@310574@1930218@60@6@3@0@0@161857@161858@161859@161860@161861@161862@161863@161864@161865@161866@310297@310299";
    	String[] strArr = str.split("@BDDH@");
    	System.out.println(strArr.length);
    	for(int i=0;i<strArr.length;i++){
    		System.out.println("第"+i+"个 是 ：" + strArr[i]);
    	}
    }
}
