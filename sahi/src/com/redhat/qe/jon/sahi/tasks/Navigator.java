package com.redhat.qe.jon.sahi.tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.redhat.qe.jon.sahi.base.inventory.Resource;
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
	/**
	 * navigates to resource in inventory defined by its path
	 * @param agent = platform containing desired resource
	 * @param it = inventory tab you want to jump in (Operations/Drifts/...)
	 * @param resourcePath for example 'Eap server-one','datasources','java:jboss/datasources/ExampleDS'
	 * would navigate to example datasource on EAP server-one resource
	 */
	public void inventoryGoToResource(String agent, String it, String... resourcePath) {		
        inventoryGoToResource(new InventoryNavigation(agent, it, resourcePath));
	}
	
	public void inventoryGoToResource(InventoryNavigation nav) {
		tasks.link("Inventory").click();
        tasks.cell("Platforms").click();
        log.fine("Select agent "+nav.getAgent());
        tasks.link(nav.getAgent()).click();
        for (String element : nav.getResourcePath()) {
        	log.fine("Select resource : "+element);
        	tasks.waitFor(timeout);
	        inventorySelectTab("Inventory","Child Resources");	        
	        tasks.link(element).click();
	        log.fine("Clicked resource : "+element);
        }
        tasks.waitFor(timeout);
        inventorySelectTab(nav.getInventoryTab());
        log.fine("Navigation to "+nav.toString()+ " done.");
	}
	public void inventoryGoToResource(Resource res) {
		tasks.link("Inventory").click();
		tasks.waitFor(timeout);
		tasks.cell("Platforms").click();
		tasks.waitFor(timeout);
        log.fine("Select platform ["+res.getPlatform()+"]");
        tasks.link(res.getPlatform()).click();
        for (int i = 1;i<res.getPath().size();i++) {
        	String element = res.getPath().get(i);
        	log.fine("Select element ["+element+"]");
        	tasks.waitFor(timeout);
	        inventorySelectTab("Inventory","Child Resources");	        
	        tasks.link(element).click();
	        log.fine("Clicked element ["+element+"]");
        }
        tasks.waitFor(timeout);
        log.fine("Navigation to "+res.toString()+ " done.");
	}
	
	public static class InventoryNavigation {
		private final String agent;
		private final String inventoryTab;
		private final String[] resourcePath;
		
		public InventoryNavigation(String agent, String it,String...resourcePath) {
			this.agent = agent;
			this.inventoryTab = it;
			this.resourcePath = resourcePath;
			assert this.agent != null;
			assert this.inventoryTab != null;
			assert this.resourcePath !=null;
		}
		/**
		 * gets name of agent
		 * @return
		 */
		public String getAgent() {
			return agent;
		}
		/**
		 * gets Inventory tab
		 * @return
		 */
		public String getInventoryTab() {
			return inventoryTab;
		}
		/**
		 * gets path to be navigated to within agent
		 * @return
		 */
		public String[] getResourcePath() {
			return resourcePath;
		}
		/**
		 * gets target resource name (last item of {@link InventoryNavigation#getResourcePath()}
		 * @return
		 */
		public String getResourceName() {
			return getResourcePath()[getResourcePath().length-1];
		}
		/**
		 * returns new instance of Navigation class with added item 'element' at the end of resourcePath
		 * @param element
		 * @return
		 */
		public InventoryNavigation pathPush(String element) {
			InventoryNavigation nav = new InventoryNavigation(this.agent, this.inventoryTab, Arrays.copyOf(this.resourcePath, this.resourcePath.length+1));
			nav.resourcePath[nav.resourcePath.length-1] = element;
			//log.fine("Created new navigation: "+nav.toString());
			return nav;
		}
		/**
		 * returns new instance of Navigation class with last item removed from resourcePath
		 * @return
		 */
		public InventoryNavigation pathPop() {
			if (this.resourcePath.length>0) {
				String newPath[] = new String[this.resourcePath.length-1];
				for (int i=0;i<this.resourcePath.length-1;i++) {
					newPath[i] = this.resourcePath[i];
				}
				InventoryNavigation nav = new InventoryNavigation(this.agent, this.inventoryTab, newPath);
				//log.fine("Created new navigation: "+nav.toString());
				return nav;
			}
			else {
				throw new RuntimeException("cannot pop item from path, path is already empty");
			}
		}
		/**
		 * returns new instance of Navigation class having inventory tab specified by 'name' param
		 * @param name
		 * @return
		 */
		public InventoryNavigation setInventoryTab(String name) {
			InventoryNavigation nav = new InventoryNavigation(this.agent, name, resourcePath);
			//log.fine("Created new navigation: "+nav.toString());
			return nav;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(getInventoryTab()+" : "+getAgent());
			for (String path : getResourcePath()) {
				sb.append("/"+path);
			}
			return sb.toString();
		}
	}
}
