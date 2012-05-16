package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class ManualImportTest extends AS7StandaloneTest {
	
	@BeforeTest()
    protected void setupSysProperty() {
		// remember original as7.standalone1.name system property
		System.setProperty("as7.standalone1.name.original", System.getProperty("as7.standalone1.name"));
		System.setProperty("as7.standalone1.name","EAP (localhost:9990)");
		log.info("System property : [as7.standalone1.name] has now value ["+System.getProperty("as7.standalone1.name")+"]");
    }
	@BeforeClass
	protected void beforeClass() {
		// uninventory original (autodetected) server
		new Resource(sahiTasks,System.getProperty("agent.name"),System.getProperty("as7.standalone1.name.original")).uninventory(false);
		// also uninventory manually imported server (could exist from passed test runs)
		new Resource(sahiTasks,System.getProperty("agent.name"),System.getProperty("as7.standalone1.name")).uninventory(false);
	}
	
	@Test
	public void manualImport() {
		Inventory inventory = server.parent().inventory();
		NewChildWizard child = inventory.childResources().importResource("JBossAS7 Standalone Server");
		child.getEditor().setText("hostname", "localhost");
		child.getEditor().setText("port", "9990");
		child.finish();
		server.assertExists(true);
		server.assertAvailable(true, "Manually imported server MUST be available");
	}
	
	@AfterTest()
	public void restoreSysProperty() {
		// restore original as7.standalone1.name system property
		System.setProperty("as7.standalone1.name",System.getProperty("as7.standalone1.name.original"));
		log.info("System property : [as7.standalone1.name] has now value ["+System.getProperty("as7.standalone1.name")+"]");
	}
}
