package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class AlertDefinitionPageSnippet {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final Editor editor;
    protected SahiTasks tasks = null;
    
    public AlertDefinitionPageSnippet(SahiTasks tasks) {
        this.editor = new Editor(tasks);
        this.tasks = tasks;
    }
    /**
     * Returns a helper object used for filling alert definition dialog. 
     * @param alertName name of the alert definition to be created
     * @return AlertDefinitionEditor
     */
    public AlertDefinitionEditor createAlarmDefinition(String alertName) {
        log.finer("Creating a new alarm definition " + alertName);
        tasks.cell("New").click();
        tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);
        AlertDefinitionEditor alDefEd = new AlertDefinitionEditor(tasks);
        alDefEd.setName(alertName);
        
        return alDefEd;
    }
   
    /**
     * Returns all found alert definitions with given name.
     * Data are parsed from the alert definitions page.
     * @param alertName name of the alert definition
     * @return all found alert definitions with given name.
     * @throws RuntimeException when parsing of the page failed
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

            if(tasks.cell("/tallCell.*/[6]").in(row).isVisible()){
                alertDef.setParent(tasks.cell("/tallCell.*/[6]").in(row).getText());
            }
            if(tasks.cell("/tallCell.*/[7]").in(row).isVisible()){
                alertDef.setProtectedField(tasks.cell("/tallCell.*/[7]").in(row).getText());
            }

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
     * Returns a helper object used for setting/getting values from alert definition dialog. 
     * @param alertDefName name of the alert definition to be edited.
     * @return AlertDefinitionEditor
     */
    public AlertDefinitionEditor editAlertDefinition(String alertDefName){
        log.fine("Editing alert definitions with name " + alertDefName);
        int rows = tasks.cell(alertDefName).countSimilar();
        if(rows == 0){
            throw new RuntimeException("Alert definition with name '"+alertDefName+"' not found on the page!!");
        }
        tasks.xy(tasks.cell(alertDefName+"[0]"),3,3).doubleClick();
        tasks.waitForElementVisible(tasks, tasks.cell("Edit"), "Edit button", Timing.WAIT_TIME);
        tasks.cell("Edit").click();
        
        tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);
        
        return new AlertDefinitionEditor(tasks);
    }
    
    /**
     * Deletes all alert definitions with given name.
     * @param alertDefName
     * @return true when given alert definition was deleted
     * @throws RuntimeException when a button was not found
     */
    public boolean deleteAlertDefinition(String alertDefName){
        log.fine("Deleting alert definitions with name " + alertDefName);
        while(editor.selectRow(alertDefName,0)){
            ElementStub delBut = editor.getVisibleElement(tasks.cell("Delete"));
            if(delBut == null){
                throw new RuntimeException("No visible Delete button found!");
            }
            delBut.click();
            editor.serveConfirmDialog("Yes");
            
        }
        String msg = "/Successfully deleted.*/";
        return tasks.waitForAnyElementsToBecomeVisible(tasks,
                new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                "Successful message", Timing.WAIT_TIME);
    }
    
 
    /**
     * Disables all alert definitions with given name.
     * @param alertDefName
     * @return true when given alert definition was disabled
     * @throws RuntimeException when a button was not found
     */
    public boolean disableAlertDefinition(String alertDefName){
        log.fine("Disabling alert definitions with name " + alertDefName);
        int rows = tasks.cell(alertDefName).countSimilar();
        for(int i=0;i<rows;i++){
            editor.selectRow(alertDefName,i);
            ElementStub disBut = editor.getVisibleElement(tasks.cell("Disable"));
            if(disBut == null){
                throw new RuntimeException("No visible Disable button found!");
            }
            disBut.click();
            editor.serveConfirmDialog("Yes");
            
        }
        String msg = "/Successfully disabled.*/";
        return tasks.waitForAnyElementsToBecomeVisible(tasks,
                new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                "Successful message", Timing.WAIT_TIME);
    }
    
    /**
     * Enables all alert definitions with given name.
     * @param alertDefName
     * @return true when given alert definition was enabled
     * @throws RuntimeException when a enable button was not found
     */
    public boolean enableAlertDefinition(String alertDefName){
        log.fine("Enabling alert definition with name " + alertDefName);
        int rows = tasks.cell(alertDefName).countSimilar();
        for(int i=0;i<rows;i++){
            editor.selectRow(alertDefName,i);
             ElementStub enBut = editor.getVisibleElement(tasks.cell("Enable"));
            if(enBut == null){
                throw new RuntimeException("No visible Enable button found!");
            }
            enBut.click();
            editor.serveConfirmDialog("Yes");
            
        }
        String msg = "/Successfully enabled.*/";
        return tasks.waitForAnyElementsToBecomeVisible(tasks,
                new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                "Successful message", Timing.WAIT_TIME);
    }
}
