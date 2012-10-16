package com.redhat.qe.jon.javacli;

import org.rhq.enterprise.clientapi.RemoteClient;
import org.testng.annotations.Guice;

import com.google.inject.Inject;
import com.redhat.qe.auto.testng.TestScript;

@Guice(modules = RemoteClientGuiceModule.class)
public class RemoteApiTest extends TestScript {

	@Inject 
	protected RemoteClient remoteClient;
}
