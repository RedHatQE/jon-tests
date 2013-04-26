package org.rhq.remoting.ejbclient.examples;

import java.util.Properties;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

/**
 * this is an alternative way to configure EJB3 Client. By default it is enough to define all these properties in 
 * jboss-ejb-client.properties file and place it on class-path. But as we need to define RHQ/JBoss ON hostname 
 * from command line, we use this approach. 
 * @author lzoubek
 *
 */
public class EjbClientConfiguration {

    public static void configure() {
	String rhqServer = System.getProperty("rhq.server.host","localhost");
	Properties p = new Properties();
        {
            p.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
            p.put("remote.connections", "default");
            p.put("remote.connection.default.host", rhqServer);
            p.put("remote.connection.default.port", "3447");
            p.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
            //p.put("remote.connection.default.username", "testX");
            //p.put("remote.connection.default.password", "test1234");                      
            p.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
            p.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");  
        }
        EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(p);
        ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
        EJBClientContext.setSelector(selector);
    }
}
