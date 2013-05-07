package com.redhat.qe.jon.clitest.tests.samples;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class SamplesFromCliClientCliTest extends CliEngine {
	
	private static Logger LOG = Logger.getLogger(SamplesFromCliClientCliTest.class.getName());
	private File cliSamplesDir = null;
	boolean isSampleDirRemote = false;
	private CliTasks cliTasks = CliTasks.getCliTasks();
	/**
	 * Location on the local machine where the remote cli samples will be stored
	 */
	private static final String SAMPLES_DIR_LOCAL_PATH =  "/tmp/cliSamples";
	
	@BeforeClass
	public void getCliSampleDir(){
		cliSamplesDir = getCliSamplesDir();
		if(cliTasks.isRemote()){
			isSampleDirRemote = true;
			// prepare local directory for cli samples
			new File(SAMPLES_DIR_LOCAL_PATH).mkdir();
		}
	}
	@Test
	public void measurementUtilsTest() throws IOException{
		File utilsLocalLocation = null;
		File measurementUtilsLocalLocation = null;
		// get cli sample files from remote host if necessary
		if(isSampleDirRemote){
			utilsLocalLocation = new File(SAMPLES_DIR_LOCAL_PATH,"util.js");
			measurementUtilsLocalLocation = new File(SAMPLES_DIR_LOCAL_PATH,"measurement_utils.js");
			cliTasks.getFile(cliSamplesDir.getPath()+"/util.js", SAMPLES_DIR_LOCAL_PATH);
			cliTasks.getFile(cliSamplesDir.getPath()+"/measurement_utils.js",SAMPLES_DIR_LOCAL_PATH);
		}else{
			utilsLocalLocation = new File(cliSamplesDir.getPath()+"/util.js");
			measurementUtilsLocalLocation = new File(cliSamplesDir.getPath()+"/measurement_utils.js");
		}
		
		
		// run the test
		createJSRunner("samplesFromCliClient/measurementUtilsTest.js").
		addDepends("rhqapi.js," +
				"file://"+utilsLocalLocation +
				",file://" + measurementUtilsLocalLocation).
		run();
	}
	
	@Test
	public void bundlesTest() throws IOException{
		File utilsLocalLocation = null;
		File bundlesLocalLocation = null;
		// get cli sample files from remote host if necessary
		if(isSampleDirRemote){
			utilsLocalLocation = new File(SAMPLES_DIR_LOCAL_PATH,"util.js");
			bundlesLocalLocation = new File(SAMPLES_DIR_LOCAL_PATH,"bundles.js");
			cliTasks.getFile(cliSamplesDir.getPath()+"/util.js", SAMPLES_DIR_LOCAL_PATH);
			cliTasks.getFile(cliSamplesDir.getPath()+"/bundles.js",SAMPLES_DIR_LOCAL_PATH);
		}else{
			utilsLocalLocation = new File(cliSamplesDir.getPath()+"/util.js");
			bundlesLocalLocation = new File(cliSamplesDir.getPath()+"/bundles.js");
		}
		
		
		// run the test
		createJSRunner("samplesFromCliClient/bundlesTest.js").
		addDepends("rhqapi.js," +
				"file://"+utilsLocalLocation+
				",file://"+bundlesLocalLocation).
				resourceSrcs("/bundles/bundle.zip").
				resourceDests("/tmp/bundle.zip").
				run();
	}
	
	private File getCliSamplesDir(){
		File cliBinLocation = new File(CliTest.cliShLocation); 
		File cliSamplesDir = new File(cliBinLocation.getParentFile().getParent()+File.separator+"samples");
		LOG.info("Following directory with CLI samples will be used: " + cliSamplesDir.getPath());
		
		return cliSamplesDir;
	}
}
