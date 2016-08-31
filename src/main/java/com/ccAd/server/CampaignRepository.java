package com.ccAd.server;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ccAd.common.Campaign;

/**
 * This class is in charge of CRUD operations against a MongoDB database.
 * 
 * @author Juliano DeCarvalho
 */
public interface CampaignRepository extends PagingAndSortingRepository<Campaign, String> {

	/**
	 * Find a campaign by the specified partner_id and active state.
	 * 
	 * @param partnerId
	 * @param active
	 * @return
	 */
	Campaign findByPartnerIdAndActive(long partnerId, boolean active);

	/**
	 * Find a campaign by the specified internal ID.
	 * 
	 * @param campaignId
	 * @return
	 */
	Campaign findById(String campaignId);

	/**
	 * Find all campaigns by the specified partner_id.
	 * 
	 * @param partnerId
	 * @return
	 */
	Iterable<Campaign> findByPartnerId(long partnerId);

}
