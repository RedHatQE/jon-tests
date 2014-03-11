package com.redhat.qe.jon.clitest.tests.bundles;

import org.testng.annotations.Test;

public class BundleComplianceFullTest extends BundleComplianceBase {


    @Test()
    public void bundleComplianceFull() {
        createJSRunner("bundles/init.js")
            .withArg("groupName", "my-destination")
            .withResource("antbundle-full:bundle:1.0", "bundle1")
            .withResource("antbundle-full:bundle:2.0", "bundle2")
            .run();
        log.info("Bundles initialized, creating test subdirs/files in future bundle destination");
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js").withArg("version", "1.0").addExpect("status : Success").run();
        client.runAndWait("find /tmp/foo");
        assertFile("subdir1/test", false);
        assertDir("subdir1/subdir1", false);
        assertDir("subdir2", false);
        assertFile("subdir2/test", false);
        assertFile("test", false);
        assertFile("subdir1/bundle.war", true);
        assertDir("subdir3", false);
    }
    
    @Test(dependsOnMethods="bundleComplianceFull")
    public void bundleComplianceFullUpgrade() {
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js").addExpect("status : Success").withArg("version", "1.0").run();
        client.runAndWait("find /tmp/foo");
        assertFile("subdir1/test", false);
        assertDir("subdir1/subdir1", false);
        assertDir("subdir2", false);
        assertFile("subdir2/test", false);
        assertFile("test", false);
        assertFile("subdir1/bundle.war", true);
        assertDir("subdir3", false);
    }
}
