package org.rhq.remoting.cli.examples.test;



import org.junit.Assert;
import org.junit.Test;
import org.rhq.enterprise.clientapi.RemoteClient;


public class LoginTest {

    @Test
    public void loginTest() {
	RemoteClient client = TestUtil.createClient();
	Assert.assertNotNull(client);
	client.logout();
    }
}
