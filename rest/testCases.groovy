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
    	client.get(path : resource, headers : [ Accept : 'application/xml' ]) { resp,xml ->	
		Assert.assertTrue resp.status == 200, 'Response status is 200'
		Assert.assertTrue resp.contentType == 'application/xml', 'reponse contentType is XML'
		print xml.class
	}
    	client.get(path : resource, headers : [ Accept : 'application/json' ]) { resp,json ->	
		Assert.assertTrue resp.status == 200, 'Response status is 200'
		Assert.assertTrue resp.contentType == 'application/json', 'reponse contentType is JSON'
		Assert.assertTrue json instanceof net.sf.json.JSON, 'response parsed to JSON object'
	}
    }
}

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 12.12.2011
 */
class LoginTest extends RestClientTest {

    @Test(expectedExceptions=groovyx.net.http.HttpResponseException.class)
    void loginUnauthorized() {
    	client.get( path : 'status')
    }

    @Test(dependsOnMethods='loginUnauthorized',expectedExceptions=groovyx.net.http.HttpResponseException.class)
    void loginInvalidCredentials() {
    	client.auth.basic 'myUserName', 'myPassword'
    	client.get( path : 'status')
    }

    @Test(dependsOnMethods='loginInvalidCredentials')
    void loginSuccessfull() {
    	login()
	client.get( path : 'status')
    }
}

/**
 * @author Libor Zoubek (lzoubek@redhat.com)
 * @since 13.12.2011
 */
class ResourceTest extends RestClientTest {

	@BeforeClass
	def setUp() {
		login()
	}
	@Test
	void platforms() {
		acceptHeaderTest('resource/platforms')
	}

	void visitResource(href) {
		println "VisitResource href=${href}"
		client.get( path : href ) { resp, json -> 
			println "Visited ${json.resourceId} : ${json.resourceName}"
			json.links.each { link ->
				// find link to resource's children
				if (link.get('rel') == 'children') {
					// list children
					client.get( path : link.get('href')) { resp2, json2 ->
						for (child in json2) {
							println "Found child ${child.resourceId}"
							// find each child link & recurse
							child.links.each { l -> 
								if (l.get('rel') == 'self') {
									visitResource(l.get('href'))
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	void visitAllResources() {
		client.get( path : 'resource/platforms.json' ) { resp, json ->
			json.each {
				it.links.each { link ->
					if (link.get('rel') == 'self') {
						visitResource(link.get('href'))
					}
				}
			}
		}
	} 
 }
