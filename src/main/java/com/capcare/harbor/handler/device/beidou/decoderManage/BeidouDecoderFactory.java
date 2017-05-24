package com.capcare.harbor.handler.device.beidou.decoderManage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import module.util.ClassUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.capcare.harbor.StartDeviceHarbor;
import com.capcare.harbor.handler.device.beidou.decoderService.BeidouDecoderService;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;

@Component
@Scope("singleton")
public class BeidouDecoderFactory {

    private Logger                                            log = LoggerFactory.getLogger (getClass ());

    private HashMap <BeidouDecoderType, BeidouDecoderService> map = new HashMap <BeidouDecoderType, BeidouDecoderService> ();

    private final Object                                      obj = new Object ();

    private BeidouDecoderFactory () {

    }

    private void init () {

        ClassPathXmlApplicationContext context = StartDeviceHarbor.getInstance ().getContext ();

        try {
            List <Class < ? >> allAssignedClass = ClassUtil.getAllAssignedClass (BeidouDecoderService.class);
            for (Class < ? > cls : allAssignedClass) {
                BeidouDecoderService service = (BeidouDecoderService) context.getBean (cls);
                map.put (service.getDecoderType (), service);
            }
        }
        catch (ClassNotFoundException e) {
            log.error ("can not get class : {}", e);
        }
        catch (IOException e) {
            log.error ("io exception : {}", e);
        }

    }

    public BeidouDecoderService getDecoderService (String typeStr) {

        synchronized (obj) {
            if (this.map.size () == 0) {
                init ();
            }
        }
        String [] split = typeStr.split (DecoderKey.BEIDOU_BEGIN);
        if (split == null || split.length == 0)
            return null;
        BeidouDecoderType type = BeidouDecoderType.toType (split [0]);
        return map.get (type);
    }

}
