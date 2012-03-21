package com.redhat.qe.jon.sahi.base.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.redhat.qe.jon.rest.tasks.RestClient;
import com.redhat.qe.jon.rest.tasks.RestClient.URIs;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.sun.jersey.api.client.WebResource;
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
	private final String id;
	private static final Logger log = Logger.getLogger(Resource.class.getName());

	public Resource(SahiTasks tasks, String... path) {
		this(null,tasks,Arrays.asList(path));
	}
	public Resource(String id,SahiTasks tasks, String... path) {
		this(id,tasks,Arrays.asList(path));
	}
	/**
	 * creates new instance of resource, no actions (navigation etc) are performed
	 * @param tasks
	 * @param path
	 */
	private Resource(String id,SahiTasks tasks, List<String> path) {
		this.tasks = tasks;
		this.path = path;
		this.id = id;
		if (this.path.isEmpty()) {
			throw new RuntimeException("Resource path cannot be empty");
		}
	}
	/**
	 * gets ID of resource, can be null which is perfectly valid for cases when we do not use/know it
	 * @return
	 */
	public String getId() {
		return id;
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
		return (Inventory)new Inventory(tasks, this).navigateFull();
	}
	/**
	 * creates <b>Configuration</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Configuration configuration() {
		return (Configuration)new Configuration(tasks,this).navigateFull();
	}
	/**
	 * returns {@link ResourceTab} object representing <b>Configuration</b> tab
	 * without navigating to it
	 * @return
	 */
	public Configuration configurationNoNav() {
		return new Configuration(tasks,this);
	}
	/**
	 * creates <b>Operations</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Operations operations() {
		return (Operations)new Operations(tasks, this).navigateFull();
	}
	/**
	 * creates <b>Summary</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Summary summary() {
		return (Summary)new Summary(tasks, this).navigateFull();
	}
	/**
	 * creates <b>Monitoring</b> resource tab for this resource and navigates to it
	 * @return
	 */
	public Monitoring monitoring() {
		return (Monitoring)new Monitoring(tasks, this).navigateFull();
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
	 * this method uses REST API to retrieve all children of current resource
	 * @return a List of resources that are direct or indirect ancestors to this resource
	 */
	public List<Resource> getChildrenTree() {
		log.fine("getChildrenRecursive for resource "+toString());
		List<Resource> children = new ArrayList<Resource>();
		
		//first we need to find current resource
		String url = System.getProperty("jon.server.url")+"/rest/1";
		RestClient rc = new RestClient();
		WebResource res = rc.getWebResource(url, "rhqadmin", "rhqadmin");		
		String platformId = findPlatformId(rc, res);
		if (platformId==null) {
			throw new RuntimeException("Unable to find platform ID of "+toString()+" using REST API");
		}
		
		try {
			String myId = findResourceId(rc, res, platformId);
			return getChidrenRecursive(rc, res, myId, this);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return children;
	}
	private List<Resource> getChidrenRecursive(RestClient rc, WebResource res, String myID, Resource current) throws Exception {
		List<Resource> children = new ArrayList<Resource>();
		for (Entry<String,String> entry : getChildren(rc, res, myID).entrySet()) {
			Resource child = current.child(entry.getValue(),entry.getKey());
			children.add(child);
			children.addAll(getChidrenRecursive(rc, res, entry.getKey(), child));
		}
		return children;
	}
	/**
	 * finds id of platform for this resource
	 * @param rc
	 * @param res
	 * @return
	 */
	private String findPlatformId(RestClient rc, WebResource res) {
		HashMap<String, Object> result = rc.getReponse(res, URIs.PLATFORMS.getUri()+".json");
		
		JSONArray jsonArray = null;
		try {
			jsonArray = rc.getJSONArray((String) result.get("response.content"));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		log.fine("Number of Platfoms(s): "+jsonArray.size());
		Assert.assertTrue(jsonArray.size()>0, "Number of Platform(s) [>0] : "+jsonArray.size());
		JSONObject jsonObject;
		String platformId = null;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			String platform = jsonObject.get("resourceName").toString();
			if (getPlatform().equals(platform)) {
				platformId = jsonObject.get("resourceId").toString();
				break;
			}
		}
		return platformId;
	}
	/**
	 * finds ID of this resource using REST
	 * @param rc
	 * @param res
	 * @param platformId
	 * @return
	 * @throws Exception
	 */
	private String findResourceId(RestClient rc, WebResource res, String platformId) throws Exception {
		int pathIndex = 1;
		String currentResource = platformId;
		while (pathIndex<getPath().size()) {
			String node = getPath().get(pathIndex);
			boolean found = false;
			for (Entry<String,String> entry : getChildren(rc, res, currentResource).entrySet()) {
				if (entry.getValue().equals(node)) {
					currentResource = entry.getKey();
					pathIndex+=1;
					found = true;
					break;
				}	
			}
			if (!found) {
				throw new RuntimeException("Unable to find resource child ["+node+"] with parent [ID="+currentResource+"] via REST API");
			}
			
		}
		
		return currentResource;
	}
	/**
	 * gets children of given resource (resourceId) using REST
	 * @param rc
	 * @param res
	 * @param resourceId
	 * @return
	 * @throws Exception
	 */
	private Map<String,String> getChildren(RestClient rc, WebResource res,String resourceId) throws Exception {
		Map<String,String> children = new HashMap<String, String>();
		HashMap<String, Object> result = rc.getReponse(res,"resource/"+resourceId+"/children.json");
		
		JSONArray jsonArray = rc.getJSONArray((String)result.get("response.content"));		
		
		log.fine("Number of Resource(s): "+jsonArray.size());
		JSONObject jsonObject;
		for(int i=0; i<jsonArray.size();i++){
			jsonObject = (JSONObject) jsonArray.get(i);
			children.put(jsonObject.get("resourceId").toString(),jsonObject.get("resourceName").toString());
		}
		return children;
	}
	/**
	 * 
	 * @param name of child resource
	 * @param id id of a new child resource
	 * @return new Resource instance representing its child of given name
	 * 
	 */
	public Resource child(String name, String id) {
		List<String> newPath = new ArrayList<String>();
		newPath.addAll(this.path);
		newPath.add(name);
		return new Resource(id, tasks,newPath);
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
		return new Resource(null, tasks,newPath);
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
		return new Resource(null,tasks,newPath);
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
