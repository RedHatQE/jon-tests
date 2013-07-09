package com.redhat.qe.jon.sahi.tests;

import java.util.List;

import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.StorageNodesAdministration;
import com.redhat.qe.jon.sahi.base.storage.StorageNode;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class StorageNodesTest extends SahiTestScript {

	protected static String storageNodeName;
	protected static List<StorageNode> storageNodes;
	protected static StorageNodesAdministration storageNodesAdmin;

	@Test
	public void checkStorageNodeDetails() {
		storageNodeName = System.getProperty("jon.server.host");
		storageNodesAdmin = new StorageNodesAdministration(sahiTasks);
		storageNodesAdmin.navigate();
		// get list of storage nodes
		storageNodes = storageNodesAdmin.getStorageNodes();
		// check at least one storage node exist
		Assert.assertTrue(storageNodes.size() >= 1, "Storage node count");

		// get first storage node
		StorageNode storageNode = storageNodes.get(0);

		// check the first storage node properties
		Assert.assertEquals(storageNode.getEndpointAddress(), storageNodeName);
		Assert.assertEquals(storageNode.getJmxPort(), "7299");
		Assert.assertEquals(storageNode.getMode(), "NORMAL");
		Assert.assertNotNull(storageNode.getInstallationDate(),
				"Installation Date");
		Assert.assertNotNull(storageNode.getLastUpdateTime(),
				"last Update Time");
		Assert.assertEquals(storageNode.getResource(), "Link to Resource");
		Assert.assertNotNull(storageNode.getResourceLink(),
				"Link to Resource in Inventory");

		storageNodesAdmin.navigateToStorageNodesDetails(storageNodeName);

		Assert.assertTrue(
				sahiTasks.cell("RHQ Storage Node(" + storageNodeName + ")")
						.exists(), "Associated Resource checked.");
		sahiTasks.link("RHQ Storage Node(" + storageNodeName + ")").click();

		// assert storage node resource is selected/visible in Inventory
		sahiTasks.waitForElementVisible(sahiTasks,
				sahiTasks.cell("RHQ Storage Node(" + storageNodeName + ")"),
				"RHQ Storage Node(" + storageNodeName + ")", Timing.WAIT_TIME);
		Assert.assertTrue(sahiTasks.cell(
				"RHQ Storage Node(" + storageNodeName + ")").exists());
	}

	@Test
	public void checkStorageNodeResourceInInventory() {
		storageNodesAdmin.navigate();
		// get list of storage nodes
		storageNodes = storageNodesAdmin.getStorageNodes();
		// check at least one storage node exist
		Assert.assertTrue(storageNodes.size() >= 1, "Storage node count");

		storageNodesAdmin.navigateToStorageNodesDetails(storageNodeName);
		sahiTasks.bold("Back to List").click();
		sahiTasks.waitForElementVisible(sahiTasks, storageNodes.get(0)
				.getResourceLink(), storageNodes.get(0).getEndpointAddress(),
				Timing.WAIT_TIME);
		storageNodes.get(0).getResourceLink().click();
		// assert storage node resource is selected/visible in Inventory
		sahiTasks.waitForElementVisible(sahiTasks,
				sahiTasks.cell("RHQ Storage Node(" + storageNodeName + ")"),
				"RHQ Storage Node(" + storageNodeName + ")", Timing.WAIT_TIME);

		Assert.assertTrue(sahiTasks.cell(
				"RHQ Storage Node(" + storageNodeName + ")").exists());

	}
}
