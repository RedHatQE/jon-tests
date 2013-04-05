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
    public void updateAgentConfiguration() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(resources.length>0);
	
	ResourceConfigurationUpdate result = new ResourceConfiguration(this.client)
		.updateResourceConfiguration(resources[0], "rhq.agent.server.alias", "test");
	Assert.assertEquals(ConfigurationUpdateStatus.SUCCESS, result.getStatus());
	
	result = new ResourceConfiguration(this.client)
	.updateResourceConfiguration(resources[0], "rhq.agent.server.alias", "test");
	Assert.assertNull(result);
	
	result = new ResourceConfiguration(this.client)
	.updateResourceConfiguration(resources[0], "rhq.agent.server.alias", "rhqserver");
	Assert.assertEquals(ConfigurationUpdateStatus.SUCCESS, result.getStatus());
    }
}
