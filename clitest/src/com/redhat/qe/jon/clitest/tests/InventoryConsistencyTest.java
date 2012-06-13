package com.redhat.qe.jon.clitest.tests;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.tasks.CliTasksException;
import com.redhat.qe.tools.remotelog.CheckRemoteLog;
import com.redhat.qe.tools.remotelog.RemoteLog;

/**
 * this test checks whether inventory is consistent. This test runs UninventoryResources.js and ImportResources.js and checks server log
 * for ERRORS
 * @author lzoubek
 *
 */
@CheckRemoteLog(
		logs=@RemoteLog(
			host="${HOST_NAME}",
			user="${HOST_USER}",
			pass="${HOST_PASSWORD}",
			logFile="${rhq.server.home}/logs/rhq-server-log4j.log",
			failExpression="JDBCExceptionReporter"
			)
		)
public class InventoryConsistencyTest extends CliTest {
	
	@DataProvider
	public Object[][] attempts() {
		int count = 10;
		Object[][] output = new Object[count][];
		for (int i=0;i<count;i++) {
			output[i] = new Object[] {i};
		}		
		return output;
	}
	
	@Test(dataProvider="attempts",description="this test imports everything, waits and immediatelly unimports everything")
	public void importAndUninventory(int attempt) throws IOException, CliTasksException {
		runJSfile(null, "rhqadmin", "rhqadmin", "bug830158.js", null,null, null);
	}
}
