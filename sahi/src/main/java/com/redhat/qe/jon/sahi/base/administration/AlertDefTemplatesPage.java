package com.redhat.qe.jon.sahi.base.administration;

import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.AlertDefinitionPageSnippet;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class AlertDefTemplatesPage {
    private static Logger log = Logger.getLogger(AlertDefTemplatesPage.class.getName());
    protected SahiTasks tasks = null;
    
    public AlertDefTemplatesPage(SahiTasks tasks){
        this.tasks = tasks;
    }
    
    /**
     * Navigates to the page
     */
    public AlertDefTemplatesPage navigate(){
        String serverBaseUrl = tasks.getNavigator().getServerBaseUrl();
        String url = serverBaseUrl+"/#Administration/Configuration/AlertDefTemplates";
        log.fine("Navigating to ["+url+"]");
        tasks.navigateTo(url,false);
        
        return this;
    }
    
    public AlertDefinitionPageSnippet editTemplate(String name){
        boolean found = false;
        int i = 0;
        while(!found && i < 15){
            if(tasks.div(name).exists()){
                found = true;
            }else{
                int count = tasks.image("/vscroll_end.*/").countSimilar();
                log.info("Number of vscroll elements: " + count);
                if(tasks.image("/vscroll_end.*/["+ (count-1) +"]").isVisible()){
                    tasks.image("/vscroll_end.*/["+ (count-1) +"]").click();
                }else{
                    count = tasks.image("/vscroll_Over_end.*/["+ (count-1) +"]").countSimilar();
                    tasks.image("/vscroll_Over_end.*/["+ (count-1) +"]").click();
                }
            }
        }
        if(found){
            ElementStub row = tasks.div(name).parentNode("tr");
            tasks.image("edit.png").in(row).click();
            tasks.waitForElementVisible(tasks, tasks.cell("New"), "New button", Timing.WAIT_TIME);
            
            return new AlertDefinitionPageSnippet(tasks);
        }else{
            throw new RuntimeException("Alert definition templated named "+name+", was not found!");
        }
    }
}
