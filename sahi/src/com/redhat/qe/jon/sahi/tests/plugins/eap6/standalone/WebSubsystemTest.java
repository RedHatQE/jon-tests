package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
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
        myConnector = web.child("myconnector");
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
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=web/connector=http", "max-save-post-size").get("result").asString().equals("8192"),"Connector configuration change was successfull");
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
		nc.getEditor().setText("socket-binding", "http");
//		int scrollbars = sahiTasks.image("vscroll_track.png").countSimilar();
//		if (scrollbars>1) {
//			ElementStub es = sahiTasks.image("vscroll_track.png["+(scrollbars-1)+"]");
//			String strHeight = es.style("height");
//			strHeight = strHeight.replace("px", "");
//			int height = Integer.parseInt(strHeight);
//			log.fine("height "+height);
//			sahiTasks.xy(es,3,height-3).click();
//			sahiTasks.waitFor(Timing.WAIT_TIME);
//			sahiTasks.xy(sahiTasks.image("vscroll_end.png[1]"),3,3).click();
//			for (int i = 0; i < 10 ; i++) {
//				sahiTasks.xy(sahiTasks.image("vscroll_Over_end.png"),3,3).click();
//			}
//		}
//		sahiTasks.waitFor(Timing.WAIT_TIME);
		nc.finish();
		sahiTasks.waitFor(Timing.WAIT_TIME);
		
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsytem=web", "connector", myConnector.getName(), true);
		myConnector.assertExists(true);

	}
	@Test(dependsOnMethods="createConnector",groups={"connector"})
	public void removeConnector() {
		myConnector.delete();
		web.inventory().childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsytem=web", "connector", myConnector.getName(), false);
		myConnector.assertExists(false);
	}
	
}
