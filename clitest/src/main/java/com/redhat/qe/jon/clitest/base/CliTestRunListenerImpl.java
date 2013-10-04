package com.redhat.qe.jon.clitest.base;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.jon.common.util.DynamicAntBundle;

/**
 * default implementation of {@link CliTestRunListener}
 * this implementation can generate dynamic bundles by implementing {@link CliTestRunListener#prepareResource(String)}
 * and reacts on pattern "antbundle:<name>:<version>". Subclasses extending this implementation should call super class 
 * methods to keep it's functionality
 * @author lzoubek
 *
 */
public class CliTestRunListenerImpl implements CliTestRunListener {

    protected static Logger log = Logger.getLogger(CliTestRunListenerImpl.class.getName());
    
    @Override
    public File prepareResource(String name) {
	if (name.startsWith("antbundle:")) {
	    String[] params = name.split(":");
	    if (params.length!=3) {
		throw new RuntimeException("antbundle resource must be in format antbundle:<bundle name>:<bundle version>");
	    }
	    try {
		return new DynamicAntBundle().withName(params[1]).withVersion(params[2]).build();
	    } catch (Exception e) {
		log.log(Level.SEVERE, "Failed to create dynamic bundle", e);
		return null;
	    }
	}
	return null;
    }

    @Override
    public File onResourceProcessed(String name, File resource) {
	return resource;
    }

}
