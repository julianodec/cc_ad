package com.ccAd.common.exception;

/**
 * This exception is thrown when an active campaign is being added for a
 * partner, but another active campaign already exists.
 * 
 * @author Juliano DeCarvalho
 */
public class ExistingActiveCampaignException extends CampaignException {

	private static final long serialVersionUID = 3848817967547401727L;

	public ExistingActiveCampaignException(String msg, long partnerId) {
		super(msg, partnerId);
	}

}
