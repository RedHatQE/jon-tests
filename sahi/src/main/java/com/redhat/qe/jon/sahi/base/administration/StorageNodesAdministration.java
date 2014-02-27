package com.redhat.qe.jon.sahi.base.administration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.storage.StorageNode;
import com.redhat.qe.jon.sahi.base.storage.StorageNodeMetric;
import com.redhat.qe.jon.sahi.base.storage.StorageNodeMetricConst;
import com.redhat.qe.jon.sahi.tasks.Navigator;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class StorageNodesAdministration {
	private static Logger log = Logger.getLogger(Navigator.class.getName());
	protected SahiTasks tasks = null;
	protected Editor editor = null;
	
	public StorageNodesAdministration(SahiTasks tasks) {
		this.tasks = tasks;
		this.editor = new Editor(tasks);
	}
	
	/**
	 * Navigates to Storage Nodes page
	 * @return this object
	 */
	public StorageNodesAdministration navigate() {
		String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
		String url = serverBaseUrl+"/#Administration/Topology/StorageNodes";
		log.fine("Navigating to ["+url+"]");
		tasks.navigateTo(url,false);
		tasks.waitForElementVisible(tasks, tasks.cell("Storage Nodes"), "Storage Nodes", Timing.WAIT_TIME);
		Assert.assertTrue(tasks.cell("Storage Nodes").exists());
		return this;
	}
	
	private int getStorageNodeCount() {
		ElementStub storageNodeElements = tasks.image("row_collapsed.png");
		return storageNodeElements.countSimilar();
	}
	
	public List<StorageNode> getStorageNodes() {
		int tableRowCount = getStorageNodeCount();
		
		List<StorageNode> storageNodes = new LinkedList<StorageNode>();
		for(int i = 0; i < tableRowCount; i++) {
			StorageNode storageNode = new StorageNode();
			ElementStub img = tasks.image("row_collapsed.png[" + i + "]");
			ElementStub trElem = img.parentNode("tr");
			
			log.info("storage node row detected");
			storageNode.setEndpointAddress(tasks.cell(1).in(trElem).getText());
			storageNode.setAlerts(tasks.cell(2).in(trElem).getText());
			storageNode.setMemory(tasks.cell(3).in(trElem).getText());
			storageNode.setDisk(tasks.cell(4).in(trElem).getText());
			storageNode.setClusterStatus(tasks.cell(5).in(trElem).getText());
			storageNode.setInstallationDate(tasks.cell(6).in(trElem).getText());
			storageNode.setResourceLinkText(tasks.cell(7).in(trElem).getText());
			
			log.info("### storage node link to resource detection");
			String setResourceLink = tasks.link("").in(tasks.cell(7).in(trElem)).fetch("href");
			log.info("### storage node link to resource detection 2");
			storageNode.setResourceLink(setResourceLink);
			storageNodes.add(storageNode);
		}
		return storageNodes;
	}
	
	public void navigateToStorageNodesDetails(final String storageIP) {
		log.fine("Clicking on Storage Node with IP " + storageIP + " - Storage Node Details page will be displayed.");
		tasks.link(storageIP).click();
	}
	
	private StorageNodeMetric createAndFillStorageNodeMetric(final String metricName) {
		StorageNodeMetric storageNodeMetric = new StorageNodeMetric();
		ElementStub trElem = tasks.cell(metricName).parentNode("tr");
		storageNodeMetric.setMin(tasks.cell(1).in(trElem).getText()); 
		storageNodeMetric.setAvg(tasks.cell(2).in(trElem).getText());
		storageNodeMetric.setMax(tasks.cell(3).in(trElem).getText());
		return storageNodeMetric;
	}
	
	private Map<String, StorageNodeMetric> createAndFillStorageNodeDetails() {
		Map<String, StorageNodeMetric> storageNodeDetails = new HashMap<String, StorageNodeMetric>();
		storageNodeDetails.put(StorageNodeMetricConst.HEAP_USED, createAndFillStorageNodeMetric(StorageNodeMetricConst.HEAP_USED));
		storageNodeDetails.put(StorageNodeMetricConst.HEAP_PERCENT_USED, createAndFillStorageNodeMetric(StorageNodeMetricConst.HEAP_PERCENT_USED));
		storageNodeDetails.put(StorageNodeMetricConst.TOTAL_DISK_SPACE_USED_BY_STORAGE_NODE, createAndFillStorageNodeMetric(StorageNodeMetricConst.TOTAL_DISK_SPACE_USED_BY_STORAGE_NODE));
		storageNodeDetails.put(StorageNodeMetricConst.TOTAL_DISK_SPACE_PERCENT_USED, createAndFillStorageNodeMetric(StorageNodeMetricConst.TOTAL_DISK_SPACE_PERCENT_USED));
		storageNodeDetails.put(StorageNodeMetricConst.DATA_DISK_SPACE_PERCENT_USED, createAndFillStorageNodeMetric(StorageNodeMetricConst.DATA_DISK_SPACE_PERCENT_USED));
		storageNodeDetails.put(StorageNodeMetricConst.OWNERSHIP, createAndFillStorageNodeMetric(StorageNodeMetricConst.OWNERSHIP));
		storageNodeDetails.put(StorageNodeMetricConst.NUMBER_OF_TOKENS, createAndFillStorageNodeMetric(StorageNodeMetricConst.NUMBER_OF_TOKENS));
		storageNodeDetails.put(StorageNodeMetricConst.FREE_DISK_TO_DATA_SIZE_RATIO, createAndFillStorageNodeMetric(StorageNodeMetricConst.FREE_DISK_TO_DATA_SIZE_RATIO));
		return storageNodeDetails;
	}
	
	public StorageNode getStorageNodesDetails(final StorageNode storageNode) {
		navigateToStorageNodesDetails(storageNode.getEndpointAddress());
		storageNode.setStorageNodeDetails(createAndFillStorageNodeDetails());
		return storageNode;
	}
	
	public boolean isStorageNodeResourceLinkCorrect(final StorageNode storageNode) {
		String resourceLinkPattern = ".*#Resource/\\d+";
		return Pattern.compile(resourceLinkPattern).matcher(storageNode.getResourceLink()).matches();
	}
}
