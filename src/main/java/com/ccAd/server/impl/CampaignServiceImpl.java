package com.ccAd.server.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccAd.common.Campaign;
import com.ccAd.common.exception.ExistingActiveCampaignException;
import com.ccAd.common.exception.NoActiveCampaignException;
import com.ccAd.server.CampaignRepository;
import com.ccAd.server.CampaignService;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Spring backed service (read: singleton) that handles CRUD operations for
 * campaigns. This class delegates to the {@link CampaignRepository} for the
 * actual CRUD operations against the MongoDB instance.
 * 
 * @author Juliano DeCarvalho
 */
@Service
public class CampaignServiceImpl implements CampaignService, ApplicationContextAware {

	/**
	 * Spring repository in charge of dealing with MongoDB operations.
	 */
	@Autowired
	private CampaignRepository campaignRepo;

	/**
	 * MongoDB launcher in charge of starting and stopping the MongoDB instance.
	 */
	private static MongodExecutable mongodExecutable;

	/**
	 * The spring context, used to shutdown the MongoDB instance when the JVM
	 * goes down.
	 */
	private ApplicationContext applicationContext;

	/**
	 * Start MongoDB.
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void start() throws Exception {
		// shutdown spring context on JVM shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				((ConfigurableApplicationContext) applicationContext).close();
			}
		});

		// instantiate the MongoDB embedded server
		if (mongodExecutable == null) {
			MongodStarter starter = MongodStarter.getDefaultInstance();

			int port = 27017;
			IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
					.net(new Net(port, Network.localhostIsIPv6())).build();

			mongodExecutable = starter.prepare(mongodConfig);
			mongodExecutable.start();
		}
	}

	/**
	 * Stop MongoDB.
	 * 
	 * @throws Exception
	 */
	@PreDestroy
	public void stop() throws Exception {
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

	/**
	 * Add a new campaign. Makes sure that an active campaign cannot be added if
	 * one already exists for the specified partner.
	 */
	@Override
	@Transactional
	public void addCampaign(Campaign newCampaign) throws ExistingActiveCampaignException {
		if (newCampaign.isActive() && getActiveCampaign(newCampaign.getPartnerId(), false) != null) {
			throw new ExistingActiveCampaignException(
					"This partner already has an active campaign.  You may only add non-active campaigns.",
					newCampaign.getPartnerId());
		}

		newCampaign.setId(null);
		newCampaign.setLastActivationDate(System.currentTimeMillis());

		campaignRepo.save(newCampaign);
	}

	/**
	 * Activates the specified campaign. If an active campaign already exists,
	 * it is deactivated first.
	 */
	@Override
	@Transactional
	public Campaign activateCampaign(String campaignId) {
		Campaign campaign = campaignRepo.findById(campaignId);
		campaign.setActive(true);
		campaign.setLastActivationDate(System.currentTimeMillis());

		Campaign oldActive = getActiveCampaign(campaign.getPartnerId(), false);
		if (oldActive != null) {
			oldActive.setActive(false);
			campaignRepo.save(oldActive);
		}

		campaignRepo.save(campaign);

		return campaign;
	}

	/**
	 * Get the active campaign for the specified partner. If there is no
	 * campaign, or the current time is greater than the campaing's creation
	 * time + it's duration time, then inactivate campaign and throw an
	 * exception.
	 */
	@Override
	public Campaign getActiveCampaign(long partnerId) throws NoActiveCampaignException {
		return getActiveCampaign(partnerId, true);
	}

	private Campaign getActiveCampaign(long partnerId, boolean errorOnMissing) throws NoActiveCampaignException {
		Campaign campaign = campaignRepo.findByPartnerIdAndActive(partnerId, true);

		if (campaign == null || System.currentTimeMillis() > campaign.getLastActivationDate()
				+ (campaign.getDurationInSeconds() * 1000)) {
			if (campaign != null) {
				campaign.setActive(false);
				campaignRepo.save(campaign);
			}

			if (errorOnMissing) {
				throw new NoActiveCampaignException("No active campaign found for this partner.", partnerId);
			}
		}

		return campaign;
	}

	/**
	 * Returns a list of all campaigns for the specified partner, regardless of
	 * its active state.
	 */
	@Override
	public Iterable<Campaign> getCampaigns(long partnerId) {
		Query query = new Query(Criteria.where("partnerId").is(partnerId));

		return campaignRepo.findByPartnerId(partnerId);
	}

	/**
	 * Called on construction of this bean by Spring, to make its context
	 * available for use by the code.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
