package com.redhat.qe.jon.sahi.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
/**
 * This class should abstract navigation on RHQ in general
 * @author lzoubek
 * @since 25.Nov 2011
 *
 */
public class Navigator {

	private static Logger log = Logger.getLogger(Navigator.class.getName());
	private final SahiTasks tasks;
	private final Map<String,String> itNav;
	private int timeout = Timing.WAIT_TIME;
	private final String serverBaseUrl;
	public Navigator(SahiTasks tasks) {
		this.tasks = tasks;
		// initialize navigation mappings
		itNav = new HashMap<String, String>();
		itNav.put("Summary", "Service_up_16.png");
		itNav.put("Inventory", "Inventory_grey_16.png");
		itNav.put("Alerts", "Alerts_16.png");
		itNav.put("Operations", "Operation_grey_16.png");
		itNav.put("Monitoring", "Monitor_grey_16.png");
		itNav.put("Configuration", "Configure_grey_16.png");
		
		String url = System.getProperty("jon.server.url");
		if (!url.endsWith("coregui")) {
			url+="/coregui";
		}
		serverBaseUrl = url;
	}

	public String getServerBaseUrl() {
		return serverBaseUrl;
	}
	/**
	 * selects given inventory tab
	 * you have to be in Inventory to use this method
	 * @param it inventory Tab name
	 * @param subTab sub tab to jump in within inventory tab
	 */
	public void inventorySelectTab(String it, String subTab) {		
		if (it !=null) {
			if (!itNav.containsKey(it)) {
				throw new RuntimeException("Selecting tab of type "+it.toString()+" is not implemented");
			}
			log.fine("Select Tab ["+it.toString()+"]");
			if ("Summary".equals(it)) {
				tasks.image(itNav.get(it)).near(tasks.cell(it)).click();
			}
			else if("Inventory".equals(it)) {
				 tasks.cell(it).near(tasks.image(itNav.get(it))).click();
			}
			else {
				tasks.image(itNav.get(it)).click();
			}
			log.fine("Tab ["+it.toString()+"] selected");
		}
		if (subTab!=null) {
			tasks.waitFor(timeout);
			tasks.xy(tasks.cell(subTab), 3, 3).click();
			log.fine("Switched to subtab ["+subTab+"]");
		}
		tasks.waitFor(timeout);
	}
	/**
	 * selects given inventory tab
	 * you HAVE to be on Inventory page when using this method
	 * @param it Inventory Tab name
	 */
	public void inventorySelectTab(String it) {
		inventorySelectTab(it, null);
	}
	public void inventoryDiscoveryQueue() {
        tasks.link("Inventory").click();
        tasks.cell("Discovery Queue").click();
        tasks.waitFor(timeout);
	}
}
