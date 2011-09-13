package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 *
 * @author jmartisk
 */
public class AS7PluginSahiTestScript extends SahiTestScript {
    
    protected static final Logger log = Logger.getLogger(AS7PluginSahiTestScript.class.getName());
    
    protected AS7PluginSahiTasks as7SahiTasks;
    
    protected String agentName;
    protected String as7StandaloneName;
    
    public AS7PluginSahiTestScript() {
	super();	        
    }

    @BeforeSuite(groups="setup", dependsOnMethods={"login", "openBrowser"})
    public void setup() {
        try {
            System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));
        } catch(IOException e) {
            log.severe("Could not load properties file for EAP6plugin testing: "+e.getMessage());
        }
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
    }
    
}
