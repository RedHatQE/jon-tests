package com.redhat.qe.jon.common.util;

import com.redhat.qe.Assert;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
/**
 * AS 7 DMR Management client
 * @author lzoubek
 *
 */
public class AS7DMRClient {

	protected static final Logger log = Logger.getLogger(AS7DMRClient.class
			.getName());

	private final ModelControllerClient client;
	private CredentialsCallbackHandler authHandler = new CredentialsCallbackHandler(); 
	private String username = "rhqadmin";
	private String password = "rhqadmin";
	private final String host;
	private final int port;

    private static final int TIMEOUT = 30000;
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * gets the username that our client will pass to client API when requested
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * gets the password that our client will pass to client API when requested
	 * @return passord
	 */
	public String getPassword() {
		return password;
	}
	
	public AS7DMRClient(String host, int port) {
		log.fine("Creating AS7 DMR Client that will conect to ["+host+":"+port+"]");
		this.host=host;
		this.port=port;
		client = createClient();
	}
	protected ModelControllerClient createClient() {
		ModelControllerClient client = null;
		try {
			client = ModelControllerClient.Factory.create(
					InetAddress.getByName(host).getHostAddress(), port,authHandler, null, TIMEOUT);
			return client;
		} catch (Exception e) {
			close();
			throw new RuntimeException(
					"Cannot create DMR Controller client for host: " + host+ ":" + port, e);
		}
	}
	
	public void close() {
		if (client==null) {
		    return;
		}
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
		boolean haveQuoted = false;
		String seg = "";
		for (String segment : pathSegments) {
			if (segment.endsWith("\\")) {
				haveQuoted=true;
				seg+=segment.replaceAll("\\\\", "/");
				continue;
			}
			else if (haveQuoted) {
				seg+=segment; // append last element of quoted name
				haveQuoted=false;
			}
			else {
				seg = segment;
			}
			String[] elements = seg.split("=");
			if (elements.length == 2) {
				list.add(elements[0], elements[1]);
			}
			seg="";
			haveQuoted=false;

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
	 * Waits for server to be in running state, throws TimeoutException in case server isn't running after specified timeout
	 */
	public void waitUntilRunning(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
		long startTime = System.currentTimeMillis();
		boolean running = false;
		while (!running && System.currentTimeMillis() < startTime + unit.toMillis(timeout)) {
			TimeUnit.SECONDS.sleep(1);
			try {
				ModelNode res = readAttribute("/", "server-state");
				if (res != null) {
					running = "success".equals(res.get("outcome").asString()) && "running".equals(res.get("result").asString());
				}
			} catch (Throwable e) {
				running = false;
			}
		}
		if (!running) throw new TimeoutException("Server not running after " + timeout + " " + unit.toString());
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
	 * @return true if operation was successful
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
	 * @return true if there's special repsonse-header 
	 */
	public boolean reloadOrRestartRequired(ModelNode result) {
		ModelNode headers = result.get("response-headers");
        log.fine("ReloadOrRestartRequired - response headers are: " + headers.toString());
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
			log.throwing(AS7DMRClient.class.getCanonicalName(),
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
			log.throwing(AS7DMRClient.class.getCanonicalName(),
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
		log.fine("Waiting maximum 60s for server to reload");
		try {
			TimeUnit.MILLISECONDS.sleep(500); // needed to not start checking before actual reload is started
			waitUntilRunning(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	/**
	 * invokes 'shutdown(restart=true)' command on server
	 */
	public void restart() {
		executeOperationVoid("/", "shutdown", new String[]{"restart=true"});
		log.fine("Waiting maximum 60s for server to restart");
		try {
			TimeUnit.MILLISECONDS.sleep(500); // needed to not start checking before actual restart is started
			waitUntilRunning(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
    
	/**
	 * @return true if resource on given address is present (returns some value when calling `read-resource` operation on it)
	 */
	public boolean isResourcePresent(String address) {
		return executeOperationVoid(address, "read-resource", new String[]{});
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
