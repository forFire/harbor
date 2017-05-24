package com.capcare.harbor.dao;

import java.util.List;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.Spot;

@Component
@Scope("singleton")
public class SpotDao  extends BaseDao<Spot, Long> {

	private Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    public SpotDao  (@Qualifier("sessionFactoryBeidou") SessionFactory session) {

        super ();
        this.setSessionFactory (session);
        this.setSessionFactorySpot (session);
    }

    public List<Object> findbyDistinctId(){
		
    	log.info("select sn");
		String hql = "select distinct i.deviceSn from Spot i";
		return find(hql);
	}
}
