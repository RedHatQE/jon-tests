package com.redhat.qe.jon.sahi.base.storage;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageNode {
	private String endpointAddress;
	private String jmxPort;
	private String mode;
	private String installationDate;
	private String lastUpdateTime;
	private String resourceLinkText;
	private String resourceLink;
	
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

	public String getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(String jmxPort) {
		this.jmxPort = jmxPort;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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
