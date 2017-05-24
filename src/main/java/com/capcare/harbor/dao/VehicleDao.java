package com.capcare.harbor.dao;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.VehicleBean;

@Component
@Scope("singleton")
public class VehicleDao extends BaseDao <VehicleBean, String> {

    @Autowired
    public VehicleDao (@Qualifier("sessionFactoryMaster") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactoryMaster (session);
    }

    public VehicleBean getVehicle (String vehicleId) {

        return this.get (vehicleId);
    }
}
