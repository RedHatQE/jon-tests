package com.redhat.qe.jon.sahi.tasks;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import org.apache.commons.io.FileUtils;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.internal.IResultListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;




public class ClearBrowserScreenListener extends SahiTestScript implements IResultListener, ISuiteListener {

	private static Logger _logger = Logger.getLogger(ClearBrowserScreenListener.class.getName());
	
	
	protected static String[] buttonsToClick = {"Cancel", "No"}; //Add button names here
	protected static String[] linksToClick = {};//Add link names here
	protected static int elementCount = 4;

    public ClearBrowserScreenListener() {
        super();
    }


    public ClearBrowserScreenListener(SahiTasks mySahiTasks) {
        super(mySahiTasks);
    }


	public void cleanPopUpOnScreen(){
		_logger.log(Level.INFO, "[Clear pop-up] Entering...");
		//For buttons
		for(String buttonNameTmp : buttonsToClick){
			elementCount = sahiTasks.cell(buttonNameTmp).countSimilar();
			_logger.log(Level.INFO, "Number of '"+buttonNameTmp+"' buttons: "+elementCount);
			if(elementCount != 0){
				for(int i=elementCount;i>0; i--){
					String buttonName = buttonNameTmp+"["+(i-1)+"]";
					if(sahiTasks.cell(buttonName).isVisible()){
						sahiTasks.cell(buttonName).click();
						_logger.log(Level.FINE, "[Clear pop-up] Button [\""+buttonName+"\"] found and clicked");
						if(sahiTasks.cell(buttonName).isVisible()){
							_logger.log(Level.INFO, "Failed to click '"+buttonName+"'  button with normal click action, trying with keyPress..");
							sahiTasks.execute("_sahi._keyPress(_sahi._cell('"+buttonName+"'), 32);"); //32 - Space bar
							_logger.log(Level.INFO, "[Clear pop-up] Button [\""+buttonName+"\"] found and clicked by keyPress");
							if(sahiTasks.cell(buttonName).isVisible()){
								_logger.log(Level.WARNING, "[Clear pop-up] Button [\""+buttonName+"\"] Still exists!");
							}
						}
						
					}else{
						_logger.log(Level.FINE, "[Clear pop-up] Button [\""+buttonName+"\"] not available to click");
					}
				}
			}else{
				_logger.log(Level.INFO, "There is no '"+buttonNameTmp+"' to click!");
			}
			
		}
		//For links
		for(String linkNameTmp : linksToClick){
			for(int i=elementCount;i>=0; i--){
				String linkName = linkNameTmp+"["+i+"]";
				if(sahiTasks.cell(linkName).exists()){
					sahiTasks.cell(linkName).click();
					_logger.log(Level.INFO, "[Clear pop-up] Link [\""+linkName+"\"] found and clicked");
				}else{
					_logger.log(Level.FINE, "[Clear pop-up] Link [\""+linkName+"\"] not available..");
				}
			}
		}
		sahiTasks.execute("_sahi._call(window.location.reload());");
		_logger.log(Level.INFO, "[Clear pop-up] Exiting...");
	}
	
	//To take Screen shot
	public void takeScreenShot(ITestResult result){
        String disableScreenShots = System.getenv("DISABLE_SCREENSHOTS");
        if (disableScreenShots != null && !disableScreenShots.isEmpty() && Boolean.parseBoolean(disableScreenShots) == true) {
           _logger.log(Level.WARNING, "Screenshots are disabled => skipping making a screenshot");
           return;
        }
        String fileDirPath = null;
        String timestamp = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ssaa").format(new Date());
		try{
			_logger.log(Level.INFO, "Taking screen shot...");
			fileDirPath = new File(result.getTestContext().getOutputDirectory()).getParent()+File.separator+"html"+File.separator;
			if(new File(fileDirPath).mkdirs()){
				_logger.log(Level.INFO, "Directory Created... : "+fileDirPath);
			}else{
				_logger.log(Level.FINER, "Directory might be available: "+fileDirPath);
			}
			String fileName = "ScreenShot_"+timestamp+".png";
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
		} finally {
            if (fileDirPath != null) {
                try {
                    String fileName = "PageHtml_" + timestamp + ".html";
                    String html = sahiTasks.fetch("innerHTML");
                    FileUtils.writeStringToFile(new File(fileDirPath, fileName), html);
                } catch (Exception ex) {
                    _logger.log(Level.WARNING, "Unable to fetch the html page to file, ", ex);
                }
            }
        }
    }

	@Override
	public void onTestFailure(ITestResult result) {
		takeScreenShot(result);
		try {		
			cleanPopUpOnScreen();
		} catch (Exception ex) {
			_logger.log(Level.SEVERE, "Failed to clean pop-ups on screen!!", ex);
		}
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
	public void onConfigurationFailure(ITestResult result) {
        takeScreenShot(result);
        try {
            cleanPopUpOnScreen();
        } catch (Exception ex) {
            _logger.log(Level.SEVERE, "Failed to clean pop-ups on screen!!", ex);
        }
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