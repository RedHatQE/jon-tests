package com.redhat.qe.jon.clitest.base;

import com.redhat.qe.jon.clitest.tasks.*;
import com.redhat.qe.jon.common.util.*;
import org.apache.commons.lang3.*;
import org.testng.annotations.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class CliEngine extends CliTestScript {
	private static final Logger _logger = Logger.getLogger(CliEngine.class.getName());
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
	/**
	 * a list of filenames created during test execution that will be deleted AfterTest
	 */
	private List<String> tempFiles = new Vector<String>();
	
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
		    _logger.finer("Found "+el.getFile());
		    if (new File(el.getFile()).exists()) {
			candidate = el;
		    }
		}
		if (candidate==null) {
		    candidate = getClass().getClassLoader().getResources(path).nextElement();
		}
		_logger.finer("Returning "+candidate.getFile()); 
		return candidate; 
		
	    } catch (IOException e) {
		return null;
	    }
	}
	
	private String getResourceFileName(String path) throws CliTasksException {
		if (path.startsWith("http://") || path.startsWith("https://")) {
			try {
				File file = File.createTempFile("temp", ".js");
				tempFiles.add(file.getAbsolutePath());
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
		    tempFiles.add(file.getAbsolutePath());
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
	public void runJSfile(@Optional String rhqTarget, @Optional String cliUsername, @Optional String cliPassword, String jsFile, @Optional String cliArgs, @Optional String expectedResult, @Optional String makeFilure,@Optional String jsDepends,@Optional String resSrc, @Optional String resDst, List<AdditionalResource> resources) throws IOException, CliTasksException{
		loadSetup(rhqTarget, cliUsername, cliPassword, makeFilure);
		cliTasks = CliTasks.getCliTasks();

		// process additional resource files
		if (resSrc!=null && resDst!=null) {
			prepareResources(resSrc, resDst);
		}
		if (cliArgs==null) {
		    cliArgs="";
		    if(resources.size() > 0){
		    	cliArgs = "--args-style=named ";
		    }
		}
		cliArgs += prepareResources(resources);
		String jsFilePath = getResourceFileName(jsFile);
		String targetFile = cliTasks.runCommand("mktemp").trim();
		tempFiles.add(targetFile);
		// upload JS file to remote host first
		cliTasks.copyFile(jsFilePath, new File(targetFile).getParent(), new File(targetFile).getName());
		if (jsDepends!=null) {
			prepareDependencies(jsFile, jsDepends,jsFilePath, targetFile);
		}
		
		String commandPrefix = "export RHQ_CLI_JAVA_HOME="+rhqCliJavaHome+"; ";
		// autodetect RHQ_CLI_JAVA_HOME if not defined
		if (StringUtils.trimToNull(rhqCliJavaHome)==null) {
			rhqCliJavaHome = cliTasks.runCommand("echo $JAVA_HOME").trim();
			if ("".equals(rhqCliJavaHome)) {
				log.info("Neither RHQ_CLI_JAVA_HOME nor JAVA_HOME environment variables were defined, trying to get java exe file location");
				commandPrefix = "export RHQ_CLI_JAVA_EXE_FILE_PATH=`which java`; ";
			}else{
				_logger.log(Level.INFO,"Environment variable RHQ_CLI_JAVA_HOME was autodetected using JAVA_HOME variable");
				commandPrefix = "export RHQ_CLI_JAVA_HOME="+rhqCliJavaHome+"; ";
			}
		}
		
		String command = commandPrefix + CliEngine.cliShLocation+" -s "+CliEngine.rhqTarget+" -u "+this.cliUsername+" -p "+this.cliPassword+" -f "+targetFile;
		command +=" "+cliArgs;

		// get live output in log file on server
		command +=" | tee -a /tmp/cli-automation.log";
		consoleOutput = cliTasks.runCommand(command);
		//_logger.log(Level.INFO, consoleOutput);
		if(!isVersionSet && consoleOutput.length()>25){
		    String cliClientVersionPrefix = "RHQ Enterprise Remote CLI";
		    String remoteServerVersionPrefix = "Remote server version is:";
		    String cliVersionOutput = cliTasks.runCommand(commandPrefix + CliEngine.cliShLocation+" -v");
		    String cliVersion = "CLI version: ";
		    if(cliVersionOutput.contains(cliClientVersionPrefix)){
		        cliVersion += cliVersionOutput.substring(cliVersionOutput.indexOf(cliClientVersionPrefix) + cliClientVersionPrefix.length(),
		                cliVersionOutput.indexOf(")")+1).trim();
		    }
		    String version = "";
		    if(consoleOutput.contains(remoteServerVersionPrefix)){
		        version += consoleOutput.substring(
		                consoleOutput.indexOf(remoteServerVersionPrefix)+remoteServerVersionPrefix.length(), 
		                consoleOutput.indexOf(")")+1).trim();
		    }
		    
		    // "\n" is there just because of nicer (shorter) view of version in reporting engine
			System.setProperty("rhq.build.version",version + "\n" + cliVersion); 
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
	    String resSrc, String resDst, List<AdditionalResource> resources) throws IOException, CliTasksException {
	File tempFile = File.createTempFile("snippet", "js");
	tempFiles.add(tempFile.getAbsolutePath());
	PrintWriter pw = new PrintWriter(tempFile);
	pw.println(snippet);
	pw.close();
	runJSfile(rhqTarget, cliUsername, cliPassword, tempFile.toURI()
		.toString(), cliArgs, expectedResult, makeFilure, jsDepends,
		resSrc, resDst, resources);
	return consoleOutput;
    }
    
    private String prepareResources(List<AdditionalResource> resources) throws CliTasksException, IOException {
	if (!resources.isEmpty()) {
	    _logger.info("Processing additional resources...");
	}
	StringBuilder sb = new StringBuilder(" ");
	for (AdditionalResource e : resources) {
	    _logger.fine("Processing resource " + e);
	    String src = e.src;
	    File dst = new File("/tmp/"+new Date().getTime());
	    String destDir = dst.getAbsolutePath();
	    tempFiles.add(destDir);
	    cliTasks.runCommand("mkdir -p " + destDir);
	    boolean generatedResource = false;
	    String resource = null;
	    // try listener to provide resource file
	    if (runListener != null) {
		File resFile = runListener.prepareResource(src);
		if (resFile != null) {
		    resource = resFile.getAbsolutePath();
		    _logger.fine("Resource [" + src + "] has been handled by listener and outputed to [" + resource + "]");
		    generatedResource = true;
		}
	    }
	    // try http location
	    if (resource == null && src.startsWith("http")) {
		File output = new File(destDir, dst.getName() + ".tmp");
		WebUtils.downloadFile(src, output);
		resource = output.getAbsolutePath();
		tempFiles.add(resource);
	    }
	    // try project resources
	    else if (resource == null) {
		resource = getResourceFileName(src);
		if (resource == null) {
		    throw new CliTasksException("Resource file " + src + " does not exist!");
		}
	    }
	    if (runListener != null) {
		try {
		    File oldResource = new File(resource);
		    File newResource = runListener.onResourceProcessed(src, oldResource);
		    if (newResource != null && newResource.exists() && newResource.isFile()) {
			if (!oldResource.equals(newResource)) {
			    // do not output when listener didn't touch resource
			    _logger.fine("Resource [" + resource + "] has been processed by listener, new result [" + newResource.getAbsolutePath() + "]");
			    tempFiles.add(newResource.getAbsolutePath());
			}
			resource = newResource.getAbsolutePath();
		    } else {
			throw new Exception("Resource file processed by listener is invalid (either null, non-existing or non-file)");
		    }
		} catch (Exception ex) {
		    _logger.log(Level.WARNING, "CliTestRunListener failed, using original resource. Error : " + ex.getMessage(), ex);
		}
	    }
	    // finally copy resource to destination place
	    String targetFile = new File(resource).getName();
	    if (e.targetName!=null) {
		targetFile = e.targetName;
	    }
	    cliTasks.copyFile(resource, destDir, targetFile);
	    if (generatedResource) {
	        new File(resource).delete();
	    }
	    sb.append(e.asArgument+"="+new File(destDir,targetFile).getAbsolutePath()+" ");
	}
	return sb.toString();
    }

    protected void prepareResources(String resSrc, String resDst) throws CliTasksException, IOException {
	_logger.info("Processing additional resources...");
	String[] sources = resSrc.split(",");
	String[] dests = resDst.split(",");
	if (sources.length != dests.length) {
	    throw new CliTasksException("res.src parameter must be same length as res.dst, please update your testng configuration!");
	}
	for (int i = 0; i < sources.length; i++) {
	    String src = sources[i];
	    File dst = new File(dests[i]);
	    String destDir = dst.getParent();

	    if (destDir == null) {
		destDir = "/tmp";
	    } else if (!dst.isAbsolute()) {
		destDir = "/tmp/" + destDir;
	    }

	    cliTasks.runCommand("mkdir -p " + destDir);
	    String resource = null;
	    // try listener to provide resource file
	    if (runListener != null) {
		File resFile = runListener.prepareResource(src);
		if (resFile != null) {
		    resource = resFile.getAbsolutePath();
		    _logger.fine("Resource [" + src + "] has been handled by listener and outputed to [" + resource + "]");
		}
	    }
	    // try http location
	    if (resource == null && src.startsWith("http")) {
		WebUtils.downloadFile(src, new File (destDir+File.separator+dst.getName()+".tmp"));
		resource = destDir + "/" + dst.getName() + ".tmp";
	    }
	    // try project resources
	    else if (resource == null) {
		resource = getResourceFileName(src);
		if (resource == null) {
		    throw new CliTasksException("Resource file " + src + " does not exist!");
		}
	    }
	    if (runListener != null) {
		try {
		    File oldResource = new File(resource);
		    File newResource = runListener.onResourceProcessed(src, oldResource);
		    if (newResource != null && newResource.exists() && newResource.isFile()) {
			if (!oldResource.equals(newResource)) {
			    // do not output when listener didn't touch resource
			    _logger.fine("Resource [" + resource + "] has been processed by listener, new result [" + newResource.getAbsolutePath() + "]");
			}
			resource = newResource.getAbsolutePath();
		    } else {
			throw new Exception("Resource file processed by listener is invalid (either null, non-existing or non-file)");
		    }
		} catch (Exception ex) {
		    _logger.log(Level.WARNING, "CliTestRunListener failed, using original resource. Error : " + ex.getMessage(), ex);
		}
	    }
	    // finally copy resource to destination place
	    cliTasks.copyFile(resource, destDir, dst.getName());

	}
    }

	protected void prepareDependencies(String jsFile, String jsDepends, String mainJsFilePath, String targetFile)
			throws IOException, CliTasksException {
		int longestDepNameLength=0;
		Map<String,Integer> lines = new LinkedHashMap<String, Integer>(); 
		_logger.info("Preparing JS file depenencies ... "+jsDepends);
		String tmpDeps = cliTasks.runCommand("mktemp").trim();
		for (String dependency : jsDepends.split(",")) {
			if (dependency.length()>longestDepNameLength) {
				longestDepNameLength = dependency.length();
			}
			String jsFilePath = getResourceFileName(dependency);
			lines.put(dependency, getFileLineCount(jsFilePath));
			String tempFile = cliTasks.runCommand("mktemp").trim();
			cliTasks.copyFile(jsFilePath, new File(tempFile).getParent(), new File(tempFile).getName());
			// as CLI does not support including, we must merge the files manually
			cliTasks.runCommand("cat "+tempFile+" >> "+tmpDeps);
			cliTasks.runCommand("rm -f "+tempFile);
		}
		// finally merge main jsFile		
		cliTasks.runCommand("cat "+targetFile+" >> "+tmpDeps+" && mv "+tmpDeps+" "+targetFile);
		_logger.info("JS file depenencies ready");

		lines.put(jsFile, getFileLineCount(mainJsFilePath));
		if (jsFile.length()>longestDepNameLength) {
			longestDepNameLength = jsFile.length();
		}
		_logger.info("Output file has been merged from JS files as follows:");
		_logger.info("===========================");
		int current = 0;
		for (String dep : lines.keySet()) {
		    	String message = "JS File: "+dep+createSpaces(longestDepNameLength-dep.length())+" lines: "+current+" - "+(current+lines.get(dep));
			_logger.info(message);
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
			e.printStackTrace();
		}
	}
	
	@AfterTest
	public void deleteJSFiles(){
	    if (System.getProperty("jon.clitest.keep-files") != null) {
		_logger.info("Keeping all files produced by clitest");
		return;
	    }
	    for (String tmpFile : tempFiles) {
		try {
			CliTasks.getCliTasks().runCommand("rm -rf "+tmpFile);
		} catch (CliTasksException ex) {
			_logger.log(Level.WARNING, "Exception on remote File deletion!, ", ex);
		}
	    }
	    tempFiles.clear();
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
			e.printStackTrace();
		}
		try {
            if (reader != null) reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	/**
	 * helper class to handle additional resource that is processed by engine and prepared for CLI test process
	 * @author lzoubek
	 *
	 */
	public static class AdditionalResource {
	    public AdditionalResource(String src, String destName, String asArgument) {
		this.src = src;
		this.targetName = destName;
		this.asArgument = asArgument;
	    }
	    
	    @Override
	    public String toString() {
	        // TODO Auto-generated method stub
	        return "[src="+src+",targetName="+targetName+",asArgument="+asArgument+"]";
	    }
	    public final String src, targetName, asArgument;
	}
}
