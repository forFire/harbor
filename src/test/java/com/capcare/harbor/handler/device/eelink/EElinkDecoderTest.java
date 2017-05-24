package com.capcare.harbor.handler.device.eelink;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.junit.Test;

import com.capcare.harbor.vo.BaseMessage;


public class EElinkDecoderTest {

    @Test
    public void test () {

        String ss="676707006A09AE53C6BC2800BE3FB8130304000000045A00000005670000000B2F0000000C0E0A00000D0E0000000EB10000000F4700000010032D000011280000001C060000001F0051000020A007A001210000000040700000004236F1000089000000B38A000042618B00000000";
        System.out.println (ss.length ());
        byte[] bts= new byte[ss.length ()/2];
        for(int num=0;num<ss.length ();num=num+2){
            bts[num/2]=(byte)Integer.parseInt (ss.substring (num,num+2) ,16);
        }
        IoBuffer io = IoBuffer.wrap (bts);
        System.out.println (new Date().getTime ());
        BaseMessage decode = EElinkDecoder.decode (io , "112233");
        System.out.println (decode);
    }

}
