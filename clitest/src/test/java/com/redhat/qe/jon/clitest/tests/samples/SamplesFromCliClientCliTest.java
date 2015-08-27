package com.redhat.qe.jon.clitest.tests.samples;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.redhat.qe.Assert;
import com.redhat.qe.auto.bugzilla.BlockedByBzBug;
import com.redhat.qe.jon.common.util.CryptoUtils;
import com.redhat.qe.jon.common.util.HTTPClient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.OnAgentCliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.jon.clitest.tests.CliTest;

public class SamplesFromCliClientCliTest extends OnAgentCliEngine {
	
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
	@Test(description = "This tests all methods from measurement_utils.js sample file.")
	public void measurementUtilsTest() throws IOException{
		// run the test
		createJSRunner("samplesFromCliClient/measurementUtilsTest.js").
				addDepends("rhqapi.js," +
						"file://"+getCliSampleFileLocation("util.js") +
						",file://" + getCliSampleFileLocation("measurement_utils.js")).
				run();
	}
	
	@Test(description = "This tests all methods from bundles.js sample file.",
			groups={"blockedByBug-1003679"})
	public void bundlesTest() throws IOException{
		// run the test
		createJSRunner("samplesFromCliClient/bundlesTest.js").
				addDepends("rhqapi.js," +
						"file://"+getCliSampleFileLocation("util.js")+
						",file://"+getCliSampleFileLocation("bundles.js")).
				withResource("/bundles/bundle.zip","bundle").
				run();
	}
	
	@Test(description = "This test loads 'bundles' module and tests all methods there.", 
			groups={"blockedByBug-1003679"})
	public void bundlesModuleTest(){
		// run the test
		createJSRunner("samplesFromCliClient/bundlesTest.js").
				addDepends("rhqapi.js").
				withResource("/bundles/bundle.zip","bundle").
				run();
	}
	
	@Test(description = "This tests all methods from drift.js sample file.")
	public void driftTest() throws IOException, CliTasksException{
	    // run it twice to cover bz1252136
	    runDriftTest("Drift def 1");
	    runDriftTest("Drift def 2");
	}
	@Test(description = "This test loads 'drift' module and tests all methods there.")
	public void driftModuleTest() throws CliTasksException{
	    CliTasks agentMachine = prepareAgentMachine();
		
	    String driftDefName = "Drift def 1";
		// run the first part
		createJSRunner("samplesFromCliClient/driftTestPart1.js").
		        withArg("drDefName", driftDefName).
				addDepends("rhqapi.js," +
				"samplesFromCliClient/driftCommon.js").
				run();
		
		createDrift(agentMachine,driftDefName);
		
		// run second part
		createJSRunner("samplesFromCliClient/driftTestPart2.js").
		        withArg("drDefName", driftDefName).
				addDepends("rhqapi.js," +
				"samplesFromCliClient/driftCommon.js").
				addExpect("+second line,Retrieved content of file: first line,baca3ae8\tbin/file1.txt").
				run();
	}
	
	
    @DataProvider
    public Object[][] getModuleNames() {
        List<String> modules = new ArrayList<String>();
        // first get all modules from CLI
        File modulesDir = new File(getCliSamplesDir(),"modules");
        for (File module : modulesDir.listFiles()) {
            modules.add("modules:/"+ FilenameUtils.getBaseName(module.getName()));
        }
        // now get all server modules
        String content = new HTTPClient(rhqTarget,7080).doGet("/downloads/script-modules/");
        Pattern regex = Pattern.compile("<a href=\"([^\"]+)");
        Matcher m = regex.matcher(content);
        while (m.find()) {
            modules.add("rhq://downloads/" + FilenameUtils.getBaseName(m.group(1)));
        }
        return getDataProviderArray(modules);
    }

    @Test(dataProvider = "getModuleNames")
    public void importModules(String module) {
        createJSRunner("samplesFromCliClient/importModules.js").withArg("module",module).run();
    }

    @Test(description = "This checks whether JS modules available on server have same content as module shipped within CLI",groups={"blockedByBug-1032053"})
    public void moduleConsistency() throws Exception {
        // get server modules & their MD5
        HTTPClient client = new HTTPClient(rhqTarget,7080);
        String content = client.doGet("/downloads/script-modules/", null, null);
        Pattern regex = Pattern.compile("<a href=\"([^\"]+)");
        Matcher m = regex.matcher(content);
        Map<String,String> serverModules = new HashMap<String, String>();
        while (m.find()) {
            String mod = client.doGet(m.group(1));
            String md5 = CryptoUtils.md5(mod);
            mod = FilenameUtils.getBaseName(m.group(1));
            log.info(mod+" : "+ md5);
            serverModules.put(mod, md5);
        }
        // get CLI modules & their MD5
        Map<String,String> cliModules = new HashMap<String, String>();
        File modulesDir = new File(getCliSamplesDir(),"modules");
        for (File module : modulesDir.listFiles()) {
            String mod =  FilenameUtils.getBaseName(module.getName());
            InputStream in = new FileInputStream(module);
            try {
                String modContent = IOUtils.toString(in);
                String md5 = CryptoUtils.md5(modContent);
                log.info(mod+" : "+ md5);
                cliModules.put(mod, md5);
            }
            finally {
                in.close();
            }
        }
        // compare & assert
        // we don't assert equal size, because there might be other (user)modules on server
        Assert.assertTrue(serverModules.size()>=cliModules.size(),"Server module count must be same or higher as CLI module count");
        for (Map.Entry<String,String> cliE : cliModules.entrySet()) {
            log.info("Checking "+cliE.getKey());
            String md5CliMod = cliE.getValue();
            String md5SMod = serverModules.get(cliE.getKey());
            Assert.assertNotNull(md5SMod,"Module "+cliE.getKey()+" is available in CLI and server");
            Assert.assertEquals(md5SMod,md5CliMod,"MD5 hash for module "+cliE.getKey()+" from CLI must match server");
        }
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
	private void waitForNewSnapshotVersionUsingModule(String version,String driftDefName){
		createJSRunner("samplesFromCliClient/drift-waitForNewSnapshot.js").
		        withArg("drDefName", driftDefName).
				addDepends("rhqapi.js," +
				"samplesFromCliClient/driftCommon.js").
				withArg("expectedVersion", version).
				run();
	}
	
	private CliTasks prepareAgentMachine() throws CliTasksException{
		checkRequiredProperties("jon.agent.host");
		CliTasks agentMachine = new CliTasks();
		agentMachine.initialize(System.getProperty("jon.agent.host"),"hudson","hudson");
		
		// clean monitored directory
		agentMachine.runCommand("rm -rf /tmp/driftFiles");
		agentMachine.runCommand("mkdir -p /tmp/driftFiles/bin");
		agentMachine.runCommand("mkdir -p /tmp/driftFiles/etc");
		
		return agentMachine;
	}
	private void createDrift(CliTasks agentMachine,String driftDefName) throws CliTasksException{
		// add one new file
		String file1Path = "/tmp/driftFiles/bin/file1.txt";
		String file2Path = "/tmp/driftFiles/bin/file2.txt";
		agentMachine.runCommand("echo \"first line\" > " + file1Path);
		waitForNewSnapshotVersionUsingModule("1", driftDefName);
		
		// add another new file
		agentMachine.runCommand("echo \"first line\" > " + file2Path);
		waitForNewSnapshotVersionUsingModule("2", driftDefName);
		
		// add one new line
		agentMachine.runCommand("echo \"second line\" >> " + file1Path);
		waitForNewSnapshotVersionUsingModule("3" ,driftDefName);
	}

    private void runDriftTest(String driftDefName) throws CliTasksException,IOException {
        CliTasks agentMachine = prepareAgentMachine();
        // run the first part
        createJSRunner("samplesFromCliClient/driftTestPart1.js").
                withArg("drDefName", driftDefName).
                addDepends("rhqapi.js," +
                "file://"+getCliSampleFileLocation("util.js")+
                ",file://"+getCliSampleFileLocation("drift.js")+
                ",samplesFromCliClient/driftCommon.js").
                run();

        createDrift(agentMachine, driftDefName);

        // run second part
        createJSRunner("samplesFromCliClient/driftTestPart2.js").
                withArg("drDefName", driftDefName).
                addDepends("rhqapi.js," +
                "file://"+getCliSampleFileLocation("util.js")+
                ",file://"+getCliSampleFileLocation("drift.js")+
                ",samplesFromCliClient/driftCommon.js").
                addExpect("+second line,Retrieved content of file: first line,baca3ae8\tbin/file1.txt").
                run();
	}
}
