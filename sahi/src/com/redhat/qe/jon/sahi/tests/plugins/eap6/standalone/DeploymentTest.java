package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.sf.sahi.client.ElementStub;

import org.jboss.dmr.ModelNode;
import com.redhat.qe.auto.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 15.12.2011
 * @see TCMS cases 96455 102978 96443
 */
public class DeploymentTest extends AS7PluginSahiTestScript {

	private static final long waitTime = 5000;

	private static final String war = "hello.war";
	@BeforeClass(groups = "deployment")
    protected void setupAS7Plugin() {
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
    }

	@Test(groups = "deployment")
	public void deployWAR() {
		if (mgmtStandalone.existsResource("", "deployment", war)) {
			removeDeployment(war);
			log.fine("Deployment removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
			log.info("manual discovery done");
		}
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"));
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell("Create Child"),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Deployment").in(sahiTasks.table("menuTable")),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();

		sahiTasks.setFileToUpload("fileUploadItem","deploy/original/"+war);
		sahiTasks.xy(sahiTasks.cell("Upload"),3,3).click();
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();
		sahiTasks.waitFor(waitTime);

		sahiTasks.xy(sahiTasks.cell("Finish"),3,3).click();
		assertDeploymentExists(war);
		Assert.assertTrue(existsResourceUI(war),"Deployment discovered by agent");
		assertDeploymentContent(war,"Original","Check whether original version of WAR has been deployed");
	}
	@Test(groups = {"deployment","blockedByBug-767974"}, dependsOnMethods="deployWAR")
	public void deployWARVersion2() {
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell("Create Child"),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Deployment").in(sahiTasks.table("menuTable")),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();
		sahiTasks.setFileToUpload("fileUploadItem","deploy/modified/"+war);
		sahiTasks.xy(sahiTasks.cell("Upload"),3,3).click();
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Finish"),3,3).click();
		assertDeploymentExists(war);
		Assert.assertTrue(existsResourceUI(war),"Deployment discovered by agent");
		assertDeploymentContent(war,"Modified","Check whether modified version of WAR has been deployed");
	}

	@Test(groups = "deployment", dependsOnMethods="deployWAR")
	public void undeployWAR() {
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(war), 3, 3).click();
		sahiTasks.byXPath("//td[@class='buttonTitle' and .='Delete']").click();
		sahiTasks.cell("Yes").click();
		assertDeploymentDoesNotExist(war);
		Assert.assertFalse(existsResourceUI(war), "Deployment exists in UI");
	}
	
	private void assertDeploymentContent(String deployment,String contains,String message) {
		String context = deployment.replaceFirst("\\..*", "");
		String url = "http://"+System.getProperty("as7.standalone.hostname")+":"+System.getProperty("as7.standalone.http.port")+"/"+context;
		HttpURLConnection connection = null;
		try {
			URL u = new URL(url);
			connection = (HttpURLConnection) u.openConnection();
			Assert.assertTrue(connection.getResponseCode() == HttpURLConnection.HTTP_OK, "Deployment "+deployment+" is reachable on EAP via HTTP request");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			for (String line; (line = reader.readLine()) != null;) {
	            if (line.contains(contains)) {
	            	Assert.assertTrue(true, message);
	            	return;
	            }
	        }
			Assert.assertTrue(false,message);
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private boolean existsResourceUI(String name) {
		sahiTasks.getNavigator().inventoryGoToResource(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.standalone.name"));
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		return sahiTasks.cell(name).exists();
	}
	private void assertDeploymentExists(String name) {
		mgmtStandalone.assertResourcePresence("", "deployment", name, true);
	}

	private void assertDeploymentDoesNotExist(String name) {
		mgmtStandalone.assertResourcePresence("", "deployment", name, false);
	}

	/**
	 * removes deployment
	 * @param name
	 */
	private void removeDeployment(String name) {
		log.info("remove datasource API");
		if (mgmtStandalone.executeOperationVoid("/deployment="+name, "remove", new String[]{})) {
			log.info("[mgmt API] Deployment was removed");
		}
	}
}
