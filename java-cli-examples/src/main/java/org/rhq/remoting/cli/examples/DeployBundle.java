package org.rhq.remoting.cli.examples;

import java.io.File;
import java.io.FileInputStream;

import org.rhq.core.domain.bundle.BundleDeployment;
import org.rhq.core.domain.bundle.BundleDeploymentStatus;
import org.rhq.core.domain.bundle.BundleDestination;
import org.rhq.core.domain.bundle.BundleVersion;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.criteria.BundleDeploymentCriteria;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.rhq.enterprise.server.bundle.BundleManagerRemote;

/**
 * this class shows several examples about how to deploy a bundle to JON server.
 * 
 * Basic concept of bundles is, that in order to deploy a bundle to several agents you need
 * 1. upload your bundle to server
 * 2. create a bundle destination - this destination exists on top of COMPATIBLE resource group
 * 3. deploy bundle of specific version to given destination
 * 
 * @author lzoubek@redhat.com
 *
 */
public class DeployBundle {

    private final RemoteClient client;
    private final BundleManagerRemote bundleManager;
    
    public DeployBundle(RemoteClient client) {
	this.client = client;
	bundleManager = client.getProxy(BundleManagerRemote.class);
    }
    /**
     * deploys a bundle
     * @param input bundle distribution ZIP file
     * @param group to be deployed to (a BundleDestination is created on top of given group), group must be compatible it's resources must support bundle deployment
     * @param config input configuratoin for bundle (for passing input param values)
     * @param destinationName - name of new destination being created
     * @param baseDirName - basedir for deployment - this must match to resourceType contained in given group
     * @param deployDir - directory to deploy to - it's based on baseDir
     * @return bundleDeployment where deployment has finished (either failed or success)
     * @throws Exception
     */
    public BundleDeployment deployBundle(File input,ResourceGroup group, Configuration config, String destinationName, String baseDirName, String deployDir) throws Exception {
	BundleVersion version = createBundleVersion(input);
	BundleDestination destination = bundleManager.createBundleDestination(
		client.getSubject(), 
		version.getBundle().getId(), 
		destinationName, 
		"", 
		baseDirName, 
		deployDir, 
		group.getId()
	);
	
	BundleDeployment deployment = bundleManager.createBundleDeployment(client.getSubject(), version.getId(), destination.getId(), "", config);
	deployment = bundleManager.scheduleBundleDeployment(client.getSubject(), deployment.getId(), false);	
	return waitForBundleDeploymentFinishes(deployment);
    }
        
    /**
     * creates a bundleVersion on server. This is done by uploading given BundleDistributionFile (ZIP) to server. Server than 
     * read's deploy.xml from ZIP file and creates appropriate Bundle in version found in ZIP
     * @param input BundleDistribution ZIP file 
     * @return
     * @throws Exception when input file cannot be read or Server fails creating BundleVersion
     */
    private BundleVersion createBundleVersion(File input) throws Exception {
	FileInputStream is = new FileInputStream(input);
	int length = (int)input.length();
	byte[] array = new byte[length];
	for (int numRead=0, offset=0; ((numRead >= 0) && (offset < array.length)); offset += numRead ) {
	    numRead = is.read(array, offset, array.length - offset);
	}
	is.close();
	return  bundleManager.createBundleVersionViaByteArray(client.getSubject(), array);
	
    }
    /**
     * waits until given BundleDeployment is not PENDING or IN_PROGRESS,
     * then returns BundleDeployment instance, which is going to be either SUCCESS or FAILURE
     * @param deployment
     * @return
     */
    private BundleDeployment waitForBundleDeploymentFinishes(BundleDeployment deployment) {
	while (deployment.getStatus().equals(BundleDeploymentStatus.IN_PROGRESS) 
		||deployment.getStatus().equals(BundleDeploymentStatus.PENDING)) {
	    try {
		Thread.currentThread().join(3 * 1000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    BundleDeploymentCriteria criteria = new BundleDeploymentCriteria();
	    criteria.addFilterBundleId(deployment.getId());
	    deployment = bundleManager.findBundleDeploymentsByCriteria(client.getSubject(), criteria).get(0);
	}
	return deployment;
    }
}
