package com.redhat.qe.jon.clitest.tests;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;

/**
 * this class is extending {@link CliTest} class the way that it requires
 * <strong>jon.agent.name</strong> system property to be defined and passes it's
 * value to each JS test as a named param <strong>agent</strong>.
 * 
 * @author lzoubek
 * 
 */
public class OnAgentCliTest extends CliTest {

    protected static String agentName;

    @BeforeSuite
    public void checkInputProperties() {
	checkRequiredProperties("jon.agent.name");
	agentName = System.getProperty("jon.agent.name");
    }

    public void runJSfile(String rhqTarget, String cliUsername,
	    String cliPassword, String jsFile, String cliArgs,
	    String expectedResult, String makeFilure, String jsDepends,
	    String resSrc, String resDst) throws IOException, CliTasksException {

	if (StringUtils.trimToNull(cliArgs) == null) {
	    cliArgs = "--args-style=named agent=" + agentName;
	} else {
	    cliArgs += " agent=" + agentName;
	}
	super.runJSfile(rhqTarget, cliUsername, cliPassword, jsFile, cliArgs,
		expectedResult, makeFilure, jsDepends, resSrc, resDst);
    }
}
