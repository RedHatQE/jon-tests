package org.rhq.remoting.cli.examples;

import org.rhq.core.domain.criteria.ResourceGroupCriteria;
import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.core.domain.util.PageList;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.enterprise.server.resource.group.ResourceGroupDeleteException;
import org.rhq.enterprise.server.resource.group.ResourceGroupManagerRemote;
import org.rhq.enterprise.server.resource.group.ResourceGroupNotFoundException;
/**
 * this class shows how to create resource groups
 * @author lzoubek
 *
 */
public class ResourceGroups {

    private final RemoteClient client;
    private final ResourceGroupManagerRemote resourceGroupManager;
    public ResourceGroups(RemoteClient client) {
	this.client = client;
	this.resourceGroupManager = client.getProxy(ResourceGroupManagerRemote.class);
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
	for (int i=0;i<children.length;i++) {
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
	group = resourceGroupManager.createResourceGroup(client.getSubject(), group);
	resourceGroupManager.addResourcesToGroup(client.getSubject(), group.getId(), ids);	
	return group;
    }
    /**
     * deletes group by given name
     * @param name of group
     * @return true if given group was deleted
     */
    public boolean deleteGroup(String name) {
	ResourceGroupCriteria criteria = new ResourceGroupCriteria();
	criteria.addFilterName(name);
	PageList<ResourceGroup> groups = resourceGroupManager.findResourceGroupsByCriteria(client.getSubject(), criteria);
	if (groups.size()>0) {
	    try {
		resourceGroupManager.deleteResourceGroup(client.getSubject(), groups.get(0).getId());
	    } catch (ResourceGroupNotFoundException e) {
		e.printStackTrace();
		return false;
	    } catch (ResourceGroupDeleteException e) {
		e.printStackTrace();
		return false;
	    }
	    return true;
	}
	return false;
    }
}
