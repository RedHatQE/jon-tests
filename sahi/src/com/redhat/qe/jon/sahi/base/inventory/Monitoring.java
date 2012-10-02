package com.redhat.qe.jon.sahi.base.inventory;

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
	 * @return tables subtab
	 */
	public Tables tables() {
		navigateUnderResource("Monitoring/Tables");
		return new Tables(tasks);
	}
	/**
	 * selects <b>Schedules</b> subtab and returns helper object
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
	    	int tables = tasks.table("listTable").countSimilar();
	    	log.fine("listTable count = "+tables);
	    	return tasks.cell(metricName).in(tasks.table("listTable["+(tables-1)+"]"));
	    }
	    /**
	     * sets collection interval for givem metric
	     * @param metric
	     * @param interval in minutes
	     */
	    public void setInterval(String metric, String interval) {                       
	        tasks.xy(getMetricCell(metric),3,3).click();
	        ElementStub textbox = tasks.textbox("interval");
	        textbox.setValue(interval);
	        for (ElementStub e : tasks.cell("Set").collectSimilar()) {
	        	tasks.xy(e,3,3).click();
	        }	        
	    }
	    /**
	     * enables metric defined by name
	     * @param metric
	     */
	    public void enable(String metric) {
	    	tasks.xy(getMetricCell(metric),3,3).click();
	        for (ElementStub disable : tasks.cell("Enable").collectSimilar()) {
	        	disable.click();
	        }	    	
	    }
	    /**
	     * disables metric defined by name
	     * @param metric
	     */
	    public void disable(String metric) {
	    	tasks.xy(getMetricCell(metric),3,3).click();
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
		public boolean containsMetricRowValue(String metric, String value) {
			for (ElementStub table : tasks.table("listTable").collectSimilar()) {
				for (ElementStub es : tasks.cell(metric).in(table).collectSimilar()) {
					ElementStub row = es.parentNode("tr");
					if (row.getText().contains(value)) {
						return true;
					}
				}
			}
			return false;
		}
	}

}
