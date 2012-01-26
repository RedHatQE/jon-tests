package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.security.NoSuchAlgorithmException;

import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.ManagementClient;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.util.SSHClient;

public class ConnectToSecured extends AS7PluginSahiTestScript {

	private SSHClient sshClient;
	private ManagementClient mgmtClient;
	
	@BeforeClass(groups = "secure")
	protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
		as7SahiTasks.inventorizeResourceByName(
				System.getProperty("agent.name"),
				System.getProperty("as7.standalone.name"));
		
		sshClient = sshStandalone;
		mgmtClient = mgmtStandalone;
		sshClient.connect();
	}

	@Test(groups = "secure")
	public void connectToSecured() {
		// our mgmt client defines credentials that is able to pass to server
		// we take them and make server to require them
		String user = mgmtClient.getUsername();
		String pass = mgmtClient.getPassword();
		String hash = null;
		// let's generate hash for pass and store it into mgmt-users.properties
		try {
			hash = new UsernamePasswordHashUtil().generateHashedHexURP(user,
					"ManagementRealm", pass.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"Unable to generate password hash to setup EAP", e);
		}
		StringBuilder command = new StringBuilder("echo " + user + "=" + hash
				+ " >> ");

		command.append(sshClient.getAsHome()
				+ "/standalone/configuration/mgmt-users.properties");
		sshClient.runAndWait(command.toString());
		// use SED to enable ManagementRealm on native interface
		command = new StringBuilder(
				"sed -i \'s/<native-interface[^>]*>/<native-interface security-realm=\"ManagementRealm\">/' ");
		command.append(sshClient.getAsHome()
				+ "/standalone/configuration/standalone.xml");
		sshClient.runAndWait(command.toString());
		// use SED to enable ManagementRealm on HTTP interface
		command = new StringBuilder(
				"sed -i \'s/<http-interface[^>]*>/<http-interface security-realm=\"ManagementRealm\">/' ");
		command.append(sshClient.getAsHome()
				+ "/standalone/configuration/standalone.xml");
		sshClient.runAndWait(command.toString());
		
		// now we restart server
		sshClient.restart("standalone.sh");

		try {
			log.info("Since now, standalone mgmt and HTTP API requires authorization");
			// let's wait some time 'till agent restarts EAP and EAP stands up
			sahiTasks.waitFor(60 * 1000);
			Assert.assertTrue(mgmtClient.isAuthRequired(),
					"EAP configuration was successfully changed to require username and password");
			boolean ok = false;
			for (int i = 0; i < 10; i++) {
				sahiTasks.waitFor(30000);
				log.fine("Checking that resource is offline: try #"
						+ Integer.toString(i + 1) + " of 10");
				if (!as7SahiTasks.checkIfResourceIsOnline(
						System.getProperty("agent.name"),
						System.getProperty("as7.standalone.name"))) {
					log.fine("Success - resource is offline!");
					ok = true;
					break;
				}
			}
			Assert.assertTrue(ok,
					"EAP server is offline when not having set auth credentials properly");
			ok = false;

			// lets setup user and pass
			sahiTasks.getNavigator().inventoryGoToResource(
					System.getProperty("agent.name"), "Inventory",
					System.getProperty("as7.standalone.name"));
			sahiTasks.waitFor(5000);
			sahiTasks.getNavigator().inventorySelectTab("Inventory",
					"Connection Settings");
			if (sahiTasks.image("checked.png").exists()) {
				sahiTasks.image("checked.png").click();
			}
			sahiTasks.textbox("user").setValue(user);

			if (sahiTasks.image("checked.png").exists()) {
				sahiTasks.image("checked.png").click();
			}
			sahiTasks.password("password").setValue(pass);

			sahiTasks.cell("Save").click();
			for (int i = 0; i < 12; i++) {
				sahiTasks.waitFor(30000);
				log.fine("Checking that resource is online: try #"
						+ Integer.toString(i + 1) + " of 12");
				if (as7SahiTasks.checkIfResourceIsOnline(
						System.getProperty("agent.name"),
						System.getProperty("as7.standalone.name"))) {
					log.fine("Success - Resource is back online!");
					ok = true;
					break;
				}
			}
			if (!ok) {
				throw new Exception("server did not appear online after setting up correct credentials");
			}
			Assert.assertTrue(ok,
					"EAP server is online after setting up correct credentials");
		} catch (Exception e) {
			// on any error, disable security on server again
			// use SED to enable ManagementRealm on native interface
			command = new StringBuilder(
					"sed -i \'s/<native-interface[^>]*>/<native-interface>/' ");
			command.append(System.getProperty("as7.standalone.home")
					+ "/standalone/configuration/standalone.xml");
			sshClient.runAndWait(command.toString());
			// use SED to enable ManagementRealm on HTTP interface
			command = new StringBuilder(
					"sed -i \'s/<http-interface[^>]*>/<http-interface>/' ");
			command.append(System.getProperty("as7.standalone.home")
					+ "/standalone/configuration/standalone.xml");
			sshClient.runAndWait(command.toString());
			
			// now we restart server
			sshClient.restart("standalone.sh");
			// lets remove user and pass settings
			sahiTasks.getNavigator().inventoryGoToResource(
					System.getProperty("agent.name"), "Inventory",
					System.getProperty("as7.standalone.name"));
			sahiTasks.waitFor(5000);
			sahiTasks.getNavigator().inventorySelectTab("Inventory",
					"Connection Settings");
			if (sahiTasks.image("unchecked.png").exists()) {
				sahiTasks.xy(sahiTasks.image("unchecked.png"),3,3).click();
			}
			if (sahiTasks.image("unchecked.png").exists()) {
				sahiTasks.xy(sahiTasks.image("unchecked.png"),3,3).click();
			}
			sahiTasks.cell("Save").click();
			for (int i = 0; i < 12; i++) {
				sahiTasks.waitFor(30000);
				log.fine("Checking that resource is back online: try #"
						+ Integer.toString(i + 1) + " of 12");
				if (as7SahiTasks.checkIfResourceIsOnline(
						System.getProperty("agent.name"),
						System.getProperty("as7.standalone.name"))) {
					log.fine("Success - Resource is back online!");
					break;
				}
			}
			log.info("server connection recovery done");
			Assert.fail("Failed due to exception: "+e.getMessage(), e);
		}
	}

}
