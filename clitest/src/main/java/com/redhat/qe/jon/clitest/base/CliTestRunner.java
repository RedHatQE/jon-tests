package com.redhat.qe.jon.clitest.base;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.testng.Assert;

import com.redhat.qe.jon.clitest.base.CliEngine.AdditionalResource;

public class CliTestRunner {

    private static Logger log = Logger.getLogger(CliTestRunner.class.getName());
    private final CliEngine engine;
    private String rhqTarget;
    private String username;
    private String password;
    private String jsFile;
    private String jsSnippet;
    private String cliArgs;
    private String expectedResult;
    private String makeFailure;
    private String[] jsDepends;
    private String[] resSrc;
    private String[] resDst;
    private List<AdditionalResource> resources;
    private CliTestRunListener runListener = new CliTestRunListenerImpl();
    
    public CliTestRunner(CliEngine engine) {
	this.engine = engine;
	setDefaults();
    }
    /**
     * sets default values
     */
    private void setDefaults() {
	this.username="rhqadmin";
	this.password="rhqadmin";
	this.resources = new Vector<CliEngine.AdditionalResource>();
	this.expectedResult="Login successful";
	this.makeFailure="Login failed:,No such file or directory";	
    }
    /**
     * validates if all required parameter values are defined and parameters are correct
     */
    private void validate() {
	if (username==null) {
	    throw new RuntimeException("user cannot be null");
	}
	if (password==null) {
	    throw new RuntimeException("password cannot be null");
	}
	if (jsFile==null && jsSnippet==null) {
	    throw new RuntimeException("both jsFile and jsSnippet cannot be null");
	}
	if (jsFile!=null && jsSnippet!=null) {
	    throw new RuntimeException("both jsFile and jsSnippet cannot be defined");
	}
	if (resSrc!=null) {
	    if (resDst==null) {
		throw new RuntimeException("Resource destinations cannot be null, when resource sources are defined");
	    }
	    if (resSrc.length!=resDst.length) {
		throw new RuntimeException("Resource destinations and sources must be same size");
	    }
	}
	for (AdditionalResource r : this.resources) {
	    if (r.asArgument == null) {
		throw new RuntimeException("Additional resource must not have null 'asArgument'");
	    }
	    if (r.src == null) {
		throw new RuntimeException("Additional resource must not have null 'src'");
	    }
	}
    }
    /**
     * sets {@link CliTestRunListener} for this run
     * @param listener
     * @return this
     */
    public CliTestRunner withRunListener(CliTestRunListener listener) {
	this.runListener = listener;
	return this;
    }
    /**
     * JS file that is going to run
     * @param jsFile
     */
    public CliTestRunner jsFile(String jsFile) {
	this.jsFile = jsFile;
	return this;
    }
    /**
     * JS file that is going to run
     * @param jsSnippet
     */
    public CliTestRunner jsSnippet(String jsSnippet) {
	this.jsSnippet = jsSnippet;
	return this;
    }
    /**
     * add jsFiles that your script depends on. Those will be merged with your {@link #jsFile(String)}
     * together. Note that order of jsFiles is preserved, {@link #jsFile(String)} is always going to be last piece
     */
    public CliTestRunner dependsOn(String... jsFiles) {
	this.jsDepends = jsFiles;
	return this;
    }
    /**
     * set additional resource source paths (looked up as java resource)
     * @param resSrc
     * @deprecated use {@link CliTestRunner#withResource(String, String, String)}
     */
    public CliTestRunner resourceSrcs(String... resSrc) {
	this.resSrc = resSrc;
	return this;
    }
    /**
     * set destinations for additional {@link #resourceSrcs(String...)}
     * @param resDst
     * @deprecated use {@link CliTestRunner#withResource(String, String, String)}
     */
    public CliTestRunner resourceDests(String... resDst) {
	this.resDst = resDst;
	return this;
    }
    /**
     * adds jsFile dependency
     * @param jsFile
     */
    public CliTestRunner addDepends(String jsFile) {
	if (jsFile != null) {
	    if (this.jsDepends == null) {
		this.jsDepends = new String[] {};
	    }
	    this.jsDepends = Arrays.copyOf(this.jsDepends, this.jsDepends.length + 1);
	    this.jsDepends[this.jsDepends.length - 1] = jsFile;
	}
	return this;
    }
    /**
     * specify message that makes test fail, if produced as output of test
     */
    public CliTestRunner addFailOn(String failOn) {
	if (failOn!=null) {
	    this.makeFailure+=","+failOn;
	}
	return this;
    }
    /**
     * specify message - if such message is missing in output produced by CLI, test will fail
     * @param expect
     */
    public CliTestRunner addExpect(String expect) {
	if (expect!=null) {
	    this.expectedResult+=","+expect;
	}
	return this;
    }
    /**
     * add a resource file to CLI test. Given resource will become ready for CLI test under named argument 'asArgument'
     * 
     * For example ("deployments/hello1.war","hello.war","deployment") will find hello1.war within project resources, copy it to 
     * some /some/path/hello.war and append named argument to CLI deployment=/some/path/hello.war
     *  
     * @param src of resource (you can use http:// for URLs file:// for absolute path on disk or path for java resource lookup)
     * @param destName name of destination file
     * @param asArgument name of argument for resource file to be ready for test
     * @return this
     */
    public CliTestRunner withResource(String src, String destName, String asArgument) {
	this.resources.add(new AdditionalResource(src,destName,asArgument));
	return this;
    }
    /**
     * add a resource file to CLI test. Given resource will become ready for CLI test under named argument 'asArgument'
     * 
     * For example ("deployments/hello1.war","deployment") will find hello1.war within project resources, copy it to 
     * some /some/path/hello1.war and append named argument to CLI deployment=/some/path/hello1.war
     *  
     * @param src of resource (you can use http:// for URLs file:// for absolute path on disk or path for java resource lookup)
     * @param asArgument name of argument for resource file to be ready for test
     * @return this
     */
    public CliTestRunner withResource(String src, String asArgument) {
	this.resources.add(new AdditionalResource(src,null,asArgument));
	return this;
    }
    /**
     * specify username (subject)
     * @param user
     */
    public CliTestRunner asUser(String user) {
	this.username = user;
	return this;
    }
    
    /**
     * specify rhq target server
     * @param rhqTarget
     * @return this
     */
    public CliTestRunner onRhqTarget(String rhqTarget){
    	this.rhqTarget = rhqTarget;
    	return this;
    }
    public CliTestRunner withArg(String name, String value) {
	if (name != null && value != null) {
	    if (this.cliArgs == null) {
		this.cliArgs = "";
	    }
	    if (value.contains(" ")) { // quote value when it contains space
		value = "\""+value+"\"";
	    }
	    this.cliArgs += " " + name + "=" + value;
	}
	return this;
    }
    /**
     * prepares array args for CLI execution (just puts ',' separator between items)
     * @param args
     */
    private String prepareArrayArgs(String[] args) {
	if (args==null) {
	    return null;
	}
	StringBuilder sb = new StringBuilder();
	for (String arg : args) {
	    sb.append(arg+",");
	}
	sb.deleteCharAt(sb.length()-1);
	return sb.toString();
    }
    /**
     * runs this CLI test
     * @return consoleOutput 
     */
    public String run() {
	validate();
	if (this.cliArgs!=null) { // we support named arguments only at this time
	    this.cliArgs = "--args-style=named"+this.cliArgs;
	    log.finer("Adding following cli arguments: " + cliArgs);
	}
	CliEngine.runListener = this.runListener;
	String jsDepends = prepareArrayArgs(this.jsDepends);
	String resSrc = prepareArrayArgs(this.resSrc);
	String resDst = prepareArrayArgs(this.resDst);
	String result = null;
	if (jsSnippet==null) {
	    try {
		engine.runJSfile(this.rhqTarget, this.username, this.password, this.jsFile, this.cliArgs, this.expectedResult, this.makeFailure, jsDepends, resSrc, resDst, this.resources);
		result = engine.consoleOutput;
	    } catch (Exception e) {
		Assert.fail("Test failed : "+e.getMessage(), e);
	    } 
	}
	else {
	    try {
		engine.runJSSnippet(this.jsSnippet, this.rhqTarget, this.username, this.password, cliArgs, expectedResult, this.makeFailure, jsDepends, resSrc, resDst, this.resources);
		result = engine.consoleOutput;
	    } catch (Exception e) {
		Assert.fail("Test failed : "+e.getMessage(), e);
	    } 
	}
	return result;	
    }
}
