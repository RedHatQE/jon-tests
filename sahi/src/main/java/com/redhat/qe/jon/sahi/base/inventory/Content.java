package com.redhat.qe.jon.sahi.base.inventory;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class Content extends ResourceTab {

    public Content(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
    }

    @Override
    protected void navigate() {
        navigateUnderResource("Content");
    }

    // TODO
}
