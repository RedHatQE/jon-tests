package com.redhat.qe.jon.javacli;

import java.util.List;

import org.rhq.core.domain.auth.Subject;
import org.rhq.core.domain.authz.Role;
import org.rhq.core.domain.criteria.RoleCriteria;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.enterprise.server.bundle.BundleManagerRemote;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.redhat.qe.jon.common.ConfigurationLoader;

@Test(groups={"unit"})
@Guice(modules = RemoteClientGuiceModule.class)
public class HelloWorldTest {
	@Inject ConfigurationLoader config;
	
	public void foo() throws Exception {
		RemoteClient remoteClient = new RemoteClient(null, "localhost", 7080);
		Subject subject = remoteClient.login("rhqadmin", "rhqadmin"); 

		RoleCriteria criteria = new RoleCriteria();
		//criteria.
		List<Role> list = remoteClient.getRoleManager().findRolesByCriteria(subject, criteria);
		System.out.println(list);

		
		BundleManagerRemote bundleMgr = remoteClient.getBundleManager();		
		
		remoteClient.logout();
	}

}
