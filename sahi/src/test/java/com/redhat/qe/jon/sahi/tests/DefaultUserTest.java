package com.redhat.qe.jon.sahi.tests;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.OnAgentSahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.Role;
import com.redhat.qe.jon.sahi.base.administration.RolesPage;
import com.redhat.qe.jon.sahi.base.administration.User;
import com.redhat.qe.jon.sahi.base.administration.UsersPage;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.groups.AllGroupsPage;
import com.redhat.qe.jon.sahi.base.inventory.groups.Group;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * This test creates user with default permissions and navigates through 
 * resource tabs and top level tabs checking if any Global error message
 * was shown.
 * 
 * @author fbrychta
 *
 */
public class DefaultUserTest extends OnAgentSahiTestScript {
    public static String USER_NAME = "defaultTestUser";
    public static String USER_PASSWORD = "password";
    
    @BeforeClass
    public void prepareUser(){
        // create a new res Group containing one platform
        AllGroupsPage groupsPage = new AllGroupsPage(sahiTasks);
        groupsPage.navigate();
        
        String groupName = "Platform";
        Group testGroup = new Group(groupName);
        testGroup.setDescription("One platfrom recursive");
        testGroup.setRecursive(true);
        ArrayList<String> resourceNames = new ArrayList<String>();
        resourceNames.add(agentName);
        testGroup.setResourceNames(resourceNames);
        
        groupsPage.deleteGroup(groupName);
        groupsPage.createNewGroup(testGroup);
        
        
        // create a new role
        RolesPage rolesPage = new RolesPage(sahiTasks);
        rolesPage.navigate();
        
        String roleName = "testRole";
        Role testRole = new Role(roleName);
        testRole.setDescription("Test role");
        ArrayList<String> resGroupNames = new ArrayList<String>();
        resGroupNames.add(groupName);
        testRole.setResourceGroupNames(resGroupNames);
        
        rolesPage.deleteRole(roleName);
        rolesPage.createRole(testRole);
        
        
        // create a new user with previous role
        UsersPage usrPage = new UsersPage(sahiTasks);
        usrPage.navigate();
        
        User testUser = new User(USER_NAME,USER_PASSWORD,"John", "Rambo", "hell@hell");
        ArrayList<String> roleNames = new ArrayList<String>();
        roleNames.add(roleName);
        testUser.setRoleNames(roleNames);
        
        usrPage.deleteUser(USER_NAME);
        usrPage.createUser(testUser);
    }
    
    @Test(priority=0)
    public void checkGlobalErrorsTest(){
        // bz 1082052
        logoutLogin(USER_NAME,USER_PASSWORD);
        checkErrors();
        
        Resource platform = new Resource(sahiTasks,agentName);
        Assert.assertTrue(platform.isAvailable(),"Resource should be available!!");
        
        // bz 1082083
        platform.summary();
        checkErrors();
        platform.inventory();
        checkErrors();
        platform.alerts();
        checkErrors();
        platform.monitoring();
        checkErrors();
        platform.events();
        checkErrors();
        platform.operations();
        checkErrors();
        platform.drift();
        checkErrors();
        platform.content();
        checkErrors();
        
        // bz 1040928
        Assert.assertTrue(platform.isAvailable(),"Resource should be available!!");
        
        sahiTasks.link("Dashboard").click();
        checkErrors();
        sahiTasks.link("Inventory").click();
        checkErrors();
        sahiTasks.link("Reports").click();
        checkErrors();
        sahiTasks.link("Bundles").click();
        checkErrors();
        sahiTasks.link("Administration").click();
        checkErrors();
        sahiTasks.link("Help").click();
        checkErrors();
    }
    
    @AfterClass
    public void loginAsRhqadmin(){
        logoutLogin("rhqadmin","rhqadmin");
    }
    
    private void logoutLogin(String userName, String password){
        sahiTasks.logout();
        sahiTasks.loginNewUser(userName, password);
        Assert.assertTrue(sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Inventory Summary"),
                "Inventory Summary label", Timing.WAIT_TIME),"User should be logged in!");
    }
    
    private void checkErrors(){
        if(sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("ErrorBlock"),"Error block",Timing.WAIT_TIME)){
            throw new RuntimeException("Error block is visible. Text: " + sahiTasks.cell("ErrorBlock").getText());
        }
    }
}
