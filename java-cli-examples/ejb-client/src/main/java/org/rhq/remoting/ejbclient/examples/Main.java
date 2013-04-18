package org.rhq.remoting.ejbclient.examples;

import org.rhq.core.domain.auth.Subject;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
	Subject subject = new Login().login("rhqadmin","rhqadmin");
	System.out.println(subject);
    }

}
