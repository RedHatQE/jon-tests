package org.rhq.remoting.cli.examples.test;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.operation.OperationRequestStatus;
import org.rhq.core.domain.operation.ResourceOperationHistory;
import org.rhq.core.domain.resource.Resource;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.PrintUtil;
import org.rhq.remoting.cli.examples.ResourceDiscovery;
import org.rhq.remoting.cli.examples.ResourceOperation;

public class ResourceOperationTest {

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
    public void viewProcessList() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("Linux");
	Assert.assertTrue(resources.length>0);
	ResourceOperationHistory result = new ResourceOperation(client).runResourceOperation(resources[0], "viewProcessList", null);
	Assert.assertEquals(OperationRequestStatus.SUCCESS, result.getStatus());
	PrintUtil.printConfiguration(result.getResults());
    }
    
    @Test
    public void runAgentCommand() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("RHQ Agent");
	Assert.assertTrue(resources.length>0);
	Configuration result = new ResourceOperation(client).runRHQAgentCommand(resources[0], "avail -f");
	PrintUtil.printConfiguration(result);
    }
    
}
