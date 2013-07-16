package com.redhat.qe.jon.clitest.tests.rhqapi;

import org.testng.annotations.Test;

public class ResourceCliTest extends RhqapiCliTest {

    @Test
    public void metrics() {
	createJSRunner("rhqapi/resource_metrics.js").run();
    }
    @Test
    public void createChild() {
	createJSRunner("rhqapi/resource_createChild.js")
		.resourceSrcs("/deployments/hello1.war")
		.resourceDests("/tmp/hello.war")
		.withArg("deployment", "/tmp/hello.war").run();
    }
    
    @Test 
    public void configuration() {
	createJSRunner("rhqapi/resource_configuration.js").run();
    }
    
    @Test 
    public void invokeOperation() {
	createJSRunner("rhqapi/resource_invokeOperation.js").run();
    }
    @Test 
    public void scheduleOperation() {
	createJSRunner("rhqapi/resource_scheduleOperation.js").run();
    }
    
    @Test 
    public void snippetTest() {
    	String snippet = "if(!resources.platform()){" +
    			"throw \"At least one imported platform is expected!!\"}";
    	createJSSnippetRunner(snippet).
    	dependsOn("/rhqapi.js").
    	run();
    }
}
