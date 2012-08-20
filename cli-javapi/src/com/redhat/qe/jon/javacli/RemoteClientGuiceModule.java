package com.redhat.qe.jon.javacli;

import org.rhq.enterprise.clientapi.RemoteClient;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.redhat.qe.jon.common.ConfigurationLoader;

public class RemoteClientGuiceModule extends AbstractModule {


	@Override
	protected void configure() {
	}
	
	@Provides
	@Inject
	RemoteClient createNewRemoteClient(ConfigurationLoader config) throws Exception {
		RemoteClient remoteClient = new RemoteClient(null, 
									config.get(ConfigurationLoader.PARAM.HOST_NAME),
									config.getInt(ConfigurationLoader.PARAM.HOST_PORT)
									);

				remoteClient.login(
					config.get(ConfigurationLoader.PARAM.HOST_USER),
					config.get(ConfigurationLoader.PARAM.HOST_PASSWORD)
					);
				
		return remoteClient;
	}

}


