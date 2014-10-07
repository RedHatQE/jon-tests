package com.redhat.qe.jon.sahi.base.inventory;


import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

import net.sf.sahi.client.ElementStub;

import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * represents <b>Operations</b> Tab of given resource.
 * Creating instance of this class will navigate to resource and select <b>Operations</b> Tab
 *
 * @author lzoubek
 */
public class Operations extends ResourceTab {


    public Operations(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
    }

    @Override
    protected void navigate() {
        navigateUnderResource("Operations/Schedules");
        if (!tasks.cell("Operations").exists() || !tasks.cell("Schedules").exists()) {
            log.fine("Sometimes the browser becomes stuck when navigating to other page, lets try one more time");
            navigateUnderResource("Operations/Schedules");
        }
        raiseErrorIfCellDoesNotExist("Operations");
        raiseErrorIfCellDoesNotExist("Schedules");
    }

    public void history() {
        navigateUnderResource("Operations/History");
        if (!tasks.cell("Operations").exists() || !tasks.cell("History").exists()) {
            log.fine("Sometimes the browser becomes stuck when navigating to other page, lets try one more time");
            navigateUnderResource("Operations/History");
        }
        raiseErrorIfCellDoesNotExist("Operations");
        raiseErrorIfCellDoesNotExist("History");
    }

    /**
     * Creates new Operation of given name, also selects it in <b>Operation:</b> combo
     *
     * @param name of new Operation
     * @return new operation
     */
    public Operation newOperation(String name) {
        // try one more time to navigate to operations page if you don't see the button
        // (sometimes it failed to correctly load the page and this should improve stability)
        if (!tasks.cell("New").exists()) {
            tasks.reloadPage();
            this.navigate();
        }
        tasks.cell("New").click();
        if (!tasks.cell("Create New Operation Schedule").isVisible()) {
            tasks.waitFor(Timing.TIME_1S);
            if (tasks.cell("New").exists()) {
                log.fine("Trying one more time clicking New button");
                tasks.cell("New").click();
            }
        }
        if (!tasks.cell("Create New Operation Schedule").isVisible()) {
            tasks.waitFor(Timing.TIME_1S);
        }
        if (!tasks.cell("Create New Operation Schedule").isVisible()) {
            tasks.reloadPage();
        }
        return new Operation(tasks, name);
    }



    /**
     * asserts operation result, waits until operation is either success or failure.
     *
     * @param op      operation
     * @param success if true, success is expected, otherwise failure is expected
     * @return result map returned by this operation if operation succeeded, otherwise null.
     *         In this map, keys are result properties and values are result values
     */
    public Map<String, String> assertOperationResult(Operation op, boolean success) {
        String opName = op.name;
        String resultImage = "Operation_failed_16.png";
        String succ = "Failed";
        if (success) {
            resultImage = "Operation_ok_16.png";
            succ = "Success";
        }
        log.fine("Asserting operation [" + opName + "] result, expecting " + succ);
        final String NOT_YET_STARTED_MESSAGE = "not yet started";
        this.history();

        tasks.reloadPage();

        if (tasks.div(NOT_YET_STARTED_MESSAGE).exists() || tasks.cell(NOT_YET_STARTED_MESSAGE).exists()) {
            log.finer("Page contains " + NOT_YET_STARTED_MESSAGE);
        }
        int allOperationStartedTimeout = 2*Timing.TIME_1M;
        while ((tasks.div(NOT_YET_STARTED_MESSAGE).exists() || tasks.cell(NOT_YET_STARTED_MESSAGE).exists())
                && allOperationStartedTimeout > 0) {
            log.finer("Operation not yet started, remaining waiting time "+ Timing.toString(allOperationStartedTimeout));
            allOperationStartedTimeout -= Timing.WAIT_TIME;
            tasks.waitFor(Timing.WAIT_TIME);
            tasks.cell("Refresh").click();
        }

        log.finer("Sorting operations by Date Submitted");
        // sort by Date Submitted
        tasks.cell("Date Submitted").doubleClick();
        tasks.waitFor(Timing.WAIT_TIME);
        tasks.cell("Date Submitted").doubleClick();
        tasks.waitFor(Timing.WAIT_TIME);
        int timeout = 20 * Timing.TIME_1M;
        int time = 0;
        while (time < timeout && tasks.image("Operation_inprogress_16.png").in(tasks.div(opName + "[0]").parentNode("tr")).exists()) {
            time += Timing.TIME_10S;
            log.fine("Operation [" + opName + "] in progress, waiting " + Timing.toString(Timing.TIME_10S));
            tasks.waitFor(Timing.TIME_10S);
            tasks.cell("Refresh").click();
//            getResource().operations().history();  // it would have to be sorted again
        }
        if (tasks.image("Operation_inprogress_16.png").in(tasks.div(opName + "[0]").parentNode("tr")).exists()) {
            log.info("Operation [" + opName + "] did NOT finish after " + Timing.toString(time) + "!!!");
            Assert.assertEquals(!success, success, "Operation [" + opName + "] result: " + succ);
        } else {
            log.info("Operation [" + opName + "] finished after " + Timing.toString(time));
        }
        boolean existsImage = tasks.image(resultImage).in(tasks.div(opName + "[0]").parentNode("tr")).exists();
        // when operation failed and success was expected, let's get operation error message
        if (!existsImage && success) {
            log.info("Retrieving the error message as it was expected that operation ends successfully but it didn't");
            String message = null;
            tasks.xy(tasks.image("Operation_failed_16.png").in(tasks.div(opName + "[0]").parentNode("tr")), 3, 3).click();
            if (tasks.preformatted("").exists()) {
                message = tasks.preformatted("").getText();
            }
            tasks.waitFor(Timing.WAIT_TIME);
            String imgName = "close.png";
            int buttons = tasks.image(imgName).countSimilar();
            // patternFly change
            if(buttons == 0){
                imgName = "close.gif";
                buttons = tasks.image(imgName).countSimilar();
            }
            tasks.xy(tasks.image(imgName + "[" + (buttons - 1) + "]"), 3, 3).click();
            if (message != null) {
                Assert.assertTrue(existsImage, "Operation [" + opName + "] result: " + succ + " errorMessage:\n" + message);
                return null;
            }
        }
        if (tasks.cell(NOT_YET_STARTED_MESSAGE).in(tasks.div(opName).parentNode("tr")).isVisible()) {
            log.warning("There exist to be some not yet started operation");
        }
        Assert.assertTrue(existsImage, "Operation [" + opName + "] result: " + succ);
        log.fine("Getting operation result");
        ElementStub linkToOperationResults = tasks.link(0).near(tasks.image(resultImage).in(tasks.div(opName + "[0]").parentNode("tr")));
        linkToOperationResults.click();
        if (!tasks.cell("Execution ID :").isVisible()) {
            log.fine("Operation results not opened correctly");
            tasks.xy(tasks.image(resultImage).in(tasks.div(opName + "[0]").parentNode("tr")), 3, 3).doubleClick();
        }
        tasks.reloadPage();
        log.finest("The property element: " + tasks.cell("Property").fetch());
        List<ElementStub> headerCells = tasks.cell("Property").collectSimilar();
        for (ElementStub el : headerCells) {
            log.finest("Header cell: " + el.fetch());
        }
        if (headerCells.size() > 0) {
            Map<String, String> result = new HashMap<String, String>();
            ElementStub table = headerCells.get(headerCells.size() - 1).parentNode("table");
            log.finer("Table element is " + table.toString());
            List<ElementStub> rows = tasks.row("").in(table).collectSimilar();
            // starting with 3rd row, because 1st some shit and 2nd is table header
            // we also ignore last because sahi was failing
            log.fine("Have " + (rows.size() - 2) + " result rows");
            log.finest("The rows are: " + rows.toString());
            log.finest("The last row first column: " + tasks.cell(0).in(rows.get(rows.size()-1)).getText());
            for (int i = 2; i < rows.size(); i++) {
                ElementStub row = rows.get(i);
                ElementStub key = tasks.cell(0).in(row);
                ElementStub value = tasks.cell(2).in(row);
                if (key.exists() && value.exists()) {
                    log.fine("Found result property [" + key.getText() + "]");
                    result.put(key.getText(), value.getText());
                } else {
                    log.warning("Missing key or value column in the results table - probably caused by nonstandard result output");
                }
            }
            return result;
        }
        log.fine("Result table not found");
        return null;
    }



    public static class Operation {
        private final SahiTasks tasks;
        private final String name;
        private final Editor editor;
        private final Logger log = Logger.getLogger(this.getClass().getName());

        public Operation(SahiTasks tasks, String name) {
            this.tasks = tasks;
            this.name = name;
            this.editor = new Editor(tasks);
            tasks.waitFor(Timing.WAIT_TIME);
            selectOperation(this.name);
        }

        public void selectOperation(String op) {
            getEditor().selectCombo(op);
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
            String msg = "/Operation Schedule created.*/";
            Assert.assertTrue(tasks.waitForAnyElementsToBecomeVisible(tasks,
                    new ElementStub[]{tasks.cell(msg),tasks.div(msg)},
                    "Successful message", 2*Timing.WAIT_TIME)
                    ,"Operation was scheduled");
        }
    }

}
