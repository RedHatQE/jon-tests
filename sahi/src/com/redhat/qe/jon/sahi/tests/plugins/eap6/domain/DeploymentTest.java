package com.redhat.qe.jon.sahi.tests.plugins.eap6.domain;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.AS7PluginSahiTasks;
/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 20.01.2011
 * @see TCMS cases 
 */
public class DeploymentTest extends AS7DomainTest {

	private static final int waitTime = Timing.WAIT_TIME;

	private static final String war = "hello.war";
	Resource serverGroup;
	@BeforeClass(groups = "deployment")
    protected void setupAS7Plugin() {		
		as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.importResource(controller);
        serverGroup = controller.child("main-server-group");
    }

	@Test(groups = "deployment")
	public void createDomainDeploymentWAR() {
		if (mgmtDomain.existsResource("", "deployment", war)) {
			removeDeployment(war);
			log.fine("Deployment removed using API, we have perform manual discovery for deployment to disappear from RHQ UI");
			controller.performManualAutodiscovery();
			
		}
		
		Inventory inventory = controller.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard newChild = childResources.newChild("DomainDeployment");
		newChild.next();
		newChild.upload("deploy/original/"+war);
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		newChild.next();
		newChild.finish();		
		mgmtClient.assertResourcePresence("", "deployment", war, true);
		controller.child(war).assertExists(true);
	}
	//@Test(groups = "deployment", dependsOnMethods="createDomainDeploymentWAR")
	public void createDomainDeploymentWARv2() {		
		Inventory inventory = controller.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard newChild = childResources.newChild("DomainDeployment");
		newChild.next();
		newChild.upload("deploy/modified/"+war);
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		newChild.next();
		newChild.finish();		
		mgmtClient.assertResourcePresence("", "deployment", war, true);
		controller.child(war).assertExists(true);
	}
	
	@Test(groups = "deployment", dependsOnMethods="createDomainDeploymentWAR")
	public void deployToServerGroup() {
		Operations operations = controller.child(war).operations();
		Operation op = operations.newOperation("Deploy to Server-Group");		
		sahiTasks.radio(serverGroup.getName()).check();		
		sahiTasks.waitFor(waitTime);
		op.assertRequiredInputs();
		op.schedule();
		operations.assertOperationResult(op, true);		
		mgmtClient.assertResourcePresence("/server-group="+serverGroup.getName(), "deployment", war, true);
		// deployments on server group are not recognized automatically, we need to run manual discovery
		controller.performManualAutodiscovery();
		log.fine("Waiting "+Timing.toString(waitTime*10)+" after manual autodiscovery..");
		sahiTasks.waitFor(waitTime*10);
		serverGroup.child(war).assertExists(true);
		// we KNOW that managed servers server-one, server-two belong to main-server-group and are UP 
		// deployment should be on both of them
		httpDomainOne.assertDeploymentContent(war, "Original", "Check whether original version of WAR has been deployed to "+httpDomainOne.getServerAddress());
		httpDomainTwo.assertDeploymentContent(war, "Original", "Check whether original version of WAR has been deployed to "+httpDomainTwo.getServerAddress());
		
	}
	@Test(groups = "deployment", dependsOnMethods="deployToServerGroup")
	public void undeployFromServerGroup() {
		serverGroup.inventory().childResources().deleteChild(war);
		mgmtClient.assertResourcePresence("/server-group="+serverGroup.getName(), "deployment", war, false);
		serverGroup.child(war).assertExists(false);
		Assert.assertTrue(!httpDomainOne.isDeploymentAvailable(war),"Deployment is no longer reachable on "+httpDomainOne.getServerAddress());
		Assert.assertTrue(!httpDomainTwo.isDeploymentAvailable(war),"Deployment is no longer reachable on "+httpDomainTwo.getServerAddress());
	}
	@Test(groups = "deployment", dependsOnMethods="undeployFromServerGroup")
	public void removeDomainDeployment() {
		controller.inventory().childResources().deleteChild(war);
		mgmtDomain.assertResourcePresence("", "deployment", war, false);
		controller.child(war).assertExists(false);
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
