package com.redhat.qe.jon.sahi.base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.common.TestScript;
import com.redhat.qe.jon.common.util.HTTPClient;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class SahiTestScript extends TestScript {
	private static Logger log = Logger.getLogger(SahiTestScript.class.getName());
	public static SahiTasks sahiTasks = null;

	protected String browserPath			= System.getProperty("jon.browser.path", "/usr/bin/firefox");
	protected String browserName			= System.getProperty("jon.browser.name", "firefox");
	protected String browserOpt				= System.getProperty("jon.browser.opt", "");

	protected String sahiBaseDir			= System.getProperty("jon.sahi.base.dir", "/home/hudson/sahi");
	protected String sahiUserdataDir		= System.getProperty("jon.sahi.userdata.dir", sahiBaseDir+"/userdata");

	protected String bundleHostURL			= System.getProperty("jon.bundleServer.url");

	public SahiTestScript() {
		super();
		checkForCorrectSahiLocation();
		sahiTasks = new SahiTasks(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
	}

    public SahiTestScript(SahiTasks sahiTasksToUse) {
        super();
        sahiTasks = sahiTasksToUse;
    }
        
	@BeforeSuite(groups={"setup"})
	public void openBrowser() {
		checkForSahiProxyRunning();
		log.finer("Opening browser");
		sahiTasks.open();
		log.finer("Loading RHQ system page: "+System.getProperty("jon.server.url"));
		sahiTasks.navigateTo(System.getProperty("jon.server.url"), true);
	}

	@AfterSuite(groups={"teardown"})
	public void closeBrowser() {
		log.finer("Closing browser");
		sahiTasks.close();
	}
	private Properties getSahiConfigProperties() {
		File config = new File(sahiBaseDir+File.separator+"config"+File.separator+"sahi.properties");
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
}
