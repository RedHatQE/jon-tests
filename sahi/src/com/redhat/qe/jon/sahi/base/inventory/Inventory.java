package com.redhat.qe.jon.sahi.base.inventory;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class Inventory extends ResourceTab{

	public Inventory(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		navigateUnderResource("Inventory");
		raiseErrorIfCellDoesNotExist("Inventory");
	}
	/**
	 * selects <b>Child Resources</b> subtab and returns helper object
	 * @return
	 */
	public ChildResources childResources() {
		navigateUnderResource("Inventory/Children");
		return new ChildResources(tasks);
	}
	/**
	 * selects <b>Child History</b> subtab and returns helper object
	 * @return
	 */
	public ChildHistory childHistory() {
		navigateUnderResource("Inventory/ChildHistory");
		return new ChildHistory(tasks);
	}
	/**
	 * returns true whether there can be some children resources
	 * @return
	 */
	public boolean hasChildren() {
		return tasks.cell("Child Resources").exists();
	}
	/**
	 * selects <b>Connection Settings</b> subtab and returns helper object
	 * @return
	 */
	public ConnectionSettings connectionSettings() {
		navigateUnderResource("Inventory/ConnectionSettings");
		return new ConnectionSettings(tasks);
	}
	/**
	 * removes resource defined by childName from inventory
	 * @param childName
	 */
	public void uninventory(String childName) {
		log.fine("Uninventoy child ["+childName+"]");
		childResources().uninventoryChild(childName);
		log.fine("Child resource ["+childName+"] uninventorized");
	}
	
	public static class ConnectionSettings {
		private final SahiTasks tasks;
		private ConnectionSettings(SahiTasks tasks) {
			this.tasks = tasks;
		}
		/**
		 * clicks <b>Save</b> button
		 */
		public void save() {
			tasks.cell("Save").click();
		}
	}
	
	public static class NewChildWizard {
		private final Editor editor;
		private final SahiTasks tasks;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private NewChildWizard(SahiTasks tasks) {
			this.tasks = tasks;
			this.editor = new Editor(tasks);
		}
		public Editor getEditor() {
			return editor;
		}
		/**
		 * clicks <b>Next</b> button
		 */
		public void next() {
			tasks.waitFor(Timing.WAIT_TIME);
			tasks.xy(tasks.cell("Next"),3,3).click();			
		}
		/**
		 * clicks <b>Finish</b> button
		 */
		public void finish() {
			tasks.waitFor(Timing.WAIT_TIME);
			log.fine("Finish buttons: "+tasks.cell("Finish").countSimilar());
			ElementStub es = tasks.cell("Finish");
			tasks.xy(es,3,3).click();
			// if click does not work we send enter key and pray
			if (es.isVisible()) {
				tasks.execute("_sahi._keyPress(_sahi._cell('Finish'), 13);"); //13 - Enter key
			}
		}
		/**
		 * sets file to upload and starts uploading it
		 * @param path to file to be uploaded - relative to /automatjon/jon/sahi/resources/
		 */
		public void upload(String path) {
			tasks.waitFor(Timing.WAIT_TIME);
			tasks.setFileToUpload("fileUploadItem",path);
			tasks.xy(tasks.cell("Upload"),3,3).click();
		}
		/**
		 * clicks <b>Cancel</b> button
		 */
		public void cancel() {
			tasks.waitFor(Timing.WAIT_TIME);
			tasks.xy(tasks.cell("Cancel"),3,3).click();
		}
	}
	
	public static class ChildHistory {
		private final SahiTasks tasks;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		
		private ChildHistory(SahiTasks tasks) {
			this.tasks = tasks;
		}
		private ElementStub getFirstRow() {
			List<ElementStub> tables = tasks.table("listTable").collectSimilar();
			log.fine("Tables :"+tables.size());
			for (int i=tables.size()-1;i>=0;i--) {
				if (tables.get(i).isVisible()) {
					return tasks.row(0).in(tables.get(i));
				}
			}
			return tasks.row(0).in(tasks.table("listTable["+(tables.size()-1)+"]"));
		}
		/**
		 * gets status of last item in child history (first row in table) 
		 * @return
		 */
		public String getLastResourceChangeStatus() {
			ElementStub row = getFirstRow();
			log.fine("Fist row:"+row.fetch("innerHTML"));
			return tasks.cell(4).in(row).getText();
		}
		/**
		 * asserts whether resource addition/removal was successfull or not (success param)
		 * also waits 'till operation is no longer in <b>In Progress</b>
		 * @param success
		 */
		public void assertLastResourceChange(boolean success) {
			
			String status_success="Success";
			String status_failed="Failed";
			String status_progress="In Progress";
	    	String desired_status=status_success;
	    	if (!success) {
	    		desired_status=status_failed;
	    	}
	    	log.info("Asserting last child addition/removal - expected: "+desired_status);
			int waitTime=Timing.TIME_30S;
	    	int count=Timing.REPEAT;
	    	String message ="Last resource removal/addition was successfull";

	    	for (int i = 0;i<count;i++) {
	    		log.fine("Checking resource removal/addition status="+desired_status+": try #" + Integer.toString(i + 1) + " of "+count);
	    		String status = getLastResourceChangeStatus();
	    		if (success && status_success.equals(status)) {
	    			Assert.assertTrue(true, message);
	    			return;
	    		}
	    		if (!success && status_failed.equals(status)) {
	    			Assert.assertFalse(false, message);
	    			return;
	    		}
	    		if (status_progress.equals(status)) {
	    			log.fine("Operation in progess, waiting "+Timing.toString(waitTime)+", refreshing ..");    		
	    			refresh();
	    			tasks.waitFor(waitTime);
		    		continue;
	    		} else {
	    			if (success) {
	    				// success wanted, let's find error message
	    				tasks.xy(getFirstRow(),3,3).doubleClick();
	    				if (tasks.textarea("errorMessage").isVisible()) {
	    					message +="\n ERROR Message:\n" + tasks.textarea("errorMessage").getText();
	    					int buttons = tasks.image("close.png").countSimilar();
	    		    		tasks.xy(tasks.image("close.png["+(buttons-1)+"]"),3,3).click();
	    				}
	    			}
	    			Assert.assertEquals(!success, success,message);
	    		}
	    		
	    	}
	    	log.info("Checking resurce addition/removal timed out");
	    	Assert.assertTrue(false, message);
		}
		
		/**
		 * refreshes history view
		 */
		public void refresh() {
			for (ElementStub refresh : tasks.cell("Refresh").collectSimilar()) {
				tasks.xy(refresh, 3, 3).click();
			}
		}
		
	}
	
	public static class ChildResources {
		private final SahiTasks tasks;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private ChildResources(SahiTasks tasks) {
			this.tasks = tasks;
		}
		/**
		 * 
		 * @param name
		 * @return true if child resource with given name exists
		 */
		public boolean existsChild(String name) {
			return tasks.cell(name).exists();
		}
		public void refresh() {
			tasks.cell("Refresh").click();
		}
		/**
		 * creates new child resource of given type (ie. Deployment) and returns helper object
		 * @param type
		 * @return
		 */
		public NewChildWizard newChild(String type) {
			tasks.xy(tasks.cell("Create Child"),3,3).click();
			tasks.waitFor(Timing.WAIT_TIME);
			tasks.xy(tasks.cell(type).in(tasks.table("menuTable")),3,3).click();
			return new NewChildWizard(tasks);
		}
		/**
		 * removes child by given name from repository
		 * @param name
		 */
		public void uninventoryChild(String name) {
			selectChild(name);
			tasks.cell("Uninventory").click();
			tasks.cell("Yes").click();
			
		}
		private void selectChild(String name) {
			int children = tasks.cell(name).countSimilar();
			log.fine("Matched cells "+children);
			tasks.xy(tasks.cell(name+"["+(children-1)+"]"), 3, 3).click();
		}
		/**
		 * deletes child by given name from repository
		 * @param name
		 */
		public void deleteChild(String name) {
			selectChild(name);
			int buttons = tasks.byXPath("//td[@class='buttonTitle' and .='Delete']").countSimilar();
			log.fine("Found Delete buttons :"+buttons);
			tasks.byXPath("//td[@class='buttonTitle' and .='Delete']").click();
			//tasks.cell("Delete").near(tasks.cell("Uninventory")).click();
			tasks.cell("Yes").click();
		}
		/**
		 * lists children resrource names
		 * @return
		 */
		public String[] listChildren() {
			List<String> children = new ArrayList<String>();
			if (tasks.cell("No items to show.").exists()) {
				return new String[]{};
			}
			int count = tasks.table("listTable").countSimilar();
			if (count>1) {
				for (ElementStub row : tasks.row("").in(tasks.table("listTable["+(count-1)+"]")).collectSimilar()) {
					String child = tasks.cell(1).in(row).getText();
					if (child.trim().length()> 0) {
						children.add(child);
						log.fine("Found child ["+child+"]");
					}
				}
			}
			return children.toArray(new String[]{});
		}
	}

	
}
