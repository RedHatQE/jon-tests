package com.redhat.qe.jon.clitest.tests.plugins.eap6;

public class ServerStartConfig {

	
	private final String startCmd;
	private final String expectedMessage;
	private final ConfigFile[] configs;
	private final String preStartCmd;
	
	public ServerStartConfig(String startCmd, String expectedMessage) {
		this(startCmd,expectedMessage,null,null);
	}
	
	public ServerStartConfig(String startCmd, String expectedMessage,String preStartCmd) {
		this(startCmd,expectedMessage,preStartCmd,null);
	}
	
	public ServerStartConfig(String startCmd, String expectedMessage,String preStartCmd,ConfigFile configs[]) {
		this.startCmd = startCmd;
		this.expectedMessage = expectedMessage;
		this.configs = configs;
		this.preStartCmd = preStartCmd;
	}
	/**
	 * gets command to be executed before AS7 is started in working directory of AS7 HOME
	 */
	public String getPreStartCmd() {
		return preStartCmd;
	}
	public ConfigFile[] getConfigs() {
		return configs;
	}
	public String getStartCmd() {
		return startCmd;
	}
	public String getExpectedMessage() {
		return expectedMessage;
	}
	@Override
	public String toString() {
		String ret = "cmd:["+startCmd+"] expected:["+expectedMessage+"]";
		if (configs!=null) {
			for (ConfigFile cf : configs) {
				ret+=" config:["+cf.toString()+"]";
			}
		}
		return ret;
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
			return "file:["+remotePath+"] param:["+startupParam+"]";
		}
	}
}
