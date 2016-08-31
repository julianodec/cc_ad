package com.ccAd.server;

import com.ccAd.common.Campaign;
import com.ccAd.common.exception.ExistingActiveCampaignException;
import com.ccAd.common.exception.NoActiveCampaignException;

public interface CampaignService {

	void addCampaign(Campaign newCampaign) throws ExistingActiveCampaignException;

	Campaign activateCampaign(String campaignId);

	Campaign getActiveCampaign(long partnerId) throws NoActiveCampaignException;

	Iterable<Campaign> getCampaigns(long partnerId);

}
