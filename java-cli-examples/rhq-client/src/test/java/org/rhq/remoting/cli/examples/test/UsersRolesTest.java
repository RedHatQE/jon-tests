package org.rhq.remoting.cli.examples.test;



import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rhq.core.domain.auth.Subject;
import org.rhq.core.domain.authz.Role;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.UsersRoles;


public class UsersRolesTest {

    RemoteClient client;
    
    @Before
    public void initClient() {
	client = TestUtil.createClient();
    }
    
    @After
    public void logoutClient() {
	client.logout();
    }
    
    @Test
    public void createSubjectWithRole() {
        // create a new role
	Role role = new UsersRoles(client).createRole("testrole"+new Date().getTime());
	Assert.assertNotNull(role);

        // create a new subject with given role
	String subjName = "testsubject"+new Date().getTime();
	Subject subject = new UsersRoles(client).createSubject(subjName, "secure", role);
	Assert.assertNotNull(subject);

        // try to login using new subject
	RemoteClient cl = TestUtil.createClient(subjName, "secure");
	Assert.assertNotNull(cl);
	
    }
}
