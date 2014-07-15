package com.redhat.qe.jon.sahi.base.bundle;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

public class BundleBase {
	SahiTasks _tasks = null;
	static Logger _logger = Logger.getLogger(BundleBase.class.getName());
	public static final String bundleDistinationName = "bundle.destination.name";
	public static final String bundleDistinationDescription = "bundle.destination.description";
	public static final String bundleResourceGroup = "bundle.resource.group";
	public static final String bundleDeploymentDirectory = "bundle.deployment.directory";
	public static final String bundleCompliance = "bundle.compliance";

	private ElementStub bundle;
	private ElementStub bundleGroup;


	public BundleBase(SahiTasks sahiTasks) {
		_tasks 		= sahiTasks;
		bundle 		= _tasks.div("/listGrid/[0]");
		bundleGroup = _tasks.div("/listGrid/[1]");
	}

	public void navigateToBundlePage(){
		_tasks.link("Bundles").click();
	}

	public boolean createBundleGroup(BundleGroup bundleGroup){
		this.navigateToBundlePage();
		_tasks.cell("New").under(this.bundleGroup).click();
		this.bundleGroup(bundleGroup);
		return this.isBundleGroupAvailable(bundleGroup);
	}

	public void editBundleGroup(BundleGroup bundleGroup){
		this.navigateToBundlePage();
		_tasks.link(bundleGroup.getName()).click();
		this.bundleGroup(bundleGroup);				
	}

	private void bundleGroup(BundleGroup bundleGroup){
		_tasks.textbox("name").setValue(bundleGroup.getName());
		if(bundleGroup.getDescription() != null){
			_tasks.textbox("description").setValue(bundleGroup.getDescription());
		}
		if(bundleGroup.getBundles() != null){
			if(bundleGroup.getBundles().size() > 0) {
				for(String bundle: bundleGroup.getBundles()){
					_tasks.xy(_tasks.byText(bundle, "nobr"), 3,3).doubleClick();
				}
			}
			
		}
		_tasks.cell("Save").click();
	}

	public boolean createBundle(Bundle bundle){
		this.navigateToBundlePage();
		_tasks.cell("New").near(_tasks.cell("Deploy")).click();

		if(bundle.getUrl() != null){
			_tasks.radio("URL").click();
			_tasks.textbox("url").setValue(bundle.getUrl()+bundle.getFilename());
			if(bundle.getUserName() != null){
				_tasks.textbox("username").setValue(bundle.getUserName());
			}
			if(bundle.getPassword() != null){
				_tasks.password("password").setValue(bundle.getPassword());
			}
		}else if(bundle.getFilename() != null){
			//yet to Implement
		}else if(bundle.getRecipe() != null){
			//yet to Implement			
		}else if(bundle.getRecipeFile() != null){
			//yet to Implement			
		}else{
			_logger.log(Level.WARNING, "Unknown type!");
		}
		_tasks.cell("Next").click();
		_tasks.waitFor(Timing.TIME_1S*3);
		if((bundle.getGroups().size() > 0) && (bundle.getGroups() != null)){
			_tasks.radio("assign").click();
			for(String group : bundle.getGroups()){
				//_tasks.xy(_tasks.byText(group, "nobr"), 3,3).doubleClick();
				_tasks.cell(group).under(_tasks.row("Search :")).doubleClick();
				_logger.log(Level.FINER, "Selected Bundle Group: "+group);
			}
		}else{
			_tasks.radio("unassigned").click();
		}
		_tasks.cell("Next").near(_tasks.cell("Previous")).click();
		_tasks.waitFor(Timing.TIME_1S*3);
		_tasks.cell("Next").near(_tasks.cell("Previous")).click();
		_tasks.waitFor(Timing.TIME_1S*3);
		_tasks.cell("Finish").near(_tasks.cell("Previous")).click();
		return this.isBundleAvailable(bundle);
	}

	public boolean deleteBundleGUI(Bundle bundle) {
		this.navigateToBundlePage();
		_tasks.div(bundle.getName()).in(this.bundle).click();
		_tasks.cell("Delete").near(_tasks.cell("Deploy")).click();
		_tasks.cell("Yes").near(_tasks.cell("No")).click();
		return ! this.isBundleAvailable(bundle);
	}

	public boolean deleteBundleGroupGUI(BundleGroup bundleGroup) {
		this.navigateToBundlePage();
		_tasks.div(bundleGroup.getName()).in(this.bundleGroup).click();
		_tasks.cell("Delete").under(this.bundleGroup).click();
		_tasks.cell("Yes").near(_tasks.cell("No")).click();
		return ! this.isBundleGroupAvailable(bundleGroup);
	}
	
	public boolean isBundleAvailable(Bundle bundle){
		this.navigateToBundlePage();
		return _tasks.link(bundle.getName()).in(this.bundle).exists();
	}
	
	public boolean isBundleGroupAvailable(BundleGroup bundleGroup){
		this.navigateToBundlePage();
		return _tasks.link(bundleGroup.getName()).in(this.bundleGroup).exists();
	}
	
	public boolean deployBundle(Bundle bundle, Properties properties){
		this.navigateToBundlePage();
		_tasks.div(bundle.getName()).in(this.bundle).click();
		_tasks.cell("Deploy").click();
		_tasks.waitFor(3000);
		_tasks.textbox("name").setValue(properties.getProperty(bundleDistinationName));
		_tasks.textarea("description").setValue(properties.getProperty(bundleDistinationDescription));
		_tasks.image("/comboBoxPicker/").near(_tasks.textbox("group")).click(); //PatternFly Change: 15-Jul-2014
		//_tasks.div(properties.getProperty(bundleResourceGroup)).near(_tasks.textbox("group")).click();
		_tasks.div(properties.getProperty(bundleResourceGroup)).click();
		_tasks.waitFor(3000);
		_tasks.textbox("deployDir").setValue(properties.getProperty(bundleDeploymentDirectory));
		_tasks.cell("Next").near(_tasks.cell("Cancel")).click();
		_tasks.waitFor(Timing.TIME_1S*3);
		_tasks.cell("Next").near(_tasks.cell("Previous")).click();
		_tasks.waitFor(Timing.TIME_1S*3);
		_tasks.textbox("server.name").setValue("automation-bundle-test");
		_tasks.textbox("server.port").setValue("2121");
		_tasks.cell("Next").near(_tasks.cell("Previous")).click();
		_tasks.waitFor(Timing.TIME_1S*3);
		_tasks.cell("Next").near(_tasks.cell("Previous")).click();
		_tasks.waitFor(Timing.TIME_1S*3);
		if(_tasks.waitForElementExists(_tasks, _tasks.div("Bundle Deployment Scheduled!"), "div:Bundle Deployment Scheduled!", Timing.TIME_10S)){
			_tasks.cell("Finish").near(_tasks.cell("Previous")).click();
			return true;
		}else{
			return false;
		}		
	}
}
