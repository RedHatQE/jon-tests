package com.redhat.qe.jon.sahi.base.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;

import net.sf.sahi.client.ElementStub;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.common.util.HTTPClient;
import com.redhat.qe.jon.common.util.RestClient;
import com.redhat.qe.jon.common.util.RestClient.URIs;
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

	/**
	 * internal static cache of resource IDs
	 */
	private static final Map<String,String> idChache = new HashMap<String, String>();
	private final List<String> path;
	private final boolean isPlatform;
	private final SahiTasks tasks;
	private String id;
	private String resourceType;
	private static final Logger log = Logger.getLogger(Resource.class.getName());
	/**
	 * this value says, whether RHQ REST API will be used to get resource IDs and thus faster
	 * navigation, this is auto-detected (by checking whether RHQ REST end-point returns 200)
	 */
	public static final boolean HAVE_REST_API;
	
	static {
		HTTPClient client = new HTTPClient(System.getProperty("jon.server.url")+"/rest"); 		
		HAVE_REST_API = client.isRunning();
		if (HAVE_REST_API) {
			log.info("RHQ/JON server ["+System.getProperty("jon.server.url")+"] is configured with REST API, resource navigation will be fast as hell");
		}
		else {
			log.info("RHQ/JON server ["+System.getProperty("jon.server.url")+"] is configured without REST API, resource navigation will be slower");
		}
	}

	public Resource(SahiTasks tasks, String... path) {
		this(null,tasks,Arrays.asList(path));
	}
	public Resource(String id,SahiTasks tasks, String... path) {
		this(id,tasks,Arrays.asList(path));
	}
	public void setResourceType(String resourceType) {
	    this.resourceType = resourceType;
	}
	public String getResourceType() {
	    return resourceType;
	}
	/**
	 * creates new instance of resource, no actions (navigation etc) are performed
	 * @param tasks
	 * @param path
	 */
	private Resource(String id,SahiTasks tasks, List<String> path) {
		this.tasks = tasks;
		this.path = path;
		this.isPlatform = this.path.size() == 1;
		this.id = id;
		if (this.path.isEmpty()) {
			throw new RuntimeException("Resource path cannot be empty");
		}
		if (getId()!=null) {
			idChache.put(toString(), getId());
		}
	}
	/**
	 * gets ID of resource, can be null which is perfectly valid for cases when we do not use/know it
	 * @return ID of resource
	 */
	public String getId() {
		return id;
	}
	/**
	 * navigates to this resource, note that Resource Tab that being selected is undefined
	 */
	public void navigate() {
		if (HAVE_REST_API) {
			fetchId(false);
			String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
			String url = serverBaseUrl+"/#Resource/"+getId()+"/Inventory";
			log.fine("Navigating to ["+url+"]");
			tasks.navigateTo(url,false);
			ElementStub es =  tasks.byXPath("//td[@class='WarnBlock'][1]");
			if (es.exists() && es.getText().contains("does not exist")) {
				// need to refresh resource's ID
				fetchId(true);
				if (getId()!=null) {
					tasks.navigateTo(serverBaseUrl+"/#Resource/"+getId()+"/Inventory",false);
				}
			}
			log.fine("Navigation to "+toString()+ " done.");
		}
		else {
			tasks.getNavigator().inventoryGoToResource(this);
		}
	}

	/**
	 * creates <b>Inventory</b> resource tab for this resource and navigates to it
	 * @return resource tab
	 */
	public Inventory inventory() {
		return (Inventory)new Inventory(tasks, this).navigateFull();
	}
	/**
	 * creates <b>Configuration</b> resource tab for this resource and navigates to it
	 * @return resource tab
	 */
	public Configuration configuration() {
		return (Configuration)new Configuration(tasks,this).navigateFull();
	}
	/**
	 * returns {@link ResourceTab} object representing <b>Configuration</b> tab
	 * without navigating to it
	 * @return resource tab
	 */
	public Configuration configurationNoNav() {
		return new Configuration(tasks,this);
	}
	/**
	 * creates <b>Operations</b> resource tab for this resource and navigates to it
	 * @return resource tab
	 */
	public Operations operations() {
		return (Operations)new Operations(tasks, this).navigateFull();
	}
	/**
	 * creates <b>Summary</b> resource tab for this resource and navigates to it
	 * @return resource tab
	 */
	public Summary summary() {
		return (Summary)new Summary(tasks, this).navigateFull();
	}
	/**
	 * creates <b>Monitoring</b> resource tab for this resource and navigates to it
	 * @return resource tab
	 */
	public Monitoring monitoring() {
		return (Monitoring)new Monitoring(tasks, this).navigateFull();
	}
	/**
	 * resource's path
	 * @return resource's path
	 */
	public List<String> getPath() {
		return path;
	}
	/**
	 * platform name - first item in {@link Resource#getPath()}
	 * @return platform name
	 */
	public String getPlatform() {
		return this.path.get(0);
	}
	/**
	 * resource name - last item in {@link Resource#getPath()}
	 * @return resource name
	 */
	public String getName() {
		if(this.isPlatform){
			// this resource is platform
			return this.path.get(0);
		}else{
			return this.path.get(this.path.size()-1);
		}
	}
	/**
	 * tries to fetch resource ID via REST API. This might be used as a very fast
	 * check of resource existence in inventory
	 * @return true if resource was found via REST API
	 */
	public boolean tryFetchId() {
	    log.fine("Trying to fetch ID for resource "+toString());
	    try {
		if (HAVE_REST_API) {
		    fetchId(true);
		    return true;
		}
	    }
	    catch (Exception ex) {
		
	    }
	    return false;
	}
	/**
	 * whis method checks whether {@link Resource#getId()} is null. 
	 * If it is, it tries to get it using REST API. If this resource does not exist within RHQ 
	 * ( is not found using REST API) no error is raised, but  {@link Resource#getId()} stays null
	 * @param overwrite = set to true to overwrite ID even it is not null (this may be useful, when ID of a resource
	 * changes - for example by re-inventorying it)
	 */
	private void fetchId(boolean overwrite) {
		this.id = idChache.get(toString());
		
		if (overwrite || this.id == null) {
			//first we need to find current resource
			RestClient rc = RestClient.getDefault();		
			String platformId = findPlatformId(rc);
			if (platformId==null) {
				throw new RuntimeException("Unable to find platform ID for resource "+this.toString());
			}
			try {
				this.id = findResourceId(rc, platformId);
				if (getId()!=null) {
					idChache.put(toString(), getId());
				}
				log.fine("Resource "+toString()+" fetched ID="+this.id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.id==null) {
			throw new RuntimeException("Unable to fetch ID for resource "+toString()+" using REST!!");
		}
	}
	/**
	 * retrieves additional data to this resource using REST interface
	 * @throws Exception when id of this resource is not known (null)
	 */
	public void fetchRestData() throws Exception {
	    if (this.getId() == null) {
		throw new Exception("ID of this resouce is not defined, cannot fetch additional data");
	    }
	    RestClient rc = RestClient.getDefault();
	    Map<String, Object> result = rc.getResponse("resource/"+this.getId()+".json");
	    JSONObject jsonObject = rc.getJSONObject((String)result.get("response.content"));
	    String typeName = jsonObject.get("typeName").toString();
	    this.setResourceType(typeName);
	}
	/**
	 * this method uses REST API to retrieve all children of current resource
	 * @return a List of resources that are direct or indirect ancestors to this resource
	 */
	public List<Resource> getChildrenTree() {
		log.fine("getChildrenRecursive for resource "+toString());
		if (!HAVE_REST_API) {
			throw new RuntimeException("Cannot retrieve children tree, REST API does not seem to be available on "+System.getProperty("jon.server.url"));
		}
		List<Resource> children = new ArrayList<Resource>();
		
		//first we need to find current resource
		RestClient rc = RestClient.getDefault();		
		String platformId = findPlatformId(rc);
		if (platformId==null) {
			throw new RuntimeException("Unable to find platform ID of "+toString()+" using REST API");
		}
		
		try {
			this.id = findResourceId(rc,  platformId);
			return getChidrenRecursive(rc, this);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return children;
	}
	private List<Resource> getChidrenRecursive(RestClient rc, Resource current) throws Exception {
		List<Resource> children = new ArrayList<Resource>();
		current.fetchRestData();
		for (Entry<String,String> entry : getChildren(rc, current.getId()).entrySet()) {
			Resource child = current.child(entry.getValue(),entry.getKey());			
			children.add(child);
			children.addAll(getChidrenRecursive(rc, child));
		}
		return children;
	}
	/**
	 * finds id of platform for this resource
	 * @param rc
	 * @return id of platform
	 */
	private String findPlatformId(com.redhat.qe.jon.common.util.RestClient rc) {
		Map<String, Object> result = rc.getResponse( URIs.PLATFORMS.getUri()+".json");
		
		JSONArray jsonArray = null;
		try {
			jsonArray = rc.getJSONArray((String) result.get("response.content"));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		log.fine("Number of Platfoms(s): "+jsonArray.size());
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
	 * @param platformId
	 * @return ID of this resource
	 * @throws Exception
	 */
	private String findResourceId(RestClient rc, String platformId) throws Exception {
		int pathIndex = 1;
		String currentResource = platformId;
		while (pathIndex<getPath().size()) {
			String node = getPath().get(pathIndex);
			boolean found = false;
			for (Entry<String,String> entry : getChildren(rc, currentResource).entrySet()) {
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
	 * @param resourceId
	 * @return children of given resource (resourceId)
	 * @throws Exception
	 */
	private Map<String,String> getChildren(RestClient rc, String resourceId) throws Exception {
		Map<String,String> children = new HashMap<String, String>();
		Map<String, Object> result = rc.getResponse("resource/"+resourceId+"/children.json");
		
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
	 * @return true if this resource is online, false if it is offline
	 */
	public boolean isAvailable() {
		// we need to retry detection when resource is in unknown state
		int count = 0;
		while (count < Timing.REPEAT) {
			count++;
			// because UI caches availability things and we keep refreshing same page
			// we need to force it to reload .. so we navigate somewhere else
			tasks.link("Dashboard").click();
			this.summary();
	        if (tasks.image("Server_down_24.png").exists()) {
	            log.fine("Resource [" + getName() + "] is offline!");
	            return false;
	        }
	        if (tasks.image("Server_up_24.png").exists() || tasks.image("availability_green_24.png").exists()) {
	            log.fine("Resource [" + getName() + "] is online!");
	            return true;
	        }
	        log.info("Waiting "+Timing.toString(Timing.TIME_30S)+" for resource to be in known state ..");
	        tasks.waitFor(Timing.TIME_30S);
        }
        log.info("Could not verify whether a resource ["+getName()+"] is online or offline -- neither Server_down_16.png nor Server_up_16.png was found and resource remained in unknown state for too long");
        return false;
	}

  /**
   * Method which add column Last Modified Time and sort descending  the table by this column
   * 
   */
	public void sortChildResources() {
	  // 1. Add column Last Modified Time
	  //tasks.cell("Name").click();
	  //tasks.cell("Name").rightClick();
	  
	  tasks.xy(tasks.cell("Name"), 3, 3).rightClick();
	  
	  tasks.waitFor(Timing.WAIT_TIME);
/*	  
	  tasks.cell("Colums").mouseOver();
    tasks.cell("Last Modified Time[1]").click();
    tasks.waitFor(Timing.WAIT_TIME);
	  // 2. Set Auto Fit All Columns
    tasks.cell("Auto Fit All Columns").click();
    tasks.waitFor(Timing.WAIT_TIME);
	  // 3. Sort the table by Last Modified Time descending
    // sort by Last Modified Time
    tasks.cell("Last Modified Time").click();
    tasks.waitFor(Timing.WAIT_TIME);
    tasks.cell("Last Modified Time").click();
    tasks.waitFor(Timing.WAIT_TIME);
*/
  }
	
	
    /**
     * Its unstable method
     * Method which filters child resources based on the provided name using search box
     * @param name used for filtering child resources
     * 
     */
  public void filterChildResources(String name) {
    log.fine("Filtering elements by name: " + name);
    if (tasks.textbox("SearchPatternField").exists()) {
        log.fine("Textbox SearchPatternField Exists");
        tasks.textbox("SearchPatternField").setValue(name);
        tasks.hidden("search").setValue(name);
        tasks.textbox("SearchPatternField").click();
        //tasks.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);");
        //tasks.execute("_sahi._typeNativeKeyCode(java.awt.event.KeyEvent.VK_ENTER);");
        // Sahi keypress doesn't work using JDK awt robot - AWT ROBOT WORKS!!
        try {
          Robot robot = new Robot();
          robot.setAutoDelay(1000);
          robot.keyPress(KeyEvent.VK_ENTER);
          robot.keyRelease(KeyEvent.VK_ENTER);
          log.fine("After robot.keyRelease");
          log.fine("AWT isheadless: " + System.getProperty("java.awt.headless"));
        } 
        catch (AWTException ex) {
          log.fine("filterChildResources(SearchPatternField): AWT Robot pressing enter thrown exception: " + ex.getMessage());
        }
        tasks.waitFor(Timing.TIME_5S*2);  
    } else {
        tasks.textbox("search").setValue(name);
        tasks.textbox("search").click();
        //tasks.execute("_sahi._keyPress(_sahi._textbox('SearchPatternField'), 13);");
        //tasks.execute("_sahi._typeNativeKeyCode(java.awt.event.KeyEvent.VK_ENTER);");
        // Sahi keypress doesn't work using JDK awt robot - AWT ROBOT WORKS!
        try {
          Robot robot = new Robot();
          robot.setAutoDelay(1000);
          robot.keyPress(KeyEvent.VK_ENTER);
          robot.keyRelease(KeyEvent.VK_ENTER);
          log.fine("After robot.keyRelease");
          log.fine("AWT isheadless: " + System.getProperty("java.awt.headless"));
        } 
        catch (AWTException ex) {
          log.fine("filterChildResources(search): AWT Robot pressing enter thrown exception: " + ex.getMessage());
        }
        tasks.waitFor(Timing.TIME_5S*2);
    }
  }

	/**
	 * navigates to parent resource of this resource and checks whether this resource exists.
	 * Parent resource MUST exist (except for platform)!!
	 * @return true if this resource exists
	 */
	public boolean exists() {
		// because UI (tree) caches things and we keep refreshing same page
		// we need to force it to reload .. so we navigate somewhere else
		tasks.link("Dashboard").click();
		if(this.isPlatform){
	        navigateToPlatforms();
			if (tasks.cell("No items to show").isVisible()) {
				return false;
			}
      //filterChildResources(this.getName());
			sortChildResources();
			return tasks.cell(this.getName()).isVisible();
		}else{
			return parent().inventory().childResources().existsChild(getName());
		}
	}
	private void navigateToPlatforms(){
		//TODO hardcoded waiting, make common method for this somewhere
		tasks.link("Inventory").click();
		tasks.waitFor(5000);
		tasks.cell("Platforms").click();
		tasks.waitFor(5000);
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
		if(this.isPlatform){
			navigateToPlatforms();
			log.fine("Uninventoring platform " + this.getName());
			int children = tasks.cell(this.getName()).countSimilar();
			log.fine("Matched cells "+children);
			if (children==0) {
				throw new RuntimeException("Unable to select resource ["+this.getName()+"], NOT FOUND!");
			}
			tasks.xy(tasks.cell(this.getName()+"["+(children-1)+"]"), 3, 3).click();
			tasks.cell("Uninventory").click();
			tasks.cell("Yes").click();
			
		}else{
			parent().inventory().childResources().uninventoryChild(getName());
		}
	}
	/**
	 * deletes this resource from inventory
	 * resource MUST exist .. otherwise this fails
	 */
	public void delete() {
		if(this.isPlatform){
			uninventory(false);
		}else{
			Inventory inventory = parent().inventory();
			inventory.childResources().deleteChild(getName());
			inventory.childHistory().assertLastResourceChange(true);
		}
	}
	/**
	 * deletes this resource from inventory
	 * @param mustExist - if set to true this method will fail if this resource does not exist
	 */
	public void delete(boolean mustExist) {
		if (mustExist || exists()) {
			delete();
		}
	}

    /**
     * asserts resource availability
     * @param shouldBeAvailable - true for asserting resource ON, false for checking resource being OFF
     */
    public void assertAvailable(boolean shouldBeAvailable, String message) {
    	log.fine("Asserting resource "+toString()+" is available - expected: "+shouldBeAvailable);
    	int waitTime=Timing.TIME_30S;
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
    		log.fine("Waiting "+Timing.toString(waitTime)+" for resource, refreshing ..");    		
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
    	if(this.isPlatform){
    		log.info("Checking existence of [" + this.getName() +"]");
    		if (shouldExist) {
    			Assert.assertTrue(exists(), "Resource ["+this.getName()+"] exists.");
    		}else{
    			Assert.assertFalse(exists(), "Resource ["+this.getName()+"] exists.");
    		}
    	}else{
    		parent().assertChildExists(this.getName(), shouldExist);
    	}
    }
    
    /**
     * asserts child resource existence
     * @param child name of child resource
     * @param shouldExist - true for asserting resource existence, false for checking resource non-existence
     */
    public void assertChildExists(String child, boolean shouldExist) {
    	log.fine("Asserting resource ["+child+"] parent="+toString()+" exists");
    	long time0 = Calendar.getInstance().getTimeInMillis();
    	int waitTime=Timing.TIME_30S;
    	int count=Timing.REPEAT;
    	
    	String resourceName = child;
    	Inventory inventory = inventory();
    	ChildResources children = inventory.childResources();
    	boolean exists=false;
    	for (int i = 0;i<count;i++) {
    		exists=children.existsChild(resourceName);
    		long time1 = (Calendar.getInstance().getTimeInMillis()-time0)/1000;
    		if (shouldExist && exists) {
    			log.info("Resource appeared after "+time1+"s");
    			Assert.assertTrue(exists, "Resource ["+resourceName+"] exists.");
    			return;
    		}
    		if (!shouldExist && !exists) {
    			log.info("Resource disappeared after "+time1+"s");
    			Assert.assertFalse(false, "Resource ["+resourceName+"] exists.");
    			return;
    		}
    		if (i % 4 == 0) {
    			inventory = inventory();
    		}
    		log.fine("Waiting for resource, refreshing ..");
    		tasks.waitFor(waitTime);
    		children.refresh();
    	}
    	log.warning("Checking resurce (un)existence timed out");
    	Assert.assertEquals(!shouldExist,shouldExist, "Resource ["+resourceName+"] exists.");
    }
    /**
     * imports this resource from discovery queue. It is required that parent platform is already imported (this doesn't 
     * apply to platform type).
     * This is done by following steps
     * <ul>
     * <ol>it is performed a check - if this resource is already in inventory nothing else happens</ol>
     * <ol>Manual Autodiscovery operation is performed on parent platform (this doesn't 
     * apply to platform type)</ol>
     * <ol>Resource is imported</ol>
     * </ul>
     * @param sleepTime time miliseconds to sleep after resource has been imported (JON imports it's children asynchronously)
     * @return true if resource was imported, false if it was not due to error or already existed in inventory
     */
	public boolean importFromDiscoQueue(int sleepTime) {
		String resourceName = this.getName();
		String agentName = this.getPlatform();
		log.fine("Trying to inventorize resource \"" + resourceName
				+ "\" of agent \"" + agentName + "\".");

		
		if (this.exists()) {
			log.fine("Resource \"" + resourceName + "\" of agent \""
					+ agentName + "\" have been already inventorized");

			return false;
		}
		if(!this.isPlatform){
			log.fine("Will perform manual autodiscovery first.");
			this.performManualAutodiscovery();
		}
		
		try {
			tasks.link("Inventory").click();
			tasks.cell("Discovery Queue").click();
			tasks.waitFor(Timing.WAIT_TIME);
			// sort by Discovery Time
			tasks.cell("Discovery Time").doubleClick();
			tasks.waitFor(Timing.WAIT_TIME);
			tasks.cell("Discovery Time").doubleClick();
			tasks.waitFor(Timing.WAIT_TIME);
			
			ElementStub elm = tasks.cell(agentName);
			if (elm.exists()) {
				elm.doubleClick();
			} else {
				throw new IllegalStateException();
			}

		} catch (IllegalStateException ex) {
			log.fine("Could not inventorize resource "
					+ resourceName
					+ ", nothing appeared in autodiscovery queue even after performing manual autodiscovery");
			return false;
		}
		
		
		ElementStub elm = tasks.image("unchecked.png").near(
				tasks.cell(resourceName));
		if (elm.exists()) {
			elm.check();
			// this resource is platform
			if(this.isPlatform){
				tasks.cell("No").click();
			}
			tasks.cell("Import").click();
			log.fine("Waiting " + Timing.toString(sleepTime)
					+ " for resource to import...");
			tasks.waitFor(sleepTime);
			return true;
		} else {
			log.fine("Resource \""
					+ resourceName
					+ "\" of agent \""
					+ agentName
					+ "\" not found in Autodiscovery queue, it might have been already inventorized");
			
		}
		return false;
	}
    /**
     * imports this resource from discovery queue. It is required that parent platform is already imported. This
     * function does not work for platforms.
     * This is done by following steps
     * <ul>
     * <ol>it is performed a check - if this resource is already in inventory nothing else happens</ol>
     * <ol>Manual Auto-discovery operation is performed on parent platform</ol>
     * <ol>Resource is imported</ol>
     * <ol>Wait 15 minutes for child subsystems to be imported</ol>
     * </ul>
     * @return true if resource was imported, false if it was not due to error or already existed in inventory
     */
	public boolean importFromDiscoQueue() {
		return importFromDiscoQueue(15 * Timing.TIME_1M);
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
