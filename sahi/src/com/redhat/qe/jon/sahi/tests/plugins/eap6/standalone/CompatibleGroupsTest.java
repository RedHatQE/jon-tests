package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;

public class CompatibleGroupsTest extends AS7StandaloneTest {
	
	@BeforeClass(groups = "deployment")
    protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);

    }
}
