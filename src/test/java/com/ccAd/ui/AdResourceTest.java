package com.ccAd.ui;

import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import com.ccAd.common.Campaign;
import com.ccAd.common.exception.ExistingActiveCampaignException;
import com.ccAd.common.exception.NoActiveCampaignException;

public class AdResourceTest extends JerseyTest {

	/**
	 * This is not /ad/ because the servlet mapping handles that for us
	 */
	private static final String ROOT_PATH = "/";

	private static final String AD_CONTENT_1 = "ad content 1";
	private static final String AD_CONTENT_2 = "ad content 2";
	private static final String AD_CONTENT_3 = "ad content 3";
	private static final Long PARTNER_ID_1 = 10L;
	private static final Long PARTNER_ID_2 = 20L;
	private static final Long PARTNER_ID_3 = 30L;
	private static final Long PARTNER_ID_4 = 40L;

	@Override
	protected Application configure() {
		return new ResourceConfig(AdResource.class);
	}

	/**
	 * Add a new campaign using JSON.
	 */
	@Test
	public void testAddJSONCampaign() {
		Campaign response = doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_1, false, 30L);

		Assert.assertNotNull(response.getId());
		Assert.assertNotEquals("fake_id", response.getId());
	}

	private Campaign doAddJSONCampaign(String adContent, long partnerId, boolean active, long duration) {
		Campaign newCampaign = new Campaign();
		newCampaign.setAdContent(adContent);
		newCampaign.setPartnerId(partnerId);
		newCampaign.setActive(active);
		newCampaign.setDurationInSeconds(duration);
		newCampaign.setId("fake_id");

		Entity<Campaign> request = Entity.entity(newCampaign, MediaType.APPLICATION_JSON_TYPE);

		return target(ROOT_PATH).request().post(request, Campaign.class);
	}

	/**
	 * Add a new campaign using XML.
	 */
	@Test
	public void testAddXMLCampaign() {
		Campaign newCampaign = new Campaign();
		newCampaign.setAdContent(AD_CONTENT_1);
		newCampaign.setPartnerId(PARTNER_ID_1);
		newCampaign.setActive(false);
		newCampaign.setDurationInSeconds(30L);
		newCampaign.setId("fake_id");

		Entity<Campaign> request = Entity.entity(newCampaign, MediaType.APPLICATION_XML_TYPE);

		Campaign response = target(ROOT_PATH).request().post(request, Campaign.class);

		Assert.assertNotNull(response.getId());
		Assert.assertNotEquals("fake_id", response.getId());
	}

	/**
	 * Get the active campaign when none exists yet.
	 */
	@Test
	public void testGetMissingActiveCampaign() {
		try {
			target(ROOT_PATH + PARTNER_ID_4).request().get(Campaign.class);

			Assert.fail("No exception thrown.");
		} catch (ProcessingException e) {
			Assert.assertEquals(NoActiveCampaignException.class, e.getCause().getClass());
			Assert.assertEquals(PARTNER_ID_4, ((NoActiveCampaignException) e.getCause()).getPartnerId());
		}
	}

	/**
	 * Activate an existing campaign.
	 */
	@Test
	public void testActivateCampaign() {
		Campaign campaign = doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_1, false, 30L);
		Assert.assertFalse(campaign.isActive());

		Entity<String> request = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);

		Campaign response = target(ROOT_PATH + "activate/" + campaign.getId()).request().put(request, Campaign.class);

		Assert.assertEquals(campaign.getId(), response.getId());
		Assert.assertTrue(response.isActive());
	}

	/**
	 * Get the existing active campaign for the specified partner.
	 */
	@Test
	public void testGetActiveCampaign() {
		// create campaign
		Campaign campaign = doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_1, false, 30L);
		Assert.assertFalse(campaign.isActive());

		// activate campaign
		Entity<String> request = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);

		Campaign response = target(ROOT_PATH + "activate/" + campaign.getId()).request().put(request, Campaign.class);

		Assert.assertEquals(campaign.getId(), response.getId());
		Assert.assertTrue(response.isActive());

		// retrieve active campaign
		response = target(ROOT_PATH + PARTNER_ID_1).request().get(Campaign.class);

		Assert.assertEquals(campaign.getId(), response.getId());
		Assert.assertTrue(response.isActive());
	}

	/**
	 * Get a list of all campaigns for the specified partner.
	 */
	@Test
	public void testListCampaigns() {
		// create campaigns across multiple partners
		doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_2, false, 30L);
		doAddJSONCampaign(AD_CONTENT_2, PARTNER_ID_2, true, 30L);
		doAddJSONCampaign(AD_CONTENT_3, PARTNER_ID_3, true, 30L);

		// retrieve campaigns for first partner only
		GenericType<Collection<Campaign>> genericType = new GenericType<Collection<Campaign>>() {
		};

		Collection<Campaign> campaigns = target(ROOT_PATH + "all/" + PARTNER_ID_2).request().get(genericType);

		Assert.assertEquals(2, campaigns.size());

		Iterator<Campaign> iter = campaigns.iterator();
		Campaign campaign1 = iter.next();
		Campaign campaign2 = iter.next();

		if (campaign1.isActive()) {
			Assert.assertTrue(campaign1.isActive());
			Assert.assertEquals(AD_CONTENT_2, campaign1.getAdContent());
			Assert.assertFalse(campaign2.isActive());
			Assert.assertEquals(AD_CONTENT_1, campaign2.getAdContent());
		} else {
			Assert.assertFalse(campaign1.isActive());
			Assert.assertEquals(AD_CONTENT_1, campaign1.getAdContent());
			Assert.assertTrue(campaign2.isActive());
			Assert.assertEquals(AD_CONTENT_2, campaign2.getAdContent());
		}
	}

	/**
	 * Attempt to add 2 active campaigns for the specified partner, and assert
	 * that adding the second one throws an error.
	 */
	@Test
	public void testAddTwoActiveCampaigns() {
		try {
			doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_1, true, 30L);
			doAddJSONCampaign(AD_CONTENT_2, PARTNER_ID_1, true, 30L);

			Assert.fail("No exception thrown.");
		} catch (ProcessingException e) {
			Assert.assertEquals(ExistingActiveCampaignException.class, e.getCause().getClass());
			Assert.assertEquals(PARTNER_ID_1, ((ExistingActiveCampaignException) e.getCause()).getPartnerId());
		}
	}

	/**
	 * Test that we get an error trying to load an expired active campaign.
	 */
	@Test
	public void testErrorOnExpiredCampaign() {
		try {
			// add already expired campaign
			doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_1, true, 0L);

			// try to get active campaign
			target(ROOT_PATH + PARTNER_ID_1).request().get(Campaign.class);

			Assert.fail("No exception thrown.");
		} catch (ProcessingException e) {
			Assert.assertEquals(NoActiveCampaignException.class, e.getCause().getClass());
			Assert.assertEquals(PARTNER_ID_1, ((NoActiveCampaignException) e.getCause()).getPartnerId());
		}
	}

	/**
	 * Active an expired campaign, and assert that it stays active for its
	 * duration.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testActivateExpiredCampaign() throws Exception {
		Campaign campaign = null;

		try {
			// add already expired campaign
			campaign = doAddJSONCampaign(AD_CONTENT_1, PARTNER_ID_1, true, 1L);

			Thread.sleep(1250);

			// try to get active campaign
			target(ROOT_PATH + PARTNER_ID_1).request().get(Campaign.class);

			Assert.fail("No exception thrown.");
		} catch (ProcessingException e) {
			Assert.assertEquals(NoActiveCampaignException.class, e.getCause().getClass());
			Assert.assertEquals(PARTNER_ID_1, ((NoActiveCampaignException) e.getCause()).getPartnerId());
		}

		// activate expired campaign
		Entity<String> request = Entity.entity("", MediaType.APPLICATION_JSON_TYPE);

		Campaign response = target(ROOT_PATH + "activate/" + campaign.getId()).request().put(request, Campaign.class);

		response = target(ROOT_PATH + PARTNER_ID_1).request().get(Campaign.class);

		Assert.assertEquals(campaign.getId(), response.getId());
		Assert.assertTrue(response.isActive());
	}

}
