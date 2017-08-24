package com.redhat.qe.jon.sahi.base.inventory;


import com.redhat.qe.*;
import com.redhat.qe.jon.sahi.base.editor.*;
import com.redhat.qe.jon.sahi.tasks.*;
import net.sf.sahi.client.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.logging.*;

public class Inventory extends ResourceTab{

	public Inventory(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		navigateUnderResource("Inventory");
		raiseErrorIfCellIsNotVisible("Inventory");
	}
	/**
	 * selects <b>Child Resources</b> subtab and returns helper object
	 * @return child resources subtab
	 */
	public ChildResources childResources() {
		navigateUnderResource("Inventory/Children");
		return new ChildResources(tasks);
	}
	/**
	 * selects <b>Child History</b> subtab and returns helper object
	 * @return child history subtab
	 */
	public ChildHistory childHistory() {
		navigateUnderResource("Inventory/ChildHistory");
		return new ChildHistory(tasks);
	}
	/**
	 * returns true whether there can be some children resources
	 * @return true whether there are child resources
	 */
	public boolean hasChildren() {
		return tasks.cell("Child Resources").exists();
	}
	/**
	 * selects <b>Connection Settings</b> subtab and returns helper object
	 * @return connection settings subtab
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
		log.fine("Uninventoy child [" + childName + "]");
		childResources().uninventoryChild(childName);
		log.fine("Child resource ["+childName+"] uninventorized");
	}

	public static class ConnectionSettings {
		private final SahiTasks tasks;
		private final ConfigEditor editor;
		private ConnectionSettings(SahiTasks tasks) {
			this.tasks = tasks;
			this.editor = new ConfigEditor(tasks);
		}
		/**
		 * clicks <b>Save</b> button
		 */
		public void save() {
			tasks.cell("Save").click();
		}
		public ConfigEditor getEditor() {
		    return editor;
		}
	}

	public static class NewChildWizard {
		private final ConfigEditor editor;
		private final SahiTasks tasks;
		private final Logger log = Logger.getLogger(this.getClass().getName());
		private NewChildWizard(SahiTasks tasks) {
			this.tasks = tasks;
			this.editor = new ConfigEditor(tasks);
		}
		public ConfigEditor getEditor() {
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
            tasks.waitFor(Timing.TIME_5S);
            if (es.isVisible()) {
                log.info("The [Finish] button still visible => using keypress");
                es.focus();
                tasks.execute("_sahi._keyPress(_sahi._cell('Finish'), 13);"); //13 - Enter key
            }
            // if it's still visible fail the test to see what is wrong on the screenshot
            tasks.waitFor(Timing.TIME_5S);
            if (es.isVisible()) {
                log.severe("The [Finish] button still visible => failing");
                throw new RuntimeException("The [Finish] button is still visible");
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
		    ElementStub es = tasks.image("CreateChild_16.png");
		    ElementStub table = null;
		    if (es.exists() && es.isVisible()) {
		        table = es.parentNode("table");
		    }
		    else {
		        es = tasks.image("DeleteChild_16.png");
		        if (es.exists() && es.isVisible()) {
		            table = es.parentNode("table");
		        }
		    }
		    if (table == null) {
		        throw new RuntimeException("There is no record in resource child history table, is it?");
		    }
			return tasks.row(0).in(table);
		}
		/**
		 * gets status of last item in child history (first row in table)
		 * @return status string
		 */
		public String getLastResourceChangeStatus() {
			try {
			    ElementStub row = getFirstRow();
				return tasks.cell(4).in(row).getText();
			} catch (Exception ex) {
				log.fine("Unable to get last change status, returning [In Progress], exception : "+ex.getMessage());
				return "In Progress";
			}
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
                if (i == count-1 ) {
                    log.fine("This is last check, it is possible the refresh button doesn't work properly, lets try full page reload");
                    tasks.reloadPage();
                }
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
	    			log.fine("Operation in progress, waiting "+Timing.toString(waitTime)+", refreshing ..");
                    tasks.waitFor(waitTime);
	    			refresh();
		    		continue;
	    		} else {
	    			if (success) {
	    				// success wanted, let's find error message
	    				tasks.xy(getFirstRow(),3,3).doubleClick();
	    				if (tasks.textarea("errorMessage").isVisible()) {
	    					message +="\n ERROR Message:\n" + tasks.textarea("errorMessage").getText();
	    					String imgName = "close.png";
	    					int buttons = tasks.image(imgName).countSimilar();
	    					// patternFly change
	    					if(buttons == 0){
	    					    imgName = "close.gif";
	    					    buttons = tasks.image(imgName).countSimilar();
	    					}
	    					tasks.xy(tasks.image(imgName + "[" + (buttons - 1) + "]"), 3, 3).click();
	    				}
	    			}
	    			Assert.assertEquals(!success, success,message);
	    		}

	    	}
	    	log.info("Checking resource addition/removal timed out");
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
         * Its unstable method
         * Method which filters child resources based on the provided name using search box
         * @param name used for filtering child resources
         * 
         */
	      @Deprecated
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
                } 
                catch (AWTException ex) {
                  log.fine("filterChildResources(search): AWT Robot pressing enter thrown exception: " + ex.getMessage());
                }
                tasks.waitFor(Timing.TIME_5S*2);
            }
        }

        /**
         * @param name
         * @return true if child resource with given name exists
         */
        public boolean existsChild(String name) {
            if (tasks.cell("No items to show").isVisible()) {
                return false;
            }
            if (!tasks.cell(name).isVisible()) {
                tasks.sortChildResources();
            }
            if (!tasks.cell(name).isVisible() && 
                    (tasks.image("hscroll_start.png").isVisible()||
                            tasks.image("hscroll_Over_start.png").isVisible())) {
                log.fine("Trying to scroll..");
                for(int i=0;i<40;i++){
                    if(tasks.image("hscroll_start.png").isVisible()){
                        tasks.image("hscroll_start.png").click();
                    }else{
                        tasks.image("hscroll_Over_start.png").click();
                    }
                    if(tasks.cell(name).isVisible()){
                        return true;
                    }
                }
            }
            return tasks.link(name).isVisible();
        }

		public void refresh() {
			tasks.cell("Refresh").click();
		}
		/**
		 * creates new child resource of given type (ie. Deployment) and returns helper object
		 * @param type
		 * @return wizard
		 */
		public NewChildWizard newChild(String type) {
		    return selectMenu("Create Child", type);
		}
		private NewChildWizard selectMenu(String menu,String item) {
			tasks.xy(tasks.cell(menu),3,3).click();
			tasks.waitFor(Timing.WAIT_TIME);
			// we need to iterate over all menuTables and use the visible one
			// because smartGWT is so smart, that it leaves invisible menuTable within a DOM model
			for (ElementStub es : tasks.table("menuTable").collectSimilar()) {
			    if (es.isVisible()) {
				tasks.xy(tasks.cell(item).in(es),3,3).click();
				return new NewChildWizard(tasks);
			    }
			}
			throw new RuntimeException("Unable to select ["+item+"] from ["+menu+"] menu");
		}
		/**
		 * creates new child resource of given type (ie. Deployment) and returns helper object
		 * @param type
		 * @return wizard
		 */
		public NewChildWizard importResource(String type) {
			return selectMenu("Import", type);
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
            if (!tasks.cell(name).isVisible()) {
                tasks.sortChildResources();
            }
            int children = tasks.cell(name).countSimilar();
            log.fine("Matched cells " + children);
            if (children == 0) {
                throw new RuntimeException("Unable to select resource [" + name + "], NOT FOUND!");
            }
            tasks.xy(tasks.cell(name + "[" + (children - 1) + "]"), 3, 3).click();
        }

        /**
		 * deletes child by given name from repository
		 * @param name
		 */
		public void deleteChild(String name) {
			selectChild(name);
			List<ElementStub> buttons = tasks.byXPath("//td[@class='buttonTitle' and .='Delete']").collectSimilar();
			// class was renamed in jon3.3
			if(buttons.size() == 0){
			    buttons = tasks.byXPath("//td[@class='button' and .='Delete']").collectSimilar();    
			}
			log.fine("Found Delete buttons :"+buttons);
			if(buttons.size() == 0){
			    throw new RuntimeException("Delete button was not found!");
			}
			buttons.get(buttons.size() - 1).click();
			//tasks.cell("Delete").near(tasks.cell("Uninventory")).click();
            if (tasks.waitForElementVisible(tasks, tasks.cell("Yes"),
                    "Yes button", Timing.WAIT_TIME*2)) {
                for (ElementStub es : tasks.cell("Yes").collectSimilar()) {
                    if (es.isVisible()) {
                        log.info("Yes button (" + es.toString()
                                + ") is visible, clicking");
                        tasks.xy(es, 3, 3).click();
                    }
                }
            } else if (tasks.waitForElementExists(tasks, tasks.cell("Yes"),
                    "Yes button", Timing.WAIT_TIME*2)) {
                // when removing connector from EAP 6 it often happen that the Yes button doesn't become visible
                for (ElementStub es : tasks.cell("Yes").collectSimilar()) {
                    if (es.exists()) {
                        log.info("Yes button (" + es.toString()
                                + ") exists, clicking");
                        tasks.xy(es, 3, 3).click();
                    }
                }
            } else {
                throw new RuntimeException("Confirmation dialog didn't pop up");
            }
        }
		/**
		 * lists children resrource names
		 * @return list of children
		 */
		public String[] listChildren() {
            tasks.reloadPage();
            List<String> children = new ArrayList<String>();
            try {
                children = listChildren(1);
            } catch (Exception ex) {
                log.fine("Known issue caused probably by accessing wrong table: " + ex.toString());
            }
            if (children.isEmpty()) {
                children = listChildren(2);
            }
            return children.toArray(new String[]{});
		}

        /**
         * lists children resrource names
         * @return list of children
         */
        public List<String> listChildren(int tableOffsetFromTheEnd) {
            List<String> children = new ArrayList<String>();
            if (tasks.cell("No items to show.").exists()) {
                return children;
            }
            int count = tasks.table("listTable").countSimilar();
            if (count>tableOffsetFromTheEnd) {
                for (ElementStub row : tasks.row("").in(tasks.table("listTable["+(count-tableOffsetFromTheEnd)+"]")).collectSimilar()) {
                    String child = tasks.cell(1).in(row).getText();
                    if (child.trim().length()> 0) {
                        children.add(child);
                        log.fine("Found child ["+child+"]");
                    }
                }
            }
            return children;
        }
	}


}
