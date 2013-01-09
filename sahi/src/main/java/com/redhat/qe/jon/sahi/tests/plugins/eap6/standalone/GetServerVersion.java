package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Monitoring;

public class GetServerVersion extends AS7StandaloneTest {

    
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
	}
	
	@Test
	public void getServerVersion() {
	    Monitoring monitoring = server.monitoring();
	    monitoring.schedules().setInterval("Product Version", "1");
	    monitoring.schedules().setInterval("Product Name", "1");
	    log.info("Waiting 180s for [Product Version] trait to be measured");
	    sahiTasks.waitFor(180 * 1000);
	    String version = server.monitoring().traits().getMetricRowValue("Product Version", 1);
	    String name = server.monitoring().traits().getMetricRowValue("Product Name", 1);
	    version = name+" "+version;
	    log.info("Detected version "+version);
	    String buildVersion = System.getProperty("rhq.build.version", "");
	    // we expect there is \n character within value of this property
	    // we append EAP6 version just before \n character
	    String[] parts = buildVersion.split("\n");
	    StringBuilder newVersion = new StringBuilder();
	    if (parts.length>1) {
		parts[0] += " "+version;
		for (String part : parts) {
		    newVersion.append(part+"\n");
		}
	    }
	    else {
		newVersion.append(buildVersion+" "+version);
	    }
	    System.setProperty("rhq.build.version", newVersion.toString());
	    Reporter.log("<BR><b>"+System.getProperty("rhq.build.version", "")+"</b><br>");
	    
	}
}
