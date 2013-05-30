package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.alerts.Alerts;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;


/**
 * This class represents alert definitions page.
 * @author fbrychta
 *
 */
public class AlertDefinitionsPage extends Alerts {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	
	public AlertDefinitionsPage(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}
	
	/**
     * Navigates to Alert/Definitions page.
     * @return this object
     */
	public AlertDefinitionsPage navigateTo(){
		navigateUnderResource("Alerts/Definitions");
        raiseErrorIfCellDoesNotExist("Definitions");
        
        return this;
	}
	
	/**
	 * Returns a helper object used for creation of a new alert definition. 
	 * @param alertName name of the alert definition to be created
	 * @return <class>AlertDefinitionCreator</class>
	 */
	public AlertDefinitionCreator getAlertDefCreator(String alertName) {
    	log.finer("Creating a new alarm definition " + alertName);
    	
    	return new AlertDefinitionCreator(tasks, alertName);
    }
   
	/**
	 * Returns all found alert definitions with given name.
	 * Data are parsed from the alert definitions page.
	 * @param alertName name of the alert definition
	 * @return all found alert definitions with given name.
	 * @throws <class>RuntimeException</class> when parsing of the page failed
	 */
    public ArrayList<AlertDefinition> getAlertDefinitions(String alertName){
    	ArrayList<AlertDefinition> alertDefs = new ArrayList<AlertDefinition>();
    	
    	log.fine("Getting alert definitions with name "+alertName);
    	int rows = tasks.cell(alertName).countSimilar();
        log.finer("Matched cells " + rows);

        ElementStub nameCell;
        ElementStub cell;
        ElementStub row;
        AlertDefinition alertDef;
        // for all found definitions with given name
    	for(int i=0;i<rows;i++){
    		// cell containing name of the alert definition
    		nameCell = tasks.cell(alertName+"["+i+"]");
    		row = nameCell.parentNode("tr");
    		
    		// create a helper object and fill it with parsed data
    		alertDef = new AlertDefinition();
    		alertDef.setName(alertName);
    		alertDef.setDescription(tasks.cell("/tallCell.*/[1]").in(row).getText());
    		alertDef.setCreationTime(tasks.cell("/tallCell.*/[2]").in(row).getText());
    		alertDef.setModifiedTime(tasks.cell("/tallCell.*/[3]").in(row).getText());
    		
    		// set an availability according the image inside a cell
    		cell = tasks.cell("/tallCell.*/[4]").in(row);
    		if(!tasks.isVisible(cell)){
    			throw new RuntimeException("Couldn't parse a availability from the alert definitions page.");
    		}
    		if(tasks.isVisible(tasks.image("availability_green_16.png").in(cell))){
    			alertDef.setEnabled(true);
    		}else if(tasks.isVisible(tasks.image("availability_red_16.png").in(cell))){
    			alertDef.setEnabled(false);
    		}else{
    			throw new RuntimeException("Couldn't parse an availability from the alert definitions page.");
    		}
    		
    		// set a priority according the image inside a cell
    		cell = tasks.cell("/tallCell.*/[5]").in(row);
    		if(!tasks.isVisible(cell)){
    			throw new RuntimeException("Couldn't parse a priority from the alert definitions page.");
    		}
    		if(tasks.isVisible(tasks.image("Alert_MEDIUM_16.png").in(cell))){
    			alertDef.setPriority(AlertDefinition.Priority.Medium);
    		}else if(tasks.isVisible(tasks.image("Alert_LOW_16.png").in(cell))){
    			alertDef.setPriority(AlertDefinition.Priority.Low);
    		}else if(tasks.isVisible(tasks.image("Alert_HIGH_16.png").in(cell))){
    			alertDef.setPriority(AlertDefinition.Priority.High);
    		}else{
    			throw new RuntimeException("Couldn't parse a priority from the alert definitions page.");
    		}
    		
    		alertDef.setParent(tasks.cell("/tallCell.*/[6]").in(row).getText());
    		alertDef.setProtectedField(tasks.cell("/tallCell.*/[7]").in(row).getText());
    
    		log.finer("Following values were parsed from the page:"+
    				" Description: "+alertDef.getDescription()+
    				", Creation time: "+alertDef.getCreationTime()+
    				", Modified time: " +alertDef.getModifiedTime()+
    				", Enabled: " +alertDef.isEnabled()+
    				", Priority: "+alertDef.getPriority()+
    				", Parent: "+alertDef.getParent()+
    				", Protected: " +alertDef.getProtectedField());
        	
        	alertDefs.add(alertDef);
    	}
    	
    	return alertDefs;
    }
    
    /**
     * Deletes all alert definitions with given name.
     * @param alertDefName
     * @return this object
     * @throws <class>RuntimeException</class> when a button was not found
     */
    public AlertDefinitionsPage deleteAlertDefinition(String alertDefName){
    	log.fine("Deleting alert definitions with name " + alertDefName);
        while(selectRow(alertDefName,0)){
        	ElementStub delBut = getVisibleElement(tasks.cell("Delete"));
        	if(delBut == null){
        		throw new RuntimeException("No visible Delete button found!");
        	}
        	delBut.click();
        	serveConfirmDialog("Yes");
        	tasks.waitForElementVisible(tasks, tasks.cell("/Successfully deleted.*/"), "Successful message", Timing.WAIT_TIME);
        }
    	
    	return this;
    }
    
 
    /**
     * Disables all alert definitions with given name.
     * @param alertDefName
     * @return this object
     * @throws <class>RuntimeException</class> when a button was not found
     */
    public AlertDefinitionsPage disableAlertDefinition(String alertDefName){
    	log.fine("Disabling alert definitions with name " + alertDefName);
    	int rows = tasks.cell(alertDefName).countSimilar();
    	for(int i=0;i<rows;i++){
            selectRow(alertDefName,i);
            ElementStub disBut = getVisibleElement(tasks.cell("Disable"));
        	if(disBut == null){
        		throw new RuntimeException("No visible Disable button found!");
        	}
        	disBut.click();
        	serveConfirmDialog("Yes");
        	tasks.waitForElementVisible(tasks, tasks.cell("/Successfully disabled.*/"), "Successful message", Timing.WAIT_TIME);
    	}
    	return this;
    }
    
    /**
     * Enables all alert definitions with given name.
     * @param alertDefName
     * @return this object
     * @throws <class>RuntimeException</class> when a enable button was not found
     */
    public AlertDefinitionsPage enableAlertDefinition(String alertDefName){
    	log.fine("Enabling alert definition with name " + alertDefName);
    	int rows = tasks.cell(alertDefName).countSimilar();
    	for(int i=0;i<rows;i++){
        	selectRow(alertDefName,i);
        	 ElementStub enBut = getVisibleElement(tasks.cell("Enable"));
          	if(enBut == null){
          		throw new RuntimeException("No visible Enable button found!");
          	}
          	enBut.click();
        	serveConfirmDialog("Yes");
        	tasks.waitForElementVisible(tasks, tasks.cell("/Successfully enabled.*/"), "Successful message", Timing.WAIT_TIME);
    	}
    	
    	return this;
    }
    
    /**
     * Selects row with given index witch contains cell with given locator. 
     * @param name cell locator
     * @param index index of a row to be selected when more rows were found
     * @return true when at least one row was found, false otherwise
     * @throws <class>RuntimeException</class> when a selection of given row failed
     */
    private boolean selectRow(String name,int index){
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
    
    /**
     * Clicks on a button with given label in confirmation dialog.
     * @param buttonLabel
     */
    private void serveConfirmDialog(String buttonLabel){
    	log.finer("Serving confirmation dialog, button with label " + buttonLabel);
    	tasks.waitForElementVisible(tasks, tasks.cell(buttonLabel), buttonLabel+" button", Timing.WAIT_TIME);
    	tasks.cell(buttonLabel).click();
    }
    
    /**
     * Returns first visible element similar to given or null when there is no visible element.
     * @param elementToFind
     * @return first visible element similar to given or null when there is no visible element.
     */
    private ElementStub getVisibleElement(ElementStub elementToFind){
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
}