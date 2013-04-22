package org.rhq.remoting.cli.examples.test;


import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.measurement.Availability;
import org.rhq.core.domain.measurement.MeasurementData;
import org.rhq.core.domain.resource.Resource;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.ResourceDiscovery;
import org.rhq.remoting.cli.examples.ResourceMonitoring;

public class ResourceMonitoringTest {

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
    public void getCurrentAvailability() {
	Resource[] resources = new ResourceDiscovery(this.client).findResources("Linux");
	Assert.assertTrue(resources.length>0);
	Availability availability = new ResourceMonitoring(client).getCurrentAvailability(resources[0]);
        Assert.assertNotNull(availability);
        Assert.assertTrue(new ResourceMonitoring(client).isAvailable(resources[0]));        
    }
    
    @Test
    public void getLiveMetricData() {
        Resource[] resources = new ResourceDiscovery(this.client).findResources("Linux");
	Assert.assertTrue(resources.length>0);
	Set<MeasurementData> data = new ResourceMonitoring(client).getLiveMetricData(resources[0], "Free Memory");
        Assert.assertNotNull(data);
        Assert.assertTrue(data.size()>0);
    }
    
}
