package com.redhat.qe.jon.sahi.tasks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;


/**
 * @author jkandasa (Jeeva Kandasamy)
 * Sep 30, 2011
 */
public class DriftManagementSSH {

	private static Logger _logger = Logger.getLogger(DriftManagementSSH.class.getName());
	protected SSHCommandRunner sshCommandRunner = null;
	protected Connection connection = null;
	private static final String commandCreateDir = "mkdir -vp ";
	private static final String commandDeleteDir = "rm -rf "; 
	private static final long COMMAND_TIMEOUT = 1000*60*10; //Timeout for commands
	
	/*
	public static void main(String[] args) throws IOException{
		Connection conn = new Connection("10.65.201.40");		
		conn.connect();
		conn.authenticateWithPassword("root", "Lab123");		
		SSHCommandRunner sshcommand = new SSHCommandRunner(conn, "date");
		sshcommand.run();
		System.out.println("Date: "+sshcommand.getStdout());
		conn.close(); */
		/*
		SSHCommandRunner sshcr = new SSHCommandRunner("10.65.201.40", "root", "Lab123", "abc", "abcs", null);
		sshcr.runCommandAndWait("top -bd 2 -n 2", true);
		System.out.println("TOP: " + sshcr.getStdout());
		sshcr.runCommandAndWait("hostname", true);
		System.out.println("host Name: " + sshcr.getStdout());
		
	}
	*/
	public void getConnection(String hostName, String userName, String passWord) throws IOException{
		getConnection(hostName, userName, passWord, 22);
	}
	
	public void getConnection(String hostName, String userName, String passWord, int port) throws IOException{
		if(connection == null){
			connection = new Connection(hostName, port);		
			connection.connect();
			connection.authenticateWithPassword(userName, passWord);
			_logger.log(Level.FINE, "Is Authentication Complete? : "+connection.isAuthenticationComplete());
			sshCommandRunner = new SSHCommandRunner(connection, null);
		}
	}
	
	public void closeConnection(){
		if(connection != null){
			connection.close();
			connection = null;
			sshCommandRunner = null;
		}
	}
	
	private boolean runCommnad(String command, long commandTimeout){
		sshCommandRunner.runCommandAndWait(command, commandTimeout);
		_logger.log(Level.INFO, "Command["+command+"] Result: \n"+sshCommandRunner.getStdout());
		String error = sshCommandRunner.getStderr();
		if(error.length() > 0){
			_logger.log(Level.WARNING, "Command["+command+"] Error Message: \n"+sshCommandRunner.getStdout());
			return false;
		}
		return true;
	}
	
	public boolean addLineOnFile(String fileName, String fileContent, boolean append){
		if(append){
			return runCommnad("echo \""+fileContent+"\" >> "+fileName, COMMAND_TIMEOUT);
		}else{
			return runCommnad("echo \""+fileContent+"\" > "+fileName, COMMAND_TIMEOUT);
		}
	}
	
	public boolean createFileDir(String dirNamefullPath){
		 return runCommnad(commandCreateDir+dirNamefullPath, COMMAND_TIMEOUT);
	}
	
	public boolean createFileDir(String baseDir, String fileName){
		String dirName = null;
		if(fileName.contains("/")){
			dirName = fileName.substring(0,fileName.lastIndexOf("/"));
		}
		if(dirName != null){
			return createDirs(baseDir, dirName);
		}else{
			return true;
		}		
	}
	
	public boolean createDirs(String baseDir, String dirName){
		boolean status = true;
		if(!baseDir.endsWith("/")){
			baseDir += "/";
		}
		String[] dirs = dirName.split(",");
		for(String dir : dirs){
			dir = dir.substring(0, dir.lastIndexOf("/"));
			if(!createFileDir(commandCreateDir+baseDir+dir)){
				status = false;
			}
		}
		return status;		
	}
	
	public boolean deleteFilesDirs(String fileDir){
		return runCommnad(commandDeleteDir+fileDir, COMMAND_TIMEOUT);
	}	
	
}
