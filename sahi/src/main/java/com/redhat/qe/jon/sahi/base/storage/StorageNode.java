package com.redhat.qe.jon.sahi.base.storage;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageNode {
	
	private String endpointAddress;
	private String alerts;
	private String memory;
	private String disk;
	private String installationDate;
	private String clusterStatus;
	private String resourceLinkText;
	private String resourceLink;
	
	public String getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts) {
		this.alerts = alerts;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public String getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}

	public String getClusterStatus() {
		return clusterStatus;
	}

	public void setClusterStatus(String clusterStatus) {
		this.clusterStatus = clusterStatus;
	}

	
	private Map<String, StorageNodeMetric> storageNodeDetails;

	public String getResourceLink() {
		return resourceLink;
	}

	public void setResourceLink(String resourceLink) {
		this.resourceLink = resourceLink;
	}

	public String getEndpointAddress() {
		return endpointAddress;
	}

	public void setEndpointAddress(String endpointAddress) {
		this.endpointAddress = endpointAddress;
	}



	public Map<String, StorageNodeMetric> getStorageNodeDetails() {
		return storageNodeDetails;
	}

	public String getResourceLinkText() {
		return resourceLinkText;
	}

	public void setResourceLinkText(String resourceLinkText) {
		this.resourceLinkText = resourceLinkText;
	}

	public void setStorageNodeDetails(Map<String, StorageNodeMetric> storageNodeDetails) {
		this.storageNodeDetails = storageNodeDetails;
	}
	
	public String getId() {
		// http://10.16.23.95:7080/coregui/#Resource/10004
		String resourceLinkPattern = ".*#Resource/(\\d+)";	
		Matcher matcher = Pattern.compile(resourceLinkPattern).matcher(resourceLink);
		return matcher.find()? matcher.group(1) : null;
	}
}
