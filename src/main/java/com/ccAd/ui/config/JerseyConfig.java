package com.ccAd.ui.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Jersey configuration file in charge of loading Jersey resources and other
 * features.
 * 
 * @author Juliano DeCarvalho
 */
@ApplicationPath("resources")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		// load all Jersey resources recursively in the following package
		packages("com.ccAd");

		// register Jersey XML support through Moxy
		register(MoxyXmlFeature.class);

		// register Jersey JSON support through Moxy
		register(MoxyJsonFeature.class);
	}

}
