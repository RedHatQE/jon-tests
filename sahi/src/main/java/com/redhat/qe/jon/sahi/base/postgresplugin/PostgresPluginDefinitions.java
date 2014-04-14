package com.redhat.qe.jon.sahi.base.postgresplugin;

/**
 * @author mmahoney
 */

public class PostgresPluginDefinitions {
	private String databaseName = "rhq";
	private String username = "postgres";
	private String password = "postgres";
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String value) {
		this.username = value;
	}
	
	public String getpassword() {
		return this.password;
	}
	
	public void setpassword(String value) {
		this.password = value;
	}
	
	public String getDatabaseName() {
		return this.databaseName;
	}
	
	public void setDatabaseName(String value) {
		this.databaseName = value;
	}
}
