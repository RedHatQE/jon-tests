package com.redhat.qe.jon.clitest.tests.permission;

import java.io.IOException;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class PermissionCliTest extends CliTest {

	@Test(groups={"blockedByBug-841625"})
	public void createChildResourceTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-CreateChildResource.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void deleteChildResourceTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-DeleteChildResource.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageBundlesTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageBundles.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageContentTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageContent.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageDriftTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageDrift.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageInventoryTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageInventory.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageMeasurementsTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageMeasurements.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageRepositoriesTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageRepositories.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageSecurityTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageSecurity.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageSettingsTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ManageSettings.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourceConfigurationTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ResourceConfiguration.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourceControlTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ResourceControl.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourceGroupTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ResourceGroup.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourcePermissionsInventoryTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ResourcePermissionsInventory.js");
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void viewUserTest() throws IOException, CliTasksException{
		runJSfile("permissions/testPermissions-ViewUsers.js");
	}
}
