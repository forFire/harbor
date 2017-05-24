package com.capcare.harbor.service.cache;

import java.util.concurrent.TimeUnit;

import module.util.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class InstructCache {

    @SuppressWarnings("unused")
    private Logger                         log                 = LoggerFactory.getLogger (getClass ());

    @Autowired
    private RedisTemplate <String, Object> redisTemplate;

    private final String                   SEQUENCE_NAME       = "ist-seq-";

    private final String                   PACKAGE_SUBTRACT_NO = "pkg-sub-No-";

    private String toKey (String sn, int sequenceNo) {

        return SEQUENCE_NAME + sn + "-" + sequenceNo;
    }

    private String toPkgSubKey (String sn, int sequenceNo) {

        return PACKAGE_SUBTRACT_NO + sn + "-" + sequenceNo;
    }

    public boolean setSequenceNo (String sn, int sequenceNo, int instructType) {

        this.redisTemplate.opsForValue ().set (this.toKey (sn, sequenceNo), "" + instructType, 6, TimeUnit.HOURS);
        return true;
    }

    public int getSequenceNo (String sn, int sequenceNo) {

        int seq = Tools.toInt (this.redisTemplate.opsForValue ().get (this.toKey (sn, sequenceNo)));
        return seq;
    }

    public boolean setPkgSubNo (String sn, int sequenceNo, int pkgSubNo) {

        this.redisTemplate.opsForValue ().set (this.toPkgSubKey (sn, sequenceNo), pkgSubNo, 3, TimeUnit.HOURS);
        return true;
    }

    public int getPkgSubNo (String sn, int sequenceNo) {

        int subNo = Tools.toInt (this.redisTemplate.opsForValue ().get (this.toPkgSubKey (sn, sequenceNo)));
        return subNo;
    }

}
