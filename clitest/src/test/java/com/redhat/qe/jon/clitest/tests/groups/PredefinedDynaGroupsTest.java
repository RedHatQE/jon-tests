package com.redhat.qe.jon.clitest.tests.groups;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class PredefinedDynaGroupsTest extends CliEngine {

    @Test
    public void dynagroupsExistTest() {
        createJSRunner("groups/predefinedDynaGroups/dynagroupsExist.js")
                .addDepends("rhqapi.js").addDepends("/groups/utils.js").run();
    }
}
