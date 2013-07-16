package com.redhat.qe.jon.clitest.tests;

import java.io.IOException;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

public class CliTest extends CliEngine {

	/**
	 * parameters resSrc and resDest must have same count of items and are processed together (resSrc[0] will be copied to resDst[0])
	 * @param rhqTarget
	 * @param cliUsername
	 * @param cliPassword
	 * @param jsFile to be executed
	 * @param cliArgs arguments passed
	 * @param expectedResult comma-separated list of messages that are expected as output
	 * @param makeFilure comma-separated list of messages - if these are found in output, test fails
	 * @param jsDepends comma-separated list of other JS files that are required/imported by <b>jsFile</b>
	 * @param resSrc comma-separated list of source paths
	 * @param resDst comma-separated list of source paths (must be same size as resSrc)
	 * @throws IOException
	 * @throws CliTasksException
	 * @deprecated please extend {@link CliEngine} class instead of this class and use {@link CliEngine#createJSRunner(String)} builder
	 * to run CLI tests 
	 */
	@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure","js.depends","res.src","res.dst"})
	@Test	
	@Override
	public void runJSfile(@Optional String rhqTarget,
	    @Optional String cliUsername, @Optional String cliPassword,
	    String jsFile, @Optional String cliArgs,
	    @Optional String expectedResult, @Optional String makeFilure,
	    @Optional String jsDepends, @Optional String resSrc,
	    @Optional String resDst) throws IOException, CliTasksException {
	super.runJSfile(rhqTarget, cliUsername, cliPassword, jsFile, cliArgs,
		expectedResult, makeFilure, jsDepends, resSrc, resDst);
	}
	



	
}
