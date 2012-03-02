package com.redhat.qe.jon.sahi.base.inventory;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class Monitoring extends ResourceTab {

	public Monitoring(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		selectTab("Monitoring");
	}


}
