package com.redhat.qe.jon.sahi.base.administration;

import java.util.ArrayList;

public class Role {
    private String name = null;
    private String description = null;
    //TODO premissions
    private ArrayList<String> resourceGroupNames = new ArrayList<String>();
    //TODO bundle groups
    private ArrayList<String> userNames = new ArrayList<String>();
    // TODO ldap groups
    
    
    public Role(String name){
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
    public ArrayList<String> getResourceGroupNames() {
        return resourceGroupNames;
    }
    public void setResourceGroupNames(ArrayList<String> resourceGroupNames) {
        this.resourceGroupNames = resourceGroupNames;
    }
    public ArrayList<String> getUserNames() {
        return userNames;
    }
    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

}
