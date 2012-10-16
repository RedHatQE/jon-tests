package com.redhat.qe.jon.javacli;

import org.rhq.enterprise.clientapi.RemoteClient;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

public class RemoteClientGuiceModule extends AbstractModule {


	@Override
	protected void configure() {
	}
	
	@Provides
	@Inject
	RemoteClient createNewRemoteClient() throws Exception {
		String host = System.getProperty("jon.server.host");
		if (host==null) {
			throw new Exception("System property [jon.server.host] is required!");
		}
		String port = System.getProperty("jon.server.port","7080");
		RemoteClient remoteClient = new RemoteClient(null,host,Integer.parseInt(port));
		remoteClient.login("rhqadmin", "rhqadmin");		
		return remoteClient;
	}

}


