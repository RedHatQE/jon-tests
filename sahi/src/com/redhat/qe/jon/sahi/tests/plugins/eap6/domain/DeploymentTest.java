package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.Navigator.InventoryNavigation;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTestScript;
/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 20.01.2011
 * @see TCMS cases 
 */
public class DeploymentTest extends AS7PluginSahiTestScript {

	private static final long waitTime = 5000;

	private static final String war = "hello.war";
	
	private InventoryNavigation navController;
	
	@BeforeClass(groups = "deployment")
    protected void setupAS7Plugin() {
		navController = new InventoryNavigation(System.getProperty("agent.name"), "Inventory", System.getProperty("as7.domain.controller.name"));
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.inventorizeResourceByName(System.getProperty("agent.name"), System.getProperty("as7.domain.controller.name"));
    }

	@Test(groups = "deployment")
	public void createDomainDeploymentWAR() {
		if (mgmtDomain.existsResource("", "deployment", war)) {
			removeDeployment(war);
			log.fine("Deployment removed using API, we have perform manual discovery for deployment to disappear from RHQ UI");
			log.info("manual discovery");
			as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
			log.info("manual discovery done");
		}
		sahiTasks.getNavigator().inventoryGoToResource(navController);
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell("Create Child"),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("DomainDeployment").in(sahiTasks.table("menuTable")),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();

		sahiTasks.setFileToUpload("fileUploadItem","deploy/original/"+war);
		sahiTasks.xy(sahiTasks.cell("Upload"),3,3).click();
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();
		sahiTasks.waitFor(waitTime);

		sahiTasks.xy(sahiTasks.cell("Finish"),3,3).click();
		mgmtDomain.assertResourcePresence("", "deployment", war, true);
		sahiTasks.assertResourceExists(true,navController.pathPush(war));
	}
	//@Test(groups = "deployment", dependsOnMethods="createDomainDeploymentWAR")
	public void createDomainDeploymentWARv2() {
		sahiTasks.getNavigator().inventoryGoToResource(navController.pathPush(war));
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell("Create Child"),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("DomainDeployment").in(sahiTasks.table("menuTable")),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();
		sahiTasks.setFileToUpload("fileUploadItem","deploy/modified/"+war);
		sahiTasks.xy(sahiTasks.cell("Upload"),3,3).click();
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		sahiTasks.xy(sahiTasks.cell("Next"),3,3).click();
		sahiTasks.waitFor(waitTime);
		sahiTasks.xy(sahiTasks.cell("Finish"),3,3).click();
		mgmtDomain.assertResourcePresence("", "deployment", war, true);
	}
	
	@Test(groups = "deployment", dependsOnMethods="createDomainDeploymentWAR")
	public void deployToServerGroup() {
		sahiTasks.getNavigator().inventoryGoToResource(navController.pathPush(war).setInventoryTab("Operations"));
		sahiTasks.cell("New").click();
		sahiTasks.selectComboBoxes("selectItemText-->Deploy to Server-Group");
		sahiTasks.waitFor(waitTime);
		sahiTasks.radio("main-server-group").check();		
		sahiTasks.waitFor(waitTime);
		Assert.assertFalse(sahiTasks.image("exclamation.png").exists(), "All required inputs were provided");
		sahiTasks.cell("Schedule").click();
		assertOperationSuccess(navController.pathPush(war),"Deploy to Server-Group");
		mgmtDomain.assertResourcePresence("/server-group=main-server-group", "deployment", war, true);
		// deployments on server group are not recognized automatically, we need to run manual discovery
		as7SahiTasks.performManualAutodiscovery(System.getProperty("agent.name"));
		log.fine("Waiting after manual autodiscovery..");
		sahiTasks.waitFor(waitTime*10);
		sahiTasks.assertResourceExists(true,navController.pathPush("main-server-group").pathPush(war));
		// we KNOW that managed servers server-one, server-two belong to main-server-group and are UP 
		// deployment should be on both of them
		httpDomainOne.assertDeploymentContent(war, "Original", "Check whether original version of WAR has been deployed to "+httpDomainOne.getServerAddress());
		httpDomainTwo.assertDeploymentContent(war, "Original", "Check whether original version of WAR has been deployed to "+httpDomainTwo.getServerAddress());
		
	}
	@Test(groups = "deployment", dependsOnMethods="deployToServerGroup")
	public void undeployFromServerGroup() {
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(war), 3, 3).click();
		sahiTasks.byXPath("//td[@class='buttonTitle' and .='Delete']").click();
		sahiTasks.cell("Yes").click();
		mgmtDomain.assertResourcePresence("/server-group=main-server-group", "deployment", war, false);
		sahiTasks.assertResourceExists(false,navController.pathPush("main-server-group").pathPush(war));
		Assert.assertTrue(!httpDomainOne.isDeploymentAvailable(war),"Deployment is no longer reachable on "+httpDomainOne.getServerAddress());
		Assert.assertTrue(!httpDomainTwo.isDeploymentAvailable(war),"Deployment is no longer reachable on "+httpDomainTwo.getServerAddress());
	}
	@Test(groups = "deployment", dependsOnMethods="undeployFromServerGroup")
	public void removeDomainDeployment() {
		sahiTasks.getNavigator().inventoryGoToResource(navController);
		sahiTasks.getNavigator().inventorySelectTab("Inventory", "Child Resources");
		sahiTasks.xy(sahiTasks.cell(war), 3, 3).click();
		sahiTasks.byXPath("//td[@class='buttonTitle' and .='Delete']").click();
		sahiTasks.cell("Yes").click();
		mgmtDomain.assertResourcePresence("", "deployment", war, false);
		sahiTasks.assertResourceExists(false,navController.pathPush(war));
	}


	/**
	 * removes deployment
	 * @param name
	 */
	private void removeDeployment(String name) {
		log.info("remove datasource API");
		if (mgmtDomain.executeOperationVoid("/deployment="+name, "remove", new String[]{})) {
			log.info("[mgmt API] Deployment was removed");
		}
	}
}
