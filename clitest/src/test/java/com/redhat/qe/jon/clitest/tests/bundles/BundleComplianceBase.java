package com.redhat.qe.jon.clitest.tests.bundles;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.base.OnAgentCliEngine;
import com.redhat.qe.jon.common.util.SSHClient;

public class BundleComplianceBase extends OnAgentCliEngine {

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

    /**
     * create some files prior deploying/upgrading bundle
     */
    protected void createFiles() {
        client.runAndWait("mkdir -p /tmp/foo/subdir2");
        client.runAndWait("touch /tmp/foo/subdir2/test");
        client.runAndWait("touch /tmp/foo/test");
        client.runAndWait("mkdir -p /tmp/foo/subdir1/subdir1");
        client.runAndWait("touch /tmp/foo/subdir1/test");
        client.runAndWait("mkdir -p /tmp/foo/subdir3");
    }
    
    protected void assertFile(String file, boolean expectExists) {
        Assert.assertEquals(client.runAndWait("test -f /tmp/foo/"+file).getExitCode().intValue(), expectExists ? 0: 1,
                "File /tmp/foo/"+file+" exists");
    }
    protected void assertDir(String dir, boolean expectExists) {
        Assert.assertEquals(client.runAndWait("test -d /tmp/foo/"+dir).getExitCode().intValue(), expectExists ? 0: 1,
                "Dir /tmp/foo/"+dir+" exists");
    }
}
