package org.rhq.remoting.cli.examples.test;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.resource.Resource;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.ResourceDiscovery;

public class ResourceDiscoveryTest {

    RemoteClient client;
    
    @Before
    public void initClient() {
	client = TestUtil.createClient();
    }
    
    @After
    public void logoutClient() {
	client.logout();
    }
    
    @Test
    public void discoveryQueue() {
	new ResourceDiscovery(this.client).discoveryQueue();	
    }
    
    @Test
    public void importResources() {
	Resource[] resources = new ResourceDiscovery(this.client).discoveryQueue();
	new ResourceDiscovery(this.client).importResources(resources);
	Assert.assertTrue(new ResourceDiscovery(this.client).discoveryQueue().length == 0);

    }
    
    @Test
    public void importAllResources() {
	new ResourceDiscovery(this.client).importAllResources();
	Assert.assertTrue(new ResourceDiscovery(this.client).discoveryQueue().length == 0);
    }
    
    @Test
    public void findResourcesByResourceTypeName() {
	Assert.assertTrue(new ResourceDiscovery(this.client).findResources("RHQ Agent").length > 0);
    }
    
    @Test
    public void findChildResources() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("Linux");
	Assert.assertTrue(resources.length>0);
	Resource[] children =  new ResourceDiscovery(this.client).findChildResources(resources[0]);
	Assert.assertTrue(children.length>0);
	// we know that one of child resources is called RHQ Agent	
	for (Resource child : children) {
	    if ("RHQ Agent".equals(child.getName())) {
		return;
	    }
	}
	Assert.fail("RHQ Agent child resource was not found on linux platform");	
    }
}
