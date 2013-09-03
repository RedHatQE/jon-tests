package com.redhat.qe.jon.clitest.tests.metrics;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.common.util.HTTPClient;

public class ResponseTimeFilterTest extends CliEngine {
    
    private HTTPClient client = new HTTPClient("http://"+System.getProperty("jon.server.host", "localhost")+":7080");
    
    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        return super.createJSRunner(jsFile)
        	.addDepends("rhqapi.js")
        	.addDepends("metrics/common.js");
    }

    @Test
    public void responseTimeRestWar() {
	responseTime("rhq-rest.war", "rest/reports.html");
    }
    @Test
    public void responseTimeCoregui() {
	responseTime("coregui.war","coregui/");
    }
    private void responseTime(String deployment, String endPoint) {
	int hits = 10;
	createJSRunner("metrics/enableRTFilter.js")
		.withArg("deployment", deployment)
		.run();	
	for (int i = 0; i<= hits;i++) {
	    client.isDeploymentAvailable(endPoint);
	}
	waitFor(1000 * 2 * 60,"Waiting for metric collection");
	createJSRunner("metrics/validateCallTime.js")
	.withArg("deployment", deployment)
	.withArg("endpoint", "/"+endPoint)
	.withArg("hits", String.valueOf(hits))
	.run();
    }
   
}
