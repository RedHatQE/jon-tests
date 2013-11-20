package com.redhat.qe.jon.clitest.tests.bundles;

import org.testng.annotations.Test;

public class BundleComplianceFADTest extends BundleComplianceBase {

    @Test()
    public void bundleComplianceFilesAndDirectories() {
        createJSRunner("bundles/init.js")
            .withArg("groupName", "my-destination")
            .withResource("antbundle-FAD:bundle:1.0", "bundle1")
            .withResource("antbundle-FAD:bundle:2.0", "bundle2")
            .run();
        log.info("Bundles initialized, creating test subdirs/files in future bundle destination");
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js").withArg("version", "1.0").addExpect("status : Success").run();
        client.runAndWait("find /tmp/foo");
        assertFile("subdir1/test", false);
        assertDir("subdir2", true); // only everything under subdir2 must stay
        assertFile("subdir2/test", true);
        assertFile("subdir1/bundle.war", true);
    }
    
    @Test(dependsOnMethods="bundleComplianceFilesAndDirectories")
    public void bundleComplianceFilesAndDirectoriesUpgrade() {
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js").addExpect("status : Success").withArg("version", "1.0").run();
        client.runAndWait("find /tmp/foo");
        assertFile("subdir1/test", false);
        assertDir("subdir2", true);
        assertFile("subdir2/test", true); // only everything under subdir2 must stay
        assertFile("subdir1/bundle.war", true);
    }
}
