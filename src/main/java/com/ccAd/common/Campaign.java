package com.ccAd.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity/DTO object representing a campaign as a MongoDB document.
 * 
 * @author Juliano DeCarvalho
 */
@Document(collection = "campaigns")
@XmlRootElement
public class Campaign {

	private String id;
	private long partnerId;
	private long lastActivationDate;
	private long durationInSeconds;
	private String adContent;
	private boolean active;

	public Campaign() {
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "partner_id")
	public Long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}

	public long getLastActivationDate() {
		return lastActivationDate;
	}

	public void setLastActivationDate(long created) {
		this.lastActivationDate = created;
	}

	@XmlElement(name = "duration")
	public long getDurationInSeconds() {
		return durationInSeconds;
	}

	public void setDurationInSeconds(long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	@XmlElement(name = "ad_content")
	public String getAdContent() {
		return adContent;
	}

	public void setAdContent(String adContent) {
		this.adContent = adContent;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
