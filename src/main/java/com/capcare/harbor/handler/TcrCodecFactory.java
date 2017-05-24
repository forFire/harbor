package com.capcare.harbor.handler;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.stereotype.Component;

/**
 * @author fyq
 * mina监听到消息之后进行解码操作，激活拦截器，通过拦截器寻找ProtocolCodecFactory 类里面的decode方法
 */
@Component
public class TcrCodecFactory implements ProtocolCodecFactory {
	@Resource
	private  TcrEncoder encoder;
	@Resource
	private  TcrDecoder decoder;

	
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}
}