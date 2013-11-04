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
        client.runAndWait("mkdir -p /tmp/foo/subdir2");
        client.runAndWait("touch /tmp/foo/subdir2/test");
        client.runAndWait("mkdir -p /tmp/foo/subdir1/subdir1");
        client.runAndWait("touch /tmp/foo/subdir1/test");
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js")
            .addExpect("status : Success")
            .withArg("version", "1.0")
            .run();
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/subdir1/test").getExitCode().intValue(), 1);
        Assert.assertEquals(client.runAndWait("test -d /tmp/foo/subdir1/subdir1").getExitCode().intValue(), 1);
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/subdir2/test").getExitCode().intValue(), 1);
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/subdir1/bundle.war").getExitCode().intValue(), 0);

    }
    @Test(dependsOnMethods="bundleComplianceFull",groups={"blockedByBug-1026473"})
    public void bundleComplianceFullUpgrade() {
        client.runAndWait("mkdir -p /tmp/foo/subdir2");
        client.runAndWait("touch /tmp/foo/subdir2/test");
        client.runAndWait("mkdir -p /tmp/foo/subdir1/subdir1");
        client.runAndWait("touch /tmp/foo/subdir1/test");
        log.info("Deploy bundle");
        createJSRunner("bundles/deployBundle.js")
            .addExpect("status : Success")
            .withArg("version", "2.0")
            .run();
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/subdir1/test").getExitCode().intValue(), 1);
        Assert.assertEquals(client.runAndWait("test -d /tmp/foo/subdir1/subdir1").getExitCode().intValue(), 1);
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/subdir2/test").getExitCode().intValue(), 1);
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/subdir1/bundle.war").getExitCode().intValue(), 0);
    }

    //@Test
    public void bundleComplianceFilesAndDirectories() {

    }
}
