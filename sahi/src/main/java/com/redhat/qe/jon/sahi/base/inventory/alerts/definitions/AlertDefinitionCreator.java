package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions;

import java.util.ArrayList;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.jon.sahi.base.editor.AlertDefinitionEditor;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;

/**
 * This class helps to create a new alert definition.
 * @author fbrychta
 *
 */
public class AlertDefinitionCreator {
		private final SahiTasks tasks;
		private final AlertDefinitionEditor editor;
		private static final String TEXT_FIELD_SELECTOR = "/textItem.*/";
		private static final String COMBOBOX_SELECTOR = "/selectItemText.*/";
		
		/**
		 * Creates a new instance and opens creation dialog.
		 * @param tasks
		 * @param alertName name of alert definition to be created
		 */
		public AlertDefinitionCreator(SahiTasks tasks,String alertName){
			this.tasks = tasks;
			this.editor = new AlertDefinitionEditor(tasks);
			tasks.cell("New").click();
			editor.setTextNearCell(TEXT_FIELD_SELECTOR, alertName, "Name :");
		}
		
		/**
		 * Fills in fields on General Properties tab.
		 * @param description
		 * @param priority
		 * @param enabled
		 * @return this object
		 */
		public AlertDefinitionCreator withGeneralProp(String description,AlertDefinition.Priority priority,boolean enabled){
			tasks.cell("General Properties").click();
			editor.setTextInTextAreaNearCell(TEXT_FIELD_SELECTOR, description, "Name :");
	    	tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Priority :", priority.toString());
	    	
	    	if(enabled){
	    		editor.checkRadioNearCell("enabled", "Yes");
	    	}else{
	    		editor.checkRadioNearCell("enabled", "No");
	    	}
	    	
			return this;
		}
		
		/**
		 * Sets "Fire alert when" combo box.
		 * @param fireWhen
		 * @return this object
		 */
		public AlertDefinitionCreator setConditionOperator(Condition.Operator fireWhen){
			tasks.selectComboBoxByNearCellOptionByDiv(tasks, COMBOBOX_SELECTOR, "Fire alert when :", fireWhen.toString());
			
			return this;
		}
		
		/**
		 * Adds given condition.
		 * @param condition
		 * @return this object
		 * @see Condition
		 */
		public AlertDefinitionCreator addCondition(Condition condition){
			tasks.cell("Conditions").click();
			tasks.cell("Add[0]").click();
			tasks.selectComboBoxByNearCellOptionByDiv(tasks, COMBOBOX_SELECTOR, "Condition Type :", condition.getType());
			fillFields(condition.getFields());
			
			tasks.cell("OK").click();
			if(tasks.isVisible(tasks.cell("formCellError"))){
				throw new RuntimeException("Form which you are submitting contains validation errors!");
			}
			tasks.waitFor(Timing.WAIT_TIME);
			
			return this;
		}
		
		/**
		 * Adds a notification with "CLI Script" as a notification sender with given values.
		 * Other values are left default.
		 * @param repository name of the repository where the cli script can be found
		 * @param existingScript full displayed name of the existing script
		 * @return this object
		 */
		public AlertDefinitionCreator addCliScriptNotification(String repository,String existingScript){
			return addCliScriptNotification(null, null, repository, existingScript, null, null);
		}
		
		/**
		 * Adds a notification with "CLI Script" as a notification sender with given values.
		 * Other values are left default.
		 * @param repository name of the repository where the cli script will be uploaded
		 * @param newScript path to the cli script which will be uploaded
		 * @param newScriptVersion
		 * @return this object
		 */
		public AlertDefinitionCreator addCliScriptNotification(String repository,String newScript,String newScriptVersion ){
			return addCliScriptNotification(null, null, repository, null, newScript, newScriptVersion);
		}
		/**
		 * Adds a notification with "CLI Script" as a notification sender with given values.
		 * @param userName name of the user to run a script as
		 * @param password password of the user to run a script as
		 * @param repository name of the repository where the cli script can be found
		 * @param existingScript full displayed name of the existing script
		 * @return this object
		 */
		public AlertDefinitionCreator addCliScriptNotification(String userName,String password,String repository,String existingScript ){
			return addCliScriptNotification(userName, password, repository, existingScript, null, null);
		}
		/**
		 * Adds a notification with "CLI Script" as a notification sender with given values.
		 * @param userName
		 * @param password
		 * @param repository
		 * @param newScript
		 * @param newScriptVersion
		 * @return this object
		 */
		public AlertDefinitionCreator addCliScriptNotification(String userName,String password,String repository,
				String newScript,String newScriptVersion ){
			return addCliScriptNotification(userName, password, repository, null, newScript, newScriptVersion);
		} 
		
		/**
		 * Fills in given values.
		 * @param userName
		 * @param password
		 * @param repository
		 * @param existingScript
		 * @param newScript
		 * @param newScriptVersion
		 * @return this object
		 */
		private AlertDefinitionCreator addCliScriptNotification(String userName,String password,
				String repository,String existingScript,String newScript,String newScriptVersion){
			tasks.cell("Notifications").click();
			tasks.cell("Add[1]").click();
			tasks.selectComboBoxByNearCellOptionByDiv(tasks, COMBOBOX_SELECTOR, "Notification Sender :", "CLI Script");
			
			// TODO check that all the fields were filled successfully
			if(userName!=null){
				editor.checkRadioNearCell("AnotherUser", "Another User");
				editor.setTextNearCell(TEXT_FIELD_SELECTOR, userName, "User Name :");
			}
			if(password!=null){
				editor.setPasswordNearCell("password", password, "Password :");
			}
			
			
			tasks.selectComboBoxByNearCellOptionByDiv(tasks, COMBOBOX_SELECTOR,
					"Select the repository where the script should reside :", repository);
			if(existingScript!= null){
				editor.selectCombo(2, existingScript);
				//tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR,"Existing Script", existingScript);
			}
			if(newScript != null){
				editor.checkRadioNearCell("UploadNewScript", "Upload New Script");
				tasks.setFileToUpload("fileUploadItem",newScript);
			}
			if(newScriptVersion != null){
				editor.setTextNearCell(TEXT_FIELD_SELECTOR, newScriptVersion, "Version :");
			}
			
			ElementStub okBut = tasks.cell("OK");
			tasks.waitForElementVisible(tasks, okBut, "OK button", Timing.WAIT_TIME);
			okBut.click();
			// this is a hack, sometimes first click simply doesn't work
			if(okBut.isVisible()){
				okBut.click();
			}
			
			if(tasks.isVisible(tasks.cell("formCellError"))){
				throw new RuntimeException("Form which you are submitting contains validation errors!");
			}
			tasks.waitFor(Timing.WAIT_TIME);
			
			return this;
		}
		
		/**
		 * Sets fields on Recovery tab.
		 * @param recoveryAlert full displayed name of recovery alert in combo box
		 * @param disabledWhenFired
		 * @return this object
		 */
		public AlertDefinitionCreator setRecovery(String recoveryAlert,boolean disabledWhenFired){
			tasks.cell("Recovery").click();
			tasks.selectComboBoxByNearCellOptionByDiv(tasks, "selectItemText", "Recover Alert :", recoveryAlert);
			if(disabledWhenFired){
				editor.checkRadioNearCell("disableWhenFired", "Yes");
			}else{
				editor.checkRadioNearCell("disableWhenFired", "no");
			}
			
			return this;
		}
		
		/**
		 * Sets fields on Dampening tab.
		 * @param dampening string to be selected in the combo box
		 * @return this object
		 */
		public AlertDefinitionCreator setDampening(String dampening){
			tasks.cell("Dampening").click();
			tasks.selectComboBoxByNearCellOptionByDiv(tasks, "selectItemText", "Dampening :", dampening);
			
			return this;
		}
		
		
		/**
		 * Saves this alert definition.
		 */
		public void save(){
			tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);
			tasks.cell("Save").click();
			tasks.waitForElementVisible(tasks, tasks.cell("Alert definition successfully created"), 
					"Successful message",Timing.WAIT_TIME);
		}
		
		/**
		 * Fill/set given fields.
		 */
		private void fillFields(ArrayList<Field> fields){
			//TODO test all field types, so far only COMBO and TEXT are used(didn't find any condition with different fields)
			for(Field f : fields){
				switch (f.getType()){
					case COMBO:
						if(f.getFieldLocator()==null){
							tasks.selectComboBoxByNearCellOptionByDiv(tasks, COMBOBOX_SELECTOR,f.getNearLocator(), f.getValue());
						}else{
							tasks.selectComboBoxByNearCellOptionByDiv(tasks, f.getFieldLocator(),f.getNearLocator(), f.getValue());
						}
						break;
					case TEXT:
						if(f.getFieldLocator()==null){
							editor.setTextNearCell(TEXT_FIELD_SELECTOR, f.getValue(), f.getNearLocator());
						}else{
							editor.setTextNearCell(f.getFieldLocator(), f.getValue(), f.getNearLocator());
						}
						break;
					case RADIO:
						if(f.checked){
							if(f.getFieldLocator() == null){
								editor.checkRadioNearCell("/isc.*/", f.getNearLocator());
							}else{
								editor.checkRadioNearCell(f.getFieldLocator(), f.getNearLocator());
							}
						}
						break;
					case CHECKBOX:
						if(f.checked){
							tasks.image("0").near(tasks.cell(f.getFieldLocator())).check();
						}else{
							tasks.image("0").near(tasks.cell(f.getFieldLocator())).uncheck();
						}
						break;
					case FILE:
						tasks.setFileToUpload("fileUploadItem",f.getValue());
						// TODO
						break;
					case PASSWORD:
						if(f.getFieldLocator()==null){
							editor.setPasswordNearCell("password", f.getValue(), f.getNearLocator());
						}else{
							editor.setPasswordNearCell(f.getFieldLocator(), f.getValue(), f.getNearLocator());
						}
						break;
					case TEXTAREA:
						if(f.getFieldLocator()==null){
							editor.setTextInTextAreaNearCell(TEXT_FIELD_SELECTOR, f.getValue(), f.nearLocator);
						}else{
							editor.setTextInTextAreaNearCell(f.getFieldLocator(), f.getValue(), f.nearLocator);
						}
						break;
					default:
						break;
				}
			}
		}
		
		/**
		 * This is a helper class which represents a field which is part of <class>Condition</class>
		 */
		public static class Field{
			public enum FieldType{COMBO,TEXT,RADIO,PASSWORD,FILE,TEXTAREA,CHECKBOX}
			private String fieldLocator = null;
			private String nearLocator = null;
			private String value = null;
			private FieldType type = null;
			private boolean checked = false;
			
			
			public Field(String nearLocator,String value,FieldType fieldType){
				this(nearLocator,value,fieldType,null);
			}
			public Field(String nearLocator,String value,FieldType fieldType, String fieldLocator){
				this.value = value;
				this.type = fieldType;
				this.nearLocator = nearLocator;
				this.fieldLocator = fieldLocator;
			}
			
			public Field(String nearLocator,FieldType fieldType, boolean checked){
				this(nearLocator,fieldType,checked,null);
			}
			public Field(String nearLocator,FieldType fieldType, boolean checked,String fieldLocator){
				this.type = fieldType;
				this.checked = checked;
				this.nearLocator = nearLocator;
				this.fieldLocator = fieldLocator;
			}
			
			
			public String getFieldLocator(){
				return this.fieldLocator;
			}
			public String getValue(){
				return this.value;
			}
			public FieldType getType(){
				return this.type;
			}
			public String getNearLocator() {
				return nearLocator;
			}
			public boolean isChecked(){
				return this.checked;
			}
		}
		
		/**
		 * This class represents a condition which is used during creation of alert definition.
		 * <p>
		 * This class is generic so you can use it to define all possible conditions.
		 * Each <class>Field</class> contained in this class defines a form field which
		 * is filled by Sahi during the creation of condition.
		 * <p>
		 * Example: To define Availability Change condition
		 * <p>
		 * <code>
		 * Condition cond = new Condition("Availability Change");
		 * cond.addField("Availability :","Goes down",AlertDefinitionCreator.Field.FieldType.COMBO);
		 * </code>
		 * <p>
		 * You can see that a label next to the combo box was used as a near cell locator.
		 * @see AlertDefinitionCreator.Field  
		 *
		 */
		public static class Condition{
			private String type;
			private ArrayList<AlertDefinitionCreator.Field> fields = new ArrayList<AlertDefinitionCreator.Field>();
			/**
			 * Defines values for "Fire Alert when:" combo box.
			 */
			public enum Operator{ANY,ALL}
			
			/**
			 * Creates a new instance with defined type.
			 * @param type value from the "Condition type: " combo box
			 */
			public Condition(String type){
				this.type = type;
			}
			
			public String getType(){
				return this.type;
			}
			
			public ArrayList<AlertDefinitionCreator.Field> getFields(){
				return this.fields;
			}
			
			/**
			 * Adds field which is part of this condition.
			 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
			 * @param value value which will be set
			 * @param fieldType defines type of the field (COMBO, TEXT,...)
			 * @return this object
			 */
			public Condition addField(String nearLocator,String value,AlertDefinitionCreator.Field.FieldType fieldType){
				fields.add(new AlertDefinitionCreator.Field(nearLocator,value,fieldType));
				
				return this;
			}
			/**
			 * Adds field which is part of this condition.
			 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
			 * @param value value which will be set
			 * @param fieldType defines type of the field (COMBO, TEXT,...)
			 * @param fieldLocator if you want to provide your own filed locator (i.e. id of the field)
			 * @return this object
			 */
			public Condition addField(String nearLocator,String value,AlertDefinitionCreator.Field.FieldType fieldType,String fieldLocator){
				fields.add(new AlertDefinitionCreator.Field(nearLocator,value,fieldType,fieldLocator));
				
				return this;
			}
			/**
			 * Adds field which is part of this condition.
			 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
			 * @param fieldType defines type of the field (COMBO, TEXT,...)
			 * @param checked used for RADIO and CHECKBOX
			 * @return this object
			 */
			public Condition addField(String nearLocator,AlertDefinitionCreator.Field.FieldType fieldType, boolean checked){
				fields.add(new AlertDefinitionCreator.Field(nearLocator,fieldType,checked));
				
				return this;
			}
			/**
			 * Adds field which is part of this condition.
			 * @param nearLocator this locator is used to define near Cell (i.e. use label next to the field)
			 * @param fieldType defines type of the field (COMBO, TEXT,...)
			 * @param checked used for RADIO and CHECKBOX
			 * @param fieldLocator if you want to provide your own filed locator (i.e. id of the field)
			 * @return this object
			 */
			public Condition addField(String nearLocator,AlertDefinitionCreator.Field.FieldType fieldType, boolean checked,String fieldLocator){
				fields.add(new AlertDefinitionCreator.Field(nearLocator,fieldType,checked,fieldLocator));
				
				return this;
			}
		}



		/**
		 * This class represents a notification with a CLI Script as a notification sender.
		 */
		public static class CliScriptNotification{
			private boolean runAsAnotherUser = false;
			private String userName = null;
			private String password = null;
			private String repository = null;
			private String existingScript = null;
			private String newScript = null;
			private String newScriptVersion = null;
			private boolean uploadNewScript = false;
			
			// TODO not yet used, for future purpose
			public boolean isRunAsAnotherUser() {
				return runAsAnotherUser;
			}
			public void setRunAsAnotherUser(boolean runAsAnotherUser) {
				this.runAsAnotherUser = runAsAnotherUser;
			}
			public String getUserName() {
				return userName;
			}
			public void setUserName(String userName) {
				this.userName = userName;
			}
			public String getPassword() {
				return password;
			}
			public void setPassword(String password) {
				this.password = password;
			}
			public String getRepository() {
				return repository;
			}
			public void setRepository(String repository) {
				this.repository = repository;
			}
			public String getExistingScript() {
				return existingScript;
			}
			public void setExistingScript(String existingScript) {
				this.existingScript = existingScript;
			}
			public String getNewScript() {
				return newScript;
			}
			public void setNewScript(String newScript) {
				this.newScript = newScript;
			}
			public String getNewScriptVersion() {
				return newScriptVersion;
			}
			public void setNewScriptVersion(String newScriptVersion) {
				this.newScriptVersion = newScriptVersion;
			}
			public boolean isUploadNewScript() {
				return uploadNewScript;
			}
			public void setUploadNewScript(boolean uploadNewScript) {
				this.uploadNewScript = uploadNewScript;
			}
		}
}
