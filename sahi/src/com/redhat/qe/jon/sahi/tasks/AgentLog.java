package com.redhat.qe.jon.sahi.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.Assert;

/**
 * this class provides access to <b>agent.log</b> file that contains output messages from agent
 * it internaly uses {@link SSHClient}
 * @author lzoubek
 *
 */
public class AgentLog {

	/**
	 * creates default instance of AgentLog only if
	 * environment all variables $HOST_USER $HOST_NAME $HOST_PASSWORD are defined
	 * otherwise returns null
	 * @return
	 */
	public static AgentLog createDefault() {
		String user = System.getenv().get("HOST_USER");
		String host = System.getenv().get("HOST_NAME");
		String pass = System.getenv().get("HOST_PASSWORD");
		if (user==null || host == null || pass == null) {
			log.fine("Required environment variables HOST_USER HOST_NAME HOST_PASSWORD are not defined - default AgentLog instance not created");
			return null;
		}
		return new AgentLog();
	}
	
	protected static final Logger log = Logger.getLogger(AgentLog.class.getName());
	private final String agentHome;
	private final SSHClient client;
	private final SSHClient backgoundClient;
	private int startLine = -1;
	/**
	 * creates new instance of AgentLog
	 * @param client SSH Client that defines user@host + pass
	 * @param agentHome either absolute or relative path to agent home directory
	 */
	public AgentLog(SSHClient client, String agentHome) {
		this.agentHome = agentHome;
		this.client = client;
		client.connect();
		this.backgoundClient = new SSHClient(client.getUser(), client.getHost(), client.getPass());
		this.backgoundClient.connect();
	}
	/**
	 * creates new instance of AgentLog with default {@link AgentLog#getAgentHome()} which is <b>rhq-agent</b>
	 * @param client SSH Client that defines user@host + pass
	 */
	public AgentLog(SSHClient client) {
		this(client,"rhq-agent");
	}
	/**
	 * creates new instance of AgentLog with default {@link AgentLog#getAgentHome()} which is <b>rhq-agent</b>
	 * and default {@link SSHClient#SSHClient()}
	 */
	public AgentLog() {
		this(new SSHClient());
	}
	/**
	 * gets agent HOME directory - either absolute or relative path
	 * @return
	 */
	public String getAgentHome() {
		return agentHome;
	}
	private String getAgentLogfile() {
		return getAgentHome()+"/logs/agent.log";
	}
	/**
	 * checks whether agent.log file exists
	 * @return
	 */
	public boolean existsLogFile() {
		return existsFile(getAgentLogfile());
	}
	private boolean existsFile(String file) {
		return client.runAndWait("ls "+file).getExitCode().equals(0);
	}
	private int getLineNumbers(String file) {
		String line = client.runAndWait("cat "+file+" | wc -l").getStdout();
		try 
		{
			return Integer.parseInt(line.trim());
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to parse line number count of agent log "+getAgentLogfile(),ex);
		}
	}
	/**
	 * starts watching <b>agent.log</b> {@link AgentLog#getAgentLogfile()}
	 */
	public void watch() {
		this.startLine = getLineNumbers(getAgentLogfile());
	}
	public void disconnect() {
		client.disconnect();
		backgoundClient.disconnect();
	}
	/**
	 * returns content of <b>agent.log</b> since {@link AgentLog#watch()} 
	 * or {@link AgentLog#getContent()} was called. Note that calling this also calls {@link AgentLog#watch()}
	 * so you get only appended content
	 * @return
	 */
	public String getContent() {
		if (this.startLine<0) {
			return "";
		}
		int current = getLineNumbers(getAgentLogfile());
		int lines = current - this.startLine;		
		if (lines==0) {
			this.startLine = -1;
			return "";
		}
		if (lines>0) {
			this.startLine = -1;
			return client.runAndWait("tail -n "+lines+" "+getAgentLogfile()).getStdout();
		}
		else {
			// it seems'like log files was rotated, we'll return tail of agent.log.1 + whole agent.log
			String rotatedLog = getAgentLogfile()+".1";
			StringBuilder sb = new StringBuilder();
			if (existsFile(rotatedLog)) {
				current = getLineNumbers(rotatedLog);
				lines = current - this.startLine;
				if (lines>0) {
					sb.append(client.runAndWait("tail -n "+lines+" "+rotatedLog).getStdout());
				}				
			}
			sb.append(client.runAndWait("cat "+getAgentLogfile()).getStdout());
			this.startLine = -1;
			return sb.toString();
		}
	}
	/**
	 * returns content of <b>agent.log</b> since {@link AgentLog#watch()} 
	 * or {@link AgentLog#getContent()} was called. Note that calling this also calls {@link AgentLog#watch()}
	 * so you get only appended content
	 * @param grep expression to filter results
	 * @return
	 */
	public String getContent(String grep) {
		if (this.startLine<0) {
			return "";
		}
		int current = getLineNumbers(getAgentLogfile());
		int lines = current - this.startLine;		
		if (lines==0) {
			this.startLine = -1;
			return "";
		}
		if (lines>0) {
			this.startLine = -1;
			return client.runAndWait("tail -n "+lines+" "+getAgentLogfile()+" | grep "+grep).getStdout();
		}
		else {
			// it seems'like log files was rotated, we'll return tail of agent.log.1 + whole agent.log
			String rotatedLog = getAgentLogfile()+".1";
			StringBuilder sb = new StringBuilder();
			if (existsFile(rotatedLog)) {
				current = getLineNumbers(rotatedLog);
				lines = current - this.startLine;
				if (lines>0) {
					sb.append(client.runAndWait("tail -n "+lines+" "+rotatedLog+" | grep "+grep).getStdout());
				}				
			}
			sb.append(client.runAndWait("cat "+getAgentLogfile()+" | grep "+grep).getStdout());
			this.startLine = -1;
			return sb.toString();
		}
	}
	/**
	 * 
	 * @return all ERROR lines from <b>agent.log</b> since {@link AgentLog#watch()} was called
	 */
	public List<String> errorLines() {
		String content = getContent("\' ERROR \'").trim();
		String[] lines = content.split("\n");
		List<String> errorLines = new ArrayList<String>();
		for (String line : lines) {
			if (line.length()>0) {
				errorLines.add(line);
			}
		}
		return errorLines;
	}
	/**
	 * this is a convenient method for asserting that no <b>ERROR</b> message appended 
	 * into <b>agent.log</b> since {@link AgentLog#watch()} was called
	 * if such line is found, {@link Assert#fail()} is raised.
	 */
	public void assertNoError() {
		List<String> errorLines = errorLines();
		if (!errorLines.isEmpty()) {
			Assert.fail("Following ERROR lines were found in remote agent.log :"+Arrays.toString(errorLines.toArray()));
		}
	}
	/**
	 * this method enables <b>agent.log</b> messages being redirected to this logger with level {@link Level#FINE} and prefix <b>[agent]</b>
	 * this starts separate SSH session and background thread - so its completely independent from other methods of this class
	 */
	public void startRedirectingLog() {
		startRedirectingLog("[agent]",Level.FINE);
	}
	/**
	 * this method enables <b>agent.log</b> messages being redirected to this logger
	 * this starts separate SSH session and background thread - so its completely independent from other methods of this class
	 * @param level of agent messages
	 * @param prefix of each message line
	 */
	public void startRedirectingLog(String prefix, final Level level) {
		this.backgoundClient.getSshCommandRunner().runCommand("tail -f "+getAgentLogfile());	
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				InputStream is = backgoundClient.getSshCommandRunner().getStdoutStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						log.log(level,"[agent] "+line);
					}
					Thread.currentThread().join(200);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}});
		thread.start();
		log.fine("Redirecting enabled for log  ["+client.getHost()+":"+getAgentLogfile()+"]");
	}
	/**
	 * this method disables agent.log messages being redirected to classic logger
	 */
	public void stopRedirectingLog() {
		this.backgoundClient.getSshCommandRunner().reset();
		this.backgoundClient.connect();
		log.fine("Redirecting disabled for log ["+client.getHost()+":"+getAgentLogfile()+"]");
	}
	@Override
	public String toString() {
		return "["+client.getUser()+"@"+client.getHost()+":"+getAgentLogfile()+"]";
	}
}
