package com.redhat.qe.jon.sahi.tests.alerts;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.OnAgentSahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.AdministrationPage;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionPageSnippet;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.AvailChangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.AvailDurationCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.Condition;
import com.redhat.qe.jon.sahi.base.inventory.groups.AllGroupsPage;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class ResourceGroupAlertDefinitionTest extends OnAgentSahiTestScript {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private static final String ALERT_DEF_NAME1 = "Res group test alert definition1";
    private static final String ALERT_DEF_NAME2 = "Res group test alert definition2";
    private static final String GROUP_NAME = "DynaGroup - Groups by platform ( Linux )";
    private static final String REPOSITORY_NAME = "Server CLI repository";
    // conditions
    private AvailChangeCondition availChangeCond;
    private AvailDurationCondition availDurCond;

    @Test
    public void createAlertDefinitionTest() {
        // create a new empty repository
        AdministrationPage.Repositories repos = new AdministrationPage.Repositories(sahiTasks);
        repos.navigate().deleteRepository(REPOSITORY_NAME);
        repos.createNewRepository(REPOSITORY_NAME);

        AllGroupsPage groupsPage = new AllGroupsPage(sahiTasks);
        groupsPage.navigate();
        groupsPage.selectGroup(GROUP_NAME);
        // TODO use better navigation when there is page object available for groups
        sahiTasks.cell("Alerts").click();
        sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Definitions"), "Definitions label",
                Timing.WAIT_TIME);

        AlertDefinitionPageSnippet alertDefPage = new AlertDefinitionPageSnippet(sahiTasks);
        alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME1);
        alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME2);

        // prepare different types of conditions
        availChangeCond = new AvailChangeCondition(sahiTasks);
        availChangeCond.setAvailChangeType("Goes down");

        availDurCond = new AvailDurationCondition(sahiTasks);
        availDurCond.setAvailState("Stays Down").setDuration("10");

        alertDefPage.createAlarmDefinition(ALERT_DEF_NAME1)
        .setGeneralProp("This is test alert definition", AlertDefinition.Priority.Medium, true)
        .addCondition(availChangeCond)
        .addCondition(availDurCond)
        .setConditionOperator(Condition.Operator.ALL)
        .addCliScriptNotification("rhqadmin", "rhqadmin", REPOSITORY_NAME,
                "/serverCliScripts/enableMetricOnPlatforms.js", "1.0")
        .addSystemUserNotification("rhqadmin")
        .save();

        groupsPage.navigate();
        groupsPage.selectGroup(GROUP_NAME);
        // TODO use better navigation when there is page object available for groups
        sahiTasks.cell("Alerts").click();
        sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Definitions"), "Definitions label",
                Timing.WAIT_TIME);

        alertDefPage.createAlarmDefinition(ALERT_DEF_NAME2)
        .setGeneralProp("mydes", AlertDefinition.Priority.Medium, true)
        .addCondition(availChangeCond)
        .addCondition(availDurCond)
        .setConditionOperator(Condition.Operator.ALL)
        .setDampening("Consecutive")
        .addCliScriptNotification("rhqadmin", "rhqadmin", REPOSITORY_NAME,
                "enableMetricOnPlatforms.js (rhqadmin:1.0)")
        .addSystemUserNotification("rhqadmin")
        .save();

        groupsPage.navigate();
        groupsPage.selectGroup(GROUP_NAME);
        // TODO use better navigation when there is page object available for groups
        sahiTasks.cell("Alerts").click();
        sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Definitions"), "Definitions label",
                Timing.WAIT_TIME);

        // checking first alarm definition
        ArrayList<AlertDefinition> alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME1);
        Assert.assertTrue(alertDefs.size() > 0, "Previously creted alert definition with name " +
                ALERT_DEF_NAME1 + ", was not found!!");
        assertAlerDefinition(alertDefs.get(0), "This is test alert definition", true,
                AlertDefinition.Priority.Medium, "", "N/A");

        // checking second alarm definition
        alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME2);
        Assert.assertTrue(alertDefs.size() > 0, "Previously creted alert definition with name " +
                ALERT_DEF_NAME2 + ", was not found!!");
        assertAlerDefinition(alertDefs.get(0), "mydes", true,
                AlertDefinition.Priority.Medium, "", "N/A");
    }

    @Test(dependsOnMethods="createAlertDefinitionTest")
    public void deleteAlertDefinitionTest() {
        AllGroupsPage groupsPage = new AllGroupsPage(sahiTasks);
        groupsPage.navigate();
        groupsPage.selectGroup(GROUP_NAME);
        // TODO use better navigation when there is page object available for groups
        sahiTasks.cell("Alerts").click();
        sahiTasks.waitForElementVisible(sahiTasks, sahiTasks.cell("Definitions"), "Definitions label",
                Timing.WAIT_TIME);

        AlertDefinitionPageSnippet alertDefPage = new AlertDefinitionPageSnippet(sahiTasks);
        alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME1);
        alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME2);

        // check that definitions are deleted
        ArrayList<AlertDefinition> alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME1);
        Assert.assertTrue(alertDefs.size() == 0,"Alert definition with name "+ALERT_DEF_NAME1+
                " was found, but it should be deleted!!");
        alertDefs = alertDefPage.getAlertDefinitions(ALERT_DEF_NAME2);
        Assert.assertTrue(alertDefs.size() == 0,"Alert definition with name "+ALERT_DEF_NAME2+
                " was found, but it should be deleted!!");
    }

    private void assertAlerDefinition(AlertDefinition alertDef, String expectedDescription, boolean expectedAvail,
            AlertDefinition.Priority priority, String parent, String protectedField) {
        log.fine("Checking alert definition with name " + alertDef.getName());
        Assert.assertTrue(alertDef.getDescription().equals(expectedDescription), "Expected description is: " +
                expectedDescription + ", but actual is: " + alertDef.getDescription());
        Assert.assertTrue(alertDef.isEnabled() == expectedAvail, "Expected availability is: " +
                expectedAvail + ", but actual is: " + alertDef.isEnabled());
        Assert.assertTrue(alertDef.getPriority().equals(priority), "Expected priority is: " +
                priority + ", but actual is: " + alertDef.getPriority());
    }

}
