package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.sasl.util.UsernamePasswordHashUtil;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.common.util.AS7DMRClient;
import com.redhat.qe.jon.common.util.AS7SSHClient;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.ChildResources;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
import com.redhat.qe.jon.sahi.tests.plugins.eap6.exceptions.NothingInDiscoveryQueueException;


/**
 * @author Jan Martiska (jmartisk@redhat.com)
 * @since 7 September 2011
 *        How to add new testcases: Use this class instead of SahiTasks. Instantiating this class will also load eap6plugin.properties file
 */
public class AS7PluginSahiTasks {

    protected static final Logger log = Logger.getLogger(AS7PluginSahiTasks.class.getName());
    protected final SahiTasks tasks;

    /**
     * set that remembers what resources are in inventory, so we do not need to ask UI every time we need to
     * know whether resource is in
     */
    private static final Map<String,Boolean> inventoryState = new HashMap<String, Boolean>();
    public AS7PluginSahiTasks(SahiTasks tasks) {
        this.tasks = tasks;
    }


    public void uninventorizeAllDomainAS() {
    	// FIXME: update inventoryState
    }

    /**
     * imports given resource. First check internal state whether resource is already in inventory, 
     * secondly actually check inventory, whether resource is there, third, perform autodiscovery an finally import resource
     * @param res
     */
    public void importResource(Resource res) {
    	String resourceName = res.getName();
    	String agentName = res.getPlatform();
    	log.fine("Trying to inventorize resource \"" + resourceName + "\" of agent \"" + agentName + "\".");
        Boolean inInventory = inventoryState.get(agentName+resourceName);
        if (inInventory==null) {
        	inInventory = Boolean.FALSE;
        }
        if (inInventory) {
        	log.fine("[inventoryState] Resource \"" + resourceName + "\" of agent \"" + agentName + "\" have been already inventorized");
        	return;
        }
        if (!inInventory) {
        	if (res.exists()) {
        		log.fine("Resource \"" + resourceName + "\" of agent \"" + agentName + "\" have been already inventorized");
        		inventoryState.put(agentName+resourceName, Boolean.TRUE);
        		return;
        	}
        	log.fine("Will perform manual autodiscovery first.");
	        res.performManualAutodiscovery();
	        try {
	            tasks.link("Inventory").click();
                tasks.cell("Discovery Queue").click();
                tasks.waitFor(Timing.WAIT_TIME);
                ElementStub elm = tasks.cell(agentName);
                if (elm.exists()) {
                    elm.doubleClick();
                } else {
                    throw new NothingInDiscoveryQueueException();
                }
	            
	        } catch (NothingInDiscoveryQueueException ex) {
	            log.fine("Could not inventorize resource " + resourceName + ", nothing appeared in autodiscovery queue even after performing manual autodiscovery");
	            return;
	        }
	        ElementStub elm = tasks.image("unchecked.png").near(tasks.cell(resourceName));
	        if (elm.exists()) {
	            elm.check();
	            tasks.cell("Import").click();
	            log.fine("Waiting "+Timing.toString(10*Timing.TIME_1M)+" for resource to import...");
	            tasks.waitFor(10*Timing.TIME_1M);
	        } else {
	            log.fine("Resource \"" + resourceName + "\" of agent \"" + agentName + "\" not found in Autodiscovery queue, it might have been already inventorized");
	        }
	        inventoryState.put(agentName+resourceName, Boolean.TRUE);
        }
    }

    /**
     * adds a JMS queue 
     * @param hornetq resource representing hornetq
     * @param queue resource representing queue (should be child of hornetq)
     */
	public void addJMSQueue(Resource hornetq, Resource queue) {
		Inventory inventory = hornetq.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard child = childResources.newChild("JMS Queue");		
		child.getEditor().setText("resourceName", queue.getName());		
		child.next();
		//child.getEditor().checkRadio("paused[0]");
		//child.getEditor().setText("queue-address", queue.getName());
		//child.getEditor().checkRadio("temporary[1]");
		ConfigEntry ce = child.getEditor().newEntry(0);
		ce.setField("entry", queue.getName());
		ce.OK();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
	}
    /**
     * adds a JMS topic 
     * @param hornetq resource representing hornetq
     * @param topic resource representing topic (should be child of hornetq)
     */
	public void addJMSTopic(Resource hornetq, Resource topic) {
		Inventory inventory = hornetq.inventory();
		ChildResources childResources = inventory.childResources();
		NewChildWizard child = childResources.newChild("JMS Topic");		
		child.getEditor().setText("resourceName", topic.getName());		
		child.next();
		ConfigEntry ce = child.getEditor().newEntry(0);
		ce.setField("entry", topic.getName());
		ce.OK();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);	
	}
	/**
	 * installs default RHQ user for given server
	 * if 'server' param exists in RHQ UI, 10min waiting is started  
	 * @param server JON resource
	 * @param sshClient AS7SSHClient that can work with 'server' resource
	 * @param mgmtClient Management client that can manage 'server' resource
	 * @param credFile relative path within {@link AS7SSHClient#getAsHome()} to save user name + hashed pass
	 */
	 public void installRHQUser(Resource server, AS7SSHClient sshClient, AS7DMRClient mgmtClient, String credFile) {	
		String user = "rhqadmin";
		String checkCmd = "grep '"+user+"' "+sshClient.getAsHome() + credFile;
		if (sshClient.runAndWait(checkCmd).getStdout().contains(user)) {
			log.info("rhqadmin already exists");
			return;
		}
		String pass = "rhqadmin";
		String hash = null;
		// we also add default user admin:admin
		String defaultUser = "admin";
		String defaultHash = null;
		// let's generate hash for pass and store it into mgmt-users.properties
		try {
			hash = new UsernamePasswordHashUtil().generateHashedHexURP(user,"ManagementRealm", pass.toCharArray());
			defaultHash = new UsernamePasswordHashUtil().generateHashedHexURP(defaultUser,"ManagementRealm", "admin".toCharArray());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"Unable to generate password hash to setup EAP", e);
		}
		
		StringBuilder command = new StringBuilder("echo " + defaultUser + "=" + defaultHash + " > ");
		command.append(sshClient.getAsHome() + credFile);
		sshClient.runAndWait(command.toString());
		log.info("Created default user:pass "+defaultUser+":admin");
		
		command = new StringBuilder("echo " + user + "=" + hash + " >> ");
		command.append(sshClient.getAsHome() + credFile);
		sshClient.runAndWait(command.toString());
		log.info("Created testing user:pass "+user+":"+pass);	
			
		mgmtClient.setUsername("rhqadmin");
		mgmtClient.setPassword("rhqadmin");
		if (server.exists()) {
			log.fine("Waiting "+Timing.toString(10*Timing.TIME_1M)+" for server child resources to get discovered...");
			tasks.waitFor(10*Timing.TIME_1M);
		}
	}
}
