package com.redhat.qe.jon.sahi.base.inventory;

import java.util.List;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public class Monitoring extends ResourceTab {

    public Monitoring(SahiTasks tasks, Resource resource) {
	super(tasks, resource);
    }

    @Override
    protected void navigate() {
	navigateUnderResource("Monitoring");
    }

    /**
     * selects <b>Tables</b> subtab and returns helper object
     * 
     * @return tables subtab
     */
    public Tables tables() {
	navigateUnderResource("Monitoring/Tables");
	return new Tables(tasks);
    }
    /**
     * selects <b>Traits</b> subtab and returns helper object
     * 
     * @return tables subtab
     */
    public Tables traits() {
	navigateUnderResource("Monitoring/Traits");
	return new Tables(tasks);
    }

    /**
     * selects <b>Schedules</b> subtab and returns helper object
     * 
     * @return schedules subtab
     */
    public Schedules schedules() {
	navigateUnderResource("Monitoring/Schedules");
	return new Schedules(tasks);
    }

    public static class Schedules {
	private final SahiTasks tasks;
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private Schedules(SahiTasks tasks) {
	    this.tasks = tasks;
	}

	private ElementStub getMetricCell(String metricName) {
	    List<ElementStub> tables = tasks.table("listTable")
		    .collectSimilar();
	    log.fine("listTable count = " + tables.size());
	    for (ElementStub table : tables) {
		if (table.isVisible()) {
		    ElementStub metricCell = tasks.cell(metricName).in(table);
		    if (metricCell.isVisible()) {
			return metricCell;
		    }
		}
	    }
	    throw new RuntimeException("Unable to find metric cell called ["
		    + metricName + "]");
	}

	/**
	 * sets collection interval for givem metric
	 * 
	 * @param metric
	 * @param interval
	 *            in minutes
	 */
	public void setInterval(String metric, String interval) {
	    tasks.xy(getMetricCell(metric), 3, 3).click();
	    ElementStub textbox = tasks.textbox("interval");
	    textbox.setValue(interval);
	    for (ElementStub e : tasks.cell("Set").collectSimilar()) {
		tasks.xy(e, 3, 3).click();
	    }
	}

	/**
	 * enables metric defined by name
	 * 
	 * @param metric
	 */
	public void enable(String metric) {
	    tasks.xy(getMetricCell(metric), 3, 3).click();
	    for (ElementStub disable : tasks.cell("Enable").collectSimilar()) {
		disable.click();
	    }
	}

	/**
	 * disables metric defined by name
	 * 
	 * @param metric
	 */
	public void disable(String metric) {
	    tasks.xy(getMetricCell(metric), 3, 3).click();
	    for (ElementStub disable : tasks.cell("Disable").collectSimilar()) {
		disable.click();
	    }
	}
    }

    public static class Tables {
	private final SahiTasks tasks;
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private Tables(SahiTasks tasks) {
	    this.tasks = tasks;
	}

	private ElementStub getMetricCell(String metricName) {
	    List<ElementStub> tables = tasks.table("listTable")
		    .collectSimilar();
	    log.fine("listTable count = " + tables.size());
	    for (ElementStub table : tables) {
		if (table.isVisible()) {
		    ElementStub metricCell = tasks.cell(metricName).in(table);
		    if (metricCell.isVisible()) {
			return metricCell;
		    }
		}
	    }
	    throw new RuntimeException("Unable to find metric cell called ["
		    + metricName + "]");
	}

	/**
	 * checks whether given metric row contains given value
	 * 
	 * @param metric
	 *            name of metric
	 * @param value
	 *            to be contained within metric row
	 * @return true if given value is contained in metric row
	 */
	public boolean containsMetricRowValue(String metric, String value) {
	    ElementStub metricCell = getMetricCell(metric);
	    ElementStub row = metricCell.parentNode("tr");
	    return row.getText().contains(value);
	}

	/**
	 * Retrieves measured value for given metric
	 * 
	 * @param metric
	 *            name of metric
	 * @param valueColumnIndex
	 *            index in metric table (note that 0 will return metric
	 *            name)
	 * @return String value found in given column for given metric
	 */
	public String getMetricRowValue(String metric, int valueColumnIndex) {
	    ElementStub metricCell = getMetricCell(metric);
	    ElementStub row = metricCell.parentNode("tr");
	    return tasks.cell(valueColumnIndex).in(row).getText();
	}
    }

}
