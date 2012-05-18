package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.util.HashMap;
import java.util.LinkedList;

import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * check whether AS7/EAP6 is correctly detected
 * @author Jan Martiska (jmartisk@redhat.com)
 * @see TCMS test case 96426
 * @since 7 September 2011
 * 
 */
public class AutodetectConfigurationTest extends AS7StandaloneTest {

	@Test(groups = { "autodetectConfiguration" })
	public void autodetectConfiguration() {
		server.uninventory(false);
		server.performManualAutodiscovery();
		sahiTasks.getNavigator().inventoryDiscoveryQueue();
		boolean ok = false;
		String resourceTypeHTML = "";

		sahiTasks.xy(
				sahiTasks.image("opener_closed.png").in(
						sahiTasks.cell(server.getPlatform() + "[0]")
								.parentNode("tr")), 3, 3).click();
		sahiTasks.waitFor(Timing.WAIT_TIME);
		LinkedList<HashMap<String, String>> discoveryQueue = sahiTasks
				.getRHQgwtTableFullDetails(
						"listTable",
						2,
						"Resource Name, Resource Key, Resource Type, Description, Inventory Status, Discovery Time",
						null);
		log.info("Table Details: Number of Row(s): " + discoveryQueue.size());
		for (int i = 0; i < discoveryQueue.size(); i++) {
			if (server.getName().equalsIgnoreCase(
					discoveryQueue.get(i).get("Resource Name"))) {
				ok = true;
				resourceTypeHTML = discoveryQueue.get(i).get(" Resource Type");
				break;
			}
		}

		Assert.assertTrue(ok, "Resource [" + server.getName()
				+ "] is detected by agent");

		boolean found = false;
		if (resourceTypeHTML.indexOf("JBossAS7 Standalone") != -1) {
			found = true;
		}
		if (resourceTypeHTML.indexOf("EAP6") != -1) {
			found = true;
		}
		Assert.assertTrue(
				found,
				"The resource type of a standalone instance should be \"JBossAS7 Standalone\" or \"EAP6\"");
		as7SahiTasks.importResource(server);
	}

}
