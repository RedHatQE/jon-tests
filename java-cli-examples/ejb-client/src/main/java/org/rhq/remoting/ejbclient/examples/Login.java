package org.rhq.remoting.ejbclient.examples;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.rhq.core.domain.auth.Subject;
import org.rhq.enterprise.server.auth.SubjectManagerRemote;

public class Login {

    public Login() {
	EjbClientConfiguration.configure();
    }
 
    public Subject login(String username, String password) {
	try {
	    SubjectManagerRemote subjectManager = lookupSubjectManager();
	    return subjectManager.login(username, password);
	    
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;
	}
    }
    
    private static SubjectManagerRemote lookupSubjectManager() throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        final String appName = "rhq";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        final String moduleName = "rhq-enterprise-server-ejb3";
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = "SubjectManagerBean";
        // the remote view fully qualified class name
        final String viewClassName = SubjectManagerRemote.class.getName();
        // let's do the lookup        
        return (SubjectManagerRemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
    }
}
