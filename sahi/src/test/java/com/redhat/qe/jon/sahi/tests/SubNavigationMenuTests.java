package com.redhat.qe.jon.sahi.tests;

import com.redhat.qe.jon.sahi.base.SahiTestScript;

import org.testng.annotations.Test;

/**
 * @author ahovsepy (Armine Hovsepyan)
 * Jan 21, 2013
 */

public class SubNavigationMenuTests extends SahiTestScript {

	@Test(groups = "SubnavigationMenuTests")
	public void subNavigationMenuLinksExist() {
		sahiTasks.checkPlatform();
		sahiTasks.scondLevelMandatoryMenuLinksExist();
	}

	//@Test(groups = "SubnavigationMenuTests")
	public void checkAutoGroupSubnavigation() {
		sahiTasks.checkAutoGroupResourceMenues();
	}
}