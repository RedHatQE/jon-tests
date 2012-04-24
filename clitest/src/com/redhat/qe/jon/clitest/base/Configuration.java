package com.redhat.qe.jon.clitest.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

public class Configuration {
	private static Logger _logger = Logger.getLogger(Configuration.class.getName());

	public enum PARAM {
		REPORT_ENGINE_PROPERTY_FILE,
		RHQ_TARGET,
		HOST_NAME,
		HOST_USER, 
		HOST_PASSWORD,
		CLI_AGENT_BIN_SH,
		JAVA_HOME
	};
	
	protected final Properties props;
	
	
	
	
	/**
	 * Load test parameters from predefined properties file if -Denv=&lt;env&gt; system
	 * is specified. Then override and add additional values from environment variables.
	 * @return
	 */
	public static Configuration load( ) {
		Properties props = new Properties();
		String env =  System.getProperty("env");
		
		if (StringUtils.trimToNull(env) != null) {
			_logger.log(Level.INFO, "Loading parameters for " + env);
			try {
				InputStream is = Configuration.class.getResourceAsStream("/resources/env/" + env + ".properties");
				if (is != null){
					props.load(is);
				} else {
					_logger.log(Level.WARNING, "Can't load " + env + ".properties");
				}
			} catch (IOException e){
				_logger.log(Level.WARNING, e.toString());
			}		
		}

		Configuration config = new Configuration(props);
		config.refreshWithEnvironmentVariables();

		return config;
	}
	
	/**
	 * Override existing properties or add new ones from environment variables
	 */
	public void refreshWithEnvironmentVariables() {
		for(PARAM item : PARAM.values()) {
			String val = System.getenv().get(item.toString());
			if (StringUtils.trimToNull(val) != null) {
				props.setProperty(item.toString(), val.trim());
			} 
		}
	}
	
	public Configuration(Properties props) {
		this.props = props;
	}
	
	public String get(PARAM key) {
		return props.getProperty(key.toString());
	}
	
	public String get(PARAM key, String defaultValue) {
		return props.getProperty(key.toString(), defaultValue);
	}

	@Override
	public String toString() {
		return props.toString();
	}
}
