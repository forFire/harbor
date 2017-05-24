package com.capcare.harbor.service.logic.bean;

import java.io.Serializable;

import com.capcare.harbor.model.Position;

public class MediaItem implements Serializable {

	private static final long serialVersionUID = -7956801655550501865L;

	private Integer mediaId;
	private Integer mediaType;
	private Integer channelId;
	private Integer eventCode;
	private Position position;

	public MediaItem(Integer mediaId, Integer mediaType, Integer channelId,
			Integer eventCode, Position position) {
		this.mediaId = mediaId;
		this.mediaType = mediaType;
		this.channelId = channelId;
		this.eventCode = eventCode;
		this.position = position;
	}

	public MediaItem() {
	}

	public Integer getMediaId() {
		return mediaId;
	}

	public void setMediaId(Integer mediaId) {
		this.mediaId = mediaId;
	}

	public Integer getMediaType() {
		return mediaType;
	}

	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Integer getEventCode() {
		return eventCode;
	}

	public void setEventCode(Integer eventCode) {
		this.eventCode = eventCode;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

}