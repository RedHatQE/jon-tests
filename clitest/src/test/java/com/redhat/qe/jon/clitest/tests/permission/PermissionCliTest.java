package com.redhat.qe.jon.clitest.tests.permission;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;

public class PermissionCliTest extends CliEngine {

	@Test(groups={"blockedByBug-841625"})
	public void createChildResourceTest() {
		createJSRunner("permissions/testPermissions-CreateChildResource.js").
			run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void deleteChildResourceTest() {
		createJSRunner("permissions/testPermissions-DeleteChildResource.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageBundlesTest() {
		createJSRunner("permissions/testPermissions-ManageBundles.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageContentTest() {
		createJSRunner("permissions/testPermissions-ManageContent.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageDriftTest() {
		createJSRunner("permissions/testPermissions-ManageDrift.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageInventoryTest() {
		createJSRunner("permissions/testPermissions-ManageInventory.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageMeasurementsTest() {
		createJSRunner("permissions/testPermissions-ManageMeasurements.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageRepositoriesTest() {
		createJSRunner("permissions/testPermissions-ManageRepositories.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageSecurityTest() {
		createJSRunner("permissions/testPermissions-ManageSecurity.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void manageSettingsTest() {
		createJSRunner("permissions/testPermissions-ManageSettings.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourceConfigurationTest() {
		createJSRunner("permissions/testPermissions-ResourceConfiguration.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourceControlTest() {
		createJSRunner("permissions/testPermissions-ResourceControl.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourceGroupTest() {
		createJSRunner("permissions/testPermissions-ResourceGroup.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void resourcePermissionsInventoryTest() {
		createJSRunner("permissions/testPermissions-ResourcePermissionsInventory.js").
		run();
	}
	
	@Test(groups={"blockedByBug-841625"})
	public void viewUserTest() {
		createJSRunner("permissions/testPermissions-ViewUsers.js").
		run();
	}
}
