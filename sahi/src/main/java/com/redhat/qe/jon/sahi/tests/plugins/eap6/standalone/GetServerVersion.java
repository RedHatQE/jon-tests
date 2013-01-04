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
	    log.info("Waiting 120s for [Product Version] trait to be measured");
	    sahiTasks.waitFor(120 * 1000);
	    String version = server.monitoring().traits().getMetricRowValue("Product Version", 1);
	    String name = server.monitoring().traits().getMetricRowValue("Product Name", 1);
	    version = name+" "+version;
	    log.info("Detected versoin "+version);
	    System.setProperty("rhq.build.version", System.getProperty("rhq.build.version", "")+" "+version);
	    Reporter.log("<BR><b>"+version+"</b><br>");
	    
	}
}
