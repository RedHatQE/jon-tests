package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions;

import java.util.ArrayList;

import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.alerts.Alerts;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * This class represents alert definitions page.
 * 
 * @author fbrychta
 * 
 */
public class AlertDefinitionsPage extends Alerts {
    private AlertDefinitionPageSnippet alertDefSnippet = null;

    public AlertDefinitionsPage(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
        alertDefSnippet = new AlertDefinitionPageSnippet(tasks);
    }

    /**
     * Navigates to Alert/Definitions page.
     * 
     * @return this object
     */
    public AlertDefinitionsPage navigateTo() {
        navigateUnderResource("Alerts/Definitions");
        raiseErrorIfCellIsNotVisible("Definitions");

        return this;
    }

    public AlertDefinitionEditor createAlarmDefinition(String alertName) {
        return alertDefSnippet.createAlarmDefinition(alertName);
    }

    public ArrayList<AlertDefinition> getAlertDefinitions(String alertName) {
        return alertDefSnippet.getAlertDefinitions(alertName);
    }

    public AlertDefinitionEditor editAlertDefinition(String alertDefName) {
        return alertDefSnippet.editAlertDefinition(alertDefName);
    }

    public boolean deleteAlertDefinition(String alertDefName) {
        return alertDefSnippet.deleteAlertDefinition(alertDefName);
    }

    public boolean disableAlertDefinition(String alertDefName) {
        return alertDefSnippet.disableAlertDefinition(alertDefName);
    }

    public boolean enableAlertDefinition(String alertDefName) {
        return alertDefSnippet.enableAlertDefinition(alertDefName);
    }

}