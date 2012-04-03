import org.testng.annotations.*
import org.testng.TestNG
import com.redhat.qe.auto.testng.Assert
import org.testng.TestListenerAdapter
// use cool mechanism to get http-builder dependency
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1' )
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC


/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 13.12.2011
 */
class RestClientTest {

    protected client

    @BeforeClass
    def baseSetUp() {
	// print '[jon.server.url] = '+System.properties['jon.server.url']
	client = new RESTClient( System.properties['jon.server.url'] + '/rest/1/' )
	// check wheter JON is UP
	try {
		client.get( path : 'status.json' )
	}
	catch (org.apache.http.conn.HttpHostConnectException ex) {
		Assert.fail(ex.message)
	}
	catch (Exception ex) {
		// that is OK, should fail on Unauthorized
	}
    }

    def login() {
    	client.auth.basic System.properties['rest.username'], System.properties['rest.password']
    }

    def acceptHeaderTest(resource) {
    	println 'Accept header [application/xml]'
	client.get(path : resource, headers : [ Accept : 'application/xml' ]) { resp,xml ->	
		Assert.assertTrue resp.status == 200, 'Response status is 200'
		Assert.assertTrue resp.contentType == 'application/xml', 'reponse contentType is XML'
		print xml.class
	}
    	println 'Accept header [application/json]'
    	client.get(path : resource, headers : [ Accept : 'application/json' ]) { resp,json ->	
		Assert.assertTrue resp.status == 200, 'Response status is 200'
		Assert.assertTrue resp.contentType == 'application/json', 'reponse contentType is JSON'
		Assert.assertTrue json instanceof net.sf.json.JSON, 'response parsed to JSON object'
	}
    }
}

