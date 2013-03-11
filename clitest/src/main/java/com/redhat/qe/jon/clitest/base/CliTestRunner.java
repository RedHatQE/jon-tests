package com.redhat.qe.jon.clitest.base;

public class CliTestRunner {

    //private static Logger log = Logger.getLogger(CliTestRunner.class.getName());
    private final CliEngine engine;
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
    }
    /**
     * JS file that is going to run
     * @param jsFile
     * @return
     */
    public CliTestRunner jsFile(String jsFile) {
	this.jsFile = jsFile;
	return this;
    }
    /**
     * JS file that is going to run
     * @param jsFile
     * @return
     */
    public CliTestRunner jsSnippet(String jsSnippet) {
	this.jsSnippet = jsSnippet;
	return this;
    }
    /**
     * add jsFiles that your script depends on. Those will be merged with your {@link #jsFile(String)}
     * together. Note that order of jsFiles is preserved, {@link #jsFile(String)} is always going to be last piece
     * @return
     */
    public CliTestRunner dependsOn(String... jsFiles) {
	this.jsDepends = jsFiles;
	return this;
    }
    /**
     * specify message that makes test fail, if produced as output of test
     * @return
     */
    public CliTestRunner addFailOn(String failOn) {
	this.makeFailure+=","+failOn;
	return this;
    }
    /**
     * specify message - if such message is missing in output produced by CLI, test will fail
     * @param expect
     * @return
     */
    public CliTestRunner addExpect(String expect) {
	this.expectedResult+=","+expect;
	return this;
    }
    /**
     * specify username (subject)
     * @param user
     * @return
     */
    public CliTestRunner asUser(String user) {
	this.username = user;
	return this;
    }
    public CliTestRunner withArg(String name, String value) {
	this.cliArgs += " "+name+"="+value;
	return this;
    }
    /**
     * prepares array args for CLI execution (just puts ',' separator between items)
     * @param args
     * @return
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
     */
    public void run() throws Exception {
	validate();
	if (this.cliArgs!=null) { // we support named arguments only at this time
	    this.cliArgs = "--args-style=named"+this.cliArgs;
	}
	String jsDepends = prepareArrayArgs(this.jsDepends);
	String resSrc = prepareArrayArgs(this.resSrc);
	String resDst = prepareArrayArgs(this.resDst);
	if (jsSnippet==null) {
	    engine.runJSfile(null, this.username, this.password, this.jsFile, this.cliArgs, this.expectedResult, this.makeFailure, jsDepends, resSrc, resDst);
	}
	else {
	    engine.runJSSnippet(this.jsSnippet, null, this.username, this.password, cliArgs, expectedResult, this.makeFailure, jsDepends, resSrc, resDst);
	}
	
    }
}
