package com.redhat.qe.jon.sahi.base.inventory.alerts.definitions;

import com.redhat.qe.jon.sahi.base.editor.Editor;
import com.redhat.qe.jon.sahi.base.inventory.alerts.definitions.conditions.Condition;
import com.redhat.qe.jon.sahi.tasks.SahiTasks;
import com.redhat.qe.jon.sahi.tasks.Timing;
import net.sf.sahi.client.ElementStub;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class represents dialog visible during creation/editing of alert 
 * definitions and is used for setting/getting values from fields.
 * 
 * @author fbrychta
 *
 */
public class AlertDefinitionEditor {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private final SahiTasks tasks;
	private final Editor editor;
	public static final String TEXT_FIELD_SELECTOR = "/textItem.*/";
	public static final String COMBOBOX_SELECTOR = "/selectItemText.*/";
	
	/**
	 * Creates a new instance and opens creation dialog.
	 * @param tasks
	 */
	public AlertDefinitionEditor(SahiTasks tasks){
		this.tasks = tasks;
		this.editor = new Editor(tasks);
		
	}
	
	/**
	 * Fills name of this alert definition.
	 * @param alertDefinitionName
	 * @return this object
	 */
	public AlertDefinitionEditor setName(String alertDefinitionName){
		tasks.cell("General Properties").click();
		log.fine("Setting alert definition name to: " +alertDefinitionName);
		editor.setTextNearCell(TEXT_FIELD_SELECTOR, alertDefinitionName, "Name :");
		
		return this;
	}
	
	/**
	 * Gets name of this alert definition.
	 * @return alert definition name
	 */
	public String getName(){
		tasks.cell("General Properties").click();
		return editor.getTextNearCell(TEXT_FIELD_SELECTOR, "Name :");
	}
	
	/**
	 * Fills in optional fields on General Properties tab.
	 * @param description
	 * @param priority
	 * @param enabled
	 * @return this object
	 */
	public AlertDefinitionEditor setGeneralProp(String description,AlertDefinition.Priority priority,boolean enabled){
		tasks.cell("General Properties").click();
		log.fine("Setting general options of alert definition: description:" +description+", priority: " +
				priority.toString()+", enabled: " +Boolean.toString(enabled));
		editor.setTextInTextAreaNearCell(TEXT_FIELD_SELECTOR, description, "Description :");
    	tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Priority :", priority.toString());
    	
    	if(enabled){
    		editor.checkRadioNearCell("enabled", "Yes");
    	}else{
    		editor.checkRadioNearCell("enabled", "No");
    	}
    	
		return this;
	}
	
	/**
	 * Gets description from General Properties tab.
	 * @return description
	 */
	public String getDesctiption(){
		tasks.cell("General Properties").click();
		return editor.getTextInTextAreaNearCell(TEXT_FIELD_SELECTOR, "Description :");
	}
	
	/**
	 * Gets priority from General Properties tab.
	 * @return priority
	 */
	public String getPriority(){
		tasks.cell("General Properties").click();
		
		return tasks.getSelectedTextFromComboNearCell(tasks, COMBOBOX_SELECTOR, "Priority :");
	}
	
	/**
	 * Returns true if this alert definition is enabled. False otherwise.
	 * @return true if this alert definition is enabled. False otherwise.
	 */
	public boolean isEnabled(){
		return editor.isRadioNearCellChecked("enabled", "Yes");
	}
	
	/**
	 * Sets "Fire alert when" combo box.
	 * @param fireWhen
	 * @return this object
	 */
	public AlertDefinitionEditor setConditionOperator(Condition.Operator fireWhen){
		log.fine("Setting condition operator: " +fireWhen.toString());
		tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Fire alert when :", fireWhen.toString());
		
		return this;
	}
	
	/**
	 * Returns selected condition operator.
	 * @return selected condition operator
	 */
	public Condition.Operator getConditionOperator(){
		tasks.cell("Conditions").click();
		String operatorStr = tasks.getSelectedTextFromComboNearCell(tasks, COMBOBOX_SELECTOR, "Fire alert when :");
		
		if(operatorStr.equalsIgnoreCase(Condition.Operator.ALL.toString())){
			return Condition.Operator.ALL;
		}else if(operatorStr.equalsIgnoreCase(Condition.Operator.ANY.toString())){
			return Condition.Operator.ANY;
		}else{
			throw new RuntimeException("Couldn't parse condition operator from page!");
		}
	}
	
	/**
	 * Adds given condition.
	 * @param condition
	 * @return this object
	 * @see Condition
	 * @return this object
	 */
	public AlertDefinitionEditor addCondition(Condition condition){
		tasks.cell("Conditions").click();
		tasks.cell("Add[0]").click();
		log.fine("Adding new alert condition of following type " + condition.getType());
		fillCondition(condition);
		
		return this;
	}
	
	/**
	 * Updates a condition with given name with given condition. 
	 * @param completeVisibleName - condition to be updated
	 * @param updatedCondition - resulting condition
	 * @return this object
	 */
	public AlertDefinitionEditor editCondition(String completeVisibleName,Condition updatedCondition){
		tasks.cell("Conditions").click();
		log.fine("Editing alert condition with name " + completeVisibleName);
		selectCondition(completeVisibleName);
    	tasks.waitForElementVisible(tasks, tasks.cell("Edit Condition"), "Edit button", Timing.WAIT_TIME);
    	tasks.cell("Edit Condition").click();
    	
    	tasks.waitForElementVisible(tasks, tasks.cell("Condition Type :"), "Condition Type label", Timing.WAIT_TIME);
    	
    	fillCondition(updatedCondition);
    	
		return this;
	}
	
	private void fillCondition(Condition condition){
		tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Condition Type :", condition.getType());
		fillFields(condition.getFields());
		
		tasks.cell("OK").click();
		if(tasks.isVisible(tasks.cell("formCellError"))){
			throw new RuntimeException("Form which you are submitting contains validation errors!");
		}
		tasks.waitFor(Timing.WAIT_TIME);
	}
	
	
	/**
	 * Opens edit condition dialog for condition with given name and gets 
	 * condition of given type.
	 * Values of returned condition are parsed from edit condition dialog.
	 * <p>
	 * Example usage:
	 * <p>
	 * <code>
	 * AvailDurationCondition availDurCond = 
	 * 		alertDefEditor.getCondition("Availability Duration [Stays Not Up For 10 m]",
	 		AvailDurationCondition.class);
	 * </code> 
	 * @param completeVisibleName - complete name as visible in GUI in condition tab table
	 * @param clazz type of condition which should be found
	 * @throws RuntimeException when condition with given name was not found
	 * in the table or when condition type parsed from page doesn't match given condition type
	 * @return condition of given type
	 */
	public <T extends Condition> T getCondition(String completeVisibleName, Class<T> clazz){
		openConditionEditDialog(completeVisibleName);
    	
    	Constructor<T> cons;
    	T condition = null;
		try {
			cons = clazz.getConstructor(SahiTasks.class);
			condition = cons.newInstance(tasks);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get given alarm condition",e);
		} 

    	
    	String condType = tasks.getSelectedTextFromComboNearCell(tasks, COMBOBOX_SELECTOR, "Condition Type :");
    	if(!condType.equals(condition.getType())){
    		throw new RuntimeException(condition.getType()+" condition expected, but "+
    				condType+" was parsed from page!");
    	}
    	condition.parseValuesFromPage();
    	closeConditionEditDialog();
    	
    	return condition;
	}
	
	/**
	 * Returns true if condition witch given name is found on the page.
	 * @param completeVisibleName
	 * @return true if condition witch given name is found on the page
	 */
	public boolean doesConditionExist(String completeVisibleName){
		tasks.cell("Conditions").click();
		int rows = tasks.cell(completeVisibleName).countSimilar();
        if(rows==0){
        	return false;
        }
        return true;
	}
	
	private void openConditionEditDialog(String completeVisibleName){
		log.fine("Opening condition edit dialog");
		tasks.cell("Conditions").click();
		log.fine("Getting alert condition with name " + completeVisibleName);
		selectCondition(completeVisibleName);
    	tasks.waitForElementVisible(tasks, tasks.cell("Edit Condition"), "Edit button", Timing.WAIT_TIME);
    	tasks.cell("Edit Condition").click();
    	
    	tasks.waitForElementVisible(tasks, tasks.cell("Condition Type :"), "Condition Type label", Timing.WAIT_TIME);
	}
	
	private void closeConditionEditDialog(){
		log.fine("Closing condition edit dialog");
		tasks.cell("Cancel[1]").click();
		tasks.waitFor(Timing.WAIT_TIME);
	}
	
	/**
	 * Deletes given alert condition.
	 * @param completeVisibleName - complete visible name displayed in Condition table
	 * @return this object
	 */
	public AlertDefinitionEditor deleteCondition(String completeVisibleName){
		tasks.cell("Conditions").click();
		log.fine("Deleting alert condition with name " + completeVisibleName);
		selectCondition(completeVisibleName);
		editor.getVisibleElement(tasks.cell("Delete")).click();
		
    	editor.serveConfirmDialog("Yes");
    	
    	tasks.waitForElementVisible(tasks, tasks.cell("Add"), "Add button", Timing.WAIT_TIME);
    	if(this.doesConditionExist(completeVisibleName)){
    		throw new RuntimeException("Failed to remove given condition with name: " +completeVisibleName);
    	}
    	
		return this;
	}
	
	private void selectCondition(String completeVisibleName){
		if(!editor.selectRow(completeVisibleName, 0)){
			throw new RuntimeException("Condition with given name: " +completeVisibleName+
					", was not found on the page.");
		}
	}
	
	/**
	 * Adds a notification with "CLI Script" as a notification sender with given values.
	 * Other values are left default.
	 * @param repository name of the repository where the cli script can be found
	 * @param existingScript full displayed name of the existing script
	 * @return this object
	 */
	public AlertDefinitionEditor addCliScriptNotification(String repository,String existingScript){
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
	public AlertDefinitionEditor addCliScriptNotification(String repository,String newScript,String newScriptVersion ){
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
	public AlertDefinitionEditor addCliScriptNotification(String userName,String password,String repository,String existingScript ){
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
	public AlertDefinitionEditor addCliScriptNotification(String userName,String password,String repository,
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
	private AlertDefinitionEditor addCliScriptNotification(String userName,String password,
			String repository,String existingScript,String newScript,String newScriptVersion){
		tasks.cell("Notifications").click();
		tasks.cell("Add[1]").click();
		log.fine("Adding cli script notificantion");
		tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Notification Sender :", "CLI Script");
		
		// TODO check that all the fields were filled successfully
		if(userName!=null){
			editor.checkRadioNearCell("AnotherUser", "Another User");
			editor.setTextNearCell(TEXT_FIELD_SELECTOR, userName, "User Name :");
		}
		if(password!=null){
			editor.setPasswordNearCell("password", password, "Password :");
		}
		
		
		tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR,
				"Select the repository where the script should reside :", repository);
		if(existingScript!= null){
			// select last picker
			int pickers = tasks.image("comboBoxPicker.png").countSimilar();
			editor.selectCombo(pickers -1, existingScript);
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
		tasks.waitFor(500);
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
	public AlertDefinitionEditor setRecovery(String recoveryAlert,boolean disabledWhenFired){
		tasks.cell("Recovery").click();
		log.fine("Setting recovery alert to: " + recoveryAlert+". Disabled when fired: " +Boolean.toString(disabledWhenFired));
		tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Recover Alert :", recoveryAlert);
		if(disabledWhenFired){
			editor.checkRadioNearCell("disableWhenFired", "Yes");
		}else{
			editor.checkRadioNearCell("disableWhenFired", "no");
		}
		
		return this;
	}
	
	/**
	 * Gets name of alert set on Recovery tab.
	 * @return name of alert set on Recovery tab.
	 */
	public String getRecoveryAlertName(){
		tasks.cell("Recovery").click();
		
		return tasks.getSelectedTextFromComboNearCell(tasks, COMBOBOX_SELECTOR, "Recover Alert :");
	}
	
	/**
	 * Returns true if Disable when fired radio button on Recovery tab
	 * is checked. False otherwise.
	 * @return true if Disable when fired radio button on Recovery tab
	 * is checked. False otherwise.
	 */
	public boolean isDisableWhenFired(){
		tasks.cell("Recovery").click();
		return editor.isRadioNearCellChecked("disableWhenFired", "Yes");
	}
	
	/**
	 * Sets fields on Dampening tab.
	 * @param dampening string to be selected in the combo box
	 * @return this object
	 */
	public AlertDefinitionEditor setDampening(String dampening){
		tasks.cell("Dampening").click();
		log.fine("Setting dampening to: " + dampening);
		tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR, "Dampening :", dampening);
		
		return this;
	}
	
	/**
	 * Returns selected dampening on Dampening tab.
	 * @return selected dampening on Dampening tab.
	 */
	public String getDampening(){
		tasks.cell("Dampening").click();

		return tasks.getSelectedTextFromComboNearCell(tasks, COMBOBOX_SELECTOR, "Dampening :");
	}
	
	/**
	 * Saves this alert definition.
	 */
	public void save(){
		log.fine("Saving alert definition");
		tasks.waitForElementVisible(tasks, tasks.cell("Save"), "Save button", Timing.WAIT_TIME);
		tasks.cell("Save").click();
		tasks.waitForElementVisible(tasks, tasks.cell("/Alert definition successfully.*/"), 
				"Successful message",Timing.WAIT_TIME);
	}
	
	/**
	 * Cancel this alert definition.
	 */
	public void cancel(){
		log.fine("Canceling alert definition");
		tasks.waitForElementVisible(tasks, tasks.cell("Cancel"), "Cancel button", Timing.WAIT_TIME);
		tasks.cell("Cancel").click();
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
						tasks.selectComboBoxByNearCellOptionByRow(tasks, COMBOBOX_SELECTOR,f.getNearLocator(), f.getValue());
					}else{
						tasks.selectComboBoxByNearCellOptionByRow(tasks, f.getFieldLocator(),f.getNearLocator(), f.getValue());
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
	 * This is a helper class which represents a field which is part of class Condition
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
