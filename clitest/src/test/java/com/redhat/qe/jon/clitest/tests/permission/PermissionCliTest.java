package com.redhat.qe.jon.clitest.tests.permission;

import com.redhat.qe.jon.clitest.base.CliTestRunner;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

import java.util.Date;

public class PermissionCliTest extends CliEngine {

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
        return super.createJSRunner(jsFile)
            .addDepends("rhqapi.js")
            .addDepends("jon-common.js")
            .addDepends("permissions/common.js");
    }

    public void initCase(String caseName) {
        createJSRunner("permissions/testPermissions-initCases.js")
                .withArg("caseName",caseName)
                .run();
    }

    public void runPermissionTest(String user, String action, boolean hasPerm, String expectedExceptionFragment) {
        createJSRunner("permissions/testPermissions.js")
                .asUser(user)
                .withArg("hasPerm",hasPerm ? "true" : "false")
                .withArg("action",action)
                .withArg("expectedExceptionFragment", expectedExceptionFragment)
                .run();
    }
    public void runPermissionTest(String user, String action, boolean hasPerm) {
    	runPermissionTest(user, action, hasPerm, null);
    }

    @Test()
	public void createChildResourceTest() {
        initCase("createChild_");
        String action =  "findRHQServer().createChild({name:'testif"+new Date().getTime()+"',type:'Network Interface'})";
        runPermissionTest("createChild_U1", action, true);  // this will endup resource creation timeout, because user has no permission to resource he just created
        runPermissionTest("createChild_U2", action, false);
	}
	
	@Test
	public void deleteChildResourceTest() {
        initCase("deleteChild_");
        String action =  "findRHQServer().child({type:'Network Interface'}).uninventory()";
        runPermissionTest("deleteChild_U1", action, true);
        runPermissionTest("deleteChild_U2", action, false);
	}

    @Test
    public void manageSettingsTest() {
        initCase("manageSettings_");
        String action =  " SystemManager.getSystemSettings()";
        runPermissionTest("manageSettings_U1", action, true);
        runPermissionTest("manageSettings_U2", action, false);
    }

    @Test
    public void resourceConfigurationTest() {
        initCase("resourceConfig_");
        String action =  " p(findRHQServer().updateConfiguration({foo:'bar'}));"; // we can affort this
        runPermissionTest("resourceConfig_U1", action, true); // configuration will not update, but we don't care about result
        runPermissionTest("resourceConfig_U2", action, false);
    }

    @Test
    public void resourceOperationTest() {
        initCase("resourceOperation_");
        String action =  " resources.platforms()[0].invokeOperation('discovery')";
        runPermissionTest("resourceOperation_U1", action, true);
        runPermissionTest("resourceOperation_U2", action, false);
    }

    @Test
    public void resourceGroupTest() {
        initCase("resourceGroup_");
        String action =  " assertTrue(resources.platforms().length > 0)";
        runPermissionTest("resourceGroup_U1", action, true);
        runPermissionTest("resourceGroup_U2", action, false);
    }

    @Test
    public void manageInventoryTest() {
        initCase("manageInventory_");
        String action =  " assertTrue(resources.platforms().length > 0)";
        runPermissionTest("manageInventory_U1", action, true);
        runPermissionTest("manageInventory_U2", action, false);
    }

    @Test
    public void manageMeasurementsTest() {
        initCase("manageSchedules_");
        String action =  "findRHQServer().getMetric('Server Version').set(true,360)";
        runPermissionTest("manageSchedules_U1", action, true);
        runPermissionTest("manageSchedules_U2", action, false);
    }

    @Test
    public void manageSecurityTest() {
        initCase("manageSecurity_");
        String action =  " assertTrue(resources.platforms().length > 0)";
        runPermissionTest("manageSecurity_U1", action, true);
        runPermissionTest("manageSecurity_U2", action, false);
    }

    @Test
    public void viewUserTest() {
        initCase("viewUsers_");
        String action =  " assertTrue(users.find().length > 1)"; // assert for 1 because every user can at minimum see himself
        runPermissionTest("viewUsers_U1", action, true);
        runPermissionTest("viewUsers_U2", action, false);
    }

    @Test
    public void updateResourceTest() {
        initCase("resourcePermission_");
        String action =  " findRHQServer().update({location:'jonlab'})";
        runPermissionTest("resourcePermission_U1", action, true);
        runPermissionTest("resourcePermission_U2", action, false);
    }

    @Test
    public void noPermissonsAndAdminTest() {
        initCase("userRoles_");

        createJSRunner("permissions/userRoles.js")
            .asUser("userRoles_Admin")
            .run();
        String action =  " assertTrue(resources.platforms().length > 0)";
        runPermissionTest("userRoles_Badguy", action, false);
    }


    @Test
    public void manageDriftTest() {
        initCase("manageDrift_");
        String driftDefName = "manageDriftTest";
        String createAction = " createDriftDefinition(resources.find({type:'Linux'})[0].getProxy(),'"+driftDefName+"');";
        String deleteAction = " deleteDriftDefinition(resources.find({type:'Linux'})[0].getProxy(),'"+driftDefName+"');"; 
        
        // at least one Linux platform in inventory is expected and no drift definition with used name
        runPermissionTest("manageDrift_U1", createAction + deleteAction, true);
        runPermissionTest("manageDrift_U2", createAction, false,"lacks MANAGE_DRIFT");
        runPermissionTest("manageDrift_U2", deleteAction, false,"lacks MANAGE_DRIFT");
    }

    @Test
    public void manageRepositoriesTest() {
    	initCase("manageRepositories_");
        String repoName = "repo1";
        String createAction = " RepoManager.createRepo(new Repo('"+repoName+"'));";
        String deleteAction = " deleteRepoByName('"+repoName+"');";
        String findAction = " ContentManager.findPackagesByCriteria(new PackageCriteria());";
        
        // user with permission
        runPermissionTest("manageRepositories_U1", deleteAction + createAction +  findAction, true);
        // user without permission can't delete a repo owned by other user
        runPermissionTest("manageRepositories_U2", deleteAction, false,"cannot delete repository");
        // user with permission
        runPermissionTest("manageRepositories_U1", deleteAction, true);
        runPermissionTest("manageRepositories_U2", createAction, true);
        // user without permission can create/delete repos owned by him
        runPermissionTest("manageRepositories_U2", deleteAction, true);
        
        runPermissionTest("manageRepositories_U2", findAction, false,"Only repository managers can search for packages across all repos");
    }
	
	@Test
	public void manageContentTest() {
		initCase("manageContent_");
		String repoName = "repo1";
		String createRepoAction = " createRepoForUser('"+repoName+"','manageContent_U1');";
		String deleteRepoAction = " deleteRepoByName('"+repoName+"');";
		String subscribeAction = " subscribeResourceToRepoByRepoName(resources.platforms()[0].id, '"+repoName+"');";
		String unsubscribeAction = " unSubscribeResourceToRepoByRepoName(resources.platforms()[0].id, '"+repoName+"');";
		
		// create a repository owned by tested user
		runPermissionTest("rhqadmin", deleteRepoAction + createRepoAction, true);
		
		// subscribe/unsubscribe as user with permission
		runPermissionTest("manageContent_U1", subscribeAction + unsubscribeAction, true);
		
		// subscribe/unsubscribe as user without permission
		runPermissionTest("manageContent_U2", subscribeAction, false,"permission to subscribe");
		runPermissionTest("manageContent_U2", unsubscribeAction, false,"permission to unsubscribe");
	}
}
