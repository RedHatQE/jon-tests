package com.redhat.qe.jon.common.util;


import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.redhat.qe.tools.SSHCommandResult;
import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
/**
 * This class is a remote command runner that uses SSH as a transport protocol for executing remote commands 
 * @author lzoubek
 *
 */
public class SSHClient implements ICommandRunner {
	protected SSHCommandRunner sshCommandRunner = null;
	protected SCPClient scpClient = null;
	protected Connection connection = null;
	private final String user;
	private final String host;
	private final String pass;
	private final File keyFile;
	protected static final Logger log = Logger.getLogger(SSHClient.class.getName());

	public SSHClient(String user, String host, String pass) {		
		log.fine("Creating SSHClient that will connect to [" +user+"@"+host+"]");
		this.user = user;
		this.host  = host;
		this.pass = pass;
		this.keyFile = null;
	}
	/**
	 * 
	 * @param user
	 * @param host
	 * @param keyFile
	 * @param pass password to unlock private key
	 */
	public SSHClient(String user, String host, File keyFile, String pass) {
	    this.user = user;
	    this.host = host;
	    this.keyFile = keyFile;
	    this.pass = pass;
	}
	public String getHost() {
		return host;
	}
	public String getUser() {
		return user;
	}
	public String getPass() {
		return pass;
	}
	public File getKeyFile() {
	    return keyFile;
	}
	public SSHCommandRunner getSshCommandRunner() {
		return sshCommandRunner;
	}
	/**
	 * creates new SSHClient instance, input parameters are taken from environment variables
	 * HOST_USER - for user
	 * HOST_NAME - for server address
	 * HOST_PASSWORD - for user's password
	 */
	public SSHClient() {
		this(System.getenv().get("HOST_USER"), System.getenv().get("HOST_NAME"), System.getenv().get("HOST_PASSWORD"));
	}
	/**
	 * connects to SSH server. This method is a good choice if you wish to check your connection settings
	 */
	public void connect(){
		disconnect();
        connection = new Connection(host, 22);
		try {
			connection.connect();
			if (this.keyFile==null) {
			    connection.authenticateWithPassword(user, pass);
			}
			else {
			    connection.authenticateWithPublicKey(user, keyFile, pass);
			}
			sshCommandRunner = new SSHCommandRunner(connection, null);
			scpClient = new SCPClient(connection);
		} catch (IOException e) {
			connection = null;
			throw new RuntimeException(
					"Cannot connect to SSH as " + user+"@"+host, e);
		}
	}
	public boolean isConnected() {
		if (connection!=null) {
            try {
                connection.ping();
                return true;
            } catch (Exception e) {
                log.info("Ping unsuccessful => you should try to use a new connection");
                return false;
            }
        } else {
            return false;
        }
	}

	/**
	 * runs given command and waits for return value on SSH server, if client is not connected it will connect automatically, 
	 * don't forget to disconnect ;)
	 * @param command
	 */
	public SSHCommandResult runAndWait(String command) {
		if (!isConnected()) {
			connect();
		}
		return sshCommandRunner.runCommandAndWait(command);
	}
	/**
	 * runs given command and waits for return value on SSH server, if client is not connected it will connect automatically, 
	 * don't forget to disconnect ;)
	 * @param command
	 * @param timeoutMilis command timeout
	 */
	public SSHCommandResult runAndWait(String command, long timeoutMilis) {
		if (!isConnected()) {
			connect();
		}
		return sshCommandRunner.runCommandAndWait(command,timeoutMilis);

	}
	/**
	 * runs given command on SSH server, if client is not connected it will connect automatically, 
	 * don't forget to disconnect
	 * @param command
	 */
	public void run(String command) {
		if (!isConnected()) {
			connect();
		}
		sshCommandRunner.runCommand(command);
	}
	/**
	 * disconnects from SSH server
	 */
	public void disconnect() {
		if (isConnected()) {
			connection.close();
			connection = null;
		}
	}
	/**
	 * copies file to remote host
	 * @param srcPath source file
	 * @param destDir destination dir on remote host
	 * @throws IOException
	 */
	public void copyFile(String srcPath, String destDir) throws IOException {
		scpClient.put(srcPath, destDir);
		log.fine("File ["+srcPath+"] copied to "+connection.getHostname()+":"+destDir);
	}
	/**
	 * copies file to remote host when you can specify destination file name within 'destFileName' param
	 * @param srcPath source file
	 * @param destDir destinaion dir on remote host
	 * @param destFileName remote fileName
	 * @throws IOException
	 */
	public void copyFile(String srcPath, String destDir, String destFileName)throws IOException  {
		scpClient.put(srcPath, destFileName, destDir, "0600");
		log.fine("File ["+srcPath+"] copied to "+getHost()+":"+destDir+"/"+destFileName);
		
	}
	/**
	 * copies file from remote host 
	 * @param srcPath source file on remote host
	 * @param destDir destinaion dir on local host
	 * @throws IOException
	 */
	@Override
	public void getFile(String srcPath, String destDir) throws IOException {
		scpClient.get(srcPath, destDir);
		log.fine("File ["+srcPath+"] copied from "+getHost()+":"+destDir);
		
	}
	@Override
	public boolean isRemote() {
		return true;
	}
	
}
