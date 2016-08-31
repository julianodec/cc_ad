package com.ccAd.ui;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccAd.common.Campaign;
import com.ccAd.common.exception.CampaignException;
import com.ccAd.server.CampaignService;

/**
 * Jersey resource that makes available REST services for CRUDding campaigns.
 * 
 * @author Juliano DeCarvalho
 */
@Component
@Path("/")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AdResource {

	@Autowired
	private CampaignService campaignService;

	@GET
	@Path("{partnerId}")
	public Campaign getActiveCampaign(@PathParam("partnerId") long partnerId) {
		return campaignService.getActiveCampaign(partnerId);
	}

	@GET
	@Path("all/{partnerId}")
	public Collection<Campaign> getCampaigns(@PathParam("partnerId") long partnerId) {
		return campaignService.getCampaigns(partnerId);
	}

	@POST
	public Campaign addCampaign(Campaign newCampaign) throws CampaignException {
		campaignService.addCampaign(newCampaign);

		return newCampaign;
	}

	@PUT
	@Path("activate/{campaignId}")
	public Campaign activateCampaign(@PathParam("campaignId") String campaignId) {
		return campaignService.activateCampaign(campaignId);
	}

}
