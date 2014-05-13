package com.redhat.qe.jon.sahi.tests.alerts;

import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.OnAgentSahiTestScript;
import com.redhat.qe.jon.sahi.base.administration.AdministrationPage;
import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionsPage;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.AvailChangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.AvailDurationCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.Condition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.EventDetectionCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasAbsValTresholdCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasBaselineTresholdCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasValueChangeCondition;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.MeasValueRangeCondition;
import com.redhat.qe.tools.checklog.CheckLog;
import com.redhat.qe.tools.checklog.LogFile;

public class Bz1093265Test extends OnAgentSahiTestScript {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    private Resource agent;
    private static final String ALERT_DEF_NAME1 = "test alert definition1";
    private static final String REPOSITORY_NAME = "Server CLI repository";

    @Test
    public void createAlarmDefTest(){
        agent = new Resource(sahiTasks,agentName,"RHQ Agent");
        // create a new empty repository
        AdministrationPage.Repositories repos = new AdministrationPage.Repositories(sahiTasks);
        repos.navigate().deleteRepository(REPOSITORY_NAME);
        repos.createNewRepository(REPOSITORY_NAME);
        AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();
        
        alertDefPage.deleteAlertDefinition(ALERT_DEF_NAME1);

        // prepare different types of conditions
        AvailChangeCondition availChangeCond = new AvailChangeCondition(sahiTasks);
        availChangeCond.setAvailChangeType("Goes unknown");
        
        AvailDurationCondition availDurCond = new AvailDurationCondition(sahiTasks);
        availDurCond.setAvailState("Stays Not Up").setDuration("10");
        
        EventDetectionCondition eventDetectionCond = new EventDetectionCondition(sahiTasks);
        eventDetectionCond.setEventSeverity("Warn").setRegularExpression("10");
        
        MeasAbsValTresholdCondition measAbsValTresholdCond = new MeasAbsValTresholdCondition(sahiTasks);
        measAbsValTresholdCond.setMetric("Number of Commands Sent but Failed").
            setComparator(MeasAbsValTresholdCondition.Comparator.EQUAL).
            setMetricValue("12");
        
        MeasBaselineTresholdCondition measBaselineTresholdCond = new MeasBaselineTresholdCondition(sahiTasks);
        measBaselineTresholdCond.setComparator(MeasAbsValTresholdCondition.Comparator.LESS_THEN);
        measBaselineTresholdCond.setMetric("Up Time").
            setBaselinePercentage("12").
            setBaseline(MeasBaselineTresholdCondition.Baseline.Maximum);
        
        MeasValueChangeCondition measValChangeCond = new MeasValueChangeCondition(sahiTasks);
        measValChangeCond.setMetric("Number of Commands Received but Failed per Minute");
        
        MeasValueRangeCondition measValueRangeCond = new MeasValueRangeCondition(sahiTasks);
        measValueRangeCond.setMetric("Number of Agent Restarts").
            setComparator(MeasValueRangeCondition.RangeComparator.OUT_EXCLUSIVE).
            setLowValue("12").
            setHighValue("20");

        // add a new alert definitions 
        alertDefPage.createAlarmDefinition(ALERT_DEF_NAME1).
            setGeneralProp("This is test alert definition",AlertDefinition.Priority.High,true).
            addCondition(availChangeCond).
            addCondition(availDurCond).
            addCondition(eventDetectionCond).
            addCondition(measAbsValTresholdCond).
            addCondition(measBaselineTresholdCond).
            addCondition(measValChangeCond).
            addCondition(measValueRangeCond).
            setConditionOperator(Condition.Operator.ALL).
            setDampening("Consecutive").
            addCliScriptNotification("rhqadmin", "rhqadmin", REPOSITORY_NAME, "/serverCliScripts/enableMetricOnPlatforms.js", "1.0").
            save();
    }

    @CheckLog(
            logs = {
                    @LogFile(id = "server", keyfile = "${jon.agent.privatekey}", user = "${jon.server.user}", 
                            pass = "${jon.server.password}", host = "${jon.server.host}", 
                            logFile = "${jon.server.home}/logs/server.log")
                    }
            )
    @Test(dependsOnMethods="createAlarmDefTest")
    public void quicklyEditAlarmDefTest(){
        // we can't use abstraction layer here because we want to be quick to reproduce the issue
        Editor editor = new Editor(sahiTasks);
        AlertDefinitionsPage alertDefPage = agent.alerts().alertDefinitionsPage();
        alertDefPage.editAlertDefinition(ALERT_DEF_NAME1);

        String description = "descr";
        editor.setTextInTextAreaNearCell("/textItem.*/", description, "Description :");
        sahiTasks.cell("Save").click();

        for(int i=0;i<30;i++){
            log.finer("Editing " +ALERT_DEF_NAME1);
            description = description + i;
            sahiTasks.cell("Edit").click();
            sahiTasks.textarea("/textItem.*/").near(sahiTasks.cell("Description :")).setValue(description);;
            sahiTasks.cell("Save").click();
        }
    }
}
