package com.capcare.harbor.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.jms.core.MessageCreator;

@Entity
@Table(name = "rd_shortmessage")
public class ShortMessage implements Serializable, MessageCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1878001908512811377L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	@Column(name = "send_handset_id")
	private String sendHandsetId;
	@Column(name = "receive_handset_id")
	private String receiveHandsetId;
	@Column(name = "send_Date")
	private Date sendDate;
	@Column(name = "send_content")
	private String sendContent;
	@Column(name = "status")
	private int status;
	@Column(name = "type")
	private int type;
	@Column(name = "priority")
	private int priority = 1;//默认为普通类型

	private final static int DEFAULT_SEND_NOT_SUCCESS_STATUS = 0;
	
	public final static int DEFAULT_SEND_SUCCESS_STATUS = 1;

	public static int getDEFAULT_SEND_NOT_SUCCESS_STATUS() {
		return DEFAULT_SEND_NOT_SUCCESS_STATUS;
	}
	
	public void dealWith(){
	}
	
	public String getSendHandsetId() {
		return sendHandsetId;
	}

	public void setSendHandsetId(String sendHandsetId) {
		this.sendHandsetId = sendHandsetId;
	}

	public String getReceiveHandsetId() {
		return receiveHandsetId;
	}

	public void setReceiveHandsetId(String receiveHandsetId) {
		this.receiveHandsetId = receiveHandsetId;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String convertToJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();

		String json = ow.writeValueAsString(this);
		return json;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime
				* result
				+ ((receiveHandsetId == null) ? 0 : receiveHandsetId.hashCode());
		result = prime * result
				+ ((sendContent == null) ? 0 : sendContent.hashCode());
		result = prime * result
				+ ((sendDate == null) ? 0 : sendDate.hashCode());
		result = prime * result
				+ ((sendHandsetId == null) ? 0 : sendHandsetId.hashCode());
		result = prime * result + status;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShortMessage other = (ShortMessage) obj;
		if (id != other.id)
			return false;
		if (receiveHandsetId == null) {
			if (other.receiveHandsetId != null)
				return false;
		} else if (!receiveHandsetId.equals(other.receiveHandsetId))
			return false;
		if (sendContent == null) {
			if (other.sendContent != null)
				return false;
		} else if (!sendContent.equals(other.sendContent))
			return false;
		if (sendDate == null) {
			if (other.sendDate != null)
				return false;
		} else if (!sendDate.equals(other.sendDate))
			return false;
		if (sendHandsetId == null) {
			if (other.sendHandsetId != null)
				return false;
		} else if (!sendHandsetId.equals(other.sendHandsetId))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}


}
