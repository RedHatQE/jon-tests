package org.rhq.remoting.cli.examples;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.jboss.logging.Logger;
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

    private static final Logger log = Logger.getLogger(Main.class);
    public static final String separator = "------------------------------";
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	String host = System.getProperty("rhq.server.host");
	if (host == null) {
	    host = "localhost";
	    log.info("RHQ Server host was not specified via rhq.server.host property, defaulting to "+host);
	}
	// we need to set this backdoor property to be able to connect 
	// with RHQ classes to JBoss ON server
	System.setProperty("rhq.client.version-check", "false");
	
	RemoteClient client = new Login().login(host, 7080, "rhqadmin", "rhqadmin");
	if (client == null) {
	    log.error("ERROR: Failed to login to RHQ server");
	    System.exit(1);
	}	
	
	
	String roleName = "testrole"+new Date().getTime();
	log.info(String.format("Creating sample role '%s'",roleName));
	Role role = new UsersRoles(client).createRole(roleName);
	
	String subjName = "testsubject"+new Date().getTime();
	log.info(String.format("Creating new subject '%s' with role",subjName));
	new UsersRoles(client).createSubject(subjName, "secure", role);
	
	log.info("Login with new subject");
	RemoteClient cl = new Login().login(host,7080, subjName,"secure");
	log.info("Subject logged in, RemoteClient "+cl);
	
	
	log.info("Listing discovery queue");
	log.info(separator);
	Resource[] discoveryQueue = new ResourceDiscovery(client).discoveryQueue();
	for (Resource resource : discoveryQueue) {
	    log.info(resource);
	}
	log.info(separator);
	
	if (discoveryQueue.length > 0) {
	    log.info("Importing all resources from discovery queue");
	    new ResourceDiscovery(client).importAllResources();
	    log.info("Waiting 5s...");
	    Thread.currentThread().join(5000);
	}
	
	log.info("Looking up RHQ Agent resources..");
	Resource[] agents = new ResourceDiscovery(client).findResources("RHQ Agent");
	log.info(String.format("Found %d resources",agents.length));
	if (agents.length<1) {
	    log.info("No agent resources found, please connect agent to server");
	    System.exit(0);
	}
	Resource agent = agents[0];
	log.info("- "+agent);
	log.info("Looking up agent child resources");
	log.info(separator);
	for (Resource child : new ResourceDiscovery(client).findChildResources(agent)) {
	    log.info("- "+child);
	}
	log.info(separator);
	
	log.info("Updating agent's configuration property rhq.agent.server.alias to 'test'");	
	ResourceConfigurationUpdate status = new ResourceConfiguration(client).updateResourceConfiguration(agent, "rhq.agent.server.alias", "test");
	if (status == null) {
	    log.info("Configuration NOT updated, server indicated no configuration change");
	}
	else {
	    log.info("Configuration update done : " + status.getStatus());
	}
	// log.info(separator);
	// new ResourceConfiguration(client).printConfiguration(agent);
	// log.info(separator);
	
	log.info("Updating agent's configuration property rhq.agent.server.alias back to 'rhqserver'");	
	status = new ResourceConfiguration(client).updateResourceConfiguration(agent, "rhq.agent.server.alias", "rhqserver");
	if (status == null) {
	    log.info("Configuration NOT updated, server indicated no configuration change");
	}
	else {
	    log.info("Configuration update done : " + status.getStatus());
	}
	// log.info(separator);
	// new ResourceConfiguration(client).printConfiguration(agent);
	// log.info(separator);
	
	log.info("Executing command on agent 'avail -f'");
	Configuration cmdResult = new ResourceOperation(client).runRHQAgentCommand(agent, "avail -f");
	log.info("Operation finished with outcome");
	log.info(separator);
	PrintUtil.printConfiguration(cmdResult);
	log.info(separator);
	
	
	log.info("Executing executeAvailabilityScan operation on agent");
	Configuration input = new Configuration();
	input.put(new PropertySimple("changesOnly", "true"));
	ResourceOperationHistory opResult = new ResourceOperation(client).runResourceOperation(agent, "executeAvailabilityScan", input);
	log.info("Operation finished with status : "+opResult.getStatus());
	log.info(separator);
	PrintUtil.printConfiguration(opResult.getResults());
	log.info(separator);
	
	
	log.info("Delete resource groups called 'My agents'");
	boolean groupDeleted = new ResourceGroups(client).deleteGroup("My agents");
	if (groupDeleted) {
	    log.info("Group was deleted");
	}
	else {
	    log.info("Group was NOT deleted, it may have not existed");
	}
	log.info("Create resource groups called 'My agents'");
	ResourceGroup group = new ResourceGroups(client).createGroup("My agents", agents, false);
	log.info("List all group resources ");
	log.info(separator);
	for (Resource child : new ResourceDiscovery(client).findResourcesForGroup(group)) {
	    log.info("- "+child);
	}
	log.info(separator);	
	
	
	log.info("Looking up Linux platforms..");
	Resource[] linuxes = new ResourceDiscovery(client).findResources("Linux");
	log.info(String.format("Found %d resources",linuxes.length));
	if (linuxes.length<1) {
	    log.info("No Linux platform resources found");
	    System.exit(0);
	}	
	Resource platform = linuxes[0];
	log.info("- "+platform);
	
	log.info("Retrieving current availability for platform");
	Availability availability = new ResourceMonitoring(client).getCurrentAvailability(platform);
	log.info("Result : "+availability);
	
	log.info("Retrieving live data for 'Free Memory' metric");
	Set<MeasurementData> data = new ResourceMonitoring(client).getLiveMetricData(platform, "Free Memory");	
	log.info(separator);
        for (MeasurementData md : data) {
            log.info(md);
        }
        log.info(separator);
        
        log.info("Bundle deployment:");
        log.info("Create a resource group 'bundle-test' containing our Linux platform");
	new ResourceGroups(client).deleteGroup("bundle-test");
	ResourceGroup bundleTarget = new ResourceGroups(client).createGroup("bundle-test",new Resource[] {platform},false);
	log.info("Deploy a sample bundle to 'bundle-test' group");
	BundleDeployment deployment = new DeployBundle(client).deployBundle(
		new File(Main.class.getResource("/bundle.zip").getFile()), 
		bundleTarget, 
		new Configuration(), // our bundle does not require any input parameters 
		"bundletest", 
		"Root File System",
		"/tmp");
	log.info("Bundle deployment finished with status : "+deployment.getStatus());
	
    }

}