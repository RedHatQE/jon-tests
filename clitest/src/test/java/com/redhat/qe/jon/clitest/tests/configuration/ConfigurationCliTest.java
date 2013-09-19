package com.redhat.qe.jon.clitest.tests.configuration;

import java.io.IOException;

import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;


public class  ConfigurationCliTest extends CliTest {
	
    
    @Override
    public CliTestRunner createJSRunner(String jsFile) {
	return super.createJSRunner(jsFile).dependsOn("rhqapi.js");
    }
	/**
	 * 
	 * @param config
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void updateAgentConfiguration(ConfigValue config ) throws IOException, CliTasksException {
		createJSRunner("resourceConfiguration/testUpdateConfiguration.js")
			.withArg("prop", config.name)
			.withArg("propType",config.type)
			.withArg("propValue",config.value)
			.run();
	}
	
	/**
	 * 
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void getAllConfigurationProperties() throws IOException, CliTasksException {
		createJSRunner("resourceConfiguration/getAllConfigurationproperties.js").run();
	}
	
	/**
	 * 
	 * @param config
	 * @throws IOException
	 * @throws CliTasksException
	 */
	protected void updateResourceConfiguration(ConfigValue config) throws IOException, CliTasksException {
	    createJSRunner("resourceConfiguration/updateResourceConfiguration.js")
		.withArg("prop", config.name)
		.withArg("propType",config.type)
		.withArg("propValue",config.value)
		.withArg("resourceId",config.resId)
		.run();
	}
	
	public static class ConfigValue {
	    public String name,value,type,resId;
	    public ConfigValue(String name, String type, String value) {
		this.name = name;
		this.value = value;
		this.type = type;
	    }
	    
	    public ConfigValue(String name, String type, String value, String resId) {
		this(name,type,value);
		this.resId = resId;
	    }
	}
	

}
