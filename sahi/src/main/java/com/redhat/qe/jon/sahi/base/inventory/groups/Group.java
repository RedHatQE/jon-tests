package com.redhat.qe.jon.sahi.base.inventory.groups;

import java.util.ArrayList;

/**
 * This class represents general resource group.
 * @author fbrychta
 *
 */
public class Group {
    private String name = null;
    private String description = null;
    private boolean isRecursive = false;
    private ArrayList<String> resourceNames = new ArrayList<String>();
    
    public  Group(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isRecursive() {
        return isRecursive;
    }
    public void setRecursive(boolean isRecursive) {
        this.isRecursive = isRecursive;
    }
    public ArrayList<String> getResourceNames() {
        return resourceNames;
    }
    public void setResourceNames(ArrayList<String> resourceNames) {
        this.resourceNames = resourceNames;
    }

}
