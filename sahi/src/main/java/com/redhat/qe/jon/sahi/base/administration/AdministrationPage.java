package com.redhat.qe.jon.sahi.base.administration;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.tasks.Navigator;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * Class which represents the main Administration page
 * @author fbrychta
 *
 */
public class AdministrationPage {
	private static Logger log = Logger.getLogger(AdministrationPage.class.getName());
	protected SahiTasks tasks = null;
	protected Editor editor = null;
	
	public AdministrationPage(SahiTasks tasks){
		this.tasks = tasks;
		this.editor = new Editor(tasks);
	}
	
	/**
	 * Navigates to the page
	 */
	public AdministrationPage navigate(){
		String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
		String url = serverBaseUrl+"/#Administration";
		log.fine("Navigating to ["+url+"]");
		tasks.navigateTo(url,false);
		
		return this;
	}
	
	/**
	 * This class represents Repositories page
	 */
	public static class Repositories extends AdministrationPage{
		private static Logger log = Logger.getLogger(Navigator.class.getName());
		
		public Repositories(SahiTasks tasks) {
			super(tasks);
		}
		
		/**
		 * Navigates to the page
		 */
		public Repositories navigate(){
			String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
			String url = serverBaseUrl+"/#Administration/Content/Repositories";
			log.fine("Navigating to ["+url+"]");
			tasks.navigateTo(url,false);
			tasks.waitForElementVisible(tasks, tasks.submit("CREATE NEW"), "CREATE NEW submit", Timing.WAIT_TIME);
			
			return this;
		}
		
		/**
		 * Creates a new repository with given name.
		 * @param name
		 * @return this
		 */
		public Repositories createNewRepository(String name){
			tasks.submit("CREATE NEW").click();
			tasks.waitForElementVisible(tasks, tasks.textbox("createRepoDetailsForm:name"),
					"createRepoDetailsForm:name", Timing.WAIT_TIME);
			editor.setText("createRepoDetailsForm:name", name);
			//TODO set other fields - this part of GUI is still old - postponing
			
			tasks.submit("createRepoDetailsForm:saveButton").click();
			tasks.waitFor(Timing.WAIT_TIME);
			
			return this;
		}
		
		/**
		 * Deletes a repository with given name.
		 * @param name
		 * @return this
		 */
		public Repositories deleteRepository(String name){
			log.fine("Deleting a repository with name: " + name);
			int rows = tasks.link(name).countSimilar();
			if(rows != 0){
				ElementStub row = tasks.link(name).parentNode("tr");
				tasks.checkbox("selectedRepos").in(row).check();
				
				tasks.submit("DELETE SELECTED").click();
				tasks.waitFor(Timing.WAIT_TIME);
			
			}else{
				log.finer("No repository with name "+name+" found. Skipping.");
			}
			return this;
		}
		
	}
}
