package com.redhat.qe.jon.sahi.base.bundle;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Bundle {	
	private String name;
	private String description;
	private String url;
	private String userName;
	private String password;
	private String filename;
	private String recipe;
	private String recipeFile;
	private List<String> groups;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getRecipe() {
		return recipe;
	}
	public void setRecipe(String recipe) {
		this.recipe = recipe;
	}
	public String getRecipeFile() {
		return recipeFile;
	}
	public void setRecipeFile(String recipeFile) {
		this.recipeFile = recipeFile;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this).toString();
	}
}
