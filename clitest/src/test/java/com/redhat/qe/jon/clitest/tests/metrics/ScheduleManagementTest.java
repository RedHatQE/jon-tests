package com.redhat.qe.jon.clitest.tests.metrics;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;

public class ScheduleManagementTest extends CliEngine {

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        return super.createJSRunner(jsFile).addDepends("rhqapi.js");
    }
    
    @Test
    public void RHQAgentSchedules() {
        createJSRunner("metrics/scheduleManagementForRHQAgent.js").run();
    }
}
