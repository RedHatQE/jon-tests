package com.redhat.qe.jon.sahi.base.postgresplugin;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;

import org.testng.Assert;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

public class PostgresPluginInventory extends PostgresPluginBase {

	SahiTasks tasks = null;
	String uniqueName = "qetest" + System.currentTimeMillis();
	
	public PostgresPluginInventory(SahiTasks sahiTasks) {
		super(sahiTasks);
		tasks = sahiTasks;
	}
	
	public void navigateToInventoryTab() {
		tasks.image("/Inventory_grey*/").click();
	}
	
	public void createDatabaseChildTable() {
		selectMenu("Create Child", "Table");
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.label("New Resource Name"), "New Resouce Name not visible.", Timing.TIME_5S));
		tasks.textbox("resourceName").setValue(uniqueName);
		tasks.cell("Next").click();
		Assert.assertTrue(tasks.waitForElementVisible(tasks, tasks.div("Table Name"), "Table Name not visible.", Timing.TIME_5S));
		tasks.textbox("tableName").setValue(uniqueName);
		tasks.cell("Finish").click();
	}

	public boolean wasCreateDatabaseChildTableSuccess() {
		boolean resourceAvailable = false;
		
		tasks.cell("Child Resources").click();
		tasks.waitFor(Timing.WAIT_TIME);
		
		long timeOut = System.currentTimeMillis() + (Timing.TIME_1M * 3);
		
		LinkedList<ChildResources> resources = null;
		
		do {
			tasks.cell("Refresh").click();
			//TODO: As this table has lot of rows, takes more time to read table. We have to use search box to narrow down to particular row. 
			//Right now enter key feature is not working with sahi. We should implement enter key with robot class in java. Once this feature get ready we have to add search box here.
			resources = getChildResourceAll();

			for (ChildResources row : resources) {
				if(row.getName().equals(uniqueName)) {
					_logger.info("Found Resource [" + uniqueName + "] Available - Availability [" + row.getAvailability() + "].\n");
					resourceAvailable = true;
					break;
			    }
			}
		} while (!resourceAvailable && (System.currentTimeMillis() < timeOut));
		
		return resourceAvailable;
	}
	
	public LinkedList<ChildResources> getChildResourceAll(){
		int tableCountOffset = 0;
		LinkedList<ChildResources> resources = new LinkedList<ChildResources>();
		ChildResources resource = null;
		for(HashMap<String, String> map : tasks.getRHQgwtTableFullDetails(ChildResources.tableName, tableCountOffset, ChildResources.childHistoryTableColumnsDatabase, ChildResources.statusImageToString)){
			resource = new ChildResources(ChildResources.childHistoryTableColumnsDatabase, map);
			resources.add(resource);
		}
		return resources;
	}
}
