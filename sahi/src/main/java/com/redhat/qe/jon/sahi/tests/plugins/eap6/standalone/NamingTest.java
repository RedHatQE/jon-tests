package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * tests for naming subsystem
 * @author lzoubek
 *
 */
public class NamingTest extends AS7StandaloneTest {
	
	Resource naming;
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		naming = server.child("naming");
    }
	
	@Test(groups = {"blockedByBug-822886"})
	public void JNDIView() {
		Operations operations = naming.operations();
		Operation op = operations.newOperation("JNDIView");
		op.schedule();
		operations.assertOperationResult(op, true);
	}
}
