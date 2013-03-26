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
	return new Login().login(System.getProperty("rhq.server.host","localhost"), 7080, "rhqadmin", "rhqadmin");
    }
}
