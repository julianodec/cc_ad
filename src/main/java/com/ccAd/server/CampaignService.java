package com.ccAd.server;

import java.util.Collection;

import com.ccAd.common.Campaign;
import com.ccAd.common.exception.ExistingActiveCampaignException;
import com.ccAd.common.exception.NoActiveCampaignException;

public interface CampaignService {

	void addCampaign(Campaign newCampaign) throws ExistingActiveCampaignException;

	Campaign activateCampaign(String campaignId);

	Campaign getActiveCampaign(long partnerId) throws NoActiveCampaignException;

	Collection<Campaign> getCampaigns(long partnerId);

}
