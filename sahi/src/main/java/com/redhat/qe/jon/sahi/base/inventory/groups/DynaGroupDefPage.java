package com.redhat.qe.jon.sahi.base.inventory.groups;

import java.util.logging.Logger;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * This class represents /#Inventory/Groups/DynagroupDefinitions page.
 * @author fbrychta
 *
 */
public class DynaGroupDefPage {
    private static Logger log = Logger.getLogger(DynaGroupDefPage.class.getName());
    protected SahiTasks tasks = null;
    protected Editor editor = null;
    public static final String COMBOBOX_SELECTOR = "/selectItemText.*/";

    public DynaGroupDefPage(SahiTasks tasks) {
        this.tasks = tasks;
        this.editor = new Editor(tasks);
    }

    /**
     * Navigates to the page
     * @return this
     */
    public DynaGroupDefPage navigate(){
        String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
        String url = serverBaseUrl+"/#Inventory/Groups/DynagroupDefinitions";
        log.fine("Navigating to ["+url+"]");
        tasks.navigateTo(url,false);
        tasks.waitForElementVisible(tasks, tasks.cell("New"), "New button", Timing.WAIT_TIME);

        return this;
    }

    /**
     * Creates a new dynagroup definition with given parameters.
     * 
     * @param def definition to be created
     * @return true if given definition was successfully created, false otherwise
     */
    public boolean createNew(DynagroupDef def){
        tasks.click(tasks.cell("New"));
        tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);

        fillFields(def);

        log.fine("Saving a definition.");
        tasks.clickOnFirstVisibleElement(tasks.cell("Save"));
        // clicking twice to workaround bz 1098911
        tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);
        tasks.clickOnFirstVisibleElement(tasks.cell("Save"));
        return tasks.waitForElementVisible(tasks, tasks.cell("/You have successfully saved the group definition named*/"), 
                "Successful message",Timing.WAIT_TIME);
    }
    /**
     * Tries to get parameters of given dynagroup definition.
     * 
     * @param name name of the definition to get
     * @throws RuntimeException when a definition with given name is not found on the page
     * @return DynagroupDef parsed definition with given name
     */
    public DynagroupDef getDefinition(String name){
        if(tasks.link(name).isVisible()){
            tasks.link(name).click();
            tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);
            return new DynagroupDef(editor.getText("name"),
                    editor.getTextInTextArea("description"),
                    "",
                    editor.getTextInTextArea("expression"),
                    !tasks.div("Recursive").containsHTML("unchecked"),
                    Integer.parseInt(editor.getText("recalculationInterval")));
        }else{
            throw new RuntimeException("Given dynagroup definition ["+name+"] was not found!!");
        }
    }
    /**
     * Deletes a dynagroup definition with given name.
     * 
     * @param name name of the definition to delete
     * @return true if the definition was successfully deleted, false when the 
     * definition was not found or deletion failed
     */
    public boolean deleteDefinition(String name){
        log.fine("Trying to delete a definition with name " + name);
        if(tasks.div(name).isVisible()){
            tasks.div(name).click();
            tasks.click(tasks.cell("Delete"));
            tasks.click(tasks.cell("Yes"));
            return tasks.waitForElementVisible(tasks, tasks.cell("/You have successfully deleted*/"), 
                    "Successful message",Timing.WAIT_TIME);
        }
        return false;
    }
    /**
     * Edits a dynagroup definition with given name.
     * 
     * @param name name of the definition to be edited
     * @param def values from this definition will be used
     * @return true if the definition was successfully edited, false when the 
     * definition was not found or edit failed
     */
    public boolean editDefinition(String name, DynagroupDef def){
        log.fine("Trying to edit a definition with name " + name);
        if(tasks.link(name).isVisible()){
            tasks.link(name).click();
            tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);

            fillFields(def);

            tasks.clickOnFirstVisibleElement(tasks.cell("Save"));
            // take care of question dialog when editing predefined dynagroup definition
            if(tasks.cell("Yes").isVisible()){
                tasks.cell("Yes").click();
            }
            return tasks.waitForElementVisible(tasks, tasks.cell("/You have successfully saved the group definition named*/"), 
                    "Successful message",Timing.WAIT_TIME);
        }
        return false;
    }
    /**
     * Tests if given definition is marked as canned.
     * 
     * @param name name of the definition to be tested
     * @throws RuntimeException when a definition with given name is not found on the page
     * @return true if the definition is marked as canned, false otherwise
     */
    public boolean isMarkedAsCanned(String name){
        if(tasks.link(name).isVisible()){
            return tasks.link(name).parentNode("tr").containsHTML("Plugin_16.png");
        }else{
            throw new RuntimeException("Given dynagroup definition ["+name+"] was not found!!");
        }
    }
    
    private void fillFields(DynagroupDef def){
        if(def.getProvidedExprName() != null){
            tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Provided Expression :", def.getProvidedExprName());
        }
        if(def.getName() != null){
            editor.setText("name", def.getName());
        }
        if(def.getDescription() != null){
            editor.setTextInTextArea("description", def.getDescription());
        }
        if(def.getExpression() != null){
            // TODO: \n is not working
            editor.setTextInTextArea("expression", def.getExpression());
        }
        boolean isRecursive = !tasks.div("Recursive").containsHTML("unchecked");
        if(def.isRecursive() != isRecursive){
            tasks.div("Recursive").click();
        }
        if(def.getRecalcInt() >=0){
            editor.setText("recalculationInterval", Integer.toString(def.getRecalcInt()));
        }
    }

}
