package com.ccAd.common.exception;

/**
 * This exception is thrown when no active campaigns can be found.
 * 
 * @author Juliano DeCarvalho
 */
public class NoActiveCampaignException extends CampaignException {

	private static final long serialVersionUID = 8721212900240280223L;

	public NoActiveCampaignException(String msg, long partnerId) {
		super(msg, partnerId);
	}

}
