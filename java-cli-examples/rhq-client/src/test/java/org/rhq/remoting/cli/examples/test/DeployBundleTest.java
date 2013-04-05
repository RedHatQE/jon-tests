package org.rhq.remoting.cli.examples.test;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.bundle.BundleDeployment;
import org.rhq.core.domain.bundle.BundleDeploymentStatus;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.DeployBundle;
import org.rhq.remoting.cli.examples.ResourceDiscovery;
import org.rhq.remoting.cli.examples.ResourceGroups;


public class DeployBundleTest {

    RemoteClient client;
    
    @Before
    public void initClient() {
	client = TestUtil.createClient();
	new ResourceDiscovery(this.client).importAllResources();
    }
    
    @After
    public void logoutClient() {
	client.logout();
    }
    
    @Test
    public void deployBundle() throws Exception {
	// delete our resource group for sure
	new ResourceGroups(client).deleteGroup("bundletest-group");
	// we'll use Linux resourceType .. so all linux platforms
	Resource[] resources = new ResourceDiscovery(client).findResources("Linux");
	Assert.assertTrue(resources.length>0);
	// create group containing all linuxes
	ResourceGroup group = new ResourceGroups(client).createGroup("bundletest-group",resources,false);
	
	BundleDeployment deployment = new DeployBundle(client).deployBundle(
		getBundleResource(), 
		group, 
		new Configuration(), // our bundle does not require any input parameters 
		"bundletest", 
		"Root File System",
		"/tmp");
	Assert.assertEquals(BundleDeploymentStatus.SUCCESS, deployment.getStatus());
    }
    
    private File getBundleResource() {
	return new File(getClass().getResource("/bundle.zip").getFile());
    }
}
