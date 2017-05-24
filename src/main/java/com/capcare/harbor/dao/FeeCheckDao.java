package com.capcare.harbor.dao;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.FeeCheck;

@Component
@Scope("singleton")
public class FeeCheckDao extends BaseDao <FeeCheck, String> {

    @Autowired
    public FeeCheckDao (@Qualifier("sessionFactoryMaster") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactoryMaster (session);
    }

}
