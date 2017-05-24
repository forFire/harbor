package com.capcare.harbor.dao;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.Device;

/**
 * @author fyq
 */
@Component
@Scope("singleton")
public class DeviceDao extends BaseDao <Device, String> {

    @Autowired
    public DeviceDao (@Qualifier("sessionFactoryMaster") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactoryMaster (session);
    }

    public String getAppName (String deviceSn) {

        String hql = "select appName from Device ud where sn=?";
        return getHql (hql, deviceSn);
    }
    
    public Device getDeviceBySn(String sn){
    	 String hql = "from Device ud where sn=?";
         return getHql (hql, sn);
    }
}
