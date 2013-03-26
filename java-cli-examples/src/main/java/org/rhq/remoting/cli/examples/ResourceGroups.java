package org.rhq.remoting.cli.examples;

import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.enterprise.clientapi.RemoteClient;
/**
 * this class shows how to create resource groups
 * @author lzoubek
 *
 */
public class ResourceGroups {

    private final RemoteClient client;
    
    public ResourceGroups(RemoteClient client) {
	this.client = client;
    }
    /**
     * creates a new resource group
     * @param name of new group
     * @param children resources to be contained
     * @param recursive
     * @return
     */
    public ResourceGroup createGroup(String name, Resource[] children, boolean recursive) {
	ResourceGroup group = new ResourceGroup(name);
	group.setRecursive(recursive);
	int[] ids = new int[children.length];
	// check whether all resources are same type, if so, we'll create COMPATIBLE group
	// at the same time we fill array of resource IDs to tell server our group contains given resources
	ResourceType type = null;
	boolean compatible = true;
	if (children.length>0) {	    
	    type = children[0].getResourceType();
	}
	for (int i=0;i<0;i++) {
	    ids[i] = children[i].getId();
	    if (type!=null) {
		if (!type.equals(children[i].getResourceType())) {
		    compatible = false;		    
		}
	    }
	}
	if (type!=null && compatible) {
	    group.setResourceType(type); // this makes group COMPATIBLE
	}
	group = client.getResourceGroupManager().createResourceGroup(client.getSubject(), group);
	client.getResourceGroupManager().addResourcesToGroup(client.getSubject(), group.getId(), ids);	
	return group;
    }
}
