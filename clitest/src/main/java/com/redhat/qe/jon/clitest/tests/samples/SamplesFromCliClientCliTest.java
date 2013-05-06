package com.redhat.qe.jon.clitest.tests.samples;

import java.io.File;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tests.CliTest;

public class SamplesFromCliClientCliTest extends CliTest {
	
	private static Logger LOG = Logger.getLogger(SamplesFromCliClientCliTest.class.getName());

	@Test
	public void measurementUtilsTest(){
		File cliSamplesDir = getCliSamplesDir();
		createJSRunner("samplesFromCliClient/measurementUtilsTest.js").
		addDepends("rhqapi.js," +
				"file://"+cliSamplesDir.getPath()+File.separator+"util.js," +
				"file://"+cliSamplesDir.getPath()+File.separator+"measurement_utils.js").
		run();
	}
	
	@Test
	public void bundlesTest(){
		File cliSamplesDir = getCliSamplesDir();
		createJSRunner("samplesFromCliClient/bundlesTest.js").
		addDepends("rhqapi.js," +
				"file://"+cliSamplesDir.getPath()+File.separator+"util.js," +
				"file://"+cliSamplesDir.getPath()+File.separator+"bundles.js").
				resourceSrcs("/bundles/bundle.zip").
				resourceDests("/tmp/bundle.zip").
				run();
	}
	
	private File getCliSamplesDir(){
		File cliBinLocation = new File(CliTest.cliShLocation); 
		File cliSamplesDir = new File(cliBinLocation.getParentFile().getParent()+File.separator+"samples");
		LOG.info("Following directory with CLI samples will be used " + cliSamplesDir.getPath());
		
		return cliSamplesDir;
	}
}
