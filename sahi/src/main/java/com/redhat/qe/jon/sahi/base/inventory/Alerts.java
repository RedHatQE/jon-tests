package com.redhat.qe.jon.sahi.base.inventory;



import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * represents <b>Alerts</b> Tab of given resource.
 * Creating instance of this class will navigate to resource and select <b>Alerts</b> Tab
 *
 * @author ahovsepy
 */
public class Alerts extends ResourceTab {


    public Alerts(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
    }

    @Override
    protected void navigate() {
        navigateUnderResource("Alerts/Definitions");
        raiseErrorIfCellDoesNotExist("Alerts");
    }

    /**
     * Creates new Alert Definition of given name, also creates in it <b>Notification:</b> 
     *
     * @param name of new AlertsDefinition
     * @return new alertsDefinition
     */
    public AlertsDefinition newAlertsDefinition(String name) {
        tasks.cell("New").click();
        return new AlertsDefinition(tasks, name);
    }

  

    public static class AlertsDefinition {
        private final SahiTasks tasks;
        private final String name;
        private final Editor editor;
//        private final Logger log = Logger.getLogger(this.getClass().getName());

        public AlertsDefinition(SahiTasks tasks, String name) {
            this.tasks = tasks;
            this.name = name;
            this.editor = new Editor(tasks);
            tasks.waitFor(Timing.WAIT_TIME);
            createAlertDefinition(this.name);
            
        }

        public void createAlertDefinition(String alertName) {
        	ElementStub save = tasks.cell("Save");
        	tasks.textbox("textItem").near(tasks.row("Name :")).setValue(alertName);
        	tasks.xy(save, 3, 3).click();
        }

        /**
         * asserts all required input fields have been filled
         */
        public void assertRequiredInputs() {
            getEditor().assertRequiredInputs();
        }

        public Editor getEditor() {
            return editor;
        }

        /**
         * clicks <b>Schedule</b> button to start operation
         */
        public void schedule() {
            tasks.cell("Schedule").click();
            tasks.waitFor(Timing.WAIT_TIME);
        }
    }

}
