package com.capcare.harbor.dao;

import java.util.List;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.Instruct;

@Component
@Scope("singleton")
public class InstructDao extends BaseDao <Instruct, String> {

    @Autowired
    public InstructDao (@Qualifier("sessionFactoryMaster") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactoryMaster (session);
    }

    /**
     * 离线指令
     */
    public List <Instruct> find (String deviceSn) {

        String hql = "from Instruct i where i.deviceSn=? and i.reply=0";
        return find (hql, deviceSn);
    }

    public Instruct find (String deviceSn, int operate) {

        String hql = "from Instruct i where i.deviceSn=? and i.type=?";
        return getHql (hql, deviceSn, operate);
    }

}
