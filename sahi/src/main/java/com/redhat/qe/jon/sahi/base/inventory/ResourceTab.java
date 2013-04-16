package com.redhat.qe.jon.sahi.base.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
/**
 * this abstract class is extended by each Resource Tab (Inventory, Configuration, Operations etc)
 * it has always reference to {@link Resource} object
 * @author lzoubek
 *
 */
public abstract class ResourceTab {

	public final Logger log;
	protected final SahiTasks tasks;
	protected final Resource resource;
	protected static Map<String,String[]> uriToTabmapping= new HashMap<String, String[]>();
	static {
		uriToTabmapping.put("Inventory", new String[] {"Inventory"});
		uriToTabmapping.put("Inventory/Children", new String[] {"Inventory","Child Resources"});
		uriToTabmapping.put("Inventory/ChildHistory", new String[] {"Inventory","Child History"});
		uriToTabmapping.put("Inventory/ConnectionSettings", new String[] {"Inventory","Connection Settings"});
		uriToTabmapping.put("Inventory/Children", new String[] {"Inventory","Child Resources"});
		uriToTabmapping.put("Configuration", new String[] {"Configuration"});
		uriToTabmapping.put("Configuration/Current", new String[] {"Configuration","Current"});
		uriToTabmapping.put("Configuration/History", new String[] {"Configuration","History"});
		uriToTabmapping.put("Monitoring", new String[] {"Monitoring"});
		uriToTabmapping.put("Monitoring/Schedules", new String[] {"Monitoring","Schedules"});
		uriToTabmapping.put("Monitoring/Tables", new String[] {"Monitoring","Tables"});
        uriToTabmapping.put("Monitoring/Traits", new String[] {"Monitoring","Traits"});
		uriToTabmapping.put("Operations", new String[] {"Operations"});
		uriToTabmapping.put("Operations/Schedules", new String[] {"Operations","Schedules"});
		uriToTabmapping.put("Summary", new String[] {"Summary"});
	}

	/**
	 * Creates new instance of Resource tab
	 * 
	 * @param tasks
	 * @param resource
	 */
	public ResourceTab(SahiTasks tasks, Resource resource) {
		log = Logger.getLogger(this.getClass().getName());
		this.tasks = tasks;
		this.resource = resource;
	}
	
	/**
	 * navigates to given Tab within the resource.
	 */
	protected abstract void navigate();

	/**
	 * gets resource, that is being navigated on
	 * 
	 * @return resource, that is being navigated on
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * fully navigates to given Tab, (selects the resource from it's root, then
	 * selects Tab)
	 */
	public ResourceTab navigateFull() {
		getResource().navigate();
		navigate();
		return this;
	}

	private void navigateDirectly(String uri) {
		String url = tasks.getNavigator().getServerBaseUrl()+"/#Resource/"+resource.getId()+"/"+uri;
		tasks.navigateTo(url,false);
		tasks.waitFor(Timing.WAIT_TIME);
	}
	private void navigateByClick(String uri) {
		String[] tabs = uriToTabmapping.get(uri);
		if (tabs==null) {
			throw new RuntimeException("No mapping for ["+uri+"] cannot navigate by clicking!!! fix the code");
		}
		if (tabs.length==1) {
			tasks.getNavigator().inventorySelectTab(tabs[0]);
		}
		else if (tabs.length==2) {
			tasks.getNavigator().inventorySelectTab(tabs[0], tabs[1]);
		}
		else {
			throw new RuntimeException("Invalid mapping "+Arrays.toString(tabs)+" for URI ["+uri+"], fix the code");
		}
	}
	/**
	 * navigates to anything under resource's URI 
	 * @param uri for example "Configuration/Current" will navigate to resource's current configuration
	 */
	protected void navigateUnderResource(String uri) {
		if (Resource.HAVE_REST_API) {
		navigateDirectly(uri);
		}
		else {
			navigateByClick(uri);
		}
	}
	protected void raiseErrorIfCellDoesNotExist(String cell) {
		if (!tasks.cell(cell).exists()) {
			throw new RuntimeException("Tab ["+cell+"] does not exist for resource "+getResource().toString());
		}
	}
}
