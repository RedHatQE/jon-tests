package com.redhat.qe.jon.sahi.base.editor;

import com.redhat.qe.jon.sahi.tasks.*;
import net.sf.sahi.client.*;
import org.testng.*;

import java.util.*;
import java.util.logging.*;

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
        ElementStub exclamationMark = tasks.image("/exclamation.*/");
        Assert.assertFalse(tasks.waitForElementExists(tasks, exclamationMark, exclamationMark.toString(), Timing.WAIT_TIME),
                "All required inputs were provided");
    }

    /**
     * sets text field value
     *
     * @param name  of field
     * @param value to be set
     */
    public void setText(String name, String value) {
        ElementStub textbox = tasks.textbox(name);
        tasks.waitForElementExists(tasks, textbox, textbox.toString(), Timing.WAIT_TIME);
        log.info("class: "+ textbox.getAttribute("class"));
        textbox.setValue(value);
        tasks.waitForElementToContainValue(tasks, textbox, value, Timing.WAIT_TIME);
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
     * Returns value from text field with given locator near cell with given locator.
     * @param fieldSelection
     * @param cellSelection
     * @return value from text field
     */
    public String getTextNearCell(String fieldSelection,String cellSelection) {
    	ElementStub elem =tasks.textbox(fieldSelection).near(tasks.cell(cellSelection));
    	log.finer("Count of similar elements: " + elem.countSimilar());
    	tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
    	return elem.getValue();
    }

    /**
     * Returns value in specified textbox field
     *
     * @param name of the textbox field
     * @return value filled in the specified textbox field
     */
    public String getText(String name) {
        ElementStub textbox = tasks.textbox(name);
        tasks.waitForElementExists(tasks, textbox, textbox.toString(), Timing.WAIT_TIME);
        return textbox.getValue();
    }

    /**
     * Fills text in textarea specified by name
     *
     * @param name  represents name of textarea element to fill
     * @param value the text to fill the specified textarea
     */
    public void setTextInTextArea(String name, String value) {
        ElementStub textarea = tasks.textarea(name);
        tasks.waitForElementExists(tasks, textarea, textarea.toString(), Timing.WAIT_TIME);
        textarea.setValue(value);
        tasks.waitForElementToContainValue(tasks, textarea, value, Timing.WAIT_TIME);
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
     * Returns value of text area with given locator near cell with given locator.
     * @param selection
     * @param cellSelection
     * @return value of text area
     */
    public String getTextInTextAreaNearCell(String selection,String cellSelection) {
    	ElementStub elem = tasks.textarea(selection).near(tasks.cell(cellSelection));
    	tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
        return elem.getValue();
    }

    /**
     * Returns text in the specified textarea element
     *
     * @param name textarea element name
     * @return text filled in the specified textarea element
     */
    public String getTextInTextArea(String name) {
        ElementStub textarea = tasks.textarea(name);
        tasks.waitForElementExists(tasks, textarea, textarea.toString(), Timing.WAIT_TIME);
        return tasks.textarea(name).getValue();
    }

    public void setPassword(String name, String value) {
        ElementStub password = tasks.password(name);
        tasks.waitForElementExists(tasks, password, password.toString(), Timing.WAIT_TIME);
        password.setValue(value);
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
     * Returns true if given radio button is checked. False otherwise.
     * @param radioSelection
     * @param cellSelection
     * @return true if given radio button is checked. False otherwise.
     */
    public boolean isRadioNearCellChecked(String radioSelection, String cellSelection){
    	ElementStub elem = tasks.radio(radioSelection).near(tasks.cell(cellSelection));
    	tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME);
    	return elem.checked();
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
                for (ElementStub cell : tasks.cell("/menu.*/").in(es).collectSimilar()) {
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
        List<ElementStub> scrolls = tasks.image("/.*vscroll_" + type + ".*/").collectSimilar();
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
                //scroll = getScrollButton("Over_end");
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
    /**
     * selects given value in combobox - this will literally try to expand all comboboxes on page until
     * given selection is found
     * @param selection text to be selected
     */
    public void selectCombo(String selection) {
	selectCombo(0,selection);
    }
    
    
    private List<ElementStub> getSelectionCells(String selection) {
        List<ElementStub> visible = new ArrayList<ElementStub>();
        ElementStub parent = tasks.div("pickListMenuBody");
        if (!parent.exists()) {
            log.warning("Not returning any visible cells because parent div does not exist");
            return visible;
        }
        List<ElementStub> cells = tasks.cell(selection).in(parent).collectSimilar();
        for (ElementStub es : cells) {
            if (es.isVisible()) {
                visible.add(es);
            }
        }
        log.fine("Returning selection cells, found "+cells.size()+", but returning "+visible.size()+" visible ones");
        return visible;
    }

    /**
     * selects given value in combobox.
     * @param index of combobox on page
     * @param selection text to be selected
     */
    public void selectCombo(int index, String selection) {
        // if you decide to fix this method you are a brave man
        // I've spent hours of tuning this **cking code to work
        // 
        List<ElementStub> pickers = tasks.image("/comboBoxPicker.*/").collectSimilar();
        log.fine("Found " + pickers.size() + " comboboxes, required index=" + index);
        if (index > pickers.size()-1) {
            index = pickers.size() -1;
            log.warning("Requested index does not fit into found combos, setting to "+ index);
        }
        ElementStub picker = pickers.get(index);
        log.fine("Performing click on combo via mouseOver + click on hovered picker");
        tasks.xy(picker, 3, 3).mouseOver();
        ElementStub pickerOver = tasks.image("/comboBoxPicker_Over.*/");
        if (pickerOver.exists()) {
            tasks.xy(pickerOver, 3, 3).click();
        }
        else {
            log.fine("Method via mouseOver failed, fallback to click()");
            tasks.xy(picker, 3, 3).click();
        }
        log.fine("It looks like I clicked on combo");
        List<ElementStub> cells = getSelectionCells(selection);
        if (cells.isEmpty() && pickerOver.exists()) {
            log.fine("Combo did not pop up? Trying ONE more click...");
            // when combo is focused single click does NOT work - wtf!
            tasks.xy(pickerOver, 3, 3).mouseDown();
            tasks.xy(pickerOver, 3, 3).mouseUp();
            cells = getSelectionCells(selection);
        }
	if (cells.isEmpty()) {
	    if (pickers.size() - 1 > index) {
    		// combo was probably clicked, but selection did not appear
    		// I know this may sound crazy, but 'index' might be wrong, so
    		// let's
    		// try out all pickers
    		log.warning("Selection not found, maybe because of wrong index=" + index + ". Let's be smarter then QE and try out other (higher index) combo pickers");
    		while (index < pickers.size() -1) {
    		    try {
    		        index+=1;
    	            log.info("Trying out selectCombo(" + index + "," + selection + ")");
    	            //tasks.xy(picker,3,3).click();
    	            this.selectCombo(index, selection);
    	            return;
    	        } catch (RuntimeException e) {
    	            log.log(Level.WARNING, "At attempt to select ["+selection+"] in combo index="+index+" failed with exception", e);
    	        }
    		}
	    }
	    throw new RuntimeException("Unable to select [" + selection + "] comboBox did NOT pop up");
	}
	else {
        // lets click to all selections found starting with the last one
        log.fine("Found rows matching [" + selection + "] : " + cells.size());
        for (int i = cells.size()-1;i >= 0; i--) {
            log.fine("Selecting index="+i);
            ElementStub es = cells.get(i);
            if (es.isVisible()) {
                tasks.xy(es, 3, 3).click();
                log.fine("Selecting [" + selection + "].");
            }
            else {
                log.fine("Selection [" + selection + "] was not visible");
            }
        }

        return;
	}
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
        if (check) { //PatternFly Change: 15-Jul-2014
            checkBox = "/^unchecked/[" + index + "]";
        } else {
            checkBox = "/^checked/[" + index + "]";
        }
        tasks.image(checkBox).parentNode().focus();
        log.fine("Sending keypress to " + checkBox);
        tasks.execute("_sahi._keyPress(_sahi._image('" + checkBox + "'), 32);");
    }

    /**
     * check checkbox near a cell
     *
     * @param cellSelection cell name for tightening which checkbox to use
     * @param check true to check, false to uncheck
     */
    public void checkBoxNearCell(String cellSelection, boolean check) {
        tasks.waitFor(Timing.TIME_1S);
        String checkBox = null;
        if (check) {
            checkBox = "/^unchecked.*/";
        } else {
            checkBox = "/^checked.*/";
        }
        ElementStub elem = tasks.image(checkBox).near(tasks.cell(cellSelection));
        if (tasks.waitForElementVisible(tasks, elem, elem.toString(), Timing.WAIT_TIME)) {
            elem.parentNode().focus();
            log.fine("Sending keypress to " + checkBox);
            tasks.execute("_sahi._keyPress("+elem.toString()+", 32);");
        }
    }
    
    
    /**
     * Returns first visible element similar to given or null when there is no visible element.
     * @param elementToFind
     * @return first visible element similar to given or null when there is no visible element.
     */
    public ElementStub getVisibleElement(ElementStub elementToFind){
    	ElementStub elem = null;
    	List<ElementStub> elements = elementToFind.collectSimilar();
    	log.finer("Found following count of simmilar elements:" + elements.size());
    	
    	for(int i=0;i<elements.size();i++){
    		if(elements.get(i).isVisible()){
    			elem = elements.get(i);
    			break;
    		}
    	}
    	
    	return elem;
    }
    
    /**
     * Clicks on a button with given label in confirmation dialog.
     * @param buttonLabel
     */
    public void serveConfirmDialog(String buttonLabel){
    	log.finer("Serving confirmation dialog, button with label " + buttonLabel);
    	long waitTimeMilliSeconds = Timing.WAIT_TIME;
    	while(waitTimeMilliSeconds >=  0){
    		if(getVisibleElement(tasks.cell(buttonLabel)) != null){
    			getVisibleElement(tasks.cell(buttonLabel)).click();
    			break;
    		}else{
				tasks.waitFor(500);
				waitTimeMilliSeconds -= 500;
				if((waitTimeMilliSeconds%(1000*5)) <= 0){
					log.finer("Waiting for the button: ["+buttonLabel+"], Remaining wait time: "+(waitTimeMilliSeconds/1000)+" Second(s)...");
				}
			}
		}
    }
    
    /**
     * Selects row with given index witch contains cell with given locator. 
     * @param name cell locator
     * @param index index of a row to be selected when more rows were found
     * @return true when at least one row was found, false otherwise
     * @throws RuntimeException when a selection of given row failed
     */
    public boolean selectRow(String name,int index){
    	int rows = tasks.cell(name).countSimilar();
        log.finer("Matched cells " + rows);
        if(rows==0){
        	return false;
        }
        tasks.xy(tasks.cell(name+"["+index+"]"),3,3).click();
        int sel = tasks.cell("/tallCellSelected.*/").countSimilar();
        if(sel == 0){
        	throw new RuntimeException("Failed to select given row!!");
        }
        
        return true;
    }

}
