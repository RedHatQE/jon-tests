package com.redhat.qe.jon.sahi.base.bundle;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BundleGroup {
	private String name;
	private String description;
	private List<String> bundles;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getBundles() {
		return bundles;
	}
	public void setBundles(List<String> bundles) {
		this.bundles = bundles;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this).toString();
	}
}
