package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

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
	private Resource httpConnector;
	private Resource myConnector;
	private String virtualServer = "tld.example.org";
	
	@BeforeClass(groups = "setup")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        web = server.child("web");
        httpConnector = web.child("http");
        myConnector = web.child("myConnector");
    }
	@Test(groups={"connector"})
	public void configureConnector() {
		Configuration configuration = httpConnector.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().setText("max-save-post-size", "8192");
		config.save();
		ConfigHistory history = configuration.history();
		history.failOnPending();
		history.failOnFailure();
		// TODO validate using DMR
	}
	
	@Test(groups={"connector"})
	public void createConnector() {
		Inventory inventory = web.inventory();
		NewChildWizard nc = inventory.childResources().newChild("Connector");
		nc.getEditor().setText("resourceName", myConnector.getName());
		nc.next();
		// wait a little bit longer - it takes time to load connector configuration
		sahiTasks.waitFor(Timing.WAIT_TIME);
		nc.getEditor().setText("protocol", "HTTP/1.1");
		nc.getEditor().setText("socket-binding", "http");
		ConfigEntry entry = nc.getEditor().newEntry(0);
		entry.setField("virtual-server", virtualServer);
		entry.OK();
		nc.finish();
		inventory.childHistory().assertLastResourceChange(true);
		// TODO validate using DMR
		myConnector.assertExists(true);

	}
	@Test(dependsOnMethods="createConnector",groups={"connector"})
	public void removeConnector() {
		myConnector.delete();
		web.inventory().childHistory().assertLastResourceChange(true);
		// TODO validate using DMR
		myConnector.assertExists(false);
	}
	
}
