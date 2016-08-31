package com.ccAd.server;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.ccAd.common.Campaign;

/**
 * This class is in charge of CRUD operations against a MongoDB database.
 * 
 * @author Juliano DeCarvalho
 */
public interface CampaignRepository extends PagingAndSortingRepository<Campaign, String> {

	Campaign findByPartnerIdAndActive(long partnerId, boolean active);

}
