package com.redhat.qe.jon.sahi.tests;

import org.testng.annotations.Test;
import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Oct 14, 2011
 */

public class AdministrationPages extends SahiTestScript{
	
	private static final String serversColumns 			= "Name, Mode, Endpoint Address, Nonsecure Port, Secure Port, Last Update Time, Affinity Group, Agent Count";
	private static final String agentsColumns 			= "Agent Name, Connected Server, Agent Bind Address, Agent Bind Port, Last Availability Report, Affinity Group";
	private static final String affinityGroupsColumns 	= "Name, Agent Count, Server Count";
	private static final String partitionEventsColumns 	= "Execution Time, Type, Details, Initiated By, Execution Status";
	private static final String contontSourcesColumns 	= "Name, Date Created, Date Modified, Lazy Load?, Download Mode, Description";
	private static final String repositoriesColumns 	= "Name, Date Created, Sync Status, Description";
	
	@Test (groups= "Administration JSP page(s) validation")
	public void pageServers(){
		Assert.assertTrue(sahiTasks.validatePage("Servers", "serversListForm:serversDataTable", serversColumns, 1, 1), "Servers JSP page availability check");
	}
	
	@ Test (groups= "Administration JSP page(s) validation")
	public void pageAgents(){
		Assert.assertTrue(sahiTasks.validatePage("Agents", "AgentsListForm:agentsDataTable", agentsColumns, 0, 1), "Agents JSP page availability check");
	}
	
	@ Test (groups= "Administration JSP page(s) validation")
	public void pageAffinityGroups(){
		Assert.assertTrue(sahiTasks.validatePage("Affinity Groups", "affinityGroupsForm:affinityGroupsDataTable", affinityGroupsColumns, 1, 0), "Affinity Groups JSP page availability check");
	}

	@ Test (groups= "Administration JSP page(s) validation")
	public void pagePartitionEvents(){
		Assert.assertTrue(sahiTasks.validatePage("Partition Events", "partitionEventsForm:partitionEventsDataTable", partitionEventsColumns, 1, 2), "Partition Events JSP page availability check");
	}
	
	@ Test (groups= "Administration JSP page(s) validation")
	public void pageContentSources(){
		Assert.assertTrue(sahiTasks.validatePage("Content Sources", "contentProvidersListForm:contentProvidersDataTable", contontSourcesColumns, 1, 0), "Content Sources JSP page availability check");
	}
	
	@ Test (groups= "Administration JSP page(s) validation")
	public void pageRepositories(){
		Assert.assertTrue(sahiTasks.validatePage("Repositories", "reposListForm:reposDataTable", repositoriesColumns, 1, 0), "Repositories JSP page availability check");
	}
}
