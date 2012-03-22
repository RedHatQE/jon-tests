package com.redhat.qe.jon.sahi.base.inventory;

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
	 * @return
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

	/**
	 * navigates to anything under resource's URI 
	 * @param uri for example "Configuration/Current" will navigate to resource's current configuration
	 */
	protected void navigateUnderResource(String uri) {
		String url = tasks.getNavigator().getServerBaseUrl()+"/#Resource/"+resource.getId()+"/"+uri;
		tasks.navigateTo(url,false);
		tasks.waitFor(Timing.WAIT_TIME);
	}
	protected void raiseErrorIfCellDoesNotExist(String cell) {
		if (!tasks.cell(cell).exists()) {
			throw new RuntimeException("Tab ["+cell+"] does not exist for resource "+getResource().toString());
		}
	}
}
