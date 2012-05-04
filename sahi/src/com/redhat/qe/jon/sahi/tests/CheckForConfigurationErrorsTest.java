package com.redhat.qe.jon.sahi.tests;

import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class CheckForConfigurationErrorsTest extends SahiTestScript {

	private static Logger log = Logger.getLogger(CheckForConfigurationErrorsTest.class.getName());
	
	@DataProvider
	public Object[][] getResourceTree() {
		Resource root = new Resource(sahiTasks,System.getenv("AGENT_NAME"));
		List<Resource> tree = root.getChildrenTree();
		Object[][] output = new Object[tree.size()][];
		for (int i=0;i<tree.size();i++) {
			output[i] = new Object[] {tree.get(i)};
		}		
		return output;
	}

	@Test(groups = "check", 
			dataProvider = "getResourceTree",
			description="This test checks for each imported resource whether there is any error on Configuration tab and configuration can be retrieved"
	)
	public void resourceConfigurationWithoutErrors(final Resource resource) {
		String errorText = null;
		try {
			errorText = resource.configuration().current().getErrorText();		
		}
		catch (Exception ex) {
			log.info("Resource " + resource.toString()
					+ " does not HAVE Configuration TAB");
			return;
		}
		Assert.assertNull(errorText, "Resource " + resource.toString()
				+ " has error on Configuration TAB : " + errorText);
		ElementStub es =  sahiTasks.byXPath("//td[@class='InfoBlock'][2]");
		boolean hasConfig = true;
		if (es.exists()) {
			String text = es.getText();
			if (text!=null) {
				if (text.contains("failed to load the configuration")) {
					hasConfig=false;
				}
			}
		}
		Assert.assertTrue(hasConfig, "Some configuration was retrieved");
	}
}
