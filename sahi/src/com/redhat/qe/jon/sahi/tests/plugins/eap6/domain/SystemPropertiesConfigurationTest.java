package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import static org.junit.Assert.fail;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class SystemPropertiesConfigurationTest extends AS7DomainTest {
	
	private static final String addedPropName="prop";
	private static final String addedPropValue="value";
	private static final String editedPropValue="value2";
	
	@BeforeClass(groups="configuration")
    protected void setupEapPlugin() {               
        as7SahiTasks.importResource(controller);
    }
	
	@Test(groups={"configuration"})
    public void domainControllerAddPropertyTest() {
    	addSystemProperty(controller);
        Assert.assertTrue(dcReadPropertyValue(addedPropName).equals(addedPropValue),"System property has correct value");
    }
	
	@Test(groups={"configuration"},dependsOnMethods="domainControllerAddPropertyTest")
    public void domainControllerEditPropertyTest() {
    	editSystemProperty(controller);
        Assert.assertTrue(dcReadPropertyValue(addedPropName).equals(editedPropValue),"System property has correct value");
    }
	
	@Test(groups={"configuration"},dependsOnMethods="domainControllerEditPropertyTest")
    public void domainControllerDeletePropertyTest() {
    	deleteProperty(controller);
    	mgmtClient.assertResourcePresence("/", "system-property", addedPropName, false);
    }
	
	
	
	@Test(groups={"configuration"})
    public void hostControllerAddPropertyTest() {
    	addSystemProperty(hostController);
        Assert.assertTrue(hcReadPropertyValue("prop").equals("value"),"System property has correct value");
     }
	
	@Test(groups={"configuration"},dependsOnMethods="hostControllerAddPropertyTest")
    public void hostControllerEditPropertyTest() {
    	editSystemProperty(hostController);
        Assert.assertTrue(hcReadPropertyValue(addedPropName).equals(editedPropValue),"System property has correct value");
    }
	
	@Test(groups={"configuration"},dependsOnMethods="hostControllerEditPropertyTest")
    public void hostControllerDeletePropertyTest() {
    	deleteProperty(hostController);
    	mgmtClient.assertResourcePresence("/host="+hostController.getName()+"/", "system-property", addedPropName, false);
    }
	
	
	
	@Test(groups={"configuration","blockedByBug-708332"})
    public void managedServerAddPropertyTest() {
    	addSystemProperty(serverOne);
        Assert.assertTrue(msReadPropertyValue("prop").equals("value"),"System property has correct value");
    }
	@Test(groups={"configuration"},dependsOnMethods="managedServerAddPropertyTest")
    public void managedServerEditPropertyTest() {
    	editSystemProperty(serverOne);
        Assert.assertTrue(msReadPropertyValue(addedPropName).equals(editedPropValue),"System property has correct value");
    }
	
	@Test(groups={"configuration"},dependsOnMethods="managedServerEditPropertyTest")
    public void managedServerDeletePropertyTest() {
    	deleteProperty(serverOne);
    	String serverConfig = "server-one";
    	mgmtClient.assertResourcePresence("/host="+hostController.getName()+"/server-config="+serverConfig+"/", "system-property", addedPropName, false);
    }
	
	
	
	private void addSystemProperty(Resource resource) {
		Configuration config = resource.configuration();
    	CurrentConfig current = config.current();
    	ConfigEntry entry = current.newEntry(0);
    	entry.setField("name", addedPropName);
    	entry.setField("value", addedPropValue);
    	entry.OK();
        current.save();
        config.history().failOnPending();
        config.history().failOnFailure();
	}
	
	private void editSystemProperty(Resource resource) {
		Configuration config = resource.configuration();
    	CurrentConfig current = config.current();
		ConfigEntry entry = current.getEntry(addedPropName);
		entry.setField("value", editedPropValue);
		entry.OK();
		current.save();		
		config.history().failOnPending();
        config.history().failOnFailure();
	}
	
	public void deleteProperty(Resource resource) {
		Configuration config = resource.configuration();
    	CurrentConfig current = config.current();
		current.removeEntry(addedPropName);
		current.save();
		config.history().failOnPending();
        config.history().failOnFailure();
	}
	
	
	
	
	private String dcReadPropertyValue(String name) {    	
    	return mgmtClient.readAttribute("/system-property="+name, "value").get("result").asString();
    }
	private String hcReadPropertyValue(String name) {    	
    	return mgmtClient.readAttribute("/host="+hostController.getName()+"/system-property="+name, "value").get("result").asString();
    }
	private String msReadPropertyValue(String name) {
		String serverConfig = "server-one";
    	return mgmtClient.readAttribute("/host="+hostController.getName()+"/server-config="+serverConfig+"/system-property="+name, "value").get("result").asString();
    }	
}
