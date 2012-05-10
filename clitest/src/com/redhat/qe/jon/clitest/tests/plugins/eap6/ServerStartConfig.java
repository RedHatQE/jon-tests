package com.redhat.qe.jon.clitest.tests.plugins.eap6;

public class ServerStartConfig {

	
	private final String params;
	private final String expectedMessage;
	private final ConfigFile[] configs;
	private final String command;
	
	public ServerStartConfig(String params, String expectedMessage) {
		this(params,expectedMessage,null,null);
	}
	
	public ServerStartConfig(String params, String expectedMessage,String command) {
		this(params,expectedMessage,command,null);
	}
	
	public ServerStartConfig(String params, String expectedMessage,String command,ConfigFile configs[]) {
		this.params = params;
		this.expectedMessage = expectedMessage;
		this.configs = configs;
		this.command = command;
	}
	/**
	 * gets command to be executed before AS7 is started in working directory of AS7 HOME
	 * @return
	 */
	public String getCommand() {
		return command;
	}
	public ConfigFile[] getConfigs() {
		return configs;
	}
	public String getParams() {
		return params;
	}
	public String getExpectedMessage() {
		return expectedMessage;
	}
	@Override
	public String toString() {
		return params;
	}
	
	public static class ConfigFile {
		private final String localPath;
		private final String remotePath;
		private final String startupParam;
		
		public ConfigFile(String localPath, String remotePath, String startupParam) {
			this.localPath = localPath;
			this.remotePath = remotePath;
			this.startupParam = startupParam;
		}
		
		public String getLocalPath() {
			return localPath;
		}
		public String getRemotePath() {
			return remotePath;
		}
		public String getStartupParam() {
			return startupParam;
		}
		@Override
		public String toString() {
			return "file:"+remotePath+" param:"+startupParam;
		}
	}
}
