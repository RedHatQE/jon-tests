package com.redhat.qe.jon.sahi.base.administration;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * Represents Roles page.
 * @author fbrychta
 *
 */
public class RolesPage extends AdministrationPage {
    private static Logger log = Logger.getLogger(RolesPage.class.getName());
    protected SahiTasks tasks = null;
    
    public RolesPage(SahiTasks tasks) {
        super(tasks);
        this.tasks = tasks;
    }
    
    /**
     * Navigates to the page
     */
    public RolesPage navigate(){
        String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
        String url = serverBaseUrl+"/#Administration/Security/Roles";
        log.fine("Navigating to ["+url+"]");
        tasks.navigateTo(url,false);
        tasks.waitForElementVisible(tasks, tasks.cell("New"), "New button", Timing.WAIT_TIME);
        
        return this;
    }
    
    /**
     * Creates given role.
     * @param role
     * @return this
     */
    public RolesPage createRole(Role role){
        tasks.cell("New").click();
        tasks.waitForElementVisible(tasks, tasks.cell("Create New Role"), "Create New Role label", Timing.WAIT_TIME);
        
        if(role.getName() != null){
            log.info("Creating a role named " + role.getName());
            tasks.textbox("name").setValue(role.getName());
        }
        if(role.getDescription() != null){
            tasks.textbox("description").setValue(role.getDescription());
        }
        
        //TODO permissions
        
        
        tasks.cell("Resource Groups").click();
        tasks.waitForElementVisible(tasks, tasks.cell("Available Resource Groups"), "Available Resource Groups label", Timing.WAIT_TIME);
        for(String resGroupName : role.getResourceGroupNames()){
            if(!tasks.cell(resGroupName).isVisible()){
                throw new RuntimeException("Resource group named " +resGroupName+", was not found in Available Resource Groups list");
            }
            tasks.xy(tasks.cell(resGroupName),3,3).click();
            tasks.image("right.png").click();
        }
        
        //TODO bundle groups
        //TODO users
        //TODO LDAP groups
        
        tasks.cell("Save").click();
        
        String msg = "Role created.";
        tasks.waitForAnyElementsToBecomeVisible(tasks,
                new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                "Successful message", Timing.WAIT_TIME);
        
        // TODO, check for errors (validation etc.)
        return this;
    }
    
    /**
     * Removes a role with given name.
     * @param roleName
     * @return this
     */
    public RolesPage deleteRole(String roleName){
        if(tasks.div(roleName).isVisible()){
            log.info("Removing a role named " + roleName);
            tasks.div(roleName).click();
            tasks.cell("Delete").click();
            tasks.cell("Yes").click();
            
            String msg = "Role ["+roleName+"] deleted.";
            tasks.waitForAnyElementsToBecomeVisible(tasks,
                    new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                    "Successful message", Timing.WAIT_TIME);
            
            // TODO check errors
        }
        return this;
    }
}
