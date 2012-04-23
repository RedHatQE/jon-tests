package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;

public class SocketBindingGroupConfigurationTest extends AS7StandaloneTest {

	private Resource sbGroup;
	private static final String port="6666";
	private static final String port2="6123";
	private static final String name="testing-service";
	@BeforeClass(groups = "setup")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
        sbGroup = server.child("standard-sockets");
    }
	@Test(groups={"socketBindingGroup","blockedByBug-802794"})
	public void addPort() {
		Configuration configuration = sbGroup.configuration();
		CurrentConfig current = configuration.current();
		ConfigEntry entry = current.newEntry(0);
		entry.setField("port:expr", port);
		entry.setField("name", name);
		entry.OK();
		current.save();
		configuration.history().failOnFailure();
		mgmtClient.assertResourcePresence("/socket-binding-group="+sbGroup.getName(), "socket-binding", name, true);
	}
	@Test(groups="socketBindingGroup",dependsOnMethods="addPort")
	public void editPort() {
		Configuration configuration = sbGroup.configuration();
		CurrentConfig current = configuration.current();
		ConfigEntry entry = current.getEntry(name);
		entry.setField("port:expr", port2);
		entry.OK();
		current.save();
		configuration.history().failOnFailure();
		String hasPort = mgmtClient.readAttribute("/socket-binding-group="+sbGroup.getName()+"/socket-binding="+name, "port").get("result").asString();
		Assert.assertTrue(hasPort.equals(port2), "Attribute [port] was successfully updated");
	}
	@Test(groups="socketBindingGroup",dependsOnMethods="editPort")
	public void deletePort() {
		Configuration configuration = sbGroup.configuration();
    	CurrentConfig current = configuration.current();
		current.removeEntry(name);
		current.save();
		configuration.history().failOnFailure();
		mgmtClient.assertResourcePresence("/socket-binding-group="+sbGroup.getName(), "socket-binding", name, false);
	}
}
