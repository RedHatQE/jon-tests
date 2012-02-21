package com.redhat.qe.jon.clitest.tasks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.redhat.qe.tools.SSHCommandRunner;
import com.trilead.ssh2.Connection;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Feb 02, 2012
 */
public class CliTasks {
	private static Logger _logger = Logger.getLogger(CliTasks.class.getName());
	protected SSHCommandRunner sshCommandRunner = null;
	protected Connection connection = null;
	protected long COMMAND_TIMEOUT = 1000*60*10; //Timeout for commands
	private static CliTasks cliTasks;
	
	public static CliTasks getCliTasks(){
		if(cliTasks == null){
			cliTasks = new CliTasks(); 
		}
		return cliTasks;
	}
	
	public void getConnection(String hostName, String userName, String passWord) throws IOException{
		getConnection(hostName, userName, passWord, 22);
	}
	
	public void getConnection(String hostName, String userName, String passWord, int port) throws IOException{
		if(connection == null){
			connection = new Connection(hostName, port);		
			connection.connect();
			connection.authenticateWithPassword(userName, passWord);
			sshCommandRunner = new SSHCommandRunner(connection, null);
		}
	}
	
	public void closeConnection(){
		if(connection != null){
			connection.close();
			connection = null;
			sshCommandRunner = null;
			_logger.log(Level.INFO, "Connection clossed successfully!");
		}else{
			_logger.log(Level.INFO, "Connection already closed!");
		}
	}
	public String runCommnad(String command) throws CliTasksException{
		return runCommnad(command, COMMAND_TIMEOUT);
	}
	public String runCommnad(String command, long commandTimeout) throws CliTasksException{
		_logger.log(Level.INFO, "Command --> "+command);
		sshCommandRunner.runCommandAndWait(command, commandTimeout);
		String output = sshCommandRunner.getStdout();
		String error = sshCommandRunner.getStderr();
		if(error.length() > 0){
			_logger.log(Level.INFO, output);
			_logger.log(Level.SEVERE, error);			
			throw new CliTasksException("Found error logs on Error Stream, For more details do debug on above log(s)...");
		}
		return output;
	}
	
	public static String[] getArrayofCommaValues(String commaValues){
		return commaValues.split(",");
	}
	
	public void validateErrorString(String consoleOutput, String errorString) throws CliTasksException{
		for(String validateString : getArrayofCommaValues(errorString)){
			if(consoleOutput.contains(validateString.trim())){
				throw new CliTasksException("Result Contains Error String: "+validateString.trim());
			}else{
				_logger.log(Level.INFO, "Error String not available: ["+validateString.trim()+"]");
			}
		}		
	}
	
	public void validateExpectedResultString(String consoleOutput, String resultString) throws CliTasksException{
		for(String validateString : getArrayofCommaValues(resultString)){
			if(!consoleOutput.contains(validateString.trim())){
				throw new CliTasksException("Result doesn't Contains: "+validateString.trim());
			}else{
				_logger.log(Level.INFO, "Expected result available: ["+validateString.trim()+"]");
			}
		}		
	}
}