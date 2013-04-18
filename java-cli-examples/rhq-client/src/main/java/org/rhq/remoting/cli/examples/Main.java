package org.rhq.remoting.cli.examples;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.rhq.core.domain.auth.Subject;
import org.rhq.core.domain.authz.Role;
import org.rhq.core.domain.bundle.BundleDeployment;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.configuration.ResourceConfigurationUpdate;
import org.rhq.core.domain.measurement.Availability;
import org.rhq.core.domain.measurement.MeasurementData;
import org.rhq.core.domain.operation.ResourceOperationHistory;
import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.enterprise.clientapi.RemoteClient;

/**
 * Main class just runs several examples
 * @author lzoubek@redhat.com
 *
 */
public class Main {

    public static final String separator = "------------------------------";
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	String host = System.getProperty("rhq.server.host");
	if (host == null) {
	    host = "localhost";
	    System.out.println("RHQ Server host was not specified via rhq.server.host property, defaulting to "+host);	
	}
	// we need to set this backdoor property to be able to connect 
	// with RHQ classes to JBoss ON server
	System.setProperty("rhq.client.version-check", "false");
	
	RemoteClient client = new Login().login(host, 7080, "rhqadmin", "rhqadmin");
	if (client == null) {
	    System.out.println("ERROR: Failed to login to RHQ server");
	    System.exit(1);
	}	
	
	
	String roleName = "testrole"+new Date().getTime();
	System.out.println(String.format("Creating sample role '%s'",roleName));
	Role role = new UsersRoles(client).createRole(roleName);
	
	String subjName = "testsubject"+new Date().getTime();
	System.out.println(String.format("Creating new subject '%s' with role",subjName));
	new UsersRoles(client).createSubject(subjName, "secure", role);
	
	System.out.println("Login with new subject");
	RemoteClient cl = new Login().login(host,7080, subjName,"secure");
	System.out.println("Subject logged in, RemoteClient "+cl);
	
	
	System.out.println("Listing discovery queue");
	System.out.println(separator);
	Resource[] discoveryQueue = new ResourceDiscovery(client).discoveryQueue();
	for (Resource resource : discoveryQueue) {
	    System.out.println(resource);
	}
	System.out.println(separator);
	
	if (discoveryQueue.length > 0) {
	    System.out.println("Importing all resources from discovery queue");
	    new ResourceDiscovery(client).importAllResources();
	    System.out.println("Waiting 5s...");
	    Thread.currentThread().join(5000);
	}
	
	System.out.println("Looking up RHQ Agent resources..");
	Resource[] agents = new ResourceDiscovery(client).findResources("RHQ Agent");
	System.out.println(String.format("Found %d resources",agents.length));
	if (agents.length<1) {
	    System.out.println("No agent resources found, please connect agent to server");
	    System.exit(0);
	}
	Resource agent = agents[0];
	System.out.println("- "+agent);
	System.out.println("Looking up agent child resources");
	System.out.println(separator);
	for (Resource child : new ResourceDiscovery(client).findChildResources(agent)) {
	    System.out.println("- "+child);
	}
	System.out.println(separator);
	
	System.out.println("Updating agent's configuration property rhq.agent.server.alias to 'test'");	
	ResourceConfigurationUpdate status = new ResourceConfiguration(client).updateResourceConfiguration(agent, "rhq.agent.server.alias", "test");
	if (status == null) {
	    System.out.println("Configuration NOT updated, server indicated no configuration change");
	}
	else {
	    System.out.println("Configuration update done : " + status.getStatus());
	}
	// System.out.println(separator);
	// new ResourceConfiguration(client).printConfiguration(agent);
	// System.out.println(separator);
	
	System.out.println("Updating agent's configuration property rhq.agent.server.alias back to 'rhqserver'");	
	status = new ResourceConfiguration(client).updateResourceConfiguration(agent, "rhq.agent.server.alias", "rhqserver");
	if (status == null) {
	    System.out.println("Configuration NOT updated, server indicated no configuration change");
	}
	else {
	    System.out.println("Configuration update done : " + status.getStatus());
	}
	// System.out.println(separator);
	// new ResourceConfiguration(client).printConfiguration(agent);
	// System.out.println(separator);
	
	System.out.println("Executing command on agent 'avail -f'");
	Configuration cmdResult = new ResourceOperation(client).runRHQAgentCommand(agent, "avail -f");
	System.out.println("Operation finished with outcome");
	System.out.println(separator);
	PrintUtil.printConfiguration(cmdResult);
	System.out.println(separator);
	
	
	System.out.println("Executing executeAvailabilityScan operation on agent");
	Configuration input = new Configuration();
	input.put(new PropertySimple("changesOnly", "true"));
	ResourceOperationHistory opResult = new ResourceOperation(client).runResourceOperation(agent, "executeAvailabilityScan", input);
	System.out.println("Operation finished with status : "+opResult.getStatus());
	System.out.println(separator);
	PrintUtil.printConfiguration(opResult.getResults());
	System.out.println(separator);
	
	
	System.out.println("Delete resource groups called 'My agents'");
	boolean groupDeleted = new ResourceGroups(client).deleteGroup("My agents");
	if (groupDeleted) {
	    System.out.println("Group was deleted");
	}
	else {
	    System.out.println("Group was NOT deleted, it may have not existed");
	}
	System.out.println("Create resource groups called 'My agents'");
	ResourceGroup group = new ResourceGroups(client).createGroup("My agents", agents, false);
	System.out.println("List all group resources ");
	System.out.println(separator);
	for (Resource child : new ResourceDiscovery(client).findResourcesForGroup(group)) {
	    System.out.println("- "+child);
	}
	System.out.println(separator);	
	
	
	System.out.println("Looking up Linux platforms..");
	Resource[] linuxes = new ResourceDiscovery(client).findResources("Linux");
	System.out.println(String.format("Found %d resources",linuxes.length));
	if (linuxes.length<1) {
	    System.out.println("No Linux platform resources found");
	    System.exit(0);
	}	
	Resource platform = linuxes[0];
	System.out.println("- "+platform);
	
	System.out.println("Retrieving current availability for platform");
	Availability availability = new ResourceMonitoring(client).getCurrentAvailability(platform);
	System.out.println("Result : "+availability);
	
	System.out.println("Retrieving live data for 'Free Memory' metric");
	Set<MeasurementData> data = new ResourceMonitoring(client).getLiveMetricData(platform, "Free Memory");	
	System.out.println(separator);
        for (MeasurementData md : data) {
            System.out.println(md);
        }
        System.out.println(separator);
        
        System.out.println("Bundle deployment:");
        System.out.println("Create a resource group 'bundle-test' containing our Linux platform");
	new ResourceGroups(client).deleteGroup("bundle-test");
	ResourceGroup bundleTarget = new ResourceGroups(client).createGroup("bundle-test",new Resource[] {platform},false);
	System.out.println("Deploy a sample bundle to 'bundle-test' group");
	BundleDeployment deployment = new DeployBundle(client).deployBundle(
		new File(Main.class.getResource("/bundle.zip").getFile()), 
		bundleTarget, 
		new Configuration(), // our bundle does not require any input parameters 
		"bundletest", 
		"Root File System",
		"/tmp");
	System.out.println("Bundle deployment finished with status : "+deployment.getStatus());
	
    }

}