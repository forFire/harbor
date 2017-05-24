package com.capcare.harbor.service.logic;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.service.cache.InstructCache;
import com.capcare.harbor.service.cache.PositionCache;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context.xml")
public class InstructServiceTest {

    @Resource
    private PositionCache cache;
    
    @Test
    public void test () {
    	
    	System.out.println("test cache!");
    	cache.setInstruct("161865");
    	System.out.println(cache.isSetInstruct("161865"));
    }

}
