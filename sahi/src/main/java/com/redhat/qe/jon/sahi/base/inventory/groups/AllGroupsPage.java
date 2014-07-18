package com.redhat.qe.jon.sahi.base.inventory.groups;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * This class represents All Groups page.
 * @author fbrychta
 *
 */
public class AllGroupsPage {
    private static Logger log = Logger.getLogger(AllGroupsPage.class.getName());
    protected SahiTasks tasks = null;
    protected Editor editor = null;
    
    public AllGroupsPage(SahiTasks tasks) {
        this.tasks = tasks;
        this.editor = new Editor(tasks);
    }
    
    /**
     * Navigates to the page
     */
    public AllGroupsPage navigate(){
        String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
        String url = serverBaseUrl+"/#Inventory/Groups/AllGroups";
        log.fine("Navigating to ["+url+"]");
        tasks.navigateTo(url,false);
        tasks.waitForElementVisible(tasks, tasks.cell("New"), "New button", Timing.WAIT_TIME);
        
        return this;
    }
    
    /**
     * Creates a given group.
     * @param newGroup
     * @return this
     */
    public AllGroupsPage createNewGroup(Group newGroup){
        tasks.cell("New").click();
        tasks.waitForElementVisible(tasks, tasks.cell("Create Group"), "Create Group label", Timing.WAIT_TIME);
        
        if(newGroup.getName() != null){
            log.info("Creating a group named " + newGroup.getName());
            tasks.textbox("name").setValue(newGroup.getName());
        }
        if(newGroup.getDescription() != null){
            tasks.textarea("description").setValue(newGroup.getDescription());
        }

        if(newGroup.isRecursive()){
            editor.checkBox(0, true);
        }
        tasks.cell("Next").click();
        tasks.waitForElementVisible(tasks, tasks.cell("Available resource"), "Available resource label", Timing.WAIT_TIME);
        
        // select resources which will belong to this group
        for (String resName : newGroup.getResourceNames()){
            //ElementStub elem =tasks.textbox("/textItem.*/").near(tasks.cell("Search :[1]"));
            ElementStub elem =tasks.textbox("textItem").under(tasks.cell("Select Members")); //General failure fix : 18-Jul-2014
            elem.setValue(resName);
            // click there to invoke filtering
            elem.click();
            
            tasks.waitFor(Timing.WAIT_TIME);
            tasks.div(resName).click();
            tasks.image("/right/").click();//PatternFly Change: 15-Jul-2014
        }
        
        tasks.cell("Finish").click();
        String msg = "You have created a new resource group with name ["+newGroup.getName()+"].";
        tasks.waitForAnyElementsToBecomeVisible(tasks,
                new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                "Successful message", Timing.WAIT_TIME);
        // TODO check errors
        
        return this;
    }
    
    /**
     * Removes a group with given name.
     * @param groupName
     * @return this
     */
    public AllGroupsPage deleteGroup(String groupName){
        if(tasks.div(groupName).isVisible()){
            log.info("Removing a group named " + groupName);
            tasks.div(groupName).click();
            tasks.cell("Delete").click();
            tasks.cell("Yes").click();
            String msg = "/You have successfully deleted the selected resource groups*/";
            tasks.waitForAnyElementsToBecomeVisible(tasks,
                    new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                    "Successful message", Timing.WAIT_TIME);
            // TODO check errors
        }
        return this;
    }
}
