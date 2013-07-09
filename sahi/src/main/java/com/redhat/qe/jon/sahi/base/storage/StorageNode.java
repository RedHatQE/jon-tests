package com.redhat.qe.jon.sahi.base.storage;

import net.sf.sahi.client.ElementStub;

public class StorageNode {
	private String endpointAddress;
	private String jmxPort;
	private String mode;
	private String installationDate;
	private String lastUpdateTime;
	private String resource;
	private ElementStub resourceLink;

	public ElementStub getResourceLink() {
		return resourceLink;
	}

	public void setResourceLink(ElementStub resourceLink) {
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

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

}
