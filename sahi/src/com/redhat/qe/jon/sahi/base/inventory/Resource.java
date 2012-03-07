package com.redhat.qe.jon.sahi.base.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
/**
 * this represents RHQ Resource. Each resource is defined by its path within inventory. 
 * Path starts with {@link Resource#getPlatform()} and ends with {@link Resource#getName()}
 * By creating instance of this class nothing happens, but when you start calling methods like
 * {@link Resource#inventory()} or {@link Resource#exists()} the real magic begins
 * @author lzoubek
 *
 */
public class Resource {

	private final List<String> path;
	private final SahiTasks tasks;
	private static final Logger log = Logger.getLogger(Resource.class.getName());
	public Resource(SahiTasks tasks, String... path) {
		this(tasks,Arrays.asList(path));
	}
	/**
	 * creates new instance of resource, no actions (navigation etc) are performed
	 * @param tasks
	 * @param path
	 */
	private Resource(SahiTasks tasks, List<String> path) {
		this.tasks = tasks;
		this.path = path;
		if (this.path.isEmpty()) {
			throw new RuntimeException("Resource path cannot be empty");
		}
	}
	/**
	 * navigates to this resource, note that Resource Tab that being selected is undefined
	 */
	public void navigate() {
		tasks.getNavigator().inventoryGoToResource(this);
	}

	/**
	 * creates <b>Inventory</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Inventory inventory() {
		return new Inventory(tasks, this);
	}
	/**
	 * creates <b>Configuration</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Configuration configuration() {
		return new Configuration(tasks,this);
	}
	/**
	 * creates <b>Operations</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Operations operations() {
		return new Operations(tasks, this);
	}
	/**
	 * creates <b>Summary</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Summary summary() {
		return new Summary(tasks, this);
	}
	/**
	 * creates <b>Monitoring</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Monitoring monitoring() {
		return new Monitoring(tasks, this);
	}
	/**
	 * resource's path
	 * @return
	 */
	public List<String> getPath() {
		return path;
	}
	/**
	 * platform name - first item in {@link Resource#getPath()}
	 * @return
	 */
	public String getPlatform() {
		return this.path.get(0);
	}
	/**
	 * resource name - last item in {@link Resource#getPath()}
	 * @return
	 */
	public String getName() {
		return this.path.get(this.path.size()-1);
	}
	/**
	 * 
	 * @param name of child resource
	 * @return new Resource instance representing its child of given name
	 */
	public Resource child(String name) {
		List<String> newPath = new ArrayList<String>();
		newPath.addAll(this.path);
		newPath.add(name);
		return new Resource(tasks,newPath);
	}
	/**
	 * @return new instance of resource representing its parent
	 */
	public Resource parent() {
		List<String> newPath = new ArrayList<String>();
		newPath.addAll(this.path);
		if (newPath.size()<=1) {
			throw new RuntimeException("You cannot get parent resource for platform !!");
		}
		newPath.remove(newPath.size()-1);
		return new Resource(tasks,newPath);
	}
	/**
	 * checks whether this resource is available (ONLINE) by navigating to it's summary 
	 * and searching for UP/DOWN image.
	 * Note that this will call Assert.fail when unable to determine resource availability
	 * @return true if this resource is online, false if it is offline
	 */
	public boolean isAvailable() {
		this.summary();
        if (tasks.image("Server_down_24.png").exists()) {
            log.fine("Resource [" + getName() + "] is offline!");
            return false;
        }
        if (tasks.image("Server_up_24.png").exists()) {
            log.fine("Resource [" + getName() + "] is online!");
            return true;
        }
        log.info("Could not verify whether a resource ["+getName()+"] is online or offline -- neither Server_down_16.png nor Server_up_16.png was found");
        return false;

	}
	/**
	 * navigates to parent resource of this resource and checks whether this resource exists
	 * @return true if this resource exists
	 */
	public boolean exists() {
		return parent().inventory().childResources().existsChild(getName());
	}
	/**
	 *  runs <b>Manual Autodiscovery</b> operation on parent platform of this resource
	 */
	public void performManualAutodiscovery() {
		log.info("Performing [Manual Autodiscovery]"); 
		Resource platform = new Resource(tasks, getPlatform());
	     Operation op = platform.operations().newOperation("Manual Autodiscovery");
	     op.schedule();
	     platform.operations().assertOperationResult(op, true);
	     log.info("Manual Autodiscovery DONE");
	}
	/**
	 * removes resource from inventory
	 * @param mustExist - if set to true this method will fail if this resource does not exist
	 */
	public void uninventory(boolean mustExist) {
		if (!mustExist && !exists()) {
			return;
		}
		parent().inventory().childResources().uninventoryChild(getName());		
	}
	/**
	 * deletes this resource from inventory, do not use for deleting platforms
	 */
	public void delete() {
		parent().inventory().childResources().deleteChild(getName());
	}

    /**
     * asserts resource availability
     * @param shouldBeAvailable - true for asserting resource ON, false for checking resource being OFF
     */
    public void assertAvailable(boolean shouldBeAvailable, String message) {
    	log.fine("Asserting resource "+toString()+" is available - expected: "+shouldBeAvailable);
    	int waitTime=Timing.WAIT_TIME;
    	int count=Timing.REPEAT;
    	
    	String resourceName = getName();
    	if (message==null) {
    		message = "Resource ["+resourceName+"] is AVAILABLE.";
    	}
    	boolean available=false;
    	for (int i = 0;i<count;i++) {
    		log.fine("Checking that resource online="+shouldBeAvailable+": try #" + Integer.toString(i + 1) + " of "+count);
    		available=isAvailable();
    		if (shouldBeAvailable && available) {
    			Assert.assertTrue(available, message);
    			return;
    		}
    		if (!shouldBeAvailable && !available) {
    			Assert.assertFalse(false, message);
    			return;
    		}
    		log.fine("Waiting for resource, refreshing ..");
    		tasks.waitFor(waitTime);
    	}
    	log.fine("Checking resurce (un)existence timed out");
    	Assert.assertTrue(false, message);
    }
	
	
    /**
     * asserts resource existence
     * @param shouldExist - true for asserting resource existence, false for checking resource non-existence
     */
    public void assertExists(boolean shouldExist) {
    	log.fine("Asserting resource "+toString()+" exists");
    	int waitTime=Timing.WAIT_TIME;
    	int count=Timing.REPEAT;
    	
    	String resourceName = getName();
    	Inventory inventory = parent().inventory();
    	ChildResources children = inventory.childResources();
    	boolean exists=false;
    	for (int i = 0;i<count;i++) {
    		exists=children.existsChild(resourceName);
    		if (shouldExist && exists) {
    			Assert.assertTrue(exists, "Resource ["+resourceName+"] exists.");
    			return;
    		}
    		if (!shouldExist && !exists) {
    			Assert.assertFalse(false, "Resource ["+resourceName+"] exists.");
    			return;
    		}
    		if (i % 4 == 0) {
    			inventory = parent().inventory();
    		}
    		log.fine("Waiting for resource, refreshing ..");
    		tasks.waitFor(waitTime);
    		children.refresh();
    	}
    	log.fine("Checking resurce (un)existence timed out");
    	Assert.assertTrue(false, "Resource ["+resourceName+"] exists.");
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String path : getPath()) {
			sb.append("/"+path);
		}
		return "["+sb.toString()+"]";
	}
	
}
