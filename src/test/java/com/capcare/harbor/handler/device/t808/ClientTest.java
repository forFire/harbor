package com.capcare.harbor.handler.device.t808;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class ClientTest {

    @Test
    public void test () throws UnknownHostException, IOException {

        Socket core = new Socket ("agps.u-blox.com", 46434);
        BufferedReader in;
        PrintWriter out;
        out = new PrintWriter (core.getOutputStream (), true);
        in = new BufferedReader (new InputStreamReader (core.getInputStream ()));
        out.println ("cmd=aid;user=54334024@qq.com;pwd=Neukow;lat=47.28;lon=8.56;pacc=1000");
        out.flush ();
        while(true){
            String readLine = in.readLine ();
            if(readLine==null)
                break;
            System.out.println (readLine);
        }
    }

}
