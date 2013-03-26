package org.rhq.remoting.cli.examples;

import java.util.ArrayList;
import java.util.List;

import org.rhq.core.domain.criteria.ResourceCriteria;
import org.rhq.core.domain.resource.InventoryStatus;
import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.core.domain.util.PageList;
import org.rhq.enterprise.clientapi.RemoteClient;

/**
 * this class shows several examples about how to get resources from discovery queue or how to import them
 * @author lzoubek@redhat.com
 *
 */
public class ResourceDiscovery {

    private final RemoteClient client;
    
    public ResourceDiscovery(RemoteClient client) {
	this.client = client;
    }
    
    /**
     * returns array of resources present in discovery queue
     */
    public Resource[] discoveryQueue() {
	ResourceCriteria criteria = new ResourceCriteria();
	criteria.addFilterInventoryStatus(InventoryStatus.NEW);
	PageList<Resource> list = client.getResourceManager().findResourcesByCriteria(client.getSubject(), criteria);
	return list.getValues().toArray(new Resource[]{});
    }
    /**
     * imports all resources from discovery queue to inventory
     */
    public void importAllResources() {
	Resource[] resources = discoveryQueue();
	int[] ids = new int[resources.length];
	for (int i=0;i<resources.length;i++) {
	    ids[i] = resources[i].getId();
	}
	client.getDiscoveryBoss().importResources(client.getSubject(), ids);
    }
    /**
     * imports given array of resources to inventory
     * @param resources to be imported
     */
    public void importResources(Resource[] resources) {
	// when importing specific resources we have to care about it's parent resources
	// if a resource is meant to be imported but it's parent is not yet imported, we have to import it as well
	if (resources.length==0) {
	    return;
	}
	Integer[] ids = new Integer[resources.length];
	for (int i=0;i<resources.length;i++) {
	    ids[i] = resources[i].getId();
	}
	// let's find resources in discoveryQueue
	ResourceCriteria criteria = new ResourceCriteria();
	criteria.addFilterInventoryStatus(InventoryStatus.NEW);
	criteria.fetchParentResource(true); // returned objects include parentResource reference
	criteria.addFilterIds(ids);
	
	List<Integer> importIds = new ArrayList<Integer>();
	PageList<Resource> list = client.getResourceManager().findResourcesByCriteria(client.getSubject(), criteria);
	if (list.isEmpty()) {
	    // no resources in disco queue have been found based on criteria
	    return;
	}
	for (Resource resource : list.getValues()) {
	    Resource parent = resource.getParentResource();
	    if (parent != null) {
		if (parent.getInventoryStatus() == InventoryStatus.NEW) {
		    if (!importIds.contains(parent.getId())) {
			// a resource with not-yet-imported parent found
			importIds.add(parent.getId());
		    }
		}
	    }
	    importIds.add(resource.getId());
	}
	int[] submitIds = new int[importIds.size()];
	for (int i=0;i<importIds.size();i++) {
	    submitIds[i] = importIds.get(i);
	}
	client.getDiscoveryBoss().importResources(client.getSubject(), submitIds);	
    }
    
    /**
     * finds resources by given resourceTypeName in inventory
     * @param resourceTypeName
     * @return
     */
    public Resource[] findResources(String resourceTypeName) {
	ResourceCriteria criteria = new ResourceCriteria();
	criteria.setStrict(true); // enabling this we force server to match exactly name of resource type not just a substring
	criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED);
	criteria.addFilterResourceTypeName(resourceTypeName);
	PageList<Resource> resources = client.getResourceManager().findResourcesByCriteria(client.getSubject(), criteria);
	return resources.toArray(new Resource[]{});
    }
    /**
     * finds resources by it's IDs in inventory
     * @param ids
     * @return
     */
    public Resource[] findResources(Integer... ids) {
	ResourceCriteria criteria = new ResourceCriteria();
	criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED);
	criteria.addFilterIds(ids);
	PageList<Resource> resources = client.getResourceManager().findResourcesByCriteria(client.getSubject(), criteria);
	return resources.toArray(new Resource[]{});	
    }
    /**
     * finds (explicit) resources present in given ResourceGroup
     * @param group
     * @return
     */
    public Resource[] findResourcesForGroup(ResourceGroup group) {
	ResourceCriteria criteria = new ResourceCriteria();
	criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED);
	criteria.addFilterExplicitGroupIds(group.getId());
	PageList<Resource> resources = client.getResourceManager().findResourcesByCriteria(client.getSubject(), criteria);
	return resources.toArray(new Resource[]{});	
    }
}
