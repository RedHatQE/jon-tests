package com.redhat.qe.jon.clitest.tests.rhqapi;

import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.tests.OnAgentCliTest;

public class RhqapiCliTest extends OnAgentCliTest {

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        // TODO Auto-generated method stub
        return super.createJSRunner(jsFile).dependsOn("/rhqapi.js","/rhqapi/config.js");
    }
}
