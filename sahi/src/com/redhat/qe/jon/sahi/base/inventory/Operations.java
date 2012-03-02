package com.redhat.qe.jon.sahi.base.inventory;


import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
/**
 * represents <b>Operations</b> Tab of given resource.
 * Creating instance of this class will navigate to resource and select <b>Operations</b> Tab 
 * @author lzoubek
 *
 */
public class Operations extends ResourceTab {

	
	public Operations(SahiTasks tasks, Resource resource) {
		super(tasks, resource);
	}

	@Override
	protected void navigate() {
		selectTab("Operations","Schedules");
	}
	/**
	 * Creates new Operation of given name, also selects it in <b>Operation:</b> combo
	 * @param name of new Operation
	 * @return
	 */
	public Operation newOperation(String name) {
		return new Operation(tasks, name);
	}
	/**
	 * asserts operation result, waits until operation is either success or failure.
	 * @param op operation 
	 * @param success if true, success is expected, otherwise failure is expected
	 */
	public void assertOperationResult(Operation op, boolean success) {
		String opName = op.name;
		String resultImage = "Operation_failed_16.png";
    	String succ="Failed";
    	if (success) {
    		resultImage = "Operation_ok_16.png";
    		succ="Success";
    	}		
		log.fine("Asserting operation ["+opName+"] result, expecting "+succ);
    	getResource().summary();
    	int timeout = 10 * Timing.TIME_1M;
    	int time = 0;
    	while (time<timeout && tasks.image("Operation_inprogress_16.png").in(tasks.div(opName+"[0]").parentNode("tr")).exists()) {
    		time+=Timing.TIME_10S;
    		log.fine("Operation ["+opName+"] in progress, waiting "+Timing.toString(Timing.TIME_10S));
    		tasks.waitFor(Timing.TIME_10S);
    		getResource().summary(); 
    	}    	    	
    	Assert.assertTrue(tasks.image(resultImage).in(tasks.div(opName+"[0]").parentNode("tr")).exists(),"Operation ["+opName+"] result: "+succ);
    }
	
	public static class Operation {
		private final SahiTasks tasks;
		private final String name;
		
		public Operation(SahiTasks tasks, String name) {
			this.tasks = tasks;
			this.name = name;
			tasks.cell("New").click();
			tasks.selectComboBoxes("selectItemText-->"+this.name);
		}
		/**
		 * asserts all required input fields have been filled
		 */
		public void assertRequiredInputs() {
			Assert.assertTrue(!tasks.image("exclamation.png").exists(), "All required inputs were provided");
		}
		/**
		 * clicks <b>Schedule</b> button to start operation
		 */
		public void schedule() {
			tasks.cell("Schedule").click();
		}
	}

}
