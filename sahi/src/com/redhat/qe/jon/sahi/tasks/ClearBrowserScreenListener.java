package com.redhat.qe.jon.sahi.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

import com.redhat.qe.jon.sahi.base.SahiTestScript;




public class ClearBrowserScreenListener extends SahiTestScript implements IResultListener, ISuiteListener {

	private static Logger _logger = Logger.getLogger(ClearBrowserScreenListener.class.getName());
	
	
	protected static String[] buttonsToClick = {"Cancel", "No"}; //Add button names here
	protected static String[] linksToClick = {};//Add link names here
	
	
	public void cleanPopUpOnScreen(){
		_logger.log(Level.INFO, "[Clear pop-up] Entering...");
		//For buttons
		for(String buttonName : buttonsToClick){
			while(sahiTasks.cell(buttonName).exists()){
				sahiTasks.cell(buttonName).click();
				_logger.log(Level.INFO, "[Clear pop-up] Clicked on the button --> "+buttonName);
			}
		}
		//For links
		for(String linkName : linksToClick){
			while(sahiTasks.link(linkName).exists()){
				sahiTasks.link(linkName).click();
				_logger.log(Level.INFO, "[Clear pop-up] Clicked on the link --> "+linkName);
			}
		}
		_logger.log(Level.INFO, "[Clear pop-up] Exiting...");
	}
	
	
	@Override
	public void onTestFailure(ITestResult result) {
		cleanPopUpOnScreen();
	}

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationSkip(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(ISuite arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(ISuite arg0) {
		// TODO Auto-generated method stub
		
	}
}