package com.redhat.qe.jon.sahi.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.common.util.RestClient;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.editor.ConfigEditor;
import com.redhat.qe.jon.sahi.base.inventory.Resource;

public class CheckForConfigurationErrorsTest extends SahiTestScript {

    private static Logger log = Logger
	    .getLogger(CheckForConfigurationErrorsTest.class.getName());

    @DataProvider
    public Object[][] getResourceTree() throws Exception {

	// we store all resources into map grouped by resource key
	// so we do not check more than 1 resource of given type
	Map<String,Resource> tree = new HashMap<String, Resource>();
	// get really all resources from all agents
	for (String agentName : RestClient.getPlatformNames()) {
	    Resource root = new Resource(sahiTasks, agentName);
	    tree.put(agentName, root);
	    for (Resource child : root.getChildrenTree()) {
	        tree.put(child.getResourceType(), child);
	    }
	}
	log.info("Retrieved "+tree.size()+" resources (max 1 resource of given type)");
	Object[][] output = new Object[tree.values().size()][];
	Iterator<Resource> iter = tree.values().iterator();
	int i = 0;
	while (iter.hasNext()) {
	    output[i] = new Object[] {iter.next()};
	    i+=1;
	}
	return output;
    }

    @Test(groups = "check", dataProvider = "getResourceTree", description = "This test checks for each imported resource whether there is any error on Configuration tab and configuration can be retrieved")
    public void resourceConfigurationWithoutErrors(final Resource resource) {
	String errorText = null;
	try {
	    errorText = resource.configuration().current().getErrorText();
	} catch (Exception ex) {
	    log.info("Resource " + resource.toString()
		    + " does not HAVE Configuration TAB");
	    return;
	}
	Assert.assertNull(errorText, "Resource " + resource.toString()
		+ " has error on Configuration TAB : " + errorText);
	ElementStub es = sahiTasks.byXPath("//td[@class='InfoBlock'][2]");
	boolean hasConfig = true;
	if (es.exists()) {
	    String text = es.getText();
	    if (text != null) {
            if (text.contains("failed to load the configuration")) {
                ConfigEditor confEd = resource.inventory().connectionSettings().getEditor();
                
                // check if Config Management is enabled, skip this resource if not
                if(sahiTasks.waitForElementVisible(sahiTasks, 
                        sahiTasks.cell("Config Management Enabled"), "Config Management Enabled", 5000)){
                    if(confEd.isRadioNearCellChecked("configManagementEnabled", "Yes")){
                        hasConfig = false;
                    }else{
                        log.info("Resource " + resource.toString() +", doesn't have Config Management Enabled. Skipping");
                    }
                }else{
                    hasConfig = false;
                }
            }
	    }
	}
	Assert.assertTrue(hasConfig, "Some configuration was retrieved");
    }
}
