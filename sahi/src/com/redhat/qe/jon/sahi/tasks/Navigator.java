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
	public Navigator(SahiTasks tasks) {
		this.tasks = tasks;
		// initialize navigation mappings
		itNav = new HashMap<String, String>();
		itNav.put("Summary", "Service_up_16.png");
		itNav.put("Inventory", "Inventory_grey_16.png");
		itNav.put("Alerts", "Alerts_16.png");
		itNav.put("Operations", "Operation_grey_16.png");
	}
	
	/**
	 * selects given inventory tab
	 * you have to be in Inventory to use this method
	 * @param it inventory Tab name
	 * @param subTab sub tab to jump in within inventory tab
	 */
	public void inventorySelectTab(String it, String subTab) {
		if (itNav.containsKey(it)) {
			log.fine("select resourceTab "+it.toString());
			if ("Summary".equals(it)) {
				tasks.image(itNav.get(it)).near(tasks.cell(it)).click();
			}
			else {
				tasks.image(itNav.get(it)).click();
			}
			if (subTab!=null) {
				tasks.xy(tasks.cell(subTab), 3, 3).click();
			}
		}
		else {
			throw new RuntimeException("Selecting tab of type "+it.toString()+" is not implemented");
		}
	}
	/**
	 * selects given inventory tab
	 * you HAVE to be on Inventory page when using this method
	 * @param it Inventory Tab name
	 */
	public void inventorySelectTab(String it) {
		inventorySelectTab(it, null);
	}
	
	/**
	 * navigates to resource in inventory defined by its path
	 * @param agent = platform containing desired resource
	 * @param it = inventory tab you want to jump in (Operations/Drifts/...)
	 * @param resourcePath for example 'Eap server-one','datasources','java:jboss/datasources/ExampleDS'
	 * would navigate to example datasource on EAP server-one resource
	 */
	public void inventoryGoToResource(String agent, String it, String... resourcePath) {
		tasks.link("Inventory").click();
        tasks.cell("Platforms").click();
        log.fine("select agent "+agent);
        tasks.link(agent).click();
        for (String element : resourcePath) {
        	log.fine("select resource : "+element);
	        inventorySelectTab("Inventory","Child Resources");
	        tasks.link(element).click();
        }
        inventorySelectTab(it);
	}
}
