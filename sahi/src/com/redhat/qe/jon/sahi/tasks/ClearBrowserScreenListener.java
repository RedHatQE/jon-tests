package com.redhat.qe.jon.sahi.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.ITestResult;

import com.redhat.qe.jon.sahi.base.SahiTestScript;




public class ClearBrowserScreenListener extends com.redhat.qe.auto.testng.TestNGListener {

	ClearBrowsersPopUp clearBrowsersPopUp = new ClearBrowsersPopUp();

	@Override
	public void onTestFailure(ITestResult result) {
		super.onTestFailure(result);
		clearBrowsersPopUp.cleanPopUpOnScreen();
	}
}


class ClearBrowsersPopUp extends SahiTestScript{
	private static Logger _logger = Logger.getLogger(ClearBrowserScreenListener.class.getName());
	protected static String[] buttonsToClick = {"Cancel", "No"}; //Add button names here
	protected static String[] linksToClick = {};//Add link names here
	public void cleanPopUpOnScreen(){
		//For buttons
		for(String buttonName : buttonsToClick){
			if(sahiTasks.cell(buttonName).exists()){
				sahiTasks.cell(buttonName).click();
				_logger.log(Level.INFO, "[Clear pop-up] Clicked on the button --> "+buttonName);

			}else{
				_logger.log(Level.INFO, "[Clear pop-up] Button not found --> "+buttonName);
			}
		}
		//For links
		for(String linkName : linksToClick){
			if(sahiTasks.link(linkName).exists()){
				sahiTasks.link(linkName).click();
				_logger.log(Level.INFO, "[Clear pop-up] Clicked on the link --> "+linkName);

			}else{
				_logger.log(Level.INFO, "[Clear pop-up] Link not found --> "+linkName);
			}
		}
	}

}
