package com.capcare.harbor.dao;

import java.util.List;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.UserDevice;

@Component
@Scope("singleton")
public class UserDeviceDao extends BaseDao<UserDevice, Long> {

    @Autowired
    public UserDeviceDao (@Qualifier("sessionFactoryMaster") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactoryMaster (session);
    }

	public List<Long> findUserIdBySn(String deviceSn) {
		String hql = "select ud.userId from UserDevice ud where ud.deviceSn=?";
		return find(hql,deviceSn);
	}

	public List<String> findSnByUserId(Long userId) {
		String hql = "select ud.deviceSn from UserDevice ud where ud.userId=?";
		return find(hql, userId);
	}
}
