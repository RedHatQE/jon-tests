package com.redhat.qe.jon.sahi.base.inventory;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class Drift extends ResourceTab {

    public Drift(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
    }

    @Override
    protected void navigate() {
        navigateUnderResource("Drift");
    }

    // TODO
}
