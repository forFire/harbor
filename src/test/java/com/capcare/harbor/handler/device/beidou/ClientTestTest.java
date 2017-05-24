package com.capcare.harbor.handler.device.beidou;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;


public class ClientTestTest {

    //@Test
    public void test () throws UnknownHostException, IOException {

        Socket s = new Socket ("192.168.3.191", 8860);
        InputStream ips=s.getInputStream();  
        OutputStream ops=s.getOutputStream();  
          
        BufferedReader brKey = new BufferedReader(new InputStreamReader(System.in));//键盘输入  
        DataOutputStream dos = new DataOutputStream(ops);  
        BufferedReader brNet = new BufferedReader(new InputStreamReader(ips));  
  
        while(true)  
        {  
            String strWord = brKey.readLine();  
            dos.writeBytes(strWord + System.getProperty("line.separator"));  
            if(strWord.equalsIgnoreCase("quit"))  
                break;  
            else  
                System.out.println(brNet.readLine());  
        }  
        dos.close();  
        brNet.close();  
        brKey.close();  
        s.close();  
    }

}
