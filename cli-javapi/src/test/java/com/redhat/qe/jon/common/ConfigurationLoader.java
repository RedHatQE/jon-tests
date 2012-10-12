package com.redhat.qe.jon.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationLoader {
	private static Log logger = LogFactory.getLog(ConfigurationLoader.class);


	public enum PARAM {
		HOST_NAME,
		HOST_PORT,
		HOST_USER,
		HOST_PASSWORD
	}
	
	protected final PropertiesConfiguration allProps = new PropertiesConfiguration();
	
	
	
	
	/**
	 * Load test parameters from predefined properties file if -Denv=&lt;env&gt; system
	 * is specified. Then override and add additional values from environment variables.
	 * @return
	 * @throws ConfigurationException 
	 */
	public ConfigurationLoader() throws ConfigurationException {
		String env =  System.getProperty("env");
		logger.info("Loading parameters for " + env);
		
		allProps.load("resources/env/default.properties");
		
		
		if (StringUtils.trimToNull(env) != null) {
			PropertiesConfiguration p = new PropertiesConfiguration("resources/env/" + env + ".properties");
			allProps.copy(p); // merge env-specific with default 
		} else {
			logger.warn("env file is empty or null.  Using default");
		}		
	}
	
	
	public String get(PARAM key) {
		return allProps.getString(key.toString());
	}
	
	public int getInt(PARAM key) {
		return allProps.getInt(key.toString());
	}

	@Override
	public String toString() {
		return allProps.toString();
	}
}
