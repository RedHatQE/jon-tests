package com.redhat.qe.jon.clitest.base;

import org.testng.annotations.BeforeSuite;

/**
 * this class is extending {@link CliEngine} class the way that it requires
 * <strong>jon.agent.name</strong> system property to be defined and passes it's
 * value to each JS test as a named param <strong>agent</strong>.
 * 
 * @author lzoubek
 * 
 */
public class OnAgentCliEngine extends CliEngine {

    protected static String agentName;

    @BeforeSuite
    public void checkInputProperties() {
	checkRequiredProperties("jon.agent.name");
	agentName = System.getProperty("jon.agent.name");
    }
    
    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        return super.createJSRunner(jsFile).withArg("agent", agentName);
    }
}
