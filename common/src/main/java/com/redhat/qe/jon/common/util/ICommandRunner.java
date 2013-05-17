package com.redhat.qe.jon.common.util;

import java.io.IOException;

import com.redhat.qe.tools.SSHCommandResult;

/**
 * This intefrace defines Command Runner interface. 
 * @author lzoubek@redhat.com
 *
 */
public interface ICommandRunner {

	/**
	 * copies file from destination
	 * @param srcPath remote src path
	 * @param destDir must always be on local machine
	 * @throws IOException
	 */
	void getFile(String srcPath,String destDir) throws IOException;
	
	/**
	 * copies file to destination
	 * @param srcPath must always be on local machine
	 * @param destDir
	 * @throws IOException
	 */
	void copyFile(String srcPath,String destDir) throws IOException;
	/**
	 * copies file
	 * @param srcPath must always be on local machine
	 * @param destDir
	 * @param destFileName name of destination file
	 * @throws IOException
	 */
	void copyFile(String srcPath, String destDir, String destFileName) throws IOException;
	/**
	 * runs a command
	 * @param command
	 * @return returns command result
	 */
	SSHCommandResult runAndWait(String command);
	/**
	 *  runs a command with timeout
	 * @param command
	 * @param commandTimeout
	 * @return returns command result
	 */
	SSHCommandResult runAndWait(String command, long commandTimeout);
	/**
	 * disconnects (useful for remote runner implementations)
	 */
	void disconnect();
	/**
	 * connects (useful for remote runner implementations)
	 */
	void connect();
	
	/**
	 * Returns true when the instance is remote
	 * @return
	 */
	boolean isRemote();
}
