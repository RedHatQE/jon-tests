package com.redhat.qe.jon.sahi.base.inventory;

import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class Editor {

	private final SahiTasks tasks;
	private final Logger log = Logger.getLogger(this.getClass().getName());
	public Editor(SahiTasks tasks) {
		this.tasks = tasks;
	}
	/**
	 * asserts all required input fields have been filled
	 */
	public void assertRequiredInputs() {
	    	tasks.waitFor(Timing.WAIT_TIME);
		Assert.assertTrue(!tasks.image("exclamation.png").exists(), "All required inputs were provided");
	}
	/**
	 * sets text field value
	 * @param name of field
	 * @param value to be set
	 */
	public void setText(String name, String value) {
		tasks.waitFor(Timing.WAIT_TIME);
		tasks.textbox(name).setValue(value);
	}
	public void setPassword(String name, String value) {
		tasks.waitFor(Timing.WAIT_TIME);
		tasks.password(name).setValue(value);
	}
	public void checkRadio(String selection) {
		tasks.waitFor(Timing.WAIT_TIME);
		tasks.radio(selection).check();
	}
	/**
	 * jumps to section (or category)
	 * this clicks 'Jump to Section' button, and tries to click on  menu item that just <b>contains</b> text given by 'name' parameter,
	 * first match is used
	 * @param name of section - substring defining a section menuItem text
	 */
	public void jumpToSection(String name) {
		tasks.xy(tasks.cell("Jump to Section"),3,3).click();
		tasks.waitFor(Timing.WAIT_TIME);
		// we need to iterate over all menuTables and use the visible one
		// because smartGWT is so smart, that it leaves invisible menuTable within a DOM model
		for (ElementStub es : tasks.table("menuTable").collectSimilar()) {
		    if (es.isVisible()) {
			for (ElementStub cell : tasks.cell("menuTitleField").in(es).collectSimilar()) {
			    if (cell.fetch("innerHTML").contains(name)) {
				tasks.xy(cell,3,3).click();
				return;
			    }
			}
			
			
		    }
		}
		throw new RuntimeException("Unable to jump to section ["+name+"]");
	}
	/**
	 * clicks on upper scroll arrow
	 * @param clicks - how many times to click
	 */
        public void scrollUp(int clicks) {
        	ElementStub scroll = getScrollButton("start");
        	for (int i = 0; i < clicks; i++) {
        	    if (scroll!=null && scroll.exists() && scroll.isVisible()) {
        		tasks.xy(scroll, 3, 3).click();
        		log.fine("Clicked scroll arrow");
        		tasks.waitFor(Timing.TIME_5S / 5);
        		scroll = getScrollButton("Over_start");
        	    }
        	    else {
        		log.warning("Scroll arrow not found!");
        	    }
        	}
        }
	/**
	 * clicks once on upper scroll arrow
	 */
	public void scrollUp() {
	    scrollUp(1);
	}
	private ElementStub getScrollButton(String type) {
	    List<ElementStub> scrolls = tasks.image("vscroll_"+type+".png").collectSimilar();
	    if (scrolls.size()>0) {
		return scrolls.get(scrolls.size()-1);
	    }
	    return null;
	}
	/**
	 * clicks on bottom scroll arrow
	 * @param clicks - how many times to click
	 */
    public void scrollDown(int clicks) {
	ElementStub scroll = getScrollButton("end");
	for (int i = 0; i < clicks; i++) {
	    if (scroll!=null && scroll.exists() && scroll.isVisible()) {
		tasks.xy(scroll.parentNode(), 3, 3).click();
		
		log.fine("Clicked scroll arrow");
		scroll = getScrollButton("Over_end");
	    }
	    else {
		log.warning("Scroll arrow not found!");
	    }
	}
	
    }
	/**
	 * clicks once on bottom scroll arrow
	 */
	public void scrollDown() {
	    scrollDown(1);
	}
	public void selectCombo(int index, String selection) {
		 int pickers = tasks.image("comboBoxPicker.png").countSimilar();
		 log.fine("Found "+pickers+" comboboxes, required index="+index);			 
		 ElementStub picker = tasks.image("comboBoxPicker.png["+index+"]");
		 tasks.xy(picker.parentNode(),3,3).click();
		 int rows = tasks.row(selection).countSimilar();
		 if (rows == 0 && tasks.image("comboBoxPicker_Over.png").exists()) {
			 log.fine("Combo did not pop up? Trying one more click...");
			 // when combo is focused single click does NOT work - wtf!
			 tasks.xy(tasks.image("comboBoxPicker_Over.png"),3,3).click();
			 rows = tasks.row(selection).countSimilar();
		 }
		 if (rows==1) {
			 index = 0;
		 }
		 log.fine("Found rows matching ["+selection+"] : "+rows+" clicking on index="+index);
		 ElementStub es = tasks.cell(selection+"["+index+"]");
		 if (es.isVisible()) {
			 tasks.xy(es, 3, 3).click();
			 log.fine("Selected  ["+selection+"].");				
			 return;
		 }		 
		 throw new RuntimeException("Unable to select ["+selection+"] comboBox did NOT pop up");
	}
	/**
	 * check checkbox
	 * @param index of checkbox
	 * @param check true to check, false to uncheck
	 */
	public void checkBox(int index, boolean check) {
	    String checkBox =null;
		if (check) {
			checkBox = "unchecked.png["+index+"]";
		}
		else {
			checkBox = "checked.png["+index+"]";
		}
		tasks.image(checkBox).parentNode().focus();
		log.fine("Sending keypress to "+checkBox);
		tasks.execute("_sahi._keyPress(_sahi._image('" +checkBox+"'), 32);");		
	}
	/**
	 * creates new config entry, click the <b>+</b> buttton and returns helper object
	 * @param index of button on page
	 * @return new config entry
	 */
	public ConfigEntry newEntry(int index) {
		List<ElementStub> buttons = tasks.image("add.png").collectSimilar();
		log.fine("Found images "+buttons);
		int i = 0;
		for (ElementStub es : buttons) {
			ElementStub cell = es.parentNode().parentNode();
			if (cell.fetch("innerHTML").contains("class=\"buttonTitle") && cell.isVisible()) {
				log.fine(cell.fetch("innerHTML"));
				if (i == index) {
					tasks.xy(cell,3,3).click();
					tasks.waitFor(Timing.WAIT_TIME);
					return new ConfigEntry(tasks);
				}
				else {
					i++;
				}
			}
		}
		throw new RuntimeException("Unable to click to new entry (add.png) button");
	}
}
