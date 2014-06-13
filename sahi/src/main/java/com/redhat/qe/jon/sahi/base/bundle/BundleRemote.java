package com.redhat.qe.jon.sahi.base.bundle;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import com.redhat.qe.jon.common.util.FileUtils;
import com.redhat.qe.jon.common.util.SSHClient;
import com.redhat.qe.jon.common.util.ZipUtils;
import com.redhat.qe.jon.sahi.base.SahiSettings;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class BundleRemote extends BundleBase{
	private static final String tmpLocation="/tmp/bundleRemote/";
	private static final String propertiesFileName="automation-bundle-info.properties";
	private static final String bundlebaseDir="bundle.base.dir";
	private static final String bundleFiles="bundle.files";
	private static final long sshWaitTime = Timing.TIME_1M*15;
	SSHClient sshClient = new SSHClient(SahiSettings.getJonAgentSSHUser(), SahiSettings.getJonAgentName(), SahiSettings.getJonAgentSSHPassword());

	private static final String[] complianceFiles = {"/IamNotFromDeployment.txt","/tmpDir/Another-IamNotFromDeployment.txt"};

	public BundleRemote(SahiTasks sahiTasks) {
		super(sahiTasks);
	}

	private void sshConnect(){
		if(!sshClient.isConnected()){
			sshClient.connect();
		}
	}

	private boolean sshVerifyFile(String fileAbsolutePath){
		this.sshConnect();
		if(sshClient.runAndWait("[ -f "+fileAbsolutePath+" ] && echo \"--Found--\" || echo \"--Not Found--\"", sshWaitTime).toString().contains("--Not Found")){
			return false;
		}else{
			return true;
		}
	}


	private void sshCleanDestination(String destination){
		this.sshConnect();
		sshClient.runAndWait("rm -rf "+destination, sshWaitTime);
	}

	private void sshCreateFiles(String[] files, String location, String dir){
		this.sshConnect();
		sshClient.runAndWait("mkdir -p "+location+dir, sshWaitTime);
		for(String file : files){
			sshClient.runAndWait("touch "+location+file, sshWaitTime);
		}
	}
	
	private Properties getProperties(Bundle bundle) throws IOException{
		if(new File(tmpLocation).exists()){
			if(! FileUtils.cleanLocation(tmpLocation)){
				_logger.log(Level.WARNING, "Unable to clean the location: "+tmpLocation);
			}
		}else{
			if(new File(tmpLocation).mkdirs()){
				_logger.log(Level.INFO, "Specified directory not found created:"+tmpLocation);
			}
		}
		FileUtils.downloadFile(bundle.getUrl()+bundle.getFilename(), tmpLocation+bundle.getFilename());
		return ZipUtils.getOnePropertiesFromZip(tmpLocation+bundle.getFilename(), propertiesFileName);
	}

	public boolean deployFileBundle(Bundle bundle) throws IOException{
		//Read Properties File from bundle ZIP file.
		Properties properties = this. getProperties(bundle);
		//Clean destination location, to deploy
		this.sshCleanDestination(properties.getProperty(bundlebaseDir));
		if(properties.getProperty(bundleCompliance) == null){
			//Nothing to do..
		}else if(properties.getProperty(bundleCompliance).equalsIgnoreCase("full") || properties.getProperty(bundleCompliance).equalsIgnoreCase("filesAndDirectories")){
			this.sshCreateFiles(complianceFiles, properties.getProperty(bundlebaseDir), "tmpDir");
		}

		this.deployBundle(bundle, properties);
		_tasks.waitFor(Timing.TIME_30S);
		boolean status = true;
		for(String file : properties.getProperty(bundleFiles).split(",")){
			if(! this.sshVerifyFile(properties.getProperty(bundlebaseDir)+file)){
				status = false;
				_logger.log(Level.WARNING, "File not found on the location: "+properties.getProperty(bundlebaseDir)+file);
			}
		}
		if(properties.getProperty(bundleCompliance) == null){
			//Nothing to do..
		}else if(properties.getProperty(bundleCompliance).equalsIgnoreCase("full")){
			for(String file : complianceFiles){
				if(this.sshVerifyFile(properties.getProperty(bundlebaseDir)+file)){
					status = false;
					_logger.log(Level.WARNING, "File found on the location: "+properties.getProperty(bundlebaseDir)+file);
				}
			}
		}else if(properties.getProperty(bundleCompliance).equalsIgnoreCase("filesAndDirectories")){
			for(String file : complianceFiles){
				if(! this.sshVerifyFile(properties.getProperty(bundlebaseDir)+file)){
					status = false;
					_logger.log(Level.WARNING, "File not found on the location: "+properties.getProperty(bundlebaseDir)+file);
				}
			}
		}
		return status;
	}
	
	public boolean deleteBundle(Bundle bundle) throws IOException{
		if(this.deleteBundleGUI(bundle)){
			this.sshCleanDestination(this.getProperties(bundle).getProperty(bundlebaseDir));
			return true;
		}else{
			return false;
		}
		
	}

	public boolean deleteBundleGroup(BundleGroup bundleGroup){
		return this.deleteBundleGroupGUI(bundleGroup);
	}

}
