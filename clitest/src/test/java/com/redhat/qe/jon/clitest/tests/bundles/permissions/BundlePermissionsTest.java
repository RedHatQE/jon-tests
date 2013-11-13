package com.redhat.qe.jon.clitest.tests.bundles.permissions;

import org.testng.annotations.BeforeTest;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;

/**
 * base class for all bundle permission test cases
 * Test cases are based on <a href="https://docs.jboss.org/author/display/RHQ/Security+Model+for+Bundle+Provisioning">Security model for bundle provisioning</a>
 * @author lzoubek
 *
 */
public class BundlePermissionsTest extends CliEngine {
    
    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        return super.createJSRunner(jsFile)
                .addDepends("rhqapi.js");
    }
    
    @BeforeTest
    public void setupPermissions() {
        createJSRunner("bundles/permissions/initCases.js").addDepends("permissions/common.js").run();
    }
    
}
