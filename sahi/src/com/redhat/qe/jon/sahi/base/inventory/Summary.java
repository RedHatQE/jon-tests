package com.redhat.qe.jon.sahi.base.inventory;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class Summary extends ResourceTab {

	public Summary(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		selectTab("Summary");
	}

}
