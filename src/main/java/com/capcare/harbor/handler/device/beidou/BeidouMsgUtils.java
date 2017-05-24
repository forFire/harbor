package com.capcare.harbor.handler.device.beidou;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.DeviceType;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

@Component
@Scope("singleton")
public class BeidouMsgUtils {

    private Logger     log        = LoggerFactory.getLogger (getClass ());

    private DeviceType deviceType = new Beidou ();

    public String getInputToStr (IoBuffer in) {

        in.mark ();
        byte [] bytes = new byte [in.remaining ()];
        in.get (bytes);
//        in.clear ();
        String rsInStr = new String (bytes, Charset.forName ("UTF-8"));
//        log.info ("from beidou : {}", "");

        if (rsInStr.indexOf (DecoderKey.BEIDOU_BEGIN) == -1) {
            in.reset ();
            return null;
        }
        return rsInStr;
    }

    public BaseMessage toBaseMessage (IoSession session, String inputStr) {

//        String controlSn = Tools.checkEmpty (session.getAttribute ("duid")) ? "" : Tools.toString (session
//                .getAttribute ("duid"));
        LoginDevice deviceInfo = this.toDeviceInfo ();

        BaseMessage baseMsg = null;
        baseMsg = new BaseMessage (Act.POSITION, null);
        baseMsg.setLoginDevice (deviceInfo);
        return baseMsg;
    }

    private LoginDevice toDeviceInfo () {

        LoginDevice loginDevice = new LoginDevice ();
        loginDevice.setDt (deviceType);
        /**
         *  TODO beidou : 设置设备类型
         *  loginDevice.setSn (sn);
         * */
        return loginDevice;
    }

    public String getInputToStr (IoBuffer in, Charset charset) {
        in.mark ();
        byte [] bytes = new byte [in.remaining ()];
        in.get (bytes);
//        in.clear ();
        String rsInStr = new String (bytes, charset);
//        log.info ("from beidou : {}", "");

        if (rsInStr.indexOf (DecoderKey.BEIDOU_BEGIN) == -1) {
            in.reset ();
            return null;
        }
        return rsInStr;
    }
}
