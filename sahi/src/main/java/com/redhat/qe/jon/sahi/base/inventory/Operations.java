package com.redhat.qe.jon.sahi.base.inventory;


import com.redhat.qe.jon.sahi.base.editor.*;
import com.redhat.qe.jon.sahi.tasks.*;
import net.sf.sahi.client.*;
import org.testng.*;

import java.util.*;
import java.util.logging.*;

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
        raiseErrorIfCellDoesNotExist("Operations");
    }

    public void history() {
        navigateUnderResource("Operations/History");
        raiseErrorIfCellDoesNotExist("Operations");
    }

    /**
     * Creates new Operation of given name, also selects it in <b>Operation:</b> combo
     *
     * @param name of new Operation
     * @return new operation
     */
    public Operation newOperation(String name) {
        tasks.cell("New").click();
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
        getResource().operations().history();
        int timeout = 20 * Timing.TIME_1M;
        int time = 0;
        while (time < timeout && tasks.image("Operation_inprogress_16.png").in(tasks.div(opName + "[0]").parentNode("tr")).exists()) {
            time += Timing.TIME_10S;
            log.fine("Operation [" + opName + "] in progress, waiting " + Timing.toString(Timing.TIME_10S));
            tasks.waitFor(Timing.TIME_10S);
            getResource().operations().history();
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
            int buttons = tasks.image("close.png").countSimilar();
            tasks.xy(tasks.image("close.png[" + (buttons - 1) + "]"), 3, 3).click();
            if (message != null) {
                Assert.assertTrue(existsImage, "Operation [" + opName + "] result: " + succ + " errorMessage:\n" + message);
                return null;
            }
        }
        Assert.assertTrue(existsImage, "Operation [" + opName + "] result: " + succ);
        log.fine("Getting operation result");
        ElementStub linkToOperationResults = tasks.link(0).near(tasks.image(resultImage).in(tasks.div(opName + "[0]").parentNode("tr")));
        linkToOperationResults.click();
        if (!tasks.cell("Execution ID :").isVisible()) {
            log.fine("Operation results not opened correctly");
            tasks.xy(tasks.image(resultImage).in(tasks.div(opName + "[0]").parentNode("tr")), 3, 3).doubleClick();
        }
        log.finest("The property element: " + tasks.cell("Property").fetch());
        List<ElementStub> headerCells = tasks.cell("Property").collectSimilar();
        for (ElementStub el : headerCells) {
            log.finest(el.fetch());
        }
        if (headerCells.size() > 1) {
            Map<String, String> result = new HashMap<String, String>();
            ElementStub table = headerCells.get(headerCells.size() - 1).parentNode("table");
            log.finer("Table element is " + table.toString());
            List<ElementStub> rows = tasks.row("").in(table).collectSimilar();
            // starting with 3rd row, because 1st some shit and 2nd is table header
            // we also ignore last because sahi was failing
            log.fine("Have " + (rows.size() - 3) + " result rows");
            log.finest("The rows are: " + rows.toString());
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
            List<ElementStub> pickers = tasks.image("comboBoxPicker.png").collectSimilar();
            log.fine("Found " + pickers.size() + " comboboxes");
            for (ElementStub picker : pickers) {
                if (picker.isVisible()) {
                    log.fine("Clicking on " + picker.parentNode().fetch("innerHTML"));
                    tasks.xy(picker.parentNode(), 3, 3).click();
                    tasks.waitFor(Timing.TIME_1S);
                    ElementStub operation = tasks.row(op);
                    if (operation.exists()) {
                        tasks.xy(operation, 3, 3).click();
                        log.fine("Selected operation [" + op + "].");
                        return;
                    } else {
                        log.fine("Trying workaround with focused picker");
                        ElementStub focused = tasks.image("comboBoxPicker_Over.png");
                        if (focused.isVisible()) {
                            log.fine("Focused picker was visible, clicking...");
                            tasks.xy(focused, 3, 3).click();
                            operation = tasks.row(op);
                            if (operation.exists()) {
                                tasks.xy(operation, 3, 3).click();
                                log.fine("Selected operation [" + op + "].");
                                return;
                            }
                        }
                    }
                }
            }
            throw new RuntimeException("Unable to select operation [" + op + "] clicked on each visible combo, but operation did NOT pop up");
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
