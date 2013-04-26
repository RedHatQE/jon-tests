package org.rhq.remoting.cli.examples.test;

import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.remoting.cli.examples.Login;

public class TestUtil {

    /**
     * a helper method that returns connected and authenticated client.
     * RHQ defaults are used, RHQ/JBoss ON host is <b>rhq.server.host</b> system property 
     * or <b>localhost</b> by default
     * @return
     */
    public static RemoteClient createClient() {
	return createClient("rhqadmin", "rhqadmin");
    }
    /**
     * a helper method that returns connected and authenticated client.
     * RHQ defaults are used, RHQ/JBoss ON host is <b>rhq.server.host</b> system property 
     * or <b>localhost</b> by default
     * @return
     */
    public static RemoteClient createClient(String username, String password) {
	RemoteClient client =  new Login().login(System.getProperty("rhq.server.host","localhost"), 7080, username, password);
	if (client==null) {
	    throw new RuntimeException("RemoteClient could not be initialized, did you pass correct \'rhq.server.host\' system property?");
	}
	return client;
    }
}
