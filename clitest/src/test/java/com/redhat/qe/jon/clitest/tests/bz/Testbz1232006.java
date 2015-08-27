package com.redhat.qe.jon.clitest.tests.bz;

import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;
import com.redhat.qe.jon.clitest.tests.bundles.CompliantBundleRunListener;
import com.redhat.qe.jon.common.util.SSHClient;
import com.redhat.qe.tools.SSHCommandResult;

public class Testbz1232006 extends CliEngine {

	public SSHClient client;

	public String getCrazyDirPath() {
		// return System.getProperty("crazy.dir");
		return "/home/hudson/propertyfile/sp3c141_./";
	}

	public String getCrazyFileName() {
		// return "agent-prefs.properties";
		return "prefs.xml";
	}

	public String getAgentWrapperPathName() {
		return "rhq-agent/bin/rhq-agent-wrapper.sh";
	}

	public String getAgentEnvPathName() {
		return "rhq-agent/bin/rhq-agent-env.sh";
	}

	public String getCrazyFileDefaultPathName() {
		return ".java/.userPrefs/rhq-agent/default/" + getCrazyFileName();
	}

	public String getCrazyFileNewPathName() {
		return getCrazyDirPath() + getCrazyFileName();
	}

	public String getFileName(String filePath) {
		return Paths.get(filePath).getFileName().toString();
	}
	
	public void setProperty(String propertyName, String value) {
		// sed -i
		// 's/\(RHQ_AGENT_JAVA_OPTS="\).*\("\)/\nRHQ_AGENT_JAVA_OPTS="\/home\/hudson\/propertyfile\/sp3c141_.\/agent-prefs.properties"
		// /g' rhq-agent/bin/rhq-agent-env.sh
		client.runAndWait("sed -i 's/\\(" + propertyName
				+ "=\"\\).*\\(\"\\)/\\n" + propertyName + "=\""
				+ value.replace("/", "\\/") + "\"" + "/g' "
				+ getAgentEnvPathName());
	}

	@BeforeClass
	public void initSSHClient() {
		checkOptionalProperties("jon.agent.host", "jon.agent.user",
				"jon.agent.privatekey", "jon.agent.password");
		client = SSHClient.fromProperties("jon.agent.user", "jon.agent.host",
				"jon.agent.privatekey", "jon.agent.password");
		client.connect();
	}

	@AfterClass
	public void disconnectClient() {
		if (client != null) {
			roolbackFile();
			client.disconnect();
		}
	}

	@Override
	public CliTestRunner createJSRunner(String jsFile) {
		// attach our listener which generates bundles based on our templates
		return super.createJSRunner(jsFile).addDepends("rhqapi.js")
				.withRunListener(new CompliantBundleRunListener());
	}

	protected void assertFile(String file, boolean expectExists) {
		Assert.assertEquals(client.runAndWait("test -f " + file).getExitCode()
				.intValue(), expectExists ? 0 : 1, "File ~" + file + " exists");
	}

	protected void assertDir(String dir, boolean expectExists) {
		Assert.assertEquals(client.runAndWait("test -d " + dir).getExitCode()
				.intValue(), expectExists ? 0 : 1, "Dir ~" + dir + " exists");
	}

	public void startAgent() {
		Assert.assertEquals(
				client.runAndWait("./" + getAgentWrapperPathName() + " start")
						.getExitCode().intValue(), 0, "Unable to start Agent");
		createJSRunner("bugs/bug1232006_waitup.js").run();
	}

	public void stopAgent() {
		client.runAndWait("./" + getAgentWrapperPathName() + " stop");
		createJSRunner("bugs/bug1232006_waitdown.js").run();
	}

	public void roolbackFile() {
		stopAgent();
		client.runAndWait("cp " + getAgentEnvPathName() + ".old "
				+ getAgentEnvPathName());

		startAgent();
	}

	@Test
	public void bz1232006() {
		client.runAndWait("cd ~");

		stopAgent();

		client.runAndWait("cp " + getAgentEnvPathName() + " "
				+ getAgentEnvPathName() + ".old");

		client.runAndWait("mkdir -p " + getCrazyDirPath());
		assertDir(getCrazyDirPath(), true);
		client.runAndWait("cp " + getCrazyFileDefaultPathName() + " "
				+ getCrazyFileNewPathName());
		assertFile(getCrazyFileNewPathName(), true);
		assertFile(getAgentEnvPathName(), true);
		SSHCommandResult result = client
				.runAndWait("grep 'RHQ_AGENT_PREF_PROPERTIES' "
						+ getAgentEnvPathName());

		if (result.getExitCode() == 0) {
			setProperty("RHQ_AGENT_JAVA_OPTS",
					"-Xms64m -Xmx128m -Djava.net.preferIPv4Stack=true");
			setProperty("RHQ_AGENT_PREF_PROPERTIES", getCrazyFileNewPathName());
		} else if (result.getExitCode() == 1) {
			setProperty("RHQ_AGENT_JAVA_OPTS",
					"-Xms64m -Xmx128m -Djava.net.preferIPv4Stack=true -Drhq.preferences.file="
							+ getCrazyFileNewPathName());
		} else {
			// got a problem with a grep/file
			Assert.assertTrue(false,
					"Problem occured with: grep 'RHQ_AGENT_PREF_PROPERTIES' "
							+ getAgentEnvPathName());
		}

		startAgent();
	}

}
