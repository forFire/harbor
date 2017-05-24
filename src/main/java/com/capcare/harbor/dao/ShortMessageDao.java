package com.capcare.harbor.dao;

import java.io.Serializable;
import java.util.List;

import module.orm.BaseDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.ShortMessage;

@Component
@Scope("singleton")
public class ShortMessageDao extends BaseDao<ShortMessage, Serializable> {

	private final String SELECT_NOT_SEND_SUCCESS = "from ShortMessage s where s.receiveHandsetId = ? and s.status = ?";

	@Autowired
	public ShortMessageDao(
			@Qualifier("sessionFactoryBeidou") SessionFactory session) {

		super();
		this.setSessionFactory(session);
		this.setSessionFactoryMaster(session);
	}

	public List<ShortMessage> findNotSendSuccess(String receiveHandsetId) {
		List<ShortMessage> notShortMessages = this.find(
				SELECT_NOT_SEND_SUCCESS, receiveHandsetId,
				ShortMessage.getDEFAULT_SEND_NOT_SUCCESS_STATUS());
		return notShortMessages;
	}
	
	public List<ShortMessage> findNotSendSuccessById(Long messageId) {
		
		return this.find("from ShortMessage i where i.status=0 and id=?",messageId);
	}

	public List<ShortMessage> findAllNotSendMessages() {

		String hql = "from ShortMessage i where (i.type = 1 or i.type = 3) and status = 0";
		return find(hql);
	}

	public List<ShortMessage> findNotSendAppointMessages(int priority) {

		String hql = "from ShortMessage i where i.status=0 and priority=?";
		return find(hql,priority);
	}
	

	public void update(ShortMessage message) {

		this.saveOrUpdate(message);
	}

	public List<Object> findbyDistinctId() {

		String hql = "select distinct i.sendHandsetId from ShortMessage i";
		return find(hql);
	}
}
