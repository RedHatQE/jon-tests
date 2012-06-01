package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
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
	private Resource mdbResource;
	@BeforeClass()
    protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		warResource = server.child("hello.war");
		wsResource = server.child("ws.war");
		mdbResource = server.child("mdb.war");
    }


	private void deployWARFile(Resource war, String srcPath) {
		Inventory inventory = server.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard newChild = childResources.newChild("Deployment");
		newChild.next();
		newChild.upload(srcPath);
		//wait for upload to finish
		sahiTasks.waitFor(2*waitTime);
		newChild.next();
		newChild.finish();
		inventory.childHistory().assertLastResourceChange(true);
		assertDeploymentExists(war.getName());
		war.assertExists(true);
	}
	@Test
	public void deployWebService() {
		deployWARFile(wsResource, "deploy/"+wsResource.getName());
		log.fine("Waiting "+Timing.toString(Timing.TIME_1M)+" for deployment child subsystems to be discovered");
		sahiTasks.waitFor(Timing.TIME_1M);
		wsResource.assertChildExists("webservices",true);
		wsResource.child("webservices").assertChildExists("ws%3AHelloWorld",true);
	}
	@Test(dependsOnMethods="deployWebService")
	public void undeployWebService() {
		undeploy(wsResource);
	}
	
	@Test
	public void deployMessageDrivenBean() {
		deployWARFile(mdbResource, "deploy/"+mdbResource.getName());
		log.fine("Waiting "+Timing.toString(Timing.TIME_1M)+" for deployment child subsystems to be discovered");
		sahiTasks.waitFor(Timing.TIME_1M);
		mdbResource.assertChildExists("messaging",true);
		mdbResource.child("messaging").assertChildExists("default",true);
		mdbResource.child("messaging").child("default").assertChildExists("HELLOWORLDMDBQueue",true);
	}
	
	@DataProvider
	public Object[][] messagingSubsystemOperations() {
		List<OpDef> defs = new ArrayList<OpDef>();		
		defs.add(new OpDef("List Connection IDs"));
		defs.add(new OpDef("List Producers Info as JSON"));
		defs.add(new OpDef("List Connections as JSON"));
		defs.add(new OpDef("List Heuristic Committed Transactions"));
		defs.add(new OpDef("List Prepared Transaction JMS Details as HTML"));
		defs.add(new OpDef("List Prepared Transaction JMS details as JSON"));
		Object[][] output = new Object[defs.size()][];
		
		for (int i=0;i<defs.size();i++) {
			output[i] = new Object[] {defs.get(i)};
		}		
		return output;
	}
	
	@Test(dependsOnMethods="deployMessageDrivenBean",dataProvider="messagingSubsystemOperations")
	public void deployedMessagingSubsystemOperations(OpDef opDef) {
		Resource hornetInstance = mdbResource.child("messaging").child("default");
		Operations operations = hornetInstance.operations();
		Operation op = operations.newOperation(opDef.getName());
		op.schedule();
		operations.assertOperationResult(op, true);
	}
	
	@DataProvider
	public Object[][] jmsQueueOperations() {
		List<OpDef> defs = new ArrayList<OpDef>();		
		defs.add(new OpDef("List Consumers as JSON"));
		defs.add(new OpDef("List Message Counter History as HTML"));
		defs.add(new OpDef("List Message Counter History as JSON"));		
		defs.add(new OpDef("List Message Counter as HTML"));
		defs.add(new OpDef("List Message Counter as JSON"));
		defs.add(new OpDef("List Consumers as JSON"));
		defs.add(new OpDef("List Messages"));
		defs.add(new OpDef("List Messages as JSON"));
		Object[][] output = new Object[defs.size()][];
		
		for (int i=0;i<defs.size();i++) {
			output[i] = new Object[] {defs.get(i)};
		}		
		return output;
	}
	
	@Test(dependsOnMethods="deployMessageDrivenBean",dataProvider="jmsQueueOperations")
	public void deployedJMSQueueOperations(OpDef opDef) {
		Resource jmsQueue = mdbResource.child("messaging").child("default").child("HELLOWORLDMDBQueue");
		Operations operations = jmsQueue.operations();
		Operation op = operations.newOperation(opDef.getName());
		op.schedule();
		operations.assertOperationResult(op, true);
	}
	@Test(alwaysRun=true,dependsOnMethods={"deployedJMSQueueOperations"})
	public void undeployMDB() {
		undeploy(mdbResource);
	}
	
	@Test()
	public void deployWAR() {		
		deployWARFile(warResource, "deploy/original/"+warResource.getName());
		httpClient.assertDeploymentContent(war,"Original","Check whether original version of WAR has been deployed");
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

	@Test(alwaysRun=true,dependsOnMethods="deployWAR")
	public void undeployWAR() {
		undeploy(warResource);
	}
	
	private void undeploy(Resource deployment) {
		deployment.delete();
		assertDeploymentDoesNotExist(deployment.getName());
		deployment.assertExists(false);
	}

	private void assertDeploymentExists(String name) {
		mgmtStandalone.assertResourcePresence("", "deployment", name, true);
	}

	private void assertDeploymentDoesNotExist(String name) {
		mgmtStandalone.assertResourcePresence("", "deployment", name, false);
	}

	public static class OpDef {
		private final String name;
		private final Map<String,String> params = new LinkedHashMap<String, String>();
		public OpDef(String name) {
			this.name=name;
		}
		public Map<String, String> getParams() {
			return params;
		}
		public String getName() {
			return name;
		}
		@Override
		public String toString() {
			return "OperationName="+getName();
		}
	}
}
