package com.redhat.qe.jon.clitest.tests.plugins.eap6;

import java.io.File;
import java.io.FileInputStream;

import org.testng.annotations.BeforeSuite;

import com.redhat.qe.jon.clitest.tests.CliTest;

public class AS7CliTest extends CliTest {

	protected String agentName;
	protected String standalone1Home;
	protected String standalone1HostName;

	@BeforeSuite
	public void loadProperties() {
		try {
            System.getProperties().load(new FileInputStream(new File(System.getProperty("eap6plugin.configfile"))));
        } catch (Exception e) {
            try {
                System.getProperties().load(new FileInputStream(new File("config/eap6plugin.properties")));
            } catch (Exception ex) {
                try {
                    System.getProperties().load(new FileInputStream(new File("automatjon/jon/sahi/config/eap6plugin.properties")));
                } catch (Exception exc) {
                    log.severe("Could not load properties file for EAP6plugin testing: " + exc.getMessage() + " please provide the full path in system property \"eap6plugin.configfile\".");
                }
            }

        }
		agentName = System.getProperty("agent.name");
		standalone1Home = System.getProperty("as7.standalone1.home");
		standalone1HostName = System.getProperty("as7.standalone1.hostname");
	}
	public void waitFor(int ms) {
		waitFor(ms, "Waiting");
	}
	public void waitFor(int ms,String message) {
		log.fine(message+" "+(ms/1000)+"s");
		if(ms<=0) {
			ms = 1;
		}
		try {
			Thread.currentThread().join(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
