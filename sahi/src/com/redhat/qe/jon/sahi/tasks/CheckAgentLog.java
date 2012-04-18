package com.redhat.qe.jon.sahi.tasks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be on class or method and is processed by {@link AgentLogCheckTestNGListener} listener. 
 * Adding this annotation to class (or even superclass) or method does following:
 * <ol>
 * <li>Before each test method runs, new {@link AgentLog} instance is created and agent's log is being watched</li>
 * <li>After test method finishes, gathered agent output is checked for <b>ERROR</b> lines. If such line is found, test 
 * is marked as FAILED.</li>
 * </ol>
 * Method level declaration has precedence before class level. Class level has precedence before superclass. 
 * @author lzoubek
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface CheckAgentLog {
	/**
	 * host where agent runs (required password based SSH access)
	 * Default : taken from <b>HOST_NAME</b> environment variable
	 * <br><br>Note: once you define host you must define user and pass too!
	 */
	String host() default "";	
	/**
	 * user agent runs under (required password based SSH access)
	 * Default : taken from <b>HOST_USER</b> environment variable
	 * <br><br>Note: once you define user you must define host and pass too!
	 */
	String user() default "";
	/**
	 * user's pasword (required password based SSH access)
	 * Default : taken from <b>HOST_PASS</b> environment variable
	 * <br><br>Note: once you define pass you must define user and host too!
	 */
	String pass() default "";
	/**
	 * agent home dir relative to {@link CheckAgentLog#user()} HOME or absolute path 
	 * Default : <b>rhq-agent</b>
	 */
	String agentHome() default "rhq-agent";
	/**
	 * says whether checking is enabled. Set this to false to disable agent.log checking for particular class
	 * Default : <b>true</b>
	 * @return
	 */
	boolean enabled() default true;
}
