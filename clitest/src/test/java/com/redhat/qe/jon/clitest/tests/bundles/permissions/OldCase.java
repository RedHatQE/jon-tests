package com.redhat.qe.jon.clitest.tests.bundles.permissions;

import org.testng.annotations.Test;
/**
 * Implements tests that prove no regression 
 * in past previous permission model = user with MANAGE_BUNDLE can do everything with bundles/bundle groups
 * (see oldCase in /bundles/permissions/iniCases.js)
 * @author lzoubek
 *
 */
public class OldCase extends BundlePermissionsTest {

    @Test
    public void uploadBundle() {
        createJSRunner("bundles/permissions/uploadBundle.js")
        .withResource("antbundle:bundleCaseOld:1.0", "bundle")
        .asUser("oldCase_U", "rhqadmin")
        .withArg("hasPerm", "true")
        .withArg("toGroup", "oldCase_A")
        .run();
    }
    
    @Test(dependsOnMethods={"uploadBundle"})
    public void assignBundle() {
        createJSRunner("bundles/permissions/assignBundle.js")
        .withArg("bundle", "bundleCaseOld")
        .asUser("oldCase_U", "rhqadmin")
        .withArg("hasPerm", "true")
        .withArg("toGroup", "oldCase_B")
        .run();
    }
    @Test(dependsOnMethods={"assignBundle"})
    public void deployBundle() {
        createJSRunner("bundles/permissions/deployBundle.js")
        .withArg("bundle", "bundleCaseOld")
        .asUser("oldCase_U", "rhqadmin")
        .withArg("hasPerm", "true")
        .withArg("toGroup", "oldCase_X")
        .run();
    }
}
