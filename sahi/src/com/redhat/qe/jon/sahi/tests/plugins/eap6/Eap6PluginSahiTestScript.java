package com.redhat.qe.jon.sahi.tests.plugins.eap6;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author jmartisk
 */
public class Eap6PluginSahiTestScript extends SahiTestScript {
    
    protected static final Logger log = Logger.getLogger(Eap6PluginSahiTestScript.class.getName());
    
    protected Eap6PluginSahiTasks eapSahiTasks;
    
    public Eap6PluginSahiTestScript() {
	super();
	eapSahiTasks = new Eap6PluginSahiTasks(sahiTasks);
        try {
            System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));
        } catch(IOException e) {
            log.severe("Could not load properties file for EAP6plugin testing: "+e.getMessage());
        }
    }
}
