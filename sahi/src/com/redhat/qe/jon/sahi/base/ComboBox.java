package com.redhat.qe.jon.sahi.base;


import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.sahi.client.Browser;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Aug 19, 2011
 */
public class ComboBox {
	private static Logger _logger = Logger.getLogger(ComboBox.class.getName());

	public static void selectComboBoxDivRow(Browser browser, String comboBoxIdentifier, String optionToSelect){
		browser.focus(browser.div(comboBoxIdentifier));
		browser.xy(browser.div(comboBoxIdentifier), 3, 3).click();
				
		//To select on scroll location
		/*for(int i=0;i<2;i++){			
			browser.execute("_sahi._keyPress(_sahi._row('"+optionToSelect+"'), 40);");
			browser.mouseOver(browser.row(optionToSelect));
		}	*/	
		browser.focus(browser.row(optionToSelect));
		browser.xy(browser.row(optionToSelect), 3, 3).click();
		_logger.log(Level.INFO, "Clicked on \""+optionToSelect+"\"");
	}
}
