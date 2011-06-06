package com.redhat.qe.jon.sahi.tasks;

import com.redhat.qe.auto.sahi.ExtendedSahi;

public class SahiTasks extends ExtendedSahi {
	public SahiTasks(String browserPath, String browserName, String browserOpt, String sahiBaseDir, String sahiUserdataDir) {
		super(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
	}

	// ***************************************************************************
	// Inventory
	// ***************************************************************************
	public void createGroup(String groupName, String groupDesc) {
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("All Groups").click();
		this.cell("New").click();
		this.textbox("name").setValue(groupName);
		this.textarea("description").setValue(groupDesc);
		this.cell("Next").click();
		this.cell("Finish").click();
	}

	public void deleteGroup(String groupName) {
		this.link("Inventory").click();
		this.waitFor(5000);
		this.cell("All Groups").click();
		this.div(groupName).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}

	// ***************************************************************************
	// Bundle
	// ***************************************************************************
	public void createBundleURL(String bundleURL) {
		this.link("Bundles").click();
		this.cell("New").click();
		this.radio("URL").click();
		this.textbox("url").setValue(bundleURL);
		this.cell("Next").click();
		this.cell("Next").click();
		this.cell("Finish").click();
	}

	public void deleteBundle(String bundleName) {
		this.link("Bundles").click();
		this.waitFor(5000);
		this.div(bundleName).click();
		this.cell("Delete").click();
		this.cell("Yes").click();
	}
}
