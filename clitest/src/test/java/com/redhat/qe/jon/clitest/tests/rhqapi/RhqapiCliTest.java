package com.redhat.qe.jon.clitest.tests.rhqapi;

import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.base.OnAgentCliEngine;

public class RhqapiCliTest extends OnAgentCliEngine {

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        return super.createJSRunner(jsFile).dependsOn("/rhqapi.js","/rhqapi/config.js");
    }
}
