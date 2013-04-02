package org.rhq.remoting.cli.examples;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.Property;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.definition.ConfigurationDefinition;
import org.rhq.core.domain.configuration.definition.PropertyDefinition;
import org.rhq.core.domain.configuration.definition.PropertyDefinitionMap;

public class PrintUtil {

    /**
     * prints Configuration
     * @param config
     */
    public static void printConfiguration(Configuration config) {
	for (Property property : config.getAllProperties().values()) {
	    printConfigurationProperty(property, "");
	}
    }
    /**
     * prints Configuration Property 
     * @param property to be printed
     * @param indent
     */
    public static void printConfigurationProperty(Property property, String indent) {	
	if (PropertyMap.class.equals(property.getClass())) {
	    PropertyMap map = (PropertyMap)property;
	    System.out.println("{\n");
	    for (Property prop : map.getMap().values()) {
		printConfigurationProperty(prop, indent+"  ");
	    }
	    System.out.println("}\n");
	}
	else {
	    System.out.println(indent+property.toString());
	}
    }
    /**
     * prints configuration definition for given resource
     * @param configDef
     */
    public static void printConfigurationDefinition(ConfigurationDefinition configDef) {
	for (PropertyDefinition def : configDef.getPropertyDefinitions().values()) {
	    printConfigurationDefinitionProperty(def,"");
	}	
    }
    /**
     * prints Configuration definition property
     * @param definition
     * @param indent
     */
    public static void printConfigurationDefinitionProperty(PropertyDefinition definition, String indent) {	
	if (PropertyDefinitionMap.class.equals(definition.getClass())) {
	    PropertyDefinitionMap def = (PropertyDefinitionMap)definition;
	    System.out.println("{\n");
	    for (PropertyDefinition propDef : def.getMap().values()) {
		printConfigurationDefinitionProperty(propDef, indent+"  ");
	    }
	    System.out.println("}\n");
	}
	else {
	    System.out.println(indent+definition.toString());
	}
    }
}
