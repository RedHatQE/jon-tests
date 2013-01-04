package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * Unimports autodetected AS7 and imports it manually 
 * @author lzoubek
 *
 */
public class ManualImportTest extends AS7StandaloneTest {
	
	@BeforeTest()
    protected void setupSysProperty() {
		// remember original as7.standalone1.name system property
		System.setProperty("as7.standalone.name.original", System.getProperty("as7.standalone.name"));
		System.setProperty("as7.standalone.name","EAP (localhost:9990)");
		log.info("System property : [as7.standalone.name] has now value ["+System.getProperty("as7.standalone.name")+"]");
    }
	@BeforeClass
	protected void beforeClass() {
		// uninventory original (autodetected) server
		new Resource(sahiTasks,System.getProperty("agent.name"),System.getProperty("as7.standalone.name.original")).uninventory(false);
		// also uninventory manually imported server (could exist from passed test runs)
		new Resource(sahiTasks,System.getProperty("agent.name"),System.getProperty("as7.standalone.name")).uninventory(false);
	}
	
	@Test
	public void manualImport() {
		Inventory inventory = server.parent().inventory();
		NewChildWizard child = inventory.childResources().importResource("JBossAS7 Standalone Server");
		child.getEditor().setText("hostname", "localhost");
		child.getEditor().setText("port", "9990");
		child.getEditor().checkBox(0, false);
		child.getEditor().setText("user", "rhqadmin");
		child.getEditor().checkBox(0, false);
		child.getEditor().setPassword("password", "rhqadmin");
		child.finish();
		server.assertExists(true);
		server.assertAvailable(true, "Manually imported server MUST be available");
	}
	
	@AfterTest()
	public void restoreSysProperty() {
		// restore original as7.standalone1.name system property
		System.setProperty("as7.standalone.name",System.getProperty("as7.standalone.name.original"));
		log.info("System property : [as7.standalone.name] has now value ["+System.getProperty("as7.standalone.name")+"]");
		server.uninventory(false);
	}
}
