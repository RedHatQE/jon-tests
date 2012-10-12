// Viewing the Operation History from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

// find the resource
var rc = ResourceCriteria();
rc.addFilterPluginName("RHQAgent");
//rc.addFilterName("RHQ Agent");
rc.addFilterResourceTypeName("RHQ Agent");
//rc.addFilterDescription("Agent");

var agent = ResourceManager.findResourcesByCriteria(rc);

assertTrue(agent.size() > 0, "No RHQ agents found!!");

// print the operation history for the resource
var opcrit = ResourceOperationHistoryCriteria()
opcrit.addFilterResourceIds(agent.get(0).id)
var opHist = OperationManager.findResourceOperationHistoriesByCriteria(opcrit);

assertTrue(opHist.size() > 0, "No history of agent's operations found!!");


