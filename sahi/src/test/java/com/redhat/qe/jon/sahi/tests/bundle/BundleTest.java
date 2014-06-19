package com.redhat.qe.jon.sahi.tests.bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.auto.testng.TestNGUtils;
import com.redhat.qe.jon.sahi.base.SahiSettings;
import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.bundle.Bundle;
import com.redhat.qe.jon.sahi.base.bundle.BundleGroup;
import com.redhat.qe.jon.sahi.base.bundle.BundleRemote;

public class BundleTest extends SahiTestScript{
	private BundleRemote _bundle = null;
	private static final String bundleFileLocation 								= "automation-bundles/";
	private static final String bundleFileAbsoluteExplodedName 					= "absolute_exploded.zip";
	private static final String bundleFileAbsoluteNormalName 					= "absolute_normal.zip";
	private static final String bundleFileAbsoluteNormalFilesAndDirectoriesName = "absolute_normal_filesAndDirectories.zip";
	private static final String bundleFileRelativeExplodedName 					= "relative_exploded.zip";
	private static final String bundleFileRelativeNormalName 					= "relative_normal.zip";
	private static final String bundleFileRelativeNormalFilesAndDirectoriesName = "relative_normal_filesAndDirectories.zip";

	@BeforeClass(alwaysRun=true)
	public void setUp() {
		_bundle = new BundleRemote(sahiTasks);
		sahiTasks.createGroup("Compatible Groups", "platform-group-automation", "Created by Automation", SahiSettings.getJonAgentName());
	}
	
	@AfterClass
	public void tearDown() {
		sahiTasks.deleteGroup("Compatible Groups", "platform-group-automation");
	}

	@Test(groups="bundleTest", dataProvider="bundleGroupData")
	public void createBundleGroup(BundleGroup bundleGroup){
		Assert.assertTrue(_bundle.createBundleGroup(bundleGroup), "Status of Bundle Group Creation ["+bundleGroup+"]");
	}

	@Test(groups="bundleTest", dataProvider="bundleData", dependsOnMethods={"createBundleGroup"})
	public void createBundle(Bundle bundle){
		Assert.assertTrue(_bundle.createBundle(bundle), "Status of Bundle Creation["+bundle+"]");
	}

	@Test(groups="bundleTest", dataProvider="bundleData", dependsOnMethods={"createBundle"})
	public void deployBundle(Bundle bundle) throws IOException{
		Assert.assertTrue(_bundle.deployFileBundle(bundle), "Status of Bundle Deploy["+bundle+"]");
	}

	@Test(groups="bundleTest", dataProvider="bundleData", dependsOnMethods={"deployBundle"})
	public void deleteBundle(Bundle bundle) throws IOException{
		Assert.assertTrue(_bundle.deleteBundle(bundle), "Status of Bundle Deletion["+bundle+"]");
	}

	@Test(groups="bundleTest", dataProvider="bundleGroupData", dependsOnMethods={"deleteBundle"})
	public void deleteBundleGroup(BundleGroup bundleGroup){
		Assert.assertTrue(_bundle.deleteBundleGroup(bundleGroup), "Status of Bundle Group Creation ["+bundleGroup+"]");
	}



	@DataProvider(name="bundleData")
	public Object[][] bundleData() {
		return TestNGUtils.convertListObjectTo2dArray(getBundleData());
	}

	@DataProvider(name="bundleGroupData")
	public Object[][] bundleGroupData() {
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(getBundleGroup());
		return TestNGUtils.convertListObjectTo2dArray(data);
	}


	public List<Object> getBundleData() {
		ArrayList<Object> data = new ArrayList<Object>();

		//Bundle Absolute with exploded enabled and compliance=full
		Bundle bundle = new Bundle();
		bundle.setName("automation-sample-bundle-absolute-exploded");
		bundle.setFilename(bundleFileAbsoluteExplodedName);
		bundle.setDescription("Bundle by Automation");
		bundle.setUrl(SahiSettings.getFileStoreUrl()+bundleFileLocation);
		bundle.setGroups(getBundleGroupName());
		data.add(bundle);

		//Bundle Absolute with exploded disabled and compliance=full
		bundle = new Bundle();
		bundle.setName("automation-sample-bundle-absolute-normal");
		bundle.setFilename(bundleFileAbsoluteNormalName);
		bundle.setDescription("Bundle by Automation");
		bundle.setUrl(SahiSettings.getFileStoreUrl()+bundleFileLocation);
		bundle.setGroups(getBundleGroupName());
		data.add(bundle);

		//Bundle Absolute with exploded disabled and compliance=filedAndDirectories
		bundle = new Bundle();
		bundle.setName("automation-sample-bundle-absolute-normal-filesAndDirectories");
		bundle.setFilename(bundleFileAbsoluteNormalFilesAndDirectoriesName);
		bundle.setDescription("Bundle by Automation");
		bundle.setUrl(SahiSettings.getFileStoreUrl()+bundleFileLocation);
		bundle.setGroups(getBundleGroupName());
		data.add(bundle);

		//Bundle Relative with exploded enabled and compliance=full
		bundle = new Bundle();
		bundle.setName("automation-sample-bundle-relative-exploded");
		bundle.setFilename(bundleFileRelativeExplodedName);
		bundle.setDescription("Bundle by Automation");
		bundle.setUrl(SahiSettings.getFileStoreUrl()+bundleFileLocation);
		bundle.setGroups(getBundleGroupName());
		data.add(bundle);

		//Bundle Relative with exploded disabled and compliance=full
		bundle = new Bundle();
		bundle.setName("automation-sample-bundle-relative-normal");
		bundle.setFilename(bundleFileRelativeNormalName);
		bundle.setDescription("Bundle by Automation");
		bundle.setUrl(SahiSettings.getFileStoreUrl()+bundleFileLocation);
		bundle.setGroups(getBundleGroupName());
		data.add(bundle);

		//Bundle Relative with exploded disabled and compliance=filedAndDirectories
		bundle = new Bundle();
		bundle.setName("automation-sample-bundle-relative-normal-filesAndDirectories");
		bundle.setFilename(bundleFileRelativeNormalFilesAndDirectoriesName);
		bundle.setDescription("Bundle by Automation");
		bundle.setUrl(SahiSettings.getFileStoreUrl()+bundleFileLocation);
		bundle.setGroups(getBundleGroupName());
		data.add(bundle);

		return data;
	}

	public static BundleGroup getBundleGroup(){
		BundleGroup bundleGroup = new BundleGroup();
		bundleGroup.setName("automation-bundle-group");
		bundleGroup.setDescription("Bundle Group by Automation");
		return bundleGroup;	
	}

	public static List<String> getBundleGroupName(){
		ArrayList<String> bundleGroupName = new ArrayList<String>();
		bundleGroupName.add(getBundleGroup().getName());
		return bundleGroupName;	
	}
}
