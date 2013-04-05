package org.rhq.remoting.ejbclient.examples.test;

import junit.framework.Assert;

import org.junit.Test;
import org.rhq.core.domain.auth.Subject;
import org.rhq.remoting.ejbclient.examples.Login;

public class LoginTest {

    @Test
    public void loginSuccessfull() {
	Subject subject = new Login().login("rhqadmin","rhqadmin");
	Assert.assertNotNull(subject);
    }
    
    @Test(expected=Exception.class)
    public void loginFailed() {
	Subject subject = new Login().login("foo","bar");
	Assert.assertNotNull(subject);
    }
}
