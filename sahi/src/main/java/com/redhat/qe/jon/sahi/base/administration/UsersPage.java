package com.redhat.qe.jon.sahi.base.administration;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * This class represents Users page.
 * @author fbrychta
 *
 */
public class UsersPage extends AdministrationPage {
    private static Logger log = Logger.getLogger(UsersPage.class.getName());
    protected SahiTasks tasks = null;
    
    public UsersPage(SahiTasks tasks) {
        super(tasks);
        this.tasks = tasks;
    }
    
    /**
     * Navigates to the page
     */
    public UsersPage navigate(){
        String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
        String url = serverBaseUrl+"/#Administration/Security/Users";
        log.fine("Navigating to ["+url+"]");
        tasks.navigateTo(url,false);
        tasks.waitForElementVisible(tasks, tasks.cell("User Name"), "User Name label", Timing.WAIT_TIME);
        
        return this;
    }
    
    /**
     * Creates given user
     * @param user
     * @return this
     */
    public UsersPage createUser(User user){
        tasks.cell("New").click();
        tasks.waitForElementVisible(tasks, tasks.cell("Create New User"), "Create New User label", Timing.WAIT_TIME);
        
        if(user.getName() != null){
            log.info("Creating a user named " + user.getName());
            tasks.textbox("name").setValue(user.getName());
        }
        if(user.getPassword() != null){
            tasks.password("password").setValue(user.getPassword());
            tasks.password("passwordVerify").setValue(user.getPassword());
        }
        if(user.getFirsName() != null){
            tasks.textbox("firstName").setValue(user.getFirsName());
        }
        if(user.getLastName() != null){
            tasks.textbox("lastName").setValue(user.getLastName());
        }
        if(user.getEmail() != null){
            tasks.textbox("emailAddress").setValue(user.getEmail());
        }
        if(user.getDepartment() != null){
            tasks.textbox("department").setValue(user.getDepartment());
        }
        if(user.getPhoneNumber() != null){
            tasks.textbox("phoneNumber").setValue(user.getPhoneNumber());
        }
        if(!user.isLoginEnabled()){
            // TODO
        }
        
        for(String roleName : user.getRoleNames()){
            tasks.xy(tasks.div(roleName),3,3).click();
            tasks.image("/right.*/").click();
        }
        
        tasks.cell("Save").click();
        
        String msg = "User created.";
        tasks.waitForAnyElementsToBecomeVisible(tasks,
                new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                "Successful message", Timing.WAIT_TIME);
        
        // TODO, check for errors (validation etc.)
        return this;
    }
    
    /**
     * Removes user with given name.
     * @param userName
     * @return this
     */
    public UsersPage deleteUser(String userName){
        if(tasks.div(userName).isVisible()){
            log.info("Removing a user named " + userName);
            tasks.div(userName).click();
            tasks.cell("Delete").click();
            tasks.cell("Yes").click();
            
            String msg = "Deleted user [["+userName+"]]";
            tasks.waitForAnyElementsToBecomeVisible(tasks,
                    new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                    "Successful message", Timing.WAIT_TIME);
            
            // TODO check errors
        }
        return this;
    }
}
