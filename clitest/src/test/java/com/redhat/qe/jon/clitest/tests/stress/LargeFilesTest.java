package com.redhat.qe.jon.clitest.tests.stress;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.base.LargeDeploymentGenerator;

public class LargeFilesTest extends CliEngine {

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        // register listener to handle "bundle:<size>" and "war:<size>" resource names
        return super.createJSRunner(jsFile)
                .withRunListener(new LargeDeploymentGenerator())
                .addDepends("rhqapi.js");
    }
    
	@Test(groups={"blockedByBug-1015560"})
	public void deployLargeWar() {
		createJSRunner("stress/test_LargeWarDeploy.js")
		    .withResource("war:50","war")
			.run();
	}
	
	@Test(groups={"blockedByBug-955363"})
    public void updateBackingContentWithLargeWar() {
        createJSRunner("stress/updateBackingContentWithLargeWar.js")
            .withResource("deployments/hello1.war", "starter")
            .withResource("war:50","war")  
            .run();
    }
	
	@Test(groups={"blockedByBug-1015560"})
	public void deployLargeBundle(){
		createJSRunner("stress/test_LargeBundleDeploy.js")
			.withResource("bundle:50","bundle")
			.run();
	}
}
