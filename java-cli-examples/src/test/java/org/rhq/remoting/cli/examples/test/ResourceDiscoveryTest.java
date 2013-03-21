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
	new ResourceDiscovery().discoveryQueue(this.client);	
    }
    
    @Test
    public void importResources() {
	Resource[] resources = new ResourceDiscovery().discoveryQueue(this.client);
	new ResourceDiscovery().importResources(client, resources);
	Assert.assertTrue(new ResourceDiscovery().discoveryQueue(this.client).length == 0);

    }
    
    @Test
    public void importAllResources() {
	new ResourceDiscovery().importAllResources(client);
	Assert.assertTrue(new ResourceDiscovery().discoveryQueue(this.client).length == 0);
    }
    
    @Test
    public void findResourcesByResourceTypeName() {
	Assert.assertTrue(new ResourceDiscovery().findResources(client, "RHQ Agent").length > 0);
    }
}
