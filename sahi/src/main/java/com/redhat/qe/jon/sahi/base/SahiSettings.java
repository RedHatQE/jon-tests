package com.redhat.qe.jon.sahi.base;

import java.io.File;

/**
* @author jkandasa@redhat.com (Jeeva Kandasamy)
* @since Jun 12, 2014
*/
public class SahiSettings {
	private static String jonAgentName;
	private static String jonAgentSSHUser;
	private static String jonAgentSSHPassword;
	
	private static String browserPath;
	private static String browserName;
	private static String browserOpt;

	private static String sahiBaseDir;
	private static String sahiUserdataDir;

	private static String fileStoreUrl;
	
	public static void initSettings(){
		setJonAgentName(System.getProperty("jon.agent.name"));
		setJonAgentSSHUser(System.getProperty("jon.agent.ssh.user"));
		setJonAgentSSHPassword(System.getProperty("jon.agent.ssh.password"));
		setBrowserPath(System.getProperty("jon.browser.path", "/usr/bin/firefox"));
		setBrowserName(System.getProperty("jon.browser.name", "firefox"));
		setBrowserOpt(System.getProperty("jon.browser.opt", ""));
		setSahiBaseDir(System.getProperty("jon.sahi.base.dir", "/home/hudson/sahi"));
		setSahiUserdataDir(System.getProperty("jon.sahi.userdata.dir", sahiBaseDir+File.separator+"userdata"));
		setFileStoreUrl(System.getProperty("jon.file.store.url"));
	}
	public static String getJonAgentName() {
		return jonAgentName;
	}
	public static void setJonAgentName(String jonAgentName) {
		SahiSettings.jonAgentName = jonAgentName;
	}
	public static String getJonAgentSSHUser() {
		return jonAgentSSHUser;
	}
	public static void setJonAgentSSHUser(String jonAgentUser) {
		SahiSettings.jonAgentSSHUser = jonAgentUser;
	}
	public static String getJonAgentSSHPassword() {
		return jonAgentSSHPassword;
	}
	public static void setJonAgentSSHPassword(String jonAgentPassword) {
		SahiSettings.jonAgentSSHPassword = jonAgentPassword;
	}
	public static String getBrowserPath() {
		return browserPath;
	}
	public static void setBrowserPath(String browserPath) {
		SahiSettings.browserPath = browserPath;
	}
	public static String getBrowserName() {
		return browserName;
	}
	public static void setBrowserName(String browserName) {
		SahiSettings.browserName = browserName;
	}
	public static String getBrowserOpt() {
		return browserOpt;
	}
	public static void setBrowserOpt(String browserOpt) {
		SahiSettings.browserOpt = browserOpt;
	}
	public static String getSahiBaseDir() {
		return sahiBaseDir;
	}
	public static void setSahiBaseDir(String sahiBaseDir) {
		SahiSettings.sahiBaseDir = sahiBaseDir;
	}
	public static String getSahiUserdataDir() {
		return sahiUserdataDir;
	}
	public static void setSahiUserdataDir(String sahiUserdataDir) {
		SahiSettings.sahiUserdataDir = sahiUserdataDir;
	}
	public static String getFileStoreUrl() {
		return fileStoreUrl;
	}
	public static void setFileStoreUrl(String fileStoreUrl) {
		SahiSettings.fileStoreUrl = fileStoreUrl;
	}
	
}
