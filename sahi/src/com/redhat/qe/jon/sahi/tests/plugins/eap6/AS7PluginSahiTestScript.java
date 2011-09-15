package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;
import org.testng.annotations.BeforeSuite;

/**
 *
 * @author jmartisk
 */
public class AS7PluginSahiTestScript extends SahiTestScript {
    
    protected static final Logger log = Logger.getLogger(AS7PluginSahiTestScript.class.getName());
    
    protected AS7PluginSahiTasks as7SahiTasks;  
    
    public AS7PluginSahiTestScript() {
	super();	        
    }

    @BeforeSuite(groups="setup", dependsOnMethods={"login", "openBrowser"})
    public void setup1() {
        try {
            System.getProperties().load(new FileInputStream(new File(System.getProperty("eap6plugin.configfile"))));
        } catch(Exception e) {
            try {
                System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));                
            } catch(Exception ex) {
                try {
                    System.getProperties().load(new FileInputStream(new File("automatjon/jon/sahi/config/eap6plugin.properties"))); 
                } catch(Exception exc) {
                    log.severe("Could not load properties file for EAP6plugin testing: "+exc.getMessage() + " please provide the full path in system property \"eap6plugin.configfile\".");
                }                
            }
            
        }
        as7SahiTasks = new AS7PluginSahiTasks(sahiTasks);
        as7SahiTasks.uninventorizeResourceByNameIfExists(System.getProperty("agent.name"), System.getProperty("as7.standalone.name"));
    }    
    
}
