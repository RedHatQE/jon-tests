package org.rhq.remoting.cli.examples.test;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.group.GroupCategory;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.ResourceDiscovery;
import org.rhq.remoting.cli.examples.ResourceGroups;

public class ResourceGroupsTest {

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
    public void createEmptyGroup() {
	new ResourceGroups(client).createGroup("empty", new Resource[0], false);
    }
    
    @Test
    public void createCompatibleGroup() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(resources.length>0);
	ResourceGroup group = new ResourceGroups(client).createGroup("agents", resources, false);
	Assert.assertEquals(GroupCategory.COMPATIBLE, group.getGroupCategory());
	Resource[] groupResources = new ResourceDiscovery(this.client).findResourcesForGroup(group);
	Assert.assertEquals(resources.length, groupResources.length);
	
    }
    @Test
    public void createMixedGroup() {
	Resource[] agents = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(agents.length>0);
	
	Resource[] platforms = new ResourceDiscovery(this.client).findResources("Linux");
	Assert.assertTrue(platforms.length>0);
	
	Resource[] resources = new Resource[agents.length+platforms.length];
	int index = 0;
	for (int i=0;i<agents.length;i++) {
	    resources[i] = agents[i];
	    index++;
	}
	for (int i=0; i<platforms.length;i++) {
	    resources[index+i] = platforms[i];
	}
	
	ResourceGroup group = new ResourceGroups(client).createGroup("mixed", resources, false);
	Assert.assertEquals(GroupCategory.MIXED, group.getGroupCategory());
	Resource[] groupResources = new ResourceDiscovery(this.client).findResourcesForGroup(group);
	Assert.assertEquals(resources.length, groupResources.length);
    }
    
}
