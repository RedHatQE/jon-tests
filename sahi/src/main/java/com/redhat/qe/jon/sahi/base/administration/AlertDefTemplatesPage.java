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
        tasks.navigateTo(url,true);
        
        return this;
    }
    
    public AlertDefinitionPageSnippet editTemplate(String name){
        boolean found = false;
        int i = 0;
        int countOfNameCells = tasks.cell("Name").collectSimilar().size();
        ElementStub nameCell = tasks.cell("Name").collectSimilar().get(countOfNameCells - 1);
        while (!found && i < 15) {
            if (tasks.div(name).isVisible()) {
                found = true;
            } else {
                /**
                 * This is just a workaround to get last templates up. When scrolling is used click on edit 
                 * img is simply not working for some very weird reason. Not even enter key is working.
                 */
                nameCell.click();
                if (tasks.div(name).isVisible()) {
                    found = true;
                } else {
                    int count = tasks.image("/vscroll_end.*/").countSimilar();
                    log.info("Number of vscroll elements: " + count);
                    if (tasks.image("/vscroll_end.*/[" + (count - 1) + "]").isVisible()) {
                        tasks.image("/vscroll_end.*/[" + (count - 1) + "]").click();
                    } else {
                        count = tasks.image("/vscroll_Over_end.*/[" + (count - 1) + "]").countSimilar();
                        tasks.image("/vscroll_Over_end.*/[" + (count - 1) + "]").click();
                    }
                }
            }
        }
        if(found){
            ElementStub row = tasks.div(name).parentNode("tr");
            ElementStub editImg = tasks.image("edit.png").in(row);
            editImg.click();
            // if it fails try again and then fail the test
            if(!tasks.waitForElementVisible(tasks, tasks.cell("New"), "New button", Timing.WAIT_TIME)){
                tasks.xy(editImg.parentNode("div"),3,3).click();
            }
            if(!tasks.waitForElementVisible(tasks, tasks.cell("New"), "New button", Timing.WAIT_TIME)){
                throw new RuntimeException("Alert definition templated named "+name+", was found but openning failed!!");
            }
            
            return new AlertDefinitionPageSnippet(tasks);
        }else{
            throw new RuntimeException("Alert definition templated named "+name+", was not found!");
        }
    }
}
