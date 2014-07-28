package com.redhat.qe.jon.sahi.base.inventory;

import com.redhat.qe.jon.sahi.base.editor.*;
import com.redhat.qe.jon.sahi.tasks.*;

import net.sf.sahi.client.*;
import org.testng.*;

import java.util.logging.*;

public class Configuration extends ResourceTab {
    private final Logger log = Logger.getLogger(this.getClass().getName());
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
	 * @return current configuration subtab
	 */
	public CurrentConfig current() {
		navigateUnderResource("Configuration/Current");
		return new CurrentConfig(tasks);
	}

	/**
	 * navigates to <b>History</b> subtab and returns helper object
	 * 
	 * @return history subtab
	 */
	public ConfigHistory history() {
		navigateUnderResource("Configuration/History");
		return new ConfigHistory(tasks);
	}

	public static class CurrentConfig {
		private final SahiTasks tasks;
		private final ConfigEditor editor;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private CurrentConfig(SahiTasks tasks) {
			this.tasks = tasks;
			this.editor = new ConfigEditor(tasks);
		}
		public ConfigEditor getEditor() {
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
		 * @return new config entry
		 */
		public ConfigEntry newEntry(int index) {
			return getEditor().newEntry(index);
		}
		/**
		 * retrieves entry from table and returns helper object
		 * @param name of entry, must be present in entry row
		 * @return existing config entry
		 */
		public ConfigEntry getEntry(String name) {
			int entries = tasks.image("edit.png").in(tasks.cell(name).parentNode("tr")).countSimilar();
			log.info("Found entries: "+entries);
			tasks.xy(tasks.image("edit.png").in(tasks.cell(name).parentNode("tr")),3,3).click();
			//tasks.execute("_sahi._keyPress(_sahi._image('edit.png', _sahi._in(_sahi._parentRow(_sahi._cell('"+name+"')))), 32);");
			return new ConfigEntry(tasks);
		}
		/**
		 * removes entry by name from table (first match)
		 * use for tables, where remove button is part of a row
		 * @param name
		 */
		public void removeEntry(String name) {
			tasks.xy(tasks.image("remove.png").in(tasks.cell(name).parentNode("tr")),3,3).click();
            if (!tasks.cell("OK").exists()) {
                tasks.image("remove.png").in(tasks.cell(name)).click();
            }
			tasks.xy(tasks.cell("OK"), 3, 3).click();
		}
		/**
		 * removes entry from table, (first match)
		 * use for tables, where remove button is separate under a table
		 * @param select
		 * @param index - index of remove table/button on page
		 */
		public void removeSimpleProperty(int index, String select, String option) {
			tasks.select(select).choose(option);
			tasks.image("remove.png[" + index + "]").focus();
			tasks.execute("_sahi._keyPress(_sahi._image('remove.png[" + index
					+ "]'), 32);");
			tasks.xy(tasks.byXPath("//td[(@class='buttonTitleOver' or @class='buttonTitle' or @class='button' or @class='buttonOver') and .='Yes']"),3,3).click();
		}

        /**
         * saves configuration
         */
        public void save() {
            tasks.waitFor(Timing.WAIT_TIME);
            int buttons = tasks.cell("Save").countSimilar();
            log.info("Save buttons : " + buttons);
            ElementStub saveB = null;
            for (int i = 0; i < buttons; i++) {
                saveB = tasks.cell("Save[" + i + "]");
                if(saveB.isVisible()){
                    log.info("Clicking on Save button: " + saveB.toString());
                    tasks.xy(saveB, 3, 3).click();
                }
            }
            Assert.assertTrue(tasks.waitForAnyElementsToBecomeVisible(
                    tasks,
                    new ElementStub[] { tasks.cell("/Configuration updated*/"),
                            tasks.cell("/Updating configuration*/"),
                            tasks.div("/Updating configuration*/") },
                    "Successful update message", Timing.WAIT_TIME),
                    "Successful config update message expected!!");
        }
    }

	public static class ConfigEntry {
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private final SahiTasks tasks;
		private final Editor editor;
		public ConfigEntry(SahiTasks tasks) {
			this.tasks = tasks;
			this.editor = new Editor(tasks);
		}
		public Editor getEditor() {
			return editor;
		}

		/**
		 * sets text field value
		 * @param name of field
		 * @param value to be set
		 */
		public void setField(String name, String value) {
		    int count = tasks.textbox(name).countSimilar();
		    log.finer("Count of similar elements: " + count);
            String locator = name;
            // use specifier for the last only if there is more of them
            // if there is one, the name can already contain specifier which name to use
            if (count > 1) {
		        locator = name+"["+ (count-1) +"]";
            }
		    log.finer("Using following: " + locator);
		    tasks.textbox(locator).setValue(value);
		}

		/**
		 * clicks <b>OK</b> to confirm entry editing
		 */
		public void OK() {
			for (ElementStub ok : tasks.cell("OK").collectSimilar()) {
				if (ok.isVisible()) {
					try {
						tasks.xy(ok,3,3).click();
					}
					catch (Exception ex){
						log.warning("Failed to click on [OK] button");
					}
				}
			}
            tasks.waitFor(Timing.TIME_1S);
            if (tasks.cell("OK").isVisible()) {
                log.info("The [OK] button still exists => using keypress");
                tasks.cell("OK").focus();
                tasks.execute("_sahi._keyPress(_sahi._cell('OK'), 13);");
            }
		}
	}

	public static class ConfigHistory {
		private final SahiTasks tasks;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private ConfigHistory(SahiTasks tasks) {
			this.tasks = tasks;
		}

		/**
		 * 
		 * @return true if any of config change requests is pending
		 */
		public boolean isPending() {
		 return tasks.image("Configure_inprogress_16.png").isVisible();
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
				refresh();
				tasks.waitFor(Timing.TIME_10S);
			}
			return false;
		}

		public void failOnFailure() {
			if (!waitForPending()) {
				Assert.assertFalse(true,"Configuration change took too much time!");
			}
			String message = "Configuration change failed";
			boolean failure = hasFailure();
			if (failure) {
				tasks.xy(tasks.image("Configure_failed_16.png").in(getFirstRow()),3,3).click();
				if (tasks.preformatted("").isVisible()) {
	    			message += "\n ERROR message:\n"+tasks.preformatted("").getText();
	    		}
	    		tasks.waitFor(Timing.WAIT_TIME);
	    		int buttons = tasks.image("close.png").countSimilar();
	    		tasks.xy(tasks.image("close.png["+(buttons-1)+"]"),3,3).click();				
			}
			Assert.assertFalse(failure,message);
		}
		private ElementStub getFirstRow() {
			int tables = tasks.table("listTable").countSimilar();
			log.fine("Tables :"+tables);
			return tasks.row(0).in(tasks.table("listTable["+(tables-1)+"]"));
		}
		/**
		 * 
		 * @return true if any config change failed
		 */
		public boolean hasFailure() {
			return tasks.image("Configure_failed_16.png").in(getFirstRow()).isVisible();
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
