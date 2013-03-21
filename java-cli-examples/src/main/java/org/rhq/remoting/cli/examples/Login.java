package org.rhq.remoting.cli.examples;

import org.rhq.enterprise.clientapi.RemoteClient;

public class Login {

    /**
     * an examle method that logs in to remote RHQ/JBoss ON server
     * @param host RHQ/JBoss ON server host or IP
     * @param port 
     * @param username
     * @param password
     * @return connected and authenticated remote client or null on error
     */
    public RemoteClient login(String host, int port, String username, String password) {
	RemoteClient client = new RemoteClient(host, port);
	try {
	    client.login(username, password);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}	
	return client;
    }
    public static void main(String[] agrs) {
	new Login().login("10.16.23.113", 7080, "rhqadmin", "rhqadmin");
    }
}
