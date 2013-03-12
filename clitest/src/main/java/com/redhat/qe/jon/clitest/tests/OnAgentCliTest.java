package com.redhat.qe.jon.clitest.tests;

import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;

/**
 * this class is extending {@link CliTest} class the way that it requires
 * <strong>jon.agent.name</strong> system property to be defined and passes it's
 * value to each JS test as a named param <strong>agent</strong>.
 * 
 * @author lzoubek
 * 
 */
public class OnAgentCliTest extends CliEngine {

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
    
    @Override
    public CliTestRunner createJSSnippetRunner(String jsSnippet) {
        return super.createJSSnippetRunner(jsSnippet).withArg("agent", agentName);
    }
}
