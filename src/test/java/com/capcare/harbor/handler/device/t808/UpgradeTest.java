
package com.capcare.harbor.handler.device.t808;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import module.util.BytesConvert;

import org.junit.Test;

import com.thoughtworks.xstream.converters.basic.ByteConverter;





public class UpgradeTest {

    @Test
    public void test () throws UnknownHostException, IOException {
        Socket core = new Socket ("127.0.0.1", 60011);
        BufferedReader in;
        BufferedOutputStream  out =new BufferedOutputStream (core.getOutputStream ());
        in = new BufferedReader (new InputStreamReader (core.getInputStream ()));
        byte [] b = BytesConvert.str2Bcd ("7E01070048D0D0D0D0D0D00000000000000000494D45493836323935303032333539353835360000000000000000000000000000000000000800000000000000000836303130303030310101007E");
        out.write (b );
        out.flush ();
        while(true){
            String readLine = in.readLine ();
            if(readLine==null)
                break;
            System.out.println (readLine);
        }
    }

}
