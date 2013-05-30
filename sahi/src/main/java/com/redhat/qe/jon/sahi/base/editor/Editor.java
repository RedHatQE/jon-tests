package com.redhat.qe.jon.sahi.base.editor;

import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class Editor {

    protected final SahiTasks tasks;
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
     *
     * @param name  of field
     * @param value to be set
     */
    public void setText(String name, String value) {
        tasks.waitFor(Timing.WAIT_TIME);
        tasks.textbox(name).setValue(value);
    }
    
    /**
     * Fills given value into text field with given locator near cell with given locator.
     * @param fieldSelection
     * @param value
     * @param cellSelection
     */
    public void setTextNearCell(String fieldSelection, String value,String cellSelection) {
    	ElementStub elem =tasks.textbox(fieldSelection).near(tasks.cell(cellSelection));
    	tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
    	elem.setValue(value);
    }

    /**
     * Returns value in specified textbox field
     *
     * @param name of the textbox field
     * @return value filled in the specified textbox field
     */
    public String getText(String name) {
        tasks.waitFor(Timing.WAIT_TIME);
        return tasks.textbox(name).getValue();
    }

    /**
     * Fills text in textarea specified by name
     *
     * @param name  represents name of textarea element to fill
     * @param value the text to fill the specified textarea
     */
    public void setTextInTextArea(String name, String value) {
        tasks.waitFor(Timing.WAIT_TIME);
        tasks.textarea(name).setValue(value);
    }
    
    /**
     * Fills given value into text area with given locator near cell with given locator. 
     * @param selection
     * @param value
     * @param cellSelection
     */
    public void setTextInTextAreaNearCell(String selection, String value, String cellSelection) {
    	ElementStub elem = tasks.textarea(selection).near(tasks.cell(cellSelection));
    	tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
        elem.setValue(value);
    }

    /**
     * Returns text in the specified textarea element
     *
     * @param name textarea element name
     * @return text filled in the specified textarea element
     */
    public String getTextInTextArea(String name) {
        tasks.waitFor(Timing.WAIT_TIME);
        return tasks.textarea(name).getValue();
    }

    public void setPassword(String name, String value) {
        tasks.waitFor(Timing.WAIT_TIME);
        tasks.password(name).setValue(value);
    }
    
    /**
     * Fills given value into password field with given locator near a cell with given locator.
     * @param selection
     * @param value
     * @param cellSelection
     */
    public void setPasswordNearCell(String selection, String value,String cellSelection) {
        ElementStub elem = tasks.password(selection).near(tasks.cell(cellSelection));
        tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
        elem.setValue(value);
    }

    public void checkRadio(String selection) {
        tasks.waitFor(Timing.WAIT_TIME);
        tasks.radio(selection).check();
    }
    
    /**
     * Checks a radio with given locator near a cell with given locator.
     * @param radioSelection
     * @param cellSelection
     */
    public void checkRadioNearCell(String radioSelection,String cellSelection) {
    	ElementStub elem = tasks.radio(radioSelection).near(tasks.cell(cellSelection));
    	tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
    	elem.check();
    }

    /**
     * jumps to section (or category)
     * this clicks 'Jump to Section' button, and tries to click on  menu item that just <b>contains</b> text given by 'name' parameter,
     * first match is used
     *
     * @param name of section - substring defining a section menuItem text
     */
    public void jumpToSection(String name) {
        tasks.xy(tasks.cell("Jump to Section"), 3, 3).click();
        tasks.waitFor(Timing.WAIT_TIME);
        // we need to iterate over all menuTables and use the visible one
        // because smartGWT is so smart, that it leaves invisible menuTable within a DOM model
        for (ElementStub es : tasks.table("menuTable").collectSimilar()) {
            if (es.isVisible()) {
                for (ElementStub cell : tasks.cell("menuTitleField").in(es).collectSimilar()) {
                    if (cell.fetch("innerHTML").contains(name)) {
                        tasks.xy(cell, 3, 3).click();
                        return;
                    }
                }


            }
        }
        throw new RuntimeException("Unable to jump to section [" + name + "]");
    }

    /**
     * clicks on upper scroll arrow
     *
     * @param clicks - how many times to click
     */
    public void scrollUp(int clicks) {
        ElementStub scroll = getScrollButton("start");
        for (int i = 0; i < clicks; i++) {
            if (scroll != null && scroll.exists() && scroll.isVisible()) {
                tasks.xy(scroll, 3, 3).click();
                log.fine("Clicked scroll arrow");
                tasks.waitFor(Timing.TIME_5S / 5);
                scroll = getScrollButton("Over_start");
            } else {
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
        List<ElementStub> scrolls = tasks.image("vscroll_" + type + ".png").collectSimilar();
        if (scrolls.size() > 0) {
            return scrolls.get(scrolls.size() - 1);
        }
        return null;
    }

    /**
     * clicks on bottom scroll arrow
     *
     * @param clicks - how many times to click
     */
    public void scrollDown(int clicks) {
        ElementStub scroll = getScrollButton("end");
        for (int i = 0; i < clicks; i++) {
            if (scroll != null && scroll.exists() && scroll.isVisible()) {
                tasks.xy(scroll.parentNode(), 3, 3).click();

                log.fine("Clicked scroll arrow");
                scroll = getScrollButton("Over_end");
            } else {
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
        log.fine("Found " + pickers + " comboboxes, required index=" + index);
        ElementStub picker = tasks.image("comboBoxPicker.png[" + index + "]");
        tasks.xy(picker.parentNode(), 3, 3).click();
        int rows = tasks.row(selection).countSimilar();
        if (rows == 0 && tasks.image("comboBoxPicker_Over.png").exists()) {
            log.fine("Combo did not pop up? Trying one more click...");
            // when combo is focused single click does NOT work - wtf!
            tasks.xy(tasks.image("comboBoxPicker_Over.png"), 3, 3).click();
            rows = tasks.row(selection).countSimilar();
        }
        if (rows == 1) {
            index = 0;
        }
        log.fine("Found rows matching [" + selection + "] : " + rows + " clicking on index=" + index);
        ElementStub es = tasks.cell(selection + "[" + index + "]");
        if (es.isVisible()) {
            tasks.xy(es, 3, 3).click();
            log.fine("Selected  [" + selection + "].");
            return;
        }
        throw new RuntimeException("Unable to select [" + selection + "] comboBox did NOT pop up");
    }

    /**
     * check checkbox
     *
     * @param index of checkbox
     * @param check true to check, false to uncheck
     */
    public void checkBox(int index, boolean check) {
        tasks.waitFor(Timing.TIME_1S);
        String checkBox = null;
        if (check) {
            checkBox = "unchecked.png[" + index + "]";
        } else {
            checkBox = "checked.png[" + index + "]";
        }
        tasks.image(checkBox).parentNode().focus();
        log.fine("Sending keypress to " + checkBox);
        tasks.execute("_sahi._keyPress(_sahi._image('" + checkBox + "'), 32);");
    }

}
