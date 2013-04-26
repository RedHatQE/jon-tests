package org.rhq.remoting.cli.examples;

import java.util.Set;

import org.jboss.logging.Logger;
import org.rhq.core.domain.criteria.MeasurementDefinitionCriteria;
import org.rhq.core.domain.criteria.ResourceCriteria;
import org.rhq.core.domain.measurement.Availability;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementData;
import org.rhq.core.domain.measurement.MeasurementDefinition;
import org.rhq.core.domain.resource.Resource;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.domain.util.PageList;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.enterprise.server.measurement.AvailabilityManagerRemote;
import org.rhq.enterprise.server.measurement.MeasurementDataManagerRemote;
import org.rhq.enterprise.server.measurement.MeasurementDefinitionManagerRemote;
import org.rhq.enterprise.server.resource.ResourceManagerRemote;

/**
 * this class shows several examples how to get resource metric data or availability
 * 
 * @author lzoubek@redhat.com
 * 
 */
public class ResourceMonitoring {

    private static final Logger log = Logger.getLogger(ResourceMonitoring.class);
    private final RemoteClient client;
    private final MeasurementDataManagerRemote measurementDataManager;
    private final MeasurementDefinitionManagerRemote measurementDefinitionManager;
    private final AvailabilityManagerRemote availabilityManager;
    private final ResourceManagerRemote resourceManager;
    

    public ResourceMonitoring(RemoteClient client) {
	this.client = client;
        this.measurementDataManager = client.getProxy(MeasurementDataManagerRemote.class);
        this.measurementDefinitionManager = client.getProxy(MeasurementDefinitionManagerRemote.class);
        this.availabilityManager = client.getProxy(AvailabilityManagerRemote.class);
        this.resourceManager = client.getProxy(ResourceManagerRemote.class);
    }

    /**
     * gets current availability for given resource
     * @param resource
     * @return 
     */
    public Availability getCurrentAvailability(Resource resource) {
        Availability avail = availabilityManager.getCurrentAvailabilityForResource(client.getSubject(), resource.getId());
        return avail;
    }
    /**
     * checks whether availability for given resource is {@link AvailabilityType#UP}
     * @param resource
     * @return true if availability for given resource is {@link AvailabilityType#UP}
     */
    public boolean isAvailable(Resource resource) {
        return getCurrentAvailability(resource).getAvailabilityType().equals(AvailabilityType.UP);
    }
    /**
     * gets live metric data for given resource
     * @param resource we are interested in
     * @param metricName name (display name) of metric
     * @return
     */
    public Set<MeasurementData> getLiveMetricData(Resource resource, String metricName) {
        // first we'll lookup our resource and fetch it's resource type
        // if we know it's resourceType
        // we can lookup a metric definition - to make sure that 
        // a metric denoted by metricName exists for given resource
        // now, having metric definition we'll lookup live data for given resource
        ResourceCriteria resourceCriteria = new ResourceCriteria();
        resourceCriteria.addFilterId(resource.getId());
        resourceCriteria.fetchResourceType(true);        
        PageList<Resource> list = resourceManager.findResourcesByCriteria(client.getSubject(), resourceCriteria);        
        if (list.size()!=1) {
            log.error("Error looking up resource expected size = 1, but got "+list.size()+" resources");
            for (Resource r : list.getValues()) {
                log.debug(r);
            }
            throw new RuntimeException("Resource "+resource+" was not found in inventory");
        }
        
        ResourceType resourceType = list.get(0).getResourceType();
        
        MeasurementDefinitionCriteria criteria = new MeasurementDefinitionCriteria();
        criteria.addFilterResourceTypeId(resourceType.getId());
        criteria.addFilterDisplayName(metricName);
        criteria.setStrict(true); // we want to be strict, because we look for particular metric
        
        PageList<MeasurementDefinition> mds = measurementDefinitionManager.findMeasurementDefinitionsByCriteria(client.getSubject(), criteria);
        if (mds.size()!=1) {
            for (int i=0;i<mds.getTotalSize();i++) {
                log.error(mds.get(i));
            }
            throw new RuntimeException("Could not find metric definition '"+metricName+"' for resource "+resource.toString());
        }        
        int mdsId = mds.get(0).getId();
        Set<MeasurementData> data = measurementDataManager.findLiveData(client.getSubject(),resource.getId(),new int[] {mdsId});
        return data;
    }

}
