package com.redhat.qe.jon.sahi.base.postgresplugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.ExtendedSahi;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * @author mmahoney
 */

abstract class PostgresPluginBase {
	SahiTasks tasks = null;
	static ChildResources postgresServer = null;
	static Logger _logger = Logger.getLogger(ExtendedSahi.class.getName());

	PostgresPluginBase(SahiTasks sahiTasks) {
		tasks = sahiTasks;
	}
	
	public void navigateToPostgresBase() {
		Resource agent = null;
		if(postgresServer == null){
			_logger.fine("There is no server defined, Looking available servers list...");
			agent = new Resource(tasks,System.getProperty("jon.agent.name"));
		    agent.navigate();
		    tasks.cell("Postgres Servers").click();
		    LinkedList<ChildResources> postgresServers = getChildResourceAll();
		    _logger.fine("Number of Postgres Servers: "+postgresServers.size());
		    if(postgresServers.getFirst().getName() == null){
		    	new RuntimeException("Seems there is no data...");
		    }
		    _logger.fine("Postgres Server Details: "+postgresServers);
		    //Get First Postgres Server and do all the operations with it
		    postgresServer = postgresServers.getFirst();
		}else{
			_logger.fine("Postgres Server already defined, "+postgresServer);
		}
	    agent = new Resource(tasks,System.getProperty("jon.agent.name"), postgresServer.getName());
	    agent.navigate();
	    tasks.waitForElementVisible(tasks, tasks.cell("/Databases/"), "Databases Folder",Timing.TIME_5S);
	}
	
	public void navigateToDatabase(PostgresPluginDefinitions definitions) {
		tasks.cell("Databases").click();
		tasks.waitForElementVisible(tasks, tasks.cell(definitions.getDatabaseName()), "Database (" + definitions.getDatabaseName() + ")", Timing.TIME_10S);
		tasks.cell(definitions.getDatabaseName()).under(tasks.cell("Databases")).click();
		_logger.fine("Navigated to database["+definitions.getDatabaseName()+"] location...");
	}
	
	public void navigateToDatabaseTable(PostgresPluginDefinitions definitions) {
		navigateToDatabase(definitions);
		tasks.image("/folder_autogroup_closed/").near(tasks.cell("Tables")).click();
		_logger.fine("Navigated to database["+definitions.getDatabaseName()+"] Tables location...");
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
