package com.redhat.qe.jon.common;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.redhat.qe.tools.compare.CollectionSorter;

public class TestScript {
	protected static boolean initialized = false;
	protected static Logger log = Logger.getLogger(TestScript.class.getName());
	protected static final String defaultAutomationPropertiesFile=System.getenv("HOME")+"/automation.properties"; 
	protected static final String defaultLogPropertiesFile=System.getProperty("user.home")+ "/log.properties"; 

	public TestScript() {
		if (initialized) return; //only need to run this stuff once per jvm

		String propFile ="";

		//load log properties
		try{
			Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());


			propFile = System.getProperty("log.propertiesfile", defaultLogPropertiesFile);
			try {
				LogManager.getLogManager().readConfiguration(new URL(propFile).openStream());
			}
			catch (Exception e) {
				if (e instanceof MalformedURLException) 
					log.info("Unable to parse as a URL: " + propFile + ", trying as a filename.");
				else 
					log.log(Level.WARNING, "Unable to read log configuration from: " + propFile + ".", e);
				if (! new File(propFile).exists()) {
					log.fine("No log.propertiesfile specified, nor found in HOME dir, trying to use default in project.");
					propFile = "log.properties";
				}
				else{
					log.info("Found log properties file: "+propFile);
				}
				LogManager.getLogManager().readConfiguration(new FileInputStream(propFile));
			}

			log.fine("Loaded logger configuration from log.propertiesfile: "+propFile);

		} catch(Throwable e){
			e.printStackTrace();
			log.log(Level.SEVERE, "Could not load log properties from "+propFile, e);
		}

		//load automation properties
		loadProperties();


		// echo all the system properties
		Set<String> keySet = System.getProperties().stringPropertyNames();
		List<String> keyList = CollectionSorter.asSortedList(keySet);
		for (Object key: keyList){
			String value = System.getProperty((String) key);
			if (key.toString().toLowerCase().contains("password") ||
				key.toString().toLowerCase().contains("passphrase"))
				value = "********";
			log.finer("Property("+key+")= "+ value);
		}

		initialized = true;

	}
	/**
	 * creates 2-dimensional array of object required when being TestNG's dataProvider
	 * assuming your test method requires only 1 parameter
	 * @param list of parameter values
	 * @return 2-dimensional array of object parameters
	 */
	public static Object[][] getDataProviderArray(List<?> list) {
	    Object[][] output = new Object[list.size()][];
		for (int i=0;i<list.size();i++) {
			output[i] = new Object[] {list.get(i)};
		}
		return output;
	}

	public static void loadProperties(){
		String propFile = "";
		try{
			propFile = (System.getProperty("automation.propertiesfile"));

			if(propFile == null || propFile.length() == 0){
				log.info("System property automation.properties file is not set.  Defaulting to "+ defaultAutomationPropertiesFile);
				propFile = defaultAutomationPropertiesFile;
			}
			Properties p = new Properties();
			try {
				p.load(new URL(propFile).openStream());
			}catch(Exception e) {
				if (e instanceof MalformedURLException) 
					log.info("Unable to parse as a URL: " + propFile + ", trying as a filename.");
				else 
					log.log(Level.WARNING, "Unable to read automation.properties configuration from: " + propFile + ".", e);

				p.load(new FileInputStream(propFile));
				for (Object key: p.keySet()){
					// we only load properties that are not yet defined
					if (System.getProperty(key.toString()) == null) {
						System.setProperty((String)key, p.getProperty((String)(key)));
					}					
				}
				log.fine("Loaded automation properties from automation.properties file: "+propFile);
			}



			// default automation.dir to user.dir
			if(System.getProperty("automation.dir") == null){
				System.setProperty("automation.dir", System.getProperty("user.dir"));
			}

		} catch(Throwable e){
			e.printStackTrace();
			log.log(Level.SEVERE, "Could not load automation properties from "+propFile, e);
		}
	}
	public static void checkRequiredProperties(String... names) {
	    StringBuilder failed = new StringBuilder();
	    if (names!=null) {
		log.info("Checking required system properties");
		for (String prop : names) {
		    String value = System.getProperty(prop,null);
		    String msg = "Property ["+prop+"] ... ";
		    if (value==null) {
			msg+="no";
			failed.append(prop+",");
		    }
		    else {
			msg+="yes ["+value+"]";
		    }
		    log.info(msg);
		}		
	    }
	    if (failed.length()>0) {
		throw new RuntimeException("Some required properties are not defined : "+failed.toString());
	    }
	}
	public static void checkOptionalProperties(String... names) {
	    if (names!=null) {
		log.info("Checking optional system properties");
		for (String prop : names) {
		    String value = System.getProperty(prop,null);
		    String msg = "Property ["+prop+"] ... ";
		    if (value==null) {
			msg+="no";
		    }
		    else {
			msg+="yes ["+value+"]";
		    }
		    log.info(msg);
		}		
	    }
	}
}
