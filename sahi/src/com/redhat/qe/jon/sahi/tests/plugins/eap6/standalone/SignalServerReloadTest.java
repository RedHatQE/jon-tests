package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import java.io.IOException;

import net.sf.sahi.client.ElementStub;

import org.jboss.dmr.ModelNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
/**
 * this test checks whether UI correctly displays that AS7 requires either reload or restart (response-headers)
 * @author lzoubek
 *
 */
public class SignalServerReloadTest extends AS7StandaloneTest {
	
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
    }
	
	@Test
	public void signalServerReload() {
		mgmtClient.reload();
		checkReloadOrRestartRequired(false);
		server.configuration().current();
		Assert.assertNull(getServerMessage(), "Message signaled by server is NULL");
		triggerServerReload();
		checkReloadOrRestartRequired(true);
		server.configuration().current();
		String text = getServerMessage();
		Assert.assertNotNull(text, "Server signals user with non-empty message");
		Assert.assertTrue(text.contains("server needs a reload"), "Server correctly signals user");
	}
	@Test
	public void signalServerRestart() throws Exception {
		mgmtClient.reload();
		checkReloadOrRestartRequired(false);
		server.configuration().current();
		Assert.assertNull(getServerMessage(), "Message signaled by server is NULL");
		mgmtClient.executeOperationVoid("/", "server-set-restart-required", new String[]{});		
		checkReloadOrRestartRequired(true);
		server.configuration().current();
		String text = getServerMessage();
		Assert.assertNotNull(text, "Server signals user with non-empty message");
		Assert.assertTrue(text.contains("server needs a restart"), "Server correctly signals user");
	}
	
	private void checkReloadOrRestartRequired(boolean required) {
		ModelNode ret = null;
		try {
			ret = mgmtClient.executeOperation(mgmtClient.createOperation("", "read-children-names", new String[] { "child-type=deployment" }));
		} catch (IOException e) {
			Assert.fail("Failed to execute DMR operation", e);
		}
		Assert.assertEquals(mgmtClient.reloadOrRestartRequired(ret), required,"Server requires restart or reload using DMR API");
	}
	
	private void triggerServerReload() {
		try {
			mgmtClient.executeOperationAndAssertSuccess("Change that triggers server to require reload was successfull", 
					mgmtClient.createOperation("/subsystem=mail/mail-session=java:jboss\\/mail\\/Default", "write-attribute", new String[]{"name=debug","value=true"}));
			mgmtClient.executeOperationAndAssertSuccess("Change that triggers server to require reload was successfull", 
					mgmtClient.createOperation("/subsystem=mail/mail-session=java:jboss\\/mail\\/Default", "write-attribute", new String[]{"name=debug","value=false"}));
		} catch (IOException e) {
			Assert.fail("Failed to execute DMR operation", e);
		}
		checkReloadOrRestartRequired(true);
	}
	private String getServerMessage() {
		ElementStub es =  sahiTasks.byXPath("//td[@class='WarnBlock'][2]");
		if (es!=null && es.exists()) {
			return es.getText();
		}
		return null;
	}
}
