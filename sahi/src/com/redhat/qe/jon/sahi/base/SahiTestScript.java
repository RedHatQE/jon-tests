package com.redhat.qe.jon.sahi.base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.auto.testng.TestScript;

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

		sahiTasks = new SahiTasks(browserPath, browserName, browserOpt, sahiBaseDir, sahiUserdataDir);
	}
        
	@BeforeSuite(groups={"setup"})
	public void openBrowser() {
		log.finer("Opening browser");
		sahiTasks.open();
	}

	@BeforeSuite(groups={"setup"})
	public void login() {
		log.finer("Logging into RHQ system");
		sahiTasks.navigateTo(System.getProperty("jon.server.url"), true);                
		Assert.assertTrue(sahiTasks.login("rhqadmin", "rhqadmin"), "Login status");
		String loginErrorMessgae = "The username or password provided does not match our records.";
		Assert.assertFalse(sahiTasks.cell(loginErrorMessgae).exists(), "Login error message["+loginErrorMessgae+"] available?: "+sahiTasks.cell(loginErrorMessgae).exists());
		Assert.assertFalse(sahiTasks.textbox("user").exists(), "Login user TextBox available?: "+sahiTasks.textbox("user").exists());
		Assert.assertFalse(sahiTasks.password("password").exists(), "Login user password field available?: "+sahiTasks.password("password").exists());
		//Check Agent Status
		Assert.assertFalse(sahiTasks.isAgentRunning(System.getenv().get("AGENT_NAME")), "Agent["+System.getenv().get("AGENT_NAME")+"] running status");
	}

	@AfterSuite(groups={"teardown"})
	public void logout() {
		log.finer("Logging out of RHQ system");
		sahiTasks.logout();
	}

	@AfterSuite(groups={"teardown"})
	public void closeBrowser() {
		log.finer("Closing browser");
		sahiTasks.close();
	}
}
