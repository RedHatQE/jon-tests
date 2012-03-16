package com.redhat.qe.jon.sahi.base.inventory;



import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class Inventory extends ResourceTab{

	public Inventory(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		selectTab("Inventory");
	}
	/**
	 * selects <b>Child Resources</b> subtab and returns helper object
	 * @return
	 */
	public ChildResources childResources() {
		selectTab("Inventory","Child Resources");
		return new ChildResources(tasks);
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
		selectTab("Inventory","Connection Settings");
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
			tasks.xy(tasks.cell("Finish"),3,3).click();
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
			tasks.xy(tasks.cell(name), 3, 3).click();
			tasks.cell("Uninventory").click();
			tasks.cell("Yes").click();
			
		}
		/**
		 * deletes child by given name from repository
		 * @param name
		 */
		public void deleteChild(String name) {
			tasks.xy(tasks.cell(name), 3, 3).click();
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
			for (ElementStub row : tasks.row("").in(tasks.table("listTable[2]")).collectSimilar()) {
				String child = tasks.cell(1).in(row).getText();
				children.add(child);
				log.fine("Found child ["+child+"]");
			}
			return children.toArray(new String[]{});
		}
	}

	
}
