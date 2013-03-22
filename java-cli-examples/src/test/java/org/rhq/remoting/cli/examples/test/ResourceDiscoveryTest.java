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
}
