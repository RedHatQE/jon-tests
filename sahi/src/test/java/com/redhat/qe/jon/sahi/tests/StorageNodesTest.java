package com.redhat.qe.jon.sahi.tests;

import java.util.List;

import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.StorageNodesAdministration;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.storage.StorageNode;
import com.redhat.qe.jon.sahi.base.storage.StorageNodeMetric;
import com.redhat.qe.jon.sahi.base.storage.StorageNodeMetricConst;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class StorageNodesTest extends SahiTestScript {
	
	@Test
	public void checkStorageNodes() throws ParseException {
		String storageNodeName;
		List<StorageNode> storageNodes;
		StorageNodesAdministration storageNodesAdmin;
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
		Assert.assertEquals(storageNode.getEndpointAddress(), storageNodeName,
				"Endpoint address");
		Assert.assertEquals(storageNode.getJmxPort(), "7299", "JMX port");
		Assert.assertEquals(storageNode.getMode(), "NORMAL", "Mode");
		Assert.assertNotNull(storageNode.getInstallationDate(),
				"Installation date");
		Assert.assertNotNull(storageNode.getLastUpdateTime(),
				"Last update time");
		Assert.assertEquals(storageNode.getResourceLinkText(),
				"Link to Resource");
		Assert.assertNotNull(storageNode.getResourceLink(),
				"Link to resource in inventory");

		storageNodesAdmin.navigateToStorageNodesDetails(storageNodeName);

		Assert.assertTrue(
				sahiTasks.link("RHQ Storage Node(" + storageNodeName + ")")
						.exists(), "Associated Resource checked");
		Assert.assertTrue(
				storageNodesAdmin.isStorageNodeResourceLinkCorrect(storageNode),
				"Link to resource in inventory");

		Resource.createUsingId(sahiTasks, storageNodes.get(0).getId())
				.navigate();
		// assert storage node resource is selected/visible in Inventory
		sahiTasks.waitForElementVisible(sahiTasks,
				sahiTasks.cell("RHQ Storage Node(" + storageNodeName + ")"),
				"RHQ Storage Node(" + storageNodeName + ")", Timing.WAIT_TIME);
		Assert.assertTrue(sahiTasks.cell(
				"RHQ Storage Node(" + storageNodeName + ")").exists());
	}

	private void checkMetricRelation(StorageNodeMetric storageNodeMetric, String metric) {
		// remove from strings units and then convert them to double
		double min = Double.valueOf(storageNodeMetric.getMin().split(" ")[0]);
		double avg = Double.valueOf(storageNodeMetric.getAvg().split(" ")[0]);
		double max = Double.valueOf(storageNodeMetric.getMax().split(" ")[0]);
		
		Assert.assertTrue(min <= avg, metric + " min value relation");
		Assert.assertTrue(avg >= min && avg <= max, metric + " avg value relation");
		Assert.assertTrue(max >= avg, metric + " max value relation");
	}
	
	private void checkStorageNodeMetric(StorageNode storageNode, String metric) {
		// get certain metric values
		StorageNodeMetric storageNodeMetric = storageNode
				.getStorageNodeDetails().get(metric);
		Assert.assertNotNull(storageNodeMetric, "Metric " + metric);
		Assert.assertNotNull(storageNodeMetric.getMin(), metric + " min value");
		Assert.assertNotNull(storageNodeMetric.getAvg(), metric + " avg value");
		Assert.assertNotNull(storageNodeMetric.getMax(), metric + " max value");
		
		if (metric.equals(StorageNodeMetricConst.LOAD) ||
				metric.equals(StorageNodeMetricConst.DATA_DISK_SPACE_PERCENT_USED) ||
				metric.equals(StorageNodeMetricConst.TOTAL_DISK_SPACE_PERCENT_USED) ||
				metric.equals(StorageNodeMetricConst.TOTAL_DISK_SPACE_USED) ||
				metric.equals(StorageNodeMetricConst.FREE_DISK_TO_DATA_SIZE_RATIO) ||
				metric.equals(StorageNodeMetricConst.HEAP_MAXIMUM) ||
				metric.equals(StorageNodeMetricConst.HEAP_USED) ||
				metric.equals(StorageNodeMetricConst.HEAP_PERCENT_USED)) {
			// check disk and memory metrics relations
			checkMetricRelation(storageNodeMetric, metric);
		}
	}

	@Test
	public void checkStorageNodeDetails() {
		List<StorageNode> storageNodes;
		StorageNodesAdministration storageNodesAdmin;
		storageNodesAdmin = new StorageNodesAdministration(sahiTasks);
		storageNodesAdmin.navigate();
		// get list of storage nodes
		storageNodes = storageNodesAdmin.getStorageNodes();
		// check at least one storage node exist
		Assert.assertTrue(storageNodes.size() >= 1, "Storage node count");
		// get storage node with details
		StorageNode storageNode = storageNodesAdmin
				.getStorageNodesDetails(storageNodes.get(0));

		checkStorageNodeMetric(storageNode, StorageNodeMetricConst.HEAP_MAXIMUM);
		checkStorageNodeMetric(storageNode, StorageNodeMetricConst.HEAP_USED);
		checkStorageNodeMetric(storageNode,
				StorageNodeMetricConst.HEAP_PERCENT_USED);
		checkStorageNodeMetric(storageNode, StorageNodeMetricConst.LOAD);
		checkStorageNodeMetric(storageNode,
				StorageNodeMetricConst.DATA_DISK_SPACE_PERCENT_USED);
		checkStorageNodeMetric(storageNode,
				StorageNodeMetricConst.TOTAL_DISK_SPACE_PERCENT_USED);
		checkStorageNodeMetric(storageNode,
				StorageNodeMetricConst.TOTAL_DISK_SPACE_USED);
		checkStorageNodeMetric(storageNode, StorageNodeMetricConst.OWNERSHIP);
		checkStorageNodeMetric(storageNode,
				StorageNodeMetricConst.NUMBER_OF_TOKENS);
		checkStorageNodeMetric(storageNode,
				StorageNodeMetricConst.FREE_DISK_TO_DATA_SIZE_RATIO);
	}
	
	@Test
	public void checkStoragesInCluster() {
		String count = System.getProperty("jon.storage.count");
		if (count != null) {
			int storagesCount = Integer.parseInt(count);

			StorageNodesAdministration storageNodesAdmin;
			storageNodesAdmin = new StorageNodesAdministration(sahiTasks);
			storageNodesAdmin.navigate();

			for (int i = 0; i < storagesCount; i++) {
				String storageIP = System.getProperty("rhq.storage.name"
						+ (i + 1));
				StorageNode storageNode = storageNodesAdmin.getStorageNodes()
						.get(i);
				Assert.assertEquals(storageNode.getEndpointAddress(),
						storageIP, "Check storage node with endpoint address "
								+ storageIP);

				storageNodesAdmin.navigateToStorageNodesDetails(storageIP);
				Assert.assertTrue(
						sahiTasks.link("RHQ Storage Node(" + storageIP + ")")
								.exists(),
						"Check associated resource with storage node endpoint address "
								+ storageIP);

				// check the storage node ownership
				StorageNodeMetric storageNodeMetric = storageNodesAdmin
						.getStorageNodesDetails(storageNode)
						.getStorageNodeDetails()
						.get(StorageNodeMetricConst.OWNERSHIP);
				Assert.assertEquals(storageNodeMetric.getMin(), "100.0 %",
						"Check ownership min value of storage node with endpoint address " + storageIP);
				Assert.assertEquals(storageNodeMetric.getAvg(), "100.0 %",
						"Check ownership avg value of storage node with endpoint address " + storageIP);
				Assert.assertEquals(storageNodeMetric.getMax(), "100.0 %",
						"Check ownership max value of storage node with endpoint address " + storageIP);
				
				storageNodesAdmin.navigate();
			}
		}
	}
}
