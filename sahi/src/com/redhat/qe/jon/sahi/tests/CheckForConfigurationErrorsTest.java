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
			description="This test checks whether there is any error on Configuration tab of particular resource"
	)
	public void resourceConfigurationWithoutErrors(final Resource resource) {
		if (resource.getId()==null) {
			Assert.fail("resource ID must NOT be null");
		}
		String url = System.getProperty("jon.server.url");
		if (!url.endsWith("coregui")) {
			url+="/coregui/";
		}
		url+="#Resource/"+resource.getId()+"/Configuration/Current";
		sahiTasks.navigateTo(url);
		if (!sahiTasks.cell("Configuration").exists()) {
			log.info("Resource " + resource.toString()
					+ " does not HAVE Configuration TAB");
			return;
		}
		String errorText = getErrorText();
		Assert.assertNull(errorText, "Resource " + resource.toString()
				+ " has error on Configuration TAB : " + errorText);

	}
	
	private String getErrorText() {
		StringBuilder sb = new StringBuilder();
		ElementStub es =  sahiTasks.byXPath("//td[@class='ErrorBlock'][1]");
		if (!es.exists()) {
			return null;
		}
		sb.append(es.getText());			
		return sb.toString();
		
	}
}
