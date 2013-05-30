package com.redhat.qe.jon.sahi.base.editor;

import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class ConfigEditor extends Editor {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	public ConfigEditor(SahiTasks tasks) {
		super(tasks);
	}

	/**
     * creates new config entry, click the <b>+</b> buttton and returns helper object
     *
     * @param index of button on page
     * @return new config entry
     */
    public ConfigEntry newEntry(int index) {
        tasks.waitFor(Timing.TIME_1S);
        List<ElementStub> buttons = tasks.image("add.png").collectSimilar();
        log.fine("Found images " + buttons);
        int i = 0;
        for (ElementStub es : buttons) {
            ElementStub cell = es.parentNode().parentNode();
            if (cell.fetch("innerHTML").contains("class=\"buttonTitle") && cell.isVisible()) {
                log.fine(cell.fetch("innerHTML"));
                if (i == index) {
                    tasks.xy(cell, 3, 3).click();
                    tasks.waitFor(Timing.WAIT_TIME);
                    if (!tasks.cell("OK").isVisible()) {
                        log.fine("There isn't visible an OK cell => probably the config entry dialog wasn't open via clicking, trying sending a keypress");
                        tasks.execute("_sahi._keyPress(_sahi._image('add.png[" + index + "]'), 13);");
                        tasks.waitFor(Timing.WAIT_TIME);
                    }
                    return new ConfigEntry(tasks);
                } else {
                    i++;
                }
            }
        }
        throw new RuntimeException("Unable to click to new entry (add.png) button");
    }
}
