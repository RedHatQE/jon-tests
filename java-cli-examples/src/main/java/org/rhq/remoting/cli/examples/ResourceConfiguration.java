package org.rhq.remoting.cli.examples;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.configuration.Property;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.configuration.ResourceConfigurationUpdate;
import org.rhq.core.domain.configuration.definition.ConfigurationDefinition;
import org.rhq.core.domain.configuration.definition.PropertyDefinition;
import org.rhq.core.domain.configuration.definition.PropertyDefinitionMap;
import org.rhq.core.domain.resource.Resource;
import org.rhq.enterprise.clientapi.RemoteClient;
/**
 * this class shows how to configure a resource
 * @author lzoubek
 *
 */
public class ResourceConfiguration {

    private final RemoteClient client;
    
    public ResourceConfiguration(RemoteClient client) {
	this.client = client;
    }
    
    /**
     * prints configuration definition for given resource
     * @param resource
     */
    public void printConfigurationDefinition(Resource resource) {
	int resourceTypeId = resource.getResourceType().getId();
	ConfigurationDefinition configDef = client.getConfigurationManager()
		.getResourceConfigurationDefinitionForResourceType(client.getSubject(), resourceTypeId);
	for (PropertyDefinition def : configDef.getPropertyDefinitions().values()) {
	    printPropertyDefinition(def,"");
	}	
    }
    /**
     * prints configuration for given resource
     * @param resource
     */
    public void printConfiguration(Resource resource) {
	Configuration config = client.getConfigurationManager().getResourceConfiguration(client.getSubject(), resource.getId());
	for (Property property : config.getAllProperties().values()) {
	    printProperty(property, "");
	}
    }
    /**
     * updates configuration for given resource and waits until update process is finished,
     * does not support nested or list properties
     * @param resource 
     * @param key - name of configuration property
     * @param value - new configuration value
     * @return
     */
    public ResourceConfigurationUpdate updateResourceConfiguration(Resource resource, String key, Object value) {
	// there are 2 ways to retrieve configuration for resource
	
	// this method gets configuration from JON server's database, this configuration 
	// is accessible even when given resource or its agent is disconnected,
	// but may not be up-to-date
	Configuration config = client.getConfigurationManager().getResourceConfiguration(client.getSubject(), resource.getId());
	
	// this method gets live configuration directly from agent, it is up-to-date
	// but may fail if agent communication is broken
	//Configuration config = client.getConfigurationManager().getLiveResourceConfiguration(client.getSubject(), resource.getId(),true);
	PropertySimple property = config.getSimple(key);
	if (property==null) {
	    throw new RuntimeException("Property ["+key+"] not found in configuration");
	}
	property.setValue(value);
	config.put(property);
	ResourceConfigurationUpdate update = client.getConfigurationManager().updateResourceConfiguration(client.getSubject(), resource.getId(), config);
	if (update == null) {
	    // configuration has not been changed
	    return null;
	}
	update = waitForUpdateFinishes(resource.getId(), update);
	return update;
    }
    /**
     * this function waits until given resource configuration update either fails or succeeds
     * @param update
     * @return
     */
    private ResourceConfigurationUpdate waitForUpdateFinishes(int resourceId, ResourceConfigurationUpdate update) {
	while (update.getStatus().equals(ConfigurationUpdateStatus.INPROGRESS)) {
	    try {
		Thread.currentThread().join(3 * 1000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    update = client.getConfigurationManager().getLatestResourceConfigurationUpdate(client.getSubject(), resourceId);
	}
	return update;
    }

    /**
     * prints Configuration Property 
     * @param property to be printed
     * @param indent
     */
    private void printProperty(Property property, String indent) {	
	if (PropertyMap.class.equals(property.getClass())) {
	    PropertyMap map = (PropertyMap)property;
	    System.out.println("{\n");
	    for (Property prop : map.getMap().values()) {
		printProperty(prop, indent+"  ");
	    }
	    System.out.println("}\n");
	}
	else {
	    System.out.println(indent+property.toString());
	}
    }

    /**
     * prints Configuration property definition 
     * @param definition
     * @param indent
     */
    private void printPropertyDefinition(PropertyDefinition definition, String indent) {	
	if (PropertyDefinitionMap.class.equals(definition.getClass())) {
	    PropertyDefinitionMap def = (PropertyDefinitionMap)definition;
	    System.out.println("{\n");
	    for (PropertyDefinition propDef : def.getMap().values()) {
		printPropertyDefinition(propDef, indent+"  ");
	    }
	    System.out.println("}\n");
	}
	else {
	    System.out.println(indent+definition.toString());
	}
    }
}
