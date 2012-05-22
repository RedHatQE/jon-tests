package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;
/**
 * Deploys and deletes WAR 
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 15.12.2011
 * @see TCMS cases 96455 102978 96443
 */
public class DeploymentTest extends AS7StandaloneTest {

	private static final long waitTime = Timing.WAIT_TIME;

	private static final String war = "hello.war";
	private Resource wsResource;
	private Resource warResource;
	@BeforeClass()
    protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		warResource = server.child(war);
		wsResource = server.child("ws.war");
    }

	@Test()
	public void deployWAR() {
		if (mgmtClient.existsResource("", "deployment", war)) {
			removeDeployment(war);
			log.fine("Deployment removed using API, we have perform manual discovery for ds to disappear from RHQ UI");
			log.info("manual discovery");
			server.performManualAutodiscovery();
		}
		Inventory inventory = server.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard newChild = childResources.newChild("Deployment");
		newChild.next();
		newChild.upload("deploy/original/"+war);
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		newChild.next();
		newChild.finish();
		assertDeploymentExists(war);
		warResource.assertExists(true);
		httpClient.assertDeploymentContent(war,"Original","Check whether original version of WAR has been deployed");
	}
	
	@Test
	public void deployWebService() {
		Inventory inventory = server.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard newChild = childResources.newChild("Deployment");
		newChild.next();
		newChild.upload("deploy/"+wsResource.getName());
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		newChild.next();
		newChild.finish();
		assertDeploymentExists(war);
		wsResource.assertExists(true);
	}
	
	// TODO re-deployment must be done using Content subsystem
	//@Test(groups = {"deployment","blockedByBug-767974"}, dependsOnMethods="deployWAR")
	public void deployWARVersion2() {
		Inventory inventory = server.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard newChild = childResources.newChild("Deployment");
		newChild.next();
		newChild.upload("deploy/modified/"+war);
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		newChild.next();
		newChild.finish();
		assertDeploymentExists(war);
		warResource.assertExists(true);
		httpStandalone.assertDeploymentContent(war,"Modified","Check whether modified version of WAR has been deployed");
	}

	@Test(dependsOnMethods="deployWAR")
	public void undeployWAR() {
		warResource.delete();
		assertDeploymentDoesNotExist(war);
		warResource.assertExists(false);
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
