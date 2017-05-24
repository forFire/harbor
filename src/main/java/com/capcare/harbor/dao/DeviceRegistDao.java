package com.capcare.harbor.dao;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.DeviceRegisterBean;

@Component
@Scope("singleton")
public class DeviceRegistDao extends BaseDao <DeviceRegisterBean, String> {

    @Autowired
    public DeviceRegistDao (@Qualifier("sessionFactoryMaster") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactoryMaster (session);
    }

    public DeviceRegisterBean getDevice (String registId) {

        return this.getBy ("registId", registId);
    }

    public boolean delDevice (String sn) {

        return this.delDevice (sn);
    }

    public DeviceRegisterBean getDeviceBySn (String sn) {

        return this.get (sn);
    }

    public DeviceRegisterBean getDeviceByPlat (String plat) {

        return this.getBy ("plate", plat);
    }
}
