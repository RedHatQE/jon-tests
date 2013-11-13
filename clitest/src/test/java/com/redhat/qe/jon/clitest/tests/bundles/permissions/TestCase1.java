package com.redhat.qe.jon.clitest.tests.bundles.permissions;

import org.testng.annotations.Test;


/**
 * see bundles/permissions/initCases.js for this case1 definition
 */
public class TestCase1 extends BundlePermissionsTest {
    
    @Test
    public void userUUploadBundle() {
        createJSRunner("bundles/permissions/uploadBundle.js")
            .withResource("antbundle:bundleCase1:1.0", "bundle")
            .asUser("case1_U", "rhqadmin")
            .withArg("hasPerm", "true")
            .withArg("toGroup", "case1_A")
            .run();
    }
    @Test
    public void userUUploadBundleForeignGroup() {
        // this will fail even to lookup group case1_B 
        createJSRunner("bundles/permissions/uploadBundle.js")
            .withResource("antbundle:bundleCase1-2:1.0", "bundle")
            .asUser("case1_U", "rhqadmin")
            .withArg("hasPerm", "false")
            .withArg("toGroup", "case1_B")
            .addExpect("Resource group for bundle deployment was found=0")
            .run();
    }
    
    @Test(dependsOnMethods={"userUUploadBundle"})
    public void userUDeployBundle() {
        // this will fail even to lookup group case1_X
        createJSRunner("bundles/permissions/deployBundle.js")
        .asUser("case1_U", "rhqadmin")
        .withArg("hasPerm", "true")
        .withArg("toGroup", "case1_X")
        .withArg("bundle","bundleCase1")
        .run();
    }
    
    @Test(dependsOnMethods={"userUUploadBundle"})
    public void userUDeployBundleToForeignGroup() {
        createJSRunner("bundles/permissions/deployBundle.js")
        .asUser("case1_U", "rhqadmin")
        .withArg("hasPerm", "false")
        .withArg("toGroup", "case1_Y")
        .withArg("bundle","bundleCase1")
        .addExpect("Resource group for bundle deployment was found=0")
        .run();
    }
}
