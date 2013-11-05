package com.redhat.qe.jon.clitest.tests.bundles;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.base.OnAgentCliEngine;
import com.redhat.qe.jon.common.util.SSHClient;

public class BundleComplianceTest extends OnAgentCliEngine {

    public SSHClient client;

    @BeforeClass
    public void initSSHClient() {
        checkOptionalProperties("jon.agent.host", "jon.agent.user", "jon.agent.privatekey", "jon.agent.password");
        client = SSHClient.fromProperties("jon.agent.user", "jon.agent.host", "jon.agent.privatekey", "jon.agent.password");
        client.connect();
    }

    @AfterClass
    public void disconnectClient() {
        if (client != null) {
            client.disconnect();
        }
    }

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        // attach our listener which generates bundles based on our templates
        return super.createJSRunner(jsFile)
                .addDepends("rhqapi.js")
                .withRunListener(new CompliantBundleRunListener());
    }

    @Test
    public void bundleComplianceFull() {
        createJSRunner("bundles/init.js")
            .withArg("groupName", "my-destination")
            .withResource("antbundle-full:bundle:1.0", "bundle1")
            .withResource("antbundle-full:bundle:2.0", "bundle2")
            .run();
        log.info("Bundles initialized, creating test subdirs/files in future bundle destination");
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js")
            .addExpect("status : Success")
            .withArg("version", "1.0")
            .run();
        assertFile("subdir1/test",false);
        assertFile("subdir2/test",false);
        assertFile("subdir1/bundle.war",true); // only file deployed by bundle should exist

    }
    @Test(dependsOnMethods="bundleComplianceFull",groups={"blockedByBug-1026473"})
    public void bundleComplianceFullUpgrade() {
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js")
            .addExpect("status : Success")
            .withArg("version", "2.0")
            .run();
        assertFile("subdir1/test",false);
        assertFile("subdir2/test",false);
        assertFile("subdir1/bundle.war",true); // only file deployed by bundle should exist
    }
    /**
     * create some files prior deploying/upgrading bundle
     */
    private void createFiles() {
        client.runAndWait("mkdir -p /tmp/foo/subdir2");
        client.runAndWait("touch /tmp/foo/subdir2/test");
        client.runAndWait("mkdir -p /tmp/foo/subdir1/subdir1");
        client.runAndWait("touch /tmp/foo/subdir1/test");
    }
    
    private void assertFile(String file, boolean expectExists) {
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/"+file).getExitCode().intValue(), expectExists ? 0: 1,
                "File /tmp/foo/"+file+" exists");
    }
    private void assertDir(String dir, boolean expectExists) {
        Assert.assertEquals(client.runAndWait("test -d /tmp/foo/"+dir).getExitCode().intValue(), expectExists ? 0: 1,
                "Dir /tmp/foo/"+dir+" exists");
    }

    @Test
    public void bundleComplianceFilesAndDirectories() {
        createJSRunner("bundles/init.js")
            .withArg("groupName", "my-destination")
            .withResource("antbundle-FAD:bundle:1.0", "bundle1")
            .withResource("antbundle-FAD:bundle:2.0", "bundle2")
            .run();
        log.info("Bundles initialized, creating test subdirs/files in future bundle destination");
        createFiles();
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js").addExpect("status : Success").withArg("version", "1.0").run();
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
        assertFile("subdir1/test", false);
        assertDir("subdir2", true);
        assertFile("subdir2/test", true); // only everything under subdir2 must stay
        assertFile("subdir1/bundle.war", true);
    }
}
