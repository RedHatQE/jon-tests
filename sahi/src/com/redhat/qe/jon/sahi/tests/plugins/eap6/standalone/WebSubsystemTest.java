package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigHistory;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * 
 * @author lzoubek
 *
 */
public class WebSubsystemTest extends AS7StandaloneTest {
	private Resource web;
	private Resource defaultConnector;
	private Resource myConnector;
	private Resource myVHost;
	private String virtualServer = "tld.example.org";
	
	@BeforeClass(groups = "setup")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        web = server.child("web");
        defaultConnector = web.child("http");
        myConnector = web.child("myconnector");
        myVHost = web.child("my-host");
    }
	
	@Test(groups={"configure","blockedByBug-815288"})
	public void updateConfiguration() {
		String sendFile = "55555";
		String checkInterval = "60";
		String mappingName = "rhqmapping";
		Configuration configuration = web.configuration();
		CurrentConfig current = configuration.current();
		current.getEditor().setText("sendfile", sendFile);
		current.getEditor().setText("check-interval", checkInterval);
		ConfigEntry ce = current.getEditor().newEntry(0);
		ce.setField("name", mappingName);
		ce.setField("value", "foo");
		ce.OK();
		current.save();
		configuration.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/configuraton=static-resources", "sendfile").get("result").asString().equals(sendFile)," Configuration update for static-resources was successfull");
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/configuraton=jsp-configuration", "check-interval").get("result").asString().equals(checkInterval)," Configuration update for jsp-configuration was successfull");
		// TODO validate mime-mapping
		//Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/configuraton=container", "mime-mapping").get("result").asList().get(0).asPropertyList().get(0).getName().equals(mappingName)," Configuration update for container was successfull");
	}
	
	@Test(groups={"vhost"})
	public void createVHost() {
		Inventory inventory = web.inventory();
		NewChildWizard nc = inventory.childResources().newChild("VHost");
		nc.getEditor().setText("resourceName", myVHost.getName());
		nc.next();		
		ConfigEntry ce = nc.getEditor().newEntry(0);
		ce.setField("alias", virtualServer);
		ce.OK();
		nc.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=web", "virtual-server", myVHost.getName(), true);
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/virtual-server="+myVHost.getName(), "alias").get("result").asList().get(0).asString().equals(virtualServer),"New VHost has correctly set aliases");
		myVHost.assertExists(true);
	}

	@Test(groups={"vhost"},dependsOnMethods="createVHost")
	public void configureVHost() {
		Configuration configuration = myVHost.configuration();
		CurrentConfig config = configuration.current();
		ConfigEntry ce = config.getEditor().newEntry(0);
		ce.setField("alias", "test."+virtualServer);
		ce.OK();
		config.save();
		ConfigHistory history = configuration.history();
		history.failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/virtual-server="+myVHost.getName(), "alias").get("result").asList().get(1).asString().equals("test."+virtualServer),"VHost configuration change was successfull");
	}
	
	@Test(alwaysRun=true,dependsOnMethods="configureVHost",groups={"vhost"})
	public void removeVHost() {
		myVHost.delete();
		web.inventory().childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=web", "virtual-server", myVHost.getName(), false);
		myVHost.assertExists(false);
	}
	

	
	
	@Test(groups={"connector"})
	public void configureConnector() {
		Configuration configuration = defaultConnector.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().setText("max-save-post-size", "8192");
		config.save();
		ConfigHistory history = configuration.history();
		history.failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/connector="+defaultConnector.getName(), "max-save-post-size").get("result").asString().equals("8192"),"Connector configuration change was successfull");
	}
	
	@Test(groups={"connector","blockedByBug-811149"})
	public void createConnector() {
		Inventory inventory = web.inventory();
		NewChildWizard nc = inventory.childResources().newChild("Connector");
		nc.getEditor().setText("resourceName", myConnector.getName());
		nc.next();
		// wait a little bit longer - it takes time to load connector configuration
		sahiTasks.waitFor(Timing.WAIT_TIME);
		nc.getEditor().checkRadio("http");
		nc.getEditor().selectCombo(1,"remoting");
		nc.getEditor().checkBox(0, false);
		nc.getEditor().checkRadio("enabled[1]");
		nc.finish();		
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=web", "connector", myConnector.getName(), true);
		myConnector.assertExists(true);

	}
	@Test(dependsOnMethods="createConnector",groups={"connector"})
	public void removeConnector() {
		myConnector.delete();
		web.inventory().childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=web", "connector", myConnector.getName(), false);
		myConnector.assertExists(false);
	}
	
}
