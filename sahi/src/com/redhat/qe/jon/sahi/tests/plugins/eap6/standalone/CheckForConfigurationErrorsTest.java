package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.ArrayList;
import java.util.List;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckForConfigurationErrorsTest extends AS7StandaloneTest {

	@DataProvider
	public Object[][] getResourceTree() {
		Resource root = server;
		List<Resource> tree = root.getChildrenTree();
		tree.add(0, root);
		tree.add(server.child("webservices"));
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
		Configuration config = resource.configurationNoNav();
		try {
			config.navigateFull();
		} catch (Exception ex) {
			log.info("Resource " + resource.toString()
					+ " does not HAVE Configuration TAB");
			return;
		}
		String errorText = config.current().getErrorText();
		Assert.assertNull(errorText, "Resource " + resource.toString()
				+ " has error on Configuration TAB : " + errorText);

	}
}
