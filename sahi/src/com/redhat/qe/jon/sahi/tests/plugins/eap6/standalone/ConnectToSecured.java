package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.security.NoSuchAlgorithmException;

import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ConnectionSettings;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;

public class ConnectToSecured extends AS7StandaloneTest {

	
	@BeforeClass(groups = "secure")
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		
	}

	@Test(groups = "secure")
	public void connectToSecured() {
		// our mgmt client defines credentials that is able to pass to server
		// we take them and make server to require them
		String user = mgmtClient.getUsername();
		String pass = mgmtClient.getPassword();
		String hash = null;
		// we also add default user admin:admin
		String defaultUser = "admin";
		String defaultHash = null;
		// let's generate hash for pass and store it into mgmt-users.properties
		try {
			hash = new UsernamePasswordHashUtil().generateHashedHexURP(user,
					"ManagementRealm", pass.toCharArray());
			defaultHash = new UsernamePasswordHashUtil().generateHashedHexURP(defaultUser,
					"ManagementRealm", "admin".toCharArray());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"Unable to generate password hash to setup EAP", e);
		}
		
		StringBuilder command = new StringBuilder("echo " + defaultUser + "=" + defaultHash
				+ " > ");
		command.append(sshClient.getAsHome()
				+ "/standalone/configuration/mgmt-users.properties");
		sshClient.runAndWait(command.toString());
		log.info("Created default user:pass "+defaultUser+":admin");
		
		command = new StringBuilder("echo " + user + "=" + hash
				+ " >> ");
		command.append(sshClient.getAsHome()
				+ "/standalone/configuration/mgmt-users.properties");
		sshClient.runAndWait(command.toString());
		log.info("Created testing user:pass "+user+":"+pass);
		
		
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
			sahiTasks.waitFor(Timing.TIME_1M);
			if (!sshClient.isRunning() || !httpClient.isRunning() || !mgmtClient.isAuthRequired()) {
				throw new Exception("EAP is not running or authentication was not setup correctly");
			}

			boolean ok = false;
			for (int i = 0; i < Timing.REPEAT; i++) {
				sahiTasks.waitFor(Timing.TIME_30S);
				log.fine("Checking that resource is offline: try #"
						+ Integer.toString(i + 1) + " of "+Timing.REPEAT);
				if (!server.isAvailable()) {
					log.fine("Success - resource is offline!");
					ok = true;
					break;
				}
			}
			if (!ok) {
				throw new Exception("server did not appear OFFLINE in JON after securing and restarting it");
			}
			ok = false;

			// lets setup user and pass
			Inventory inventory = server.inventory();
			ConnectionSettings settings = inventory.connectionSettings();
			
			if (sahiTasks.image("checked.png").exists()) {
				sahiTasks.image("checked.png").click();
			}
			sahiTasks.textbox("user").setValue(user);

			if (sahiTasks.image("checked.png").exists()) {
				sahiTasks.image("checked.png").click();
			}
			sahiTasks.password("password").setValue(pass);

			settings.save();
			
			for (int i = 0; i < Timing.REPEAT; i++) {
				sahiTasks.waitFor(Timing.TIME_30S);
				log.fine("Checking that resource is online: try #"
						+ Integer.toString(i + 1) + " of "+Timing.REPEAT);
				if (server.isAvailable()) {
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

			Inventory inventory = server.inventory();
			ConnectionSettings settings = inventory.connectionSettings();
			
			
			if (sahiTasks.image("unchecked.png").exists()) {
				sahiTasks.xy(sahiTasks.image("unchecked.png"),3,3).click();
			}
			if (sahiTasks.image("unchecked.png").exists()) {
				sahiTasks.xy(sahiTasks.image("unchecked.png"),3,3).click();
			}
			settings.save();
			for (int i = 0; i < 12; i++) {
				sahiTasks.waitFor(30000);
				log.fine("Checking that resource is back online: try #"
						+ Integer.toString(i + 1) + " of 12");
				if (server.isAvailable()) {
					log.fine("Success - Resource is back online!");
					break;
				}
			}
			log.info("server connection recovery done, we continue in UNSECURE mode");
			Assert.fail("Failed due to exception: "+e.getMessage(), e);
		}
	}

}
