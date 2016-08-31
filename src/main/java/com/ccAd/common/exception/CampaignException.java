package com.ccAd.common.exception;

/**
 * Base class for campaign related exceptions.
 * 
 * @author Juliano DeCarvalho
 */
public class CampaignException extends RuntimeException {

	private static final long serialVersionUID = 8035128992411401182L;

	private Long partnerId;

	public CampaignException(String msg, long partnerId) {
		super(msg);
		this.partnerId = partnerId;
	}

	public Long getPartnerId() {
		return partnerId;
	}

}
