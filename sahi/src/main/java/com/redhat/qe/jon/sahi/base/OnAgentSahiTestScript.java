package com.redhat.qe.jon.sahi.base;

import org.testng.annotations.BeforeSuite;

/**
 * This class adds a check which requires system property 
 * <strong>jon.agent.name</strong> to be set.
 * @author fbrychta
 *
 */
public class OnAgentSahiTestScript extends SahiTestScript {

	 protected static String agentName;

    @BeforeSuite
    public void checkInputProperties() {
		checkRequiredProperties("jon.agent.name");
		agentName = System.getProperty("jon.agent.name");
    }
}
