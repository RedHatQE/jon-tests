package com.redhat.qe.jon.clitest.tasks;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.redhat.qe.jon.common.util.ICommandRunner;
import com.redhat.qe.jon.common.util.LocalCommandRunner;
import com.redhat.qe.jon.common.util.SSHClient;
import com.redhat.qe.tools.SSHCommandResult;

/**
 * @author jkandasa (Jeeva Kandasamy), lzoubek (Libor Zoubek)
 * Feb 02, 2012
 */
public class CliTasks {
	private static Logger _logger = Logger.getLogger(CliTasks.class.getName());
	protected ICommandRunner commandRunner;
	protected long COMMAND_TIMEOUT = 1000*60*10; //Timeout for commands
	private static CliTasks cliTasks;
	
	public static CliTasks getCliTasks(){
		if(cliTasks == null){
			cliTasks = new CliTasks(); 
		}
		return cliTasks;
	}
	public void initialize(String hostName, String userName, String passWord) {
		_logger.info("Initializing...");
		if (hostName==null || userName==null||passWord==null) {
			_logger.info("remote host or credentials are not set, using local command runner");
			commandRunner = new LocalCommandRunner(".");
		}
		else {
			commandRunner = new SSHClient(userName,hostName,passWord);			
		}
		commandRunner.connect();
	}
	public boolean isRemote(){
		return commandRunner.isRemote();
	}
	
	/**
	 * copies file from remote host
	 * @param src source file
	 * @param dest remote dir
	 * @throws IOException
	 */
	public void getFile(String src, String dest) throws IOException {
		commandRunner.getFile(src, dest);
	}
	/**
	 * copies file to remote host
	 * @param src source file
	 * @param dest remote dir
	 * @throws IOException
	 */
	public void copyFile(String src, String dest) throws IOException {
		commandRunner.copyFile(src, dest);
	}
	/**
	 * copies file to remote host when you can specify destination file name within 'destFileName' param
	 * @param srcPath source file
	 * @param destDir remote dir
	 * @param destFileName remote fileName
	 * @throws IOException
	 */
	public void copyFile(String srcPath, String destDir, String destFileName) throws IOException {
		commandRunner.copyFile(srcPath, destDir, destFileName);
	}
	
	public void closeConnection(){
		commandRunner.disconnect();
	}
	public String runCommand(String command) throws CliTasksException{
		return runCommand(command, COMMAND_TIMEOUT);
	}
	public String runCommand(String command, long commandTimeout) throws CliTasksException{
		SSHCommandResult result = commandRunner.runAndWait(command, commandTimeout);
		String output = result.getStdout();
		String error = result.getStderr();
		if(error.trim().length() > 0){
			_logger.log(Level.INFO, output);
			_logger.log(Level.SEVERE, error);			
			throw new CliTasksException("Found error logs on Error Stream :"+error);
		}
		return output;
	}
	
	public static String[] getArrayofCommaValues(String commaValues){
		return commaValues.split(",");
	}
	
	public void validateErrorString(String consoleOutput, String errorString) throws CliTasksException{
		for(String validateString : getArrayofCommaValues(errorString)){
			if(consoleOutput.contains(validateString.trim())){
				throw new CliTasksException("Result contains error string: "+validateString.trim());
			}else{
				_logger.log(Level.INFO, "Error String not available: ["+validateString.trim()+"]");
			}
		}		
	}
	
	public void validateExpectedResultString(String consoleOutput, String resultString) throws CliTasksException{
		for(String validateString : getArrayofCommaValues(resultString)){
			if(!consoleOutput.contains(validateString.trim())){
				throw new CliTasksException("Result doesn't contain: "+validateString.trim());
			}else{
				_logger.log(Level.INFO, "Expected result available: ["+validateString.trim()+"]");
			}
		}		
	}
}