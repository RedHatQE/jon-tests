package com.redhat.qe.jon.sahi.base.administration;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.storage.StorageNode;
import com.redhat.qe.jon.sahi.tasks.Navigator;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class StorageNodesAdministration {
	private static Logger log = Logger.getLogger(Navigator.class.getName());
	protected SahiTasks tasks = null;
	protected Editor editor = null;
	//private static String storageIP = System.getProperty("jon.server.host");
	private static final int ROW_START_INDEX = 1;
	private static final int COLUMN_START_INDEX = 2;
	private static final int TABLE_COLUMNS = 7;
	
	public StorageNodesAdministration(SahiTasks tasks) {
		this.tasks = tasks;
		this.editor = new Editor(tasks);
	}
	
	/**
	 * Navigates to Storage Nodes page
	 * @return
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
		ElementStub storageNodeElements = tasks.byXPath("//a[contains(@href,'Administration/Topology/StorageNodes')]");
		return storageNodeElements.countSimilar();
	}
	
	public List<StorageNode> getStorageNodes() {
		int tableRowCount = getStorageNodeCount() + ROW_START_INDEX;
		List<StorageNode> storageNodes = new LinkedList<StorageNode>();
		for(int i = ROW_START_INDEX; i < tableRowCount; i++) {
			StorageNode storageNode = new StorageNode();
			for (int j = COLUMN_START_INDEX; j <= TABLE_COLUMNS; j++) {
				String xPath = "//tbody[2]/tr[" + i + "]/td[" + j + "]/div";
				if  (j == COLUMN_START_INDEX || j == TABLE_COLUMNS) {
					xPath += "/a";
				}
				ElementStub element = tasks.byXPath(xPath);
				
				switch(j) {
					case 2:
						storageNode.setEndpointAddress(element.getText());
						break;
					case 3:
						storageNode.setJmxPort(element.getText());
						break;
					case 4:
						storageNode.setMode(element.getText());
						break;
					case 5:
						storageNode.setInstallationDate(element.getText());
						break;
					case 6:
						storageNode.setLastUpdateTime(element.getText());
						break;
					case 7:
						storageNode.setResource(element.getText());
						storageNode.setResourceLink(tasks.link(element.getText()+"["+(i-1)+"]"));
						break;
				}
					
			}
			storageNodes.add(storageNode);
		}
		return storageNodes;
	}
	
	public StorageNodesAdministration navigateToStorageNodesDetails(final String storageIP) {
		log.fine("Clicking on Storage Node endpoint address - Storage Node Details page will be displayed.");
		tasks.link(storageIP).click();
		return this;
	}
	

}
