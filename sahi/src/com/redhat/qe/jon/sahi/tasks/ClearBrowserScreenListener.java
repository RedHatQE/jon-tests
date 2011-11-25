package com.redhat.qe.jon.sahi.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.internal.IResultListener;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

import com.redhat.qe.jon.sahi.base.SahiTestScript;




public class ClearBrowserScreenListener extends SahiTestScript implements IResultListener, ISuiteListener {

	private static Logger _logger = Logger.getLogger(ClearBrowserScreenListener.class.getName());
	
	
	protected static String[] buttonsToClick = {"Cancel", "No"}; //Add button names here
	protected static String[] linksToClick = {};//Add link names here
	
	
	public void cleanPopUpOnScreen(){
		_logger.log(Level.INFO, "[Clear pop-up] Entering...");
		//For buttons
		for(String buttonName : buttonsToClick){
			if(sahiTasks.cell(buttonName).exists()){
				sahiTasks.cell(buttonName).click();
				_logger.log(Level.INFO, "[Clear pop-up] Clicked on the button --> "+buttonName);
			}else{
				_logger.log(Level.INFO, "[Clear pop-up] There is no button --> "+buttonName);
			}
		}
		//For links
		for(String linkName : linksToClick){
			if(sahiTasks.link(linkName).exists()){
				sahiTasks.link(linkName).click();
				_logger.log(Level.INFO, "[Clear pop-up] Clicked on the link --> "+linkName);
			}else{
				_logger.log(Level.INFO, "[Clear pop-up] There is no link --> "+linkName);
			}
		}
		_logger.log(Level.INFO, "[Clear pop-up] Exiting...");
	}
	
	//To take Screen shot
	public void takeScreenShot(){
		try{
			_logger.log(Level.INFO, "Taking screen shot...");
			String fileDirPath = System.getProperty("user.dir")+"/"+System.getProperty("testng.outputdir")+"/html/";
			if(new File(fileDirPath).mkdirs()){
				_logger.log(Level.INFO, "Directory Created... : "+fileDirPath);
			}else{
				_logger.log(Level.FINER, "Directory might be available: "+fileDirPath);
			}
			String fileName = "ScreenShot_"+new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ssaa").format(new Date())+".png";
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle screenRectangle = new Rectangle(screenSize);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRectangle);
			_logger.log(Level.INFO, "ScreenShot File name: "+fileDirPath+fileName);
			ImageIO.write(image, "png", new File(fileDirPath+fileName));			
			Reporter.log("<a href=\""+fileName+"\"><b>Screen Shot</b></a>");
			_logger.log(Level.INFO, "Screen shot done!!");
		}catch(Exception ex){
			_logger.log(Level.WARNING, "Unable to take screen shot, ", ex);
		}
		
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		takeScreenShot();
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