package com.redhat.qe.jon.sahi.base.inventory;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class Configuration extends ResourceTab {

	public Configuration(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		navigateUnderResource("Configuration");
		// we need to assert it, because direct navigation could redirect us somewhere else, 
		// when Configuration tab does not exist
		raiseErrorIfCellDoesNotExist("Configuration");
	}

	/**
	 * navigates to <b>Current</b> subtab and returns helper object
	 * 
	 * @return
	 */
	public CurrentConfig current() {
		navigateUnderResource("Configuration/Current");
		return new CurrentConfig(tasks);
	}

	/**
	 * navigates to <b>History</b> subtab and returns helper object
	 * 
	 * @return
	 */
	public ConfigHistory history() {
		navigateUnderResource("Configuration/History");
		return new ConfigHistory(tasks);
	}

	public static class CurrentConfig {
		private final SahiTasks tasks;
		private final Editor editor;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private CurrentConfig(SahiTasks tasks) {
			this.tasks = tasks;
			this.editor = new Editor(tasks);
		}
		public Editor getEditor() {
			return editor;
		}
		/**
		 * gathers error text (if any) from error notification field
		 * @return null if no error is on Config page
		 */
		public String getErrorText() {
			StringBuilder sb = new StringBuilder();
			ElementStub es =  tasks.byXPath("//td[@class='ErrorBlock'][1]");
			if (!es.exists()) {
				return null;
			}
			sb.append(es.getText());			
			return sb.toString();
			
		}

		/**
		 * creates new config entry, click the <b>+</b> buttton and returns helper object
		 * @param index of button on page
		 * @return
		 */
		public ConfigEntry newEntry(int index) {
			int imgindex = index = 1;
			tasks.image("add.png[" + imgindex + "]").focus();
			tasks.execute("_sahi._keyPress(_sahi._image('add.png[" + imgindex
					+ "]'), 32);");
			return new ConfigEntry(tasks);
		}
		/**
		 * retrieves entry from table and returns helper object
		 * @param name of entry, must be present in entry row
		 * @return
		 */
		public ConfigEntry getEntry(String name) {
			int entries = tasks.image("edit.png").in(tasks.cell(name).parentNode("tr")).countSimilar();
			log.info("Found entries :"+entries);
			tasks.xy(tasks.image("edit.png").in(tasks.cell(name).parentNode("tr")),3,3).click();
			return new ConfigEntry(tasks);
		}
		/**
		 * removes entry by name from table (first match)
		 * @param name
		 */
		public void removeEntry(String name) {
			tasks.xy(tasks.image("remove.png").in(tasks.cell(name).parentNode("tr")),3,3).click();
			tasks.xy(tasks.cell("OK"), 3, 3).click();
		}

		/**
		 * saves configuration
		 */
		public void save() {
			int buttons = tasks.cell("Save").countSimilar();
			log.info("Save buttons : "+buttons);
			tasks.xy(tasks.cell("Save["+(buttons-1)+"]"), 3, 3).click();
		}
	}

	public static class ConfigEntry {
		private final SahiTasks tasks;

		ConfigEntry(SahiTasks tasks) {
			this.tasks = tasks;
		}

		/**
		 * sets text field value
		 * @param name of field
		 * @param value to be set
		 */
		public void setField(String name, String value) {
			tasks.textbox(name).setValue(value);
		}

		/**
		 * clicks <b>OK</b> to confirm entry editing
		 */
		public void OK() {
			tasks.xy(tasks.cell("OK"), 3, 3).click();
		}
	}

	public static class ConfigHistory {
		private final SahiTasks tasks;

		private ConfigHistory(SahiTasks tasks) {
			this.tasks = tasks;
		}

		/**
		 * 
		 * @return true if any of config change requests is pending
		 */
		public boolean isPending() {
		 return tasks.image("Configure_inprogress_16.png").exists();
		}
		/**
		 * checks whether there is a pending config change request, tries to wait, until no pending request exists
		 * @return true if there is no pending config change, false is there still exists some
		 */
		public boolean waitForPending() {
			for (int i = 0; i < Timing.REPEAT;i++) {
				if (!isPending()) {
					return true;
				}
				tasks.waitFor(Timing.TIME_10S);
				refresh();
			}
			return false;
		}
		/*
		 * waits reasonable amount of time whether any config change request is pending
		 * if this time expires and there is still pending config change, Assert.fail is called
		 */
		public void failOnPending() {
			if (!waitForPending()) {
				Assert.fail("Configuraton change took too much time to process");
			}			
		}
		public void failOnFailure() {
			waitForPending();
			Assert.assertFalse(hasFailure(),"Configuration change failed");
		}
		/**
		 * 
		 * @return true if any config change failed
		 */
		public boolean hasFailure() {
			return tasks.image("Configure_failed_16.png").exists();
		}
		// public boolean isSuccessLastChange() {
		// return
		// tasks.image("Configure_ok_16.png").in(tasks.div("Individual[0]").parentNode("tr")).exists();
		// }
		// public boolean isFailureLastChange() {
		// return
		// tasks.image("Configure_failed_16.png").in(tasks.div("Individual[0]").parentNode("tr")).exists();
		// }

		/**
		 * refreshes history view
		 */
		public void refresh() {
			for (ElementStub refresh : tasks.cell("Refresh").collectSimilar()) {
				tasks.xy(refresh, 3, 3).click();
			}
		}
	}
}
