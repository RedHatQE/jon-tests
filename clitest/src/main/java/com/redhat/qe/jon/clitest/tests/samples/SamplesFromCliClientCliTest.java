package com.redhat.qe.jon.clitest.tests.samples;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
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
		// run the test
		createJSRunner("samplesFromCliClient/measurementUtilsTest.js").
		addDepends("rhqapi.js," +
				"file://"+getCliSampleFileLocation("util.js") +
				",file://" + getCliSampleFileLocation("measurement_utils.js")).
		run();
	}
	
	@Test
	public void bundlesTest() throws IOException{
		// run the test
		createJSRunner("samplesFromCliClient/bundlesTest.js").
		addDepends("rhqapi.js," +
				"file://"+getCliSampleFileLocation("util.js")+
				",file://"+getCliSampleFileLocation("bundles.js")).
				resourceSrcs("/bundles/bundle.zip").
				resourceDests("/tmp/bundle.zip").
				run();
	}
	
	@Test
	public void driftTest() throws IOException, CliTasksException{
		String file1Path = "/home/hudson/rhq-agent/bin/file1.txt";
		String file2Path = "/home/hudson/rhq-agent/bin/file2.txt";
		cliTasks.runCommand("rm -rf " + file1Path);
		cliTasks.runCommand("rm -rf " + file2Path);
		
		// run the first part
		createJSRunner("samplesFromCliClient/driftTestPart1.js").
				addDepends("rhqapi.js," +
				"file://"+getCliSampleFileLocation("util.js")+
				",file://"+getCliSampleFileLocation("drift.js")+
				",samplesFromCliClient/driftCommon.js").
				run();
		
		// add one new file
		cliTasks.runCommand("echo \"first line\" > " + file1Path);
		waitForNewSnapshotVersion("1");
		
		// add another new file
		cliTasks.runCommand("echo \"first line\" > " + file2Path);
		waitForNewSnapshotVersion("2");
		
		// add one new line
		cliTasks.runCommand("echo \"second line\" >> " + file1Path);
		waitForNewSnapshotVersion("3");
		
		// run second part
		createJSRunner("samplesFromCliClient/driftTestPart2.js").
				addDepends("rhqapi.js," +
				"file://"+getCliSampleFileLocation("util.js")+
				",file://"+getCliSampleFileLocation("drift.js")+
				",samplesFromCliClient/driftCommon.js").
				addExpect("+second line,Retrieved content of file: first line,baca3ae8	bin/file1.txt").
				run();
	}
	
	private File getCliSampleFileLocation(String sampleFileName) throws IOException{
		// get cli sample files from remote host if necessary
		if(isSampleDirRemote){
			cliTasks.getFile(new File(cliSamplesDir,sampleFileName).toString(), SAMPLES_DIR_LOCAL_PATH);
			return new File(SAMPLES_DIR_LOCAL_PATH,sampleFileName);
		}else{
			return new File(cliSamplesDir,sampleFileName);
		}
	}
	
	private File getCliSamplesDir(){
		File cliBinLocation = new File(CliTest.cliShLocation); 
		File cliSamplesDir = new File(cliBinLocation.getParentFile().getParent()+File.separator+"samples");
		LOG.info("Following directory with CLI samples will be used: " + cliSamplesDir.getPath());
		
		return cliSamplesDir;
	}
	
	private void waitForNewSnapshotVersion(String version) throws IOException{
		createJSRunner("samplesFromCliClient/drift-waitForNewSnapshot.js").
				addDepends("rhqapi.js," +
				"file://"+getCliSampleFileLocation("util.js")+
				",file://"+getCliSampleFileLocation("drift.js")+
				",samplesFromCliClient/driftCommon.js").
				withArg("expectedVersion", version).
				run();
	}
}
