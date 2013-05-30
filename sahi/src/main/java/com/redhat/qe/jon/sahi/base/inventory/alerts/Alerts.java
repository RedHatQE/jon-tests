package com.redhat.qe.jon.sahi.base.inventory.alerts;

import com.redhat.qe.jon.sahi.base.inventory.Resource;
import com.redhat.qe.jon.sahi.base.inventory.ResourceTab;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionsPage;
import com.redhat.qe.jon.sahi.base.inventory.alerts.history.AlertHistoryPage;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;

/**
 * represents <b>Alerts</b> Tab of given resource.
 * Creating instance of this class will navigate to resource and select <b>Alerts</b> Tab
 *
 * @author ahovsepy, fbrychta
 */
public class Alerts extends ResourceTab {

    public Alerts(SahiTasks tasks, Resource resource) {
        super(tasks, resource);
    }

    /**
     * Navigates to Alerts page.
     */
    @Override
    protected void navigate() {
        navigateUnderResource("Alerts");
        raiseErrorIfCellDoesNotExist("Alerts");
    }
    
    
    /**
	 * navigates to <b>History</b> subtab and returns helper object
	 * 
	 * @return alert history subtab
	 */
    public AlertHistoryPage alertHistoryPage(){
    	navigateUnderResource("Alerts/History");
    	
		return new AlertHistoryPage(tasks,resource);
    }
    
    /**
	 * navigates to <b>Definitions</b> subtab and returns helper object
	 * 
	 * @return alert definitions subtab
	 */
    public AlertDefinitionsPage alertDefinitionsPage(){
    	navigateUnderResource("Alerts/Definitions");
    	
		return new AlertDefinitionsPage(tasks,resource);
    }
}
