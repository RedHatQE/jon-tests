package com.redhat.qe.jon.clitest.tests.bundles;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.jon.clitest.base.CliEngine;
import com.redhat.qe.jon.clitest.base.CliTestRunner;
/**
 * Tests creating Bundle from distribution file via URL including HTTP Basic Auth, and trusted/untrusted SSL 
 * @author lzoubek
 *
 */
public class CreateBundleTest extends CliEngine {

    /**
     * name of bundle-dist file we expect 
     */
    private static final String bundleFile = "bundle.zip";
    private String bundleServer;
    
    @BeforeClass
    public void beforeClass() {
	bundleServer = System.getProperty("jon.bundle.server",null);
	String message = "System property [jon.bundle.server] must be defined (IP Address or hostname) which must serve /bundle.zip via HTTP and HTTPS (trusted) and HTTPS on 10443 (untrusted), and /basic/bundle.zip secured by Basic HTTP auth using rhqadmin:rhqadmin";
	log.info(message);
	Assert.assertNotNull(bundleServer,message);	
    }

    @Override
    public CliTestRunner createJSRunner(String jsFile) {
	return super.createJSRunner(jsFile).addDepends("/js-files/rhqapi.js");
    }
    
    @DataProvider
    public Object[][] createConfigurations() {
	List<BundleCase> cases = new ArrayList<BundleCase>();
	// public url without auth
	cases.add(new BundleCase("http://"+bundleServer+"/"+bundleFile,"","","name=Bundle App"));
	// public url with auth
	cases.add(new BundleCase("http://"+bundleServer+"/"+bundleFile,"foo","bar","name=Bundle App"));
	// with basic auth
	cases.add(new BundleCase("http://"+bundleServer+"/basic/"+bundleFile,"name=Bundle App"));	
	// unreachable host
	if(System.getProperty("rhq.build.version", "").contains("JON")){
	    cases.add(new BundleCase("http://1.2.3.4","Connection timed out"));
	}else{
	    cases.add(new BundleCase("http://1.2.3.4","refused"));
	}
	// unreachable port
	cases.add(new BundleCase("http://"+bundleServer+":6666","refused"));
	// wrong credentials
	cases.add(new BundleCase("http://"+bundleServer+"/basic/"+bundleFile,"foo","bar","401 Unauthorized"));
	// wrong file
	cases.add(new BundleCase("http://"+bundleServer+"/basic/"+bundleFile+".foo","404 Not Found"));
	// trusted https without auth
	cases.add(new BundleCase("https://"+bundleServer+"/"+bundleFile,"","","name=Bundle App"));
	// with auth
	cases.add(new BundleCase("https://"+bundleServer+"/basic/"+bundleFile,"name=Bundle App"));
	// untrusted https, this requires to add another certificate which is not in cacerts, adding virtual host and Listen directive in /etc/httpd/conf.d/ssl.conf
	cases.add(new BundleCase("https://"+bundleServer+":10443/"+bundleFile,"","","peer not authenticated"));	
	return getDataProviderArray(cases);
	
    }
    
    @Test(dataProvider="createConfigurations")
    public void createViaUrl(BundleCase bCase) {
	createJSRunner("bundles/uploadBundle.js")
	.withArg("bundleUrl", bCase.url)
	.withArg("authUser", bCase.user)
	.withArg("authPass", bCase.pass)
	.addExpect(bCase.expectOutput)
	.run();
    }
    
    public static class BundleCase {
	String url;
	String user;
	String pass;
	String expectOutput;
	public BundleCase(String url,String expect) {
	    this(url,"rhqadmin","rhqadmin",expect);
	}
	public BundleCase(String url, String user, String pass, String expect) {
	    this.url = url;
	    this.user = user;
	    this.pass = pass;
	    this.expectOutput = expect;
	}
	@Override
	public String toString() {
	    return user+"@"+pass+" url:"+url+" expected:"+expectOutput;
	}
    }
}
