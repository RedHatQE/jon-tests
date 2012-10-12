package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Dummy extends AS7StandaloneTest {

	@Test
	public void success() {
		
	}
	@Test
	public void failure() {
		Assert.fail();
	}
	
}
