// Disabling Alerts Based on Priority example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/alerts.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

//TODO create alert definitions, currently not possible via CLI

// set the search criteria for the alert definitions with a reasonable filter
var criteria = new AlertDefinitionCriteria()
criteria.addFilterPriority('Low')

//search for the alert definitions
alertdefs = AlertDefinitionManager.findAlertDefinitionsByCriteria(criteria)

//get the data from the results
alertdef = alertdefs.get(0);

//disable the matching alerts, based on ID
AlertDefinitionManager.disableAlertDefinitions([alertdef.id]);
