package com.redhat.qe.jon.sahi.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.common.Platform;
import com.redhat.qe.jon.common.TestScript;
import com.redhat.qe.jon.common.util.HTTPClient;
import com.redhat.qe.jon.common.util.LocalCommandRunner;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

public abstract class SahiTestScript extends TestScript {
	private static Logger log = Logger.getLogger(SahiTestScript.class.getName());
	public static SahiTasks sahiTasks = null;

	protected String browserPath			= System.getProperty("jon.browser.path", "/usr/bin/firefox");
	protected String browserName			= System.getProperty("jon.browser.name", "firefox");
	protected String browserOpt				= System.getProperty("jon.browser.opt", "");

	protected String sahiBaseDir			= System.getProperty("jon.sahi.base.dir", "/home/hudson/sahi");
	protected String sahiUserdataDir		= System.getProperty("jon.sahi.userdata.dir", sahiBaseDir+File.separator+"userdata");

	protected String bundleHostURL			= System.getProperty("jon.bundleServer.url");
	
	//Added this map to manage data provider for JON and RHQ
	private LinkedList<String> testNgDataProviderGroups= new LinkedList<String>();

	public SahiTestScript() {
		super();
		checkForCorrectSahiLocation();
		sahiTasks = new SahiTasks(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
	}

    public SahiTestScript(SahiTasks sahiTasksToUse) {
        super();
        sahiTasks = sahiTasksToUse;
    }
    
    
    //Add entry here if you are using new list of groups, other than listed below
    public enum TESTNG_DATA_PROVIDER_GROUPS{
    	JON,RHQ
    }
    
    //Converts comma separated groups value to LinkedList object.
    private void updateTestNgDataProviderGroups(){
    	String[] groups = System.getProperty("testng.data.provider.groups", "RHQ").split(",");
    	for(String group: groups){
    		this.testNgDataProviderGroups.addLast(group);
    	}
    	log.fine("testng.data.provider.groups: "+testNgDataProviderGroups);
    }
        
	@BeforeSuite(groups={"setup"})
	public void openBrowser() {
		checkForSahiProxyRunning();
		updateTestNgDataProviderGroups(); //Update system environment to LinkedList
		log.finer("Opening browser");
		sahiTasks.open();
		log.finer("Loading RHQ system page: "+System.getProperty("jon.server.url"));
		sahiTasks.navigateTo(System.getProperty("jon.server.url"), true);
        Platform pl = new Platform();
        String nircmdUtil = System.getProperty("nircmd.path", null);
        log.finer("Checking existence of nircmdUtil: " + nircmdUtil);
        if (pl.isWindows() && (nircmdUtil != null) && new File(nircmdUtil).exists()) {
            LocalCommandRunner commandRunner = new LocalCommandRunner(new File(nircmdUtil).getParent());
            // implement running nircd to maximize firefox window
            commandRunner.runAndWait(nircmdUtil + " win activate ititle \"JBoss ON\"");
            commandRunner.runAndWait(nircmdUtil + " win max ititle \"JBoss ON\"");
        }
	}

	@AfterSuite(groups={"teardown"})
	public void closeBrowser() {
		log.finer("Closing browser");
		sahiTasks.close();
	}
	private Properties getSahiConfigProperties() {
		File config = new File(sahiBaseDir, "config"+File.separator+"sahi.properties");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(config));
			return props;
		} catch (FileNotFoundException e) {
			log.severe("Unable to locate SAHI config file ["+config.getAbsolutePath()+"]");
			return null;
		} catch (IOException e) {
			log.severe("Unable to locate SAHI config file ["+config.getAbsolutePath()+"]");
			return null;			
		}
	}
	private void checkForCorrectSahiLocation() {
		if (getSahiConfigProperties()==null) {
			throw new RuntimeException("Unable to locate SAHI config file, system property [jon.sahi.base.dir] might not be set correctly");
		}
	
	}

	private void checkForSahiProxyRunning() {
		Properties props = getSahiConfigProperties();
		if (props==null) {
			throw new RuntimeException("Unable to locate SAHI config file, system property [jon.sahi.base.dir] might not be set correctly");
		}
		String port = props.getProperty("proxy.port", "9999");
		if (!new HTTPClient("localhost", Integer.parseInt(port)).isRunning()) {
			throw new RuntimeException(
					"Unable to connect to SAHI proxy on http://localhost:"+port+" - SAHI is not running! Please start it up");
		}

	}

	public LinkedList<String> getTestNgDataProviderGroups() {
		return testNgDataProviderGroups;
	}
}
