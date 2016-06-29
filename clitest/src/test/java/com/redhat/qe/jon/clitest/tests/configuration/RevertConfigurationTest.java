package com.redhat.qe.jon.clitest.tests.configuration;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class RevertConfigurationTest extends CliEngine {

    @Test
    public void revertResourceConfiguration(){
        createJSRunner("resourceConfiguration/revertResConfig.js").
            addDepends("/rhqapi.js").
            run();
    }
    @Test
    public void revertPluginConfiguration(){
        createJSRunner("resourceConfiguration/revertPluginConfig.js").
            addDepends("/rhqapi.js").
            run();
    }

}
