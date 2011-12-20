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
 * @since 20.12.2011
 */
@Test(testName='Status') 
class StatusTest extends RestClientTest {

	@BeforeClass
	def setUp() {
		login()
	}
	@Test
	void acceptHeader() {
		acceptHeaderTest('status')
	}
	@Test
	void rhqVersion() {
		client.get( path : 'resource/platforms.json' ) { resp, json ->
			println json.values['SERVER_VERSION']
		}
		
	}
 }
