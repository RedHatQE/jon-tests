package com.redhat.qe.jon.clitest.tests.plugins.eap6;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.plugins.eap6.AS7CliTest;

public class GetVersionTest extends AS7CliTest {


	@BeforeClass
	public void beforeClass() {
		sshClient = sshStandalone;
	}
	
	
	@Test
	public void getServerVersion() throws IOException, CliTasksException {
	    String expected = "server=";
	    runJSfile(null, "rhqadmin", "rhqadmin", "eap6/serverVersion.js", null, expected, null,"rhqapi.js,eap6/standalone/server.js",null,null);
	    Pattern regex = Pattern.compile("server=(.+?)$",Pattern.MULTILINE);
	    Matcher m = regex.matcher(this.consoleOutput);
	    if (m.find()) {
		String version = m.group(1);
		log.info("Detected server "+version);
		System.setProperty("rhq.build.version", System.getProperty("rhq.build.version", "")+" "+version);
		Reporter.log("<BR><b>"+version+"</b><br>");
	    }
	}
}
