package com.redhat.qe.jon.sahi.base.inventory;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
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
	public void checkRadio(String selection) {
		tasks.waitFor(Timing.WAIT_TIME);
		tasks.radio(selection).check();
	}
	/**
	 * check checkbox
	 * @param index of checkbox
	 * @param check true to check, false to uncheck
	 */
	public void checkBox(int index, boolean check) {
		if (check) {
			tasks.xy(tasks.image("unchecked.png["+index+"]").parentNode(),3,3).click();
		}
		else {
			tasks.xy(tasks.image("checked.png["+index+"]").parentNode(),3,3).click();
		}
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
		tasks.waitFor(Timing.WAIT_TIME);
		return new ConfigEntry(tasks);
	}
	
}
