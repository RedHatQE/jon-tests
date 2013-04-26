package org.rhq.remoting.ejbclient.examples;

import org.jboss.logging.Logger;
import org.rhq.core.domain.auth.Subject;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
	Subject subject = new Login().login("rhqadmin","rhqadmin");
	log.info(subject);
    }

}
