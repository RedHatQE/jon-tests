import org.testng.annotations.*
import org.testng.TestNG
import org.testng.TestListenerAdapter
// use cool mechanism to get http-builder dependency
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1' )
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 12.12.2011
 */
class LoginTest {

    private client

    @BeforeClass
    def setUp() {
	print '[jon.server.url] = '+System.properties['jon.server.url']
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

    @Test(expectedExceptions=groovyx.net.http.HttpResponseException.class)
    void loginUnauthorized() {
    	client.get( path : 'status.json')
    }

    @Test(dependsOnMethods='loginUnauthorized',expectedExceptions=groovyx.net.http.HttpResponseException.class)
    void loginInvalidCredentials() {
    	client.auth.basic 'myUserName', 'myPassword'
    	client.get( path : 'status.json')
    }

    @Test(dependsOnMethods='loginInvalidCredentials')
    void login() {
    	client.auth.basic System.properties['rest.username'], System.properties['rest.password']
    	client.get( path : 'status.json')
    }
}
