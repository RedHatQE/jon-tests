package com.redhat.qe.jon.sahi.base.inventory;

import java.util.logging.Logger;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
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
	

	/**
	 * Creates new instance of Resource tab, navigation is performed within
	 * constructor call
	 * 
	 * @param tasks
	 * @param resource
	 */
	public ResourceTab(SahiTasks tasks, Resource resource) {
		log = Logger.getLogger(this.getClass().getName());
		this.tasks = tasks;
		this.resource = resource;
		navigateFull();
	}

	/**
	 * navigates to given Tab within the resource.
	 */
	protected abstract void navigate();

	/**
	 * gets resource, that is being navigated on
	 * 
	 * @return
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * fully navigates to given Tab, (selects the resource from it's root, then
	 * selects Tab)
	 */
	public void navigateFull() {
		getResource().navigate();
		navigate();
	}
	/**
	 * selects tab of given name on resource page (Inventory, Configuration etc.)
	 * @param it
	 */
	protected void selectTab(String it) {
		selectTab(it,null);
	}
	/**
	 * same as {@link ResourceTab#selectTab(String)} but you can also specify sub tab under given tab. 
	 * For example <b>Connection Settings</b> is a subtab of <b>Inventory</b>
	 * @param it
	 * @param subTab
	 */
	protected void selectTab(String it, String subTab) {		
		tasks.getNavigator().inventorySelectTab(it, subTab);
	}
}
