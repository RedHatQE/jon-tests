package org.rhq.remoting.cli.examples.test;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.configuration.ResourceConfigurationUpdate;
import org.rhq.core.domain.resource.Resource;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.ResourceConfiguration;
import org.rhq.remoting.cli.examples.ResourceDiscovery;

public class ResourceConfigurationTest {

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
    public void printAgentConfigurationDefinition() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(resources.length>0);
	new ResourceConfiguration(this.client).printConfigurationDefinition(resources[0]);
    }
    
    @Test
    public void printAgentConfiguration() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(resources.length>0);
	new ResourceConfiguration(this.client).printConfiguration(resources[0]);
    }
    
    @Test
    public void updateAgentConfiguration() throws Exception {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(resources.length>0);
	
        // let's update a configuration - we assue that RHQ Client has default configuration
        // (rhq.agent.server.alias != 'test')
	ResourceConfigurationUpdate result = new ResourceConfiguration(this.client)
		.updateResourceConfiguration(resources[0], "rhq.agent.server.alias", "test");
	Assert.assertEquals(ConfigurationUpdateStatus.SUCCESS, result.getStatus());
	
        // let's try to update again, this time our property has already 
        // a value 'test' this means no update should happen
        // and result must be null
	result = new ResourceConfiguration(this.client)
	.updateResourceConfiguration(resources[0], "rhq.agent.server.alias", "test");
	Assert.assertNull(result);
	
        // put configuration back to default value
	result = new ResourceConfiguration(this.client)
	.updateResourceConfiguration(resources[0], "rhq.agent.server.alias", "rhqserver");
	Assert.assertEquals(ConfigurationUpdateStatus.SUCCESS, result.getStatus());
    }
}
