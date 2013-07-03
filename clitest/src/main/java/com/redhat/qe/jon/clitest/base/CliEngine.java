package com.redhat.qe.jon.clitest.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Optional;

import com.redhat.qe.jon.clitest.tasks.CliTasks;
import com.redhat.qe.jon.clitest.tasks.CliTasksException;

public class CliEngine extends CliTestScript{
	private static Logger _logger = Logger.getLogger(CliEngine.class.getName());
	public static String cliShLocation;
	public static String rhqCliJavaHome;
	public static String rhqTarget;
	/**
	 * CLI test run listener 
	 */
	public static CliTestRunListener runListener;
	private String cliUsername;
	private String cliPassword;
	protected CliTasks cliTasks;
	/**
	 * this handles console output of latest test run
	 */
	protected String consoleOutput;
	
	private String jsFileName;
	private static String remoteFileLocation = "/tmp/";
	
	public static boolean isVersionSet = false;
		
	
	
	
	//@Parameters({"rhq.target","cli.username","cli.password","js.file","cli.args","expected.result","make.failure"})
	public void loadSetup(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, @Optional String makeFailure) throws IOException{
		if(rhqTarget != null)
			CliEngine.rhqTarget = rhqTarget;
		if(cliUsername != null)
			this.cliUsername = cliUsername;
		if(cliPassword != null)
			this.cliPassword = cliPassword;
	}
	
	private URL findResource(String path) {
	    try {
		_logger.fine("Looking up resource "+path);
		if (path.startsWith("/")) {
		    // we have to strip starting "/" because otherwise getClassLoader().getResources(path) finds nothing
		    path = path.substring(1);
		}
		Enumeration<URL> resources = getClass().getClassLoader().getResources(path);		
		URL candidate = null;
		if (!resources.hasMoreElements()) {
		    return null;
		}
		while (resources.hasMoreElements()) {
		    URL el = resources.nextElement();
		    _logger.fine("Found "+el.getFile());
		    if (new File(el.getFile()).exists()) {
			candidate = el;
		    }
		}
		if (candidate==null) {
		    candidate = getClass().getClassLoader().getResources(path).nextElement();
		}
		_logger.fine("Returning "+candidate.getFile()); 
		return candidate; 
		
	    } catch (IOException e) {
		return null;
	    }
	}
	
	private String getResourceFileName(String path) throws CliTasksException {
		if (path.startsWith("http://") || path.startsWith("https://")) {
			try {
				File file = File.createTempFile("temp", ".js");
				file.deleteOnExit();
				cliTasks.runCommand("wget -nv --no-check-certificate '"+path+"' -O "+file.getAbsolutePath()+" 2>&1");
				return file.getAbsolutePath();
			} catch (IOException e) {
				throw new CliTasksException("Unable to create temporary file", e);
			}
			
		}
		else if (path.startsWith("file:/")) {
		    try {
			File file = new File(new URI(path));
			return file.getAbsolutePath();
		    } catch (URISyntaxException e) {
			e.printStackTrace();
		    }
		}
		URL resource = null;
		resource = findResource(path);
		if (resource==null) {
			_logger.fine("Appending js-files/ to resource path");
			resource = findResource("js-files/"+path);	
		}
		if (resource==null) {
			throw new RuntimeException("Unable to retrieve either ["+path+"] or [js-files/"+path+"] resource on classpath!");
		}
		if (new File(resource.getFile()).exists()) {
		    return resource.getFile();
		}
		try {
		    _logger.fine("Copying resource "+resource.getFile()+" from JAR");
		    File file = File.createTempFile("temp", ".tmp");
		    file.deleteOnExit();
		    InputStream is = resource.openStream();
		    OutputStream os = new FileOutputStream(file);
		    final byte[] buf = new byte[1024];
		    int len = 0;
		    while ((len = is.read(buf)) > 0) {
		        os.write(buf, 0, len);
		    }
		    is.close();
		    os.close();
		    return file.getAbsolutePath();
		}
		catch (IOException ex) {
		    throw new RuntimeException("Unable to copy ["+path+"] resource from classpath!");
		}
		
	}
	
	
	/**
	 * parameters resSrc and resDest must have same count of items and are processed together (resSrc[0] will be copied to resDst[0])
	 * @param rhqTarget
	 * @param cliUsername
	 * @param cliPassword
	 * @param jsFile to be executed
	 * @param cliArgs arguments passed
	 * @param expectedResult comma-separated list of messages that are expected as output
	 * @param makeFilure comma-separated list of messages - if these are found in output, test fails
	 * @param jsDepends comma-separated list of other JS files that are required/imported by <b>jsFile</b>
	 * @param resSrc comma-separated list of source paths
	 * @param resDst comma-separated list of source paths (must be same size as resSrc)
	 * @throws IOException
	 * @throws CliTasksException
	 */
	public void runJSfile(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, String jsFile, @Optional String cliArgs, @Optional String expectedResult, @Optional String makeFilure,@Optional String jsDepends,@Optional String resSrc, @Optional String resDst) throws IOException, CliTasksException{
		loadSetup(rhqTarget, cliUsername, cliPassword, makeFilure);
		cliTasks = CliTasks.getCliTasks();

		// process additional resource files
		if (resSrc!=null && resDst!=null) {
			prepareResources(resSrc, resDst);
		}
		String jsFilePath = getResourceFileName(jsFile);
		jsFileName = new File(jsFilePath).getName();
		
		// upload JS file to remote host first
		cliTasks.copyFile(jsFilePath, remoteFileLocation);
		if (jsDepends!=null) {
			prepareDependencies(jsFile, jsDepends,jsFilePath);
		}
		
		String command = "export RHQ_CLI_JAVA_HOME="+rhqCliJavaHome+"; ";
		// autodetect RHQ_CLI_JAVA_HOME if not defined
		if (StringUtils.trimToNull(rhqCliJavaHome)==null) {
			rhqCliJavaHome = cliTasks.runCommand("echo $JAVA_HOME").trim();
			if ("".equals(rhqCliJavaHome)) {
				log.info("Neither RHQ_CLI_JAVA_HOME nor JAVA_HOME environment variables were defined, trying to get java exe file location");
				command = "export RHQ_CLI_JAVA_EXE_FILE_PATH=`which java`; ";
			}else{
				_logger.log(Level.INFO,"Environment variable RHQ_CLI_JAVA_HOME was autodetected using JAVA_HOME variable");
				command = "export RHQ_CLI_JAVA_HOME="+rhqCliJavaHome+"; ";
			}
		}
		command += CliEngine.cliShLocation+" -s "+CliEngine.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+remoteFileLocation+jsFileName;
		if(cliArgs != null){
			command +=" "+cliArgs;
		}
		// get live output in log file on server
		command +=" | tee -a /tmp/cli-automation.log";
		consoleOutput = cliTasks.runCommand(command);
		_logger.log(Level.INFO, consoleOutput);
		if(!isVersionSet && consoleOutput.length()>25){
			System.setProperty("rhq.build.version", consoleOutput.substring(consoleOutput.indexOf("Remote server version is:")+25, consoleOutput.indexOf("Login successful")).trim());
			isVersionSet = true;
			_logger.log(Level.INFO, "RHQ/JON Version: "+System.getProperty("rhq.build.version"));
		}
				
		if(makeFilure != null){
			cliTasks.validateErrorString(consoleOutput , makeFilure);
		}
		if(expectedResult != null){
			cliTasks.validateExpectedResultString(consoleOutput , expectedResult);
		}
		
	}
	public CliTestRunner createJSRunner(String jsFile) {
	    return new CliTestRunner(this).jsFile(jsFile);
	}
	
	public final CliTestRunner createJSSnippetRunner(String jsSnippet) {
	    return createJSRunner(null).jsSnippet(jsSnippet);
	}
	
	public void runJSfile(String jsFile,String cliArgs,String expectedResult,String jsDepends,String resSrc, String resDes) 
			throws IOException, CliTasksException{
		runJSfile(null, "rhqadmin", "rhqadmin", jsFile, cliArgs, expectedResult, null, jsDepends, resSrc, resDes);
	}
	public void runJSfile(String jsFile,String cliArgs,String expectedResult,String jsDepends) throws IOException, CliTasksException{
		runJSfile(jsFile, cliArgs, expectedResult, jsDepends, null, null);
	}
	public void runJSfile(String jsFile,String cliArgs,String expectedResult) throws IOException, CliTasksException{
		runJSfile(jsFile, cliArgs, expectedResult, null);
	}
	public void runJSfile(String jsFile,String expectedResult) throws IOException, CliTasksException{
		runJSfile(jsFile, null, expectedResult);
	}
	public void runJSfile(String jsFile) throws IOException, CliTasksException{
		runJSfile(jsFile, "Login successful");
	}
	/**
	 * runs given javascript snippet
	 * @param snippet
	 * @param rhqTarget
	 * @param cliUsername
	 * @param cliPassword
	 * @param cliArgs
	 * @param expectedResult
	 * @param makeFilure
	 * @param jsDepends
	 * @param resSrc
	 * @param resDst
	 * @return text output of CLI client running this snippet
	 * @throws IOException
	 * @throws CliTasksException
	 */
    public String runJSSnippet(String snippet, String rhqTarget,
	    String cliUsername, String cliPassword, String cliArgs,
	    String expectedResult, String makeFilure, String jsDepends,
	    String resSrc, String resDst) throws IOException, CliTasksException {
	File tempFile = File.createTempFile("snippet", "js");
	PrintWriter pw = new PrintWriter(tempFile);
	pw.println(snippet);
	pw.close();
	runJSfile(rhqTarget, cliUsername, cliPassword, tempFile.toURI()
		.toString(), cliArgs, expectedResult, makeFilure, jsDepends,
		resSrc, resDst);
	return consoleOutput;
    }

	protected void prepareResources(String resSrc, String resDst)
			throws CliTasksException, IOException {
		_logger.info("Processing additional resources...");
		String[] sources = resSrc.split(",");
		String[] dests = resDst.split(",");
		if (sources.length!=dests.length) {
			throw new CliTasksException("res.src parameter must be same length as res.dst, please update your testng configuration!");
		}
		for (int i=0;i<sources.length;i++) {
			String src = sources[i];
			File dst = new File(dests[i]);
			String destDir = dst.getParent();
			
			if (destDir==null) {
				destDir="/tmp";
			}
			else if (!dst.isAbsolute()) {
				destDir="/tmp/"+destDir;
			}
							
			cliTasks.runCommand("mkdir -p "+destDir);
			if (src.startsWith("http")) {
				cliTasks.runCommand("wget -nv "+src+" -O "+destDir+"/"+dst.getName()+" 2>&1");
			}
			else {
				String resource = getResourceFileName(src);
				if (resource==null) {
					throw new CliTasksException("Resource file "+src+" does not exist!");
				}
				if (runListener!=null) {
				    try {
					File newResource = runListener.onResourceProcessed(src, new File(resource));						
					if (newResource!=null && newResource.exists() && newResource.isFile()) {
    					_logger.fine("Resource ["+resource+"] has been processed by listener, new result ["+newResource.getAbsolutePath()+"]");
    					resource = newResource.getAbsolutePath();
					}
					else {
					    throw new Exception("Resource file processed by listener is invalid (either null, non-existing or non-file)");
					}
				    } catch (Exception ex) {
					_logger.log(Level.WARNING, "CliTestRunListener failed, using original resource. Error : "+ex.getMessage(), ex);
				    }
				}
				cliTasks.copyFile(resource, destDir,dst.getName());
			}
		}
	}

	protected void prepareDependencies(String jsFile, String jsDepends, String mainJsFilePath)
			throws IOException, CliTasksException {
		int longestDepNameLength=0;
		Map<String,Integer> lines = new LinkedHashMap<String, Integer>(); 
		_logger.info("Preparing JS file depenencies ... "+jsDepends);
		for (String dependency : jsDepends.split(",")) {
			if (dependency.length()>longestDepNameLength) {
				longestDepNameLength = dependency.length();
			}
			String jsFilePath = getResourceFileName(dependency);
			lines.put(dependency, getFileLineCount(jsFilePath));
			cliTasks.copyFile(jsFilePath, remoteFileLocation, "_tmp.js");
			// as CLI does not support including, we must merge the files manually
			cliTasks.runCommand("cat "+remoteFileLocation+"_tmp.js >> "+remoteFileLocation+"_deps.js");
		}
		cliTasks.runCommand("rm "+remoteFileLocation+"_tmp.js");
		// finally merge main jsFile		
		cliTasks.runCommand("cat "+remoteFileLocation+jsFileName+" >> "+remoteFileLocation+"_deps.js && mv "+remoteFileLocation+"_deps.js "+remoteFileLocation+jsFileName);			
		_logger.info("JS file depenencies ready");
		_logger.info("Output file has been merged from JS files as follows:");
		int current = 0;
		lines.put(jsFile, getFileLineCount(mainJsFilePath));
		if (jsFile.length()>longestDepNameLength) {
			longestDepNameLength = jsFile.length();
		}
		_logger.info("===========================");
		for (String dep : lines.keySet()) {
			_logger.info("JS File: "+dep+createSpaces(longestDepNameLength-dep.length())+" lines: "+current+" - "+(current+lines.get(dep)));
			current+=lines.get(dep)+1;
		}
		_logger.info("===========================");
	}
	
	public void waitFor(int ms) {
		waitFor(ms, "Waiting");
	}
	public void waitFor(int ms,String message) {
		log.fine(message+" "+(ms/1000)+"s");
		if(ms<=0) {
			ms = 1;
		}
		try {
			Thread.currentThread().join(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterTest
	public void deleteJSFile(){
		try {
			CliTasks.getCliTasks().runCommand("rm -rf '"+remoteFileLocation+jsFileName+"'", 1000*60*3);
		} catch (CliTasksException ex) {
			_logger.log(Level.WARNING, "Exception on remote File deletion!, ", ex);
		}
	}
	private String createSpaces(int length) {
		StringBuilder sb = new StringBuilder();
		while (length>0) {
			sb.append(" ");
			length--;
		}
		return sb.toString();
	}
	private int getFileLineCount(String path) {
		BufferedReader reader = null;
		int lines = 0;
		try {
			reader = new BufferedReader(new FileReader(path));
			while (reader.readLine() != null) lines++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}
	
}
