package com.redhat.qe.jon.sahi.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
/**
 * TestNG listener that intercepts {@link AgentLog} watcher on test method calls.
 * Watching and checking agent.log can be enabled either globally by setting <b>com.redhat.qe.jon.AgentLogCheckEnabled</b> property
 * or on test class or method by adding {@link CheckAgentLog} annotation. 
 * @author lzoubek
 *
 */
public class AgentLogCheckTestNGListener implements ITestListener,ISuiteListener {

	protected static Logger log = Logger.getLogger(AgentLogCheckTestNGListener.class.getName());
	
	
	private AgentLogHandle classWatcher = null;
	private AgentLogHandle globalWatcher = null;
	/**
	 * finds {@link CheckAgentLog} annotation in given class or recursive in super classes 
	 * @param klass
	 * @return
	 */
	private CheckAgentLog getClassAnnotation(Class<?> klass) {
		if (klass==null || Object.class.equals(klass)) {
			return null;
		}
		CheckAgentLog check = klass.getAnnotation(CheckAgentLog.class);
		if (check!=null) {
			return check;
		}		
		return getClassAnnotation(klass.getSuperclass());
	}
	/**
	 * creates new instance of agent log based on {@link CheckAgentLog annotation}
	 */
	private AgentLogHandle create(CheckAgentLog check) {
		if (check==null) {
			return null;
		}
		if (!check.enabled()) {
			// user requires to turn off checker
			return new AgentLogHandle(null, false);
		}
		AgentLogHandle inst = null;
		if ("".equals(check.host()) || "".equals(check.user()) || "".equals(check.pass())) {
			if ("".equals(check.agentHome())) {
				inst = new AgentLogHandle(AgentLog.createDefault());
			}
			else {
				inst = new AgentLogHandle(new AgentLog(new SSHClient(), check.agentHome()));
			}
		}
		else {
			SSHClient client = new SSHClient(check.user(), check.host(), check.pass());
			if ("".equals(check.agentHome())) {
				inst = new AgentLogHandle(new AgentLog(client));
			}
			else {
				inst = new AgentLogHandle(new AgentLog(client, check.agentHome()));
			}
		}
		if (inst!=null) {
			log.fine("Returning new AgentLog "+inst.toString());
		}
		return inst;
	}
	private void disconnectWatcher(AgentLogHandle watcher) {
		if (watcher!=null && watcher.getAgentLog()!=null) {
			watcher.getAgentLog().disconnect();
			watcher.setEnabled(false);
		}
		watcher = null;
	}
	@Override
	public void onFinish(ITestContext context) {
		
	}

	@Override
	public void onStart(ITestContext context) {

		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		disconnectWatcher(classWatcher);		
	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		disconnectWatcher(classWatcher);
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		disconnectWatcher(classWatcher);
		
	}

	@Override
	public void onTestStart(ITestResult result) {		
		Class<?> klass = result.getTestClass().getRealClass();
		// find our annotation on method level
		CheckAgentLog check = result.getMethod().getMethod().getAnnotation(CheckAgentLog.class);
		if (check==null) {
			// not found .. lets look at class level
			check = getClassAnnotation(klass);
		}
		classWatcher = create(check);		
		if (classWatcher!=null && classWatcher.isEnabled()) {
			log.fine("Enabling class level checker "+classWatcher.toString()+ " for class "+klass.getCanonicalName());
			classWatcher.getAgentLog().watch();
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		log.fine("Examining agent.log...");
		// class/method watcher has always higher priority
		AgentLogHandle watcher = classWatcher;
		if (watcher==null) {
			watcher = globalWatcher;
		}			
		if (watcher!=null && watcher.isEnabled()) {
			List<String> errorLines = watcher.getAgentLog().errorLines();
			if (!errorLines.isEmpty()) {
				log.warning("There were ERRORs in agent.log, seting test result as FAILED");
				result.setStatus(ITestResult.FAILURE);
				result.setThrowable(new RuntimeException("Following error lines were found in "+watcher.toString()+" "+Arrays.toString(errorLines.toArray())));
			}
		}			
		disconnectWatcher(classWatcher);
	}
	@Override
	public void onFinish(ISuite arg0) {
		disconnectWatcher(globalWatcher);		
	}
	@Override
	public void onStart(ISuite arg0) {
		if (System.getProperty("com.redhat.qe.jon.AgentLogCheckEnabled")!=null) {
			globalWatcher = new AgentLogHandle(AgentLog.createDefault());
			log.fine("Enabling global agent.log checker "+globalWatcher.toString());
		}
	}
	/**
	 * this handles watcher together with a flag whether it's enabled or not
	 * @author lzoubek
	 *
	 */
	private static class AgentLogHandle {
		private final AgentLog agentLog;
		private boolean enabled = true;
		private AgentLogHandle(AgentLog al, boolean enabled) {
			this.agentLog = al;
			this.enabled = enabled;
		}
		private AgentLogHandle(AgentLog al) {
			this.agentLog = al;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public AgentLog getAgentLog() {
			return agentLog;
		}
		@Override
		public String toString() {
			if (agentLog!=null) {
				return agentLog.toString()+"(enabled="+enabled+")";
			}
			else {
				return "null (enabled="+enabled+")";
			}
		}
	}
}
