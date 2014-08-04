package com.redhat.qe.jon.clitest.tests.importPortServices;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class ImportPortServicesTest extends CliEngine {

	@Test
	public void importPortServices() {
	    if(System.getProperty("rhq.build.version", "").contains("JON")){
            throw new SkipException("rhq-netservices-plugin is not supported in JON");
        }
		createJSRunner("importPortServices/importPortServices.js").addDepends("rhqapi.js").run();
	}
}
