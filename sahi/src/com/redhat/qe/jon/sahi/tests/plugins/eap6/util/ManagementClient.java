package com.redhat.qe.jon.sahi.tests.plugins.eap6.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import com.redhat.qe.auto.testng.Assert;
import com.redhat.qe.jon.sahi.tasks.Timing;
/**
 * AS 7 DMR Management client
 * @author lzoubek
 *
 */
public class ManagementClient {

	protected static final Logger log = Logger.getLogger(ManagementClient.class
			.getName());

	private final ModelControllerClient client;
	private CredentialsCallbackHandler authHandler = new CredentialsCallbackHandler(); 
	private String username = "admin"+new Date().getTime();
	private String password = "123456";
	private final String host;
	private final int port;
	
	/**
	 * gets the username that our client will pass to client API when requested
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * gets the password that our client will pass to client API when requested
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	public ManagementClient(String host, int port) {
		log.fine("Creating AS7 DMR Client that will conect to ["+host+":"+port+"]");
		this.host=host;
		this.port=port;
		client = createClient();
	}
	protected ModelControllerClient createClient() {
		ModelControllerClient client = null;
		try {
			client = ModelControllerClient.Factory.create(
					InetAddress.getByName(host), port,authHandler);
			return client;
		} catch (Exception e) {
			close();
			throw new RuntimeException(
					"Cannot create DMR Controller client for host: " + host+ ":" + port, e);
		}
	}
	
	public void close() {
		try {
			log.fine("Releasing managementClient resources...");
			client.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error when closing conection", e);
		}
	}
	
	protected class CredentialsCallbackHandler implements CallbackHandler {
		
		private boolean authRequested = false;
		
		public boolean isAuthRequested() {
			return authRequested;
		}
		public void setAuthRequested(boolean authRequested) {
			this.authRequested = authRequested;
		}
		
		public void handle(Callback[] callbacks) throws IOException,
				UnsupportedCallbackException {
            if (callbacks.length == 1 && callbacks[0] instanceof NameCallback) {
                ((NameCallback) callbacks[0]).setName("anonymous JBossTools user");
                return;
            }

            NameCallback name = null;
            PasswordCallback pass = null;
            for (Callback current : callbacks) {
	            if (current instanceof RealmCallback) {
	            	RealmCallback rcb = (RealmCallback) current;
                    String defaultText = rcb.getDefaultText();
                    rcb.setText(defaultText); // For now just use the realm suggested.
	            }
                if (current instanceof NameCallback) {
                    name = (NameCallback) current;
                } else if (current instanceof PasswordCallback) {
                    pass = (PasswordCallback) current;
                } 
            }
            name.setName(getUsername());
            pass.setPassword(getPassword().toCharArray());
            setAuthRequested(true);
		}
	}

	private int repeatCount = 20;
	private int waitTime = 5000;

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}


	
	private void waitFor(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ModelNode createOperation(String address, String operation,
			String... params) {
		ModelNode op = new ModelNode();
		String[] pathSegments = address.split("/");
		ModelNode list = op.get("address").setEmptyList();
		for (String segment : pathSegments) {
			String[] elements = segment.split("=");
			if (elements.length == 2) {
				list.add(elements[0], elements[1]);
			}
		}
		op.get("operation").set(operation);
		if (params!=null) {
			for (String param : params) {
				String[] elements = param.split("=");
				op.get(elements[0]).set(elements[1]);
			}
		}
		return op;
	}

	public ModelNode executeOperation(final ModelNode op) throws IOException {
		log.fine("Execute operation : "+op.toString());
		ModelNode ret = client.execute(op);
		log.fine("Operation executed result: " + ret.toString());
		return ret;
	}
	/**
	 * checks, whether client's connection is valid, by simply calling some operation
	 */
	public void checkConnection() throws Exception{
		log.fine("Checking whether DMR connection is working");
		executeOperation(createOperation("", "read-children-names", "child-type=deployment"));
	}

	/**
	 * executes operation without a need to know result details, useful
	 * 
	 * @param address
	 * @param operation
	 * @param params
	 * @return true if operation was successfull
	 */
	public boolean executeOperationVoid(String address, String operation,
			String... params) {
		try {
			ModelNode ret = executeOperation(createOperation(address,
					operation, params));
			if ("success".equals(ret.get("outcome").asString())) {
				return true;
			} else {
				log.warning("Operation failed: " + ret.toString());
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public ModelNode executeOperationAndAssertSuccess(String msg,
			final ModelNode op) throws IOException {
		ModelNode ret = executeOperation(op);
		Assert.assertTrue("success".equals(ret.get("outcome").asString()), msg
				+ ret.get("failure-description").asString());
		return ret;
	}

	public ModelNode executeOperationAndAssertFailure(String msg,
			final ModelNode op) throws IOException {
		ModelNode ret = executeOperation(op);
		Assert.assertTrue("failed".equals(ret.get("outcome").asString()), msg);
		return ret;
	}
	/**
	 * checks whether given model node (server's response) contains flag 
	 * from server saying, that server needs to be reloaded (some config change required it)
	 * @param result
	 * @return
	 */
	public boolean reloadOrRestartRequired(ModelNode result) {
		ModelNode headers = result.get("response-headers");
		if (headers==null) {
			log.fine("headers null");
			return false;
		}
		return headers.get("process-state").asString().contains("required");
	}
	
	public ModelNode readAttribute(String address, String attribute) {
		log.fine("Read attribute ["+attribute+ "] of ["+address+"]");
		ModelNode op = createOperation(address, "read-attribute", new String[] {"name="+attribute});		
		try {
			op = executeOperation(op);
			return op;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.throwing(ManagementClient.class.getCanonicalName(),
					" readAttribute", e);
			e.printStackTrace();
			Assert.fail();
			return null;
		}
	}
	
	public boolean existsResource(String address, String childType,
			String resource) {
		log.fine("Exists resource [" + resource + "] of type ["+childType+"]?");
		ModelNode op = createOperation(address, "read-children-names",
				new String[] { "child-type=" + childType });
		try {
			op = executeOperation(op);
			log.fine("Operation executed result: " + op.toString());
			List<ModelNode> ds = op.get("result").asList();
			for (ModelNode mn : ds) {
				if (resource.equals(mn.asString())) {
					log.fine("Resource exists");
					return true;
				}
			}
			log.fine("Resource does not exist");
			return false;
		} catch (IOException e) {
			log.throwing(ManagementClient.class.getCanonicalName(),
					"existsResource", e);
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * invokes 'Reload' command on server
	 */
	public void reload() {
		executeOperationVoid("/", "reload", new String[]{});
		try {
			log.fine("Waiting "+Timing.toString(Timing.TIME_30S)+" for server to reload");
			Thread.currentThread().join(Timing.TIME_30S);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * asserts whether given resource is/is not present on server
	 * @param address
	 * @param childType
	 * @param resource that sould/should not exis
	 * @param exists true if assert for existence, false for non-existence
	 */
	public void assertResourcePresence(String address, String childType,
			String resource, boolean exists) {
		for (int i = 0; i < repeatCount; i++) {
			boolean existence = existsResource(address, childType, resource); 
			if (exists && existence) {
				Assert.assertTrue(exists, "[mgmt API] Resource \'"+resource+"\' exists");
				return;
			}
			if (!exists && !existence) {
				Assert.assertFalse(false, "[mgmt API] Resource \'"+resource+"\' exists");
				return;
			}
			waitFor(waitTime);
		}
		log.fine("Checking resurce (un)existence timed out");
		Assert.assertEquals(!exists, exists,"[mgmt API] Resource \'"+resource+"\' exists");
	}
	/**
	 * this does a dummy call to EAP mgmt API and detects, whether EAP requires authentication or not
	 * @return true if EAP requires auth
	 */
	public boolean isAuthRequired() {
		authHandler.setAuthRequested(false);
		if (executeOperationVoid("", "read-children-names", "child-type=deployment")) {
			return authHandler.isAuthRequested();
		}
		else {
			throw new RuntimeException("Unable to verify whether EAP requires authorization or not, invalid credentials?");
		}
	}

}
