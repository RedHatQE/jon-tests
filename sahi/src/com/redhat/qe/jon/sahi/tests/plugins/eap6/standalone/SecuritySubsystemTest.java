package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Operations;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Operations.Operation;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class SecuritySubsystemTest extends AS7StandaloneTest {
	Resource security;
	Resource securityDomain;
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		security = server.child("security");
		securityDomain = security.child("secdomain");
    }
	@Test
	public void securitySubsystemConfiguration() {
		Configuration config = security.configuration();
		CurrentConfig current = config.current();
		current.getEditor().checkRadio("deep-copy-subject-mode[0]");
		current.save();
		config.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=security", "deep-copy-subject-mode").get("result").asBoolean(), "Configuration for Security Domain was updated");
	}
	@Test
	public void addSecurityDomain() {
		Inventory inventory = security.inventory();
		NewChildWizard child = inventory.childResources().newChild("Security Domain");
		child.getEditor().setText("resourceName", securityDomain.getName());
		child.next();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=security", "security-domain", securityDomain.getName(), true);
		securityDomain.assertExists(true);
	}
	@Test(dependsOnMethods={"addSecurityDomain"})
	public void configureSecurityDomain() {
		Configuration config = securityDomain.configuration();
		CurrentConfig current = config.current();
		current.getEditor().checkBox(0, false);
		current.getEditor().checkRadio("cache-type[1]");
		current.save();
		config.history().failOnFailure();
		Assert.assertTrue(mgmtClient.readAttribute("/subsystem=security/security-domain="+securityDomain.getName(), "cache-type").get("result").asString().equals("infinispan"), "Configuration for Security Domain was updated");
	}

	@DataProvider
	public Object[][] domainOperationsDataProvider() {
		String[] types = new String[] {"List Cached Principals","Flush Cache"};
		Object[][] output = new Object[types.length][];
		for (int i=0;i<types.length;i++) {
			output[i] = new Object[] {types[i]};
		}		
		return output;
	}
	
	@Test(dataProvider="domainOperationsDataProvider")
	public void securityDomainOperations(String opName) {
		Operations operations = security.child("jboss-web-policy").operations();
		Operation op = operations.newOperation(opName);
		op.schedule();
		operations.assertOperationResult(op, true);
	}
	
	@Test(alwaysRun=true,dependsOnMethods={"configureSecurityDomain"})
	public void removeSecurityDomain() {
		securityDomain.delete();
		mgmtClient.assertResourcePresence("/subsystem=security", "security-domain", securityDomain.getName(), false);
		securityDomain.assertExists(false);
	}

}
