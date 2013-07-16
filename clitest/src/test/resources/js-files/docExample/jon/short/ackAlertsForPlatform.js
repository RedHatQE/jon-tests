// Acknowledging Alerts for Platform Resources example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/alerts.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/


//TODO create alert definitions, currently not possible via CLI

// set the criteria and search for the alerts
var criteria = new AlertCriteria() 
criteria.addFilterResourceTypeName('Linux')
var alerts = AlertManager.findAlertsByCriteria(criteria)

// go through the results and then acknowledge the alerts
if( alerts != null ) {
  if( alerts.size() > 1 ) {
     for( i =0; i < alerts.size(); ++i) {
          alert = alerts.get(i);
          AlertManager.acknowledgeAlerts([alert.id])
     }
  }
  else if( alerts.size() == 1 ) {
     alert = alerts.get(0);
     AlertManager.acknowledgeAlerts([alert.id])
  }
}
