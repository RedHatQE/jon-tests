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
 * @since 12.12.2011
 */
@Test(testName="Login")
class LoginTest extends RestClientTest {

    @Test(expectedExceptions=groovyx.net.http.HttpResponseException.class)
    void unauthorized() {
    	client.get( path : 'status')
    }

    @Test(dependsOnMethods='unauthorized',expectedExceptions=groovyx.net.http.HttpResponseException.class)
    void invalidCredentials() {
    	client.auth.basic 'myUserName', 'myPassword'
    	client.get( path : 'status')
    }

    @Test(dependsOnMethods='invalidCredentials')
    void successfull() {
    	login()
	client.get( path : 'status')
    }   
}
