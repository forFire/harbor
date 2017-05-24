package com.capcare.harbor.handler.device.beidou.encoderManage;

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
import com.capcare.harbor.handler.device.beidou.encoderService.BeidouEncoderService;
import com.capcare.harbor.vo.InstructType;

@Component
@Scope("singleton")
public class BeidouEncoderFactory {

    private Logger                                       log        = LoggerFactory.getLogger (getClass ());

    private HashMap <InstructType, BeidouEncoderService> serviceMap = new HashMap <InstructType, BeidouEncoderService> ();

    private final Object                                 obj        = new Object ();

    public BeidouEncoderFactory () {

    }

    private void init () {

        ClassPathXmlApplicationContext context = StartDeviceHarbor.getInstance ().getContext ();
        List <Class < ? >> allAssignedClass;
        try {
            allAssignedClass = ClassUtil.getAllAssignedClass (BeidouEncoderService.class);
            for (Class < ? > cls : allAssignedClass) {

                BeidouEncoderService service = (BeidouEncoderService) context.getBean (cls);
                InstructType instructType = service.getInstructType ();
                serviceMap.put (instructType, service);
            }
        }
        catch (ClassNotFoundException e) {
            log.info ("class not found : {}", e);
        }
        catch (IOException e) {
            log.info ("io error : {}", e);
        }

    }

    public BeidouEncoderService getService (InstructType type) {

        synchronized (obj) {
            if (this.serviceMap.size () == 0)
                init ();
        }
        return serviceMap.get (type);
    }

}
