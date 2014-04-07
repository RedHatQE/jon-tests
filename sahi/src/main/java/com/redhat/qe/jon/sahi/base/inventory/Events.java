package com.redhat.qe.jon.sahi.base.inventory;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class Events extends ResourceTab {

    public Events(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
    }

    @Override
    protected void navigate() {
        navigateUnderResource("Events");
    }
    
    // TODO

}
