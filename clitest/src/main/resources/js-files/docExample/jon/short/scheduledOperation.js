// Scheduled operation example from http://docs.redhat.com/docs/en-US/JBoss_Operations_Network/3.1/html/Dev_Writing_JON_Command-Line_Scripts/ops.html
/**             
 * @author fbrychta@redhat.com (Filip Brychta)
 * June 20, 2012        
 **/

var verbose = 0; // logging level to INFO
var common = new _common(); // object with common methods


// find the agent
var rc = ResourceCriteria();
rc.addFilterResourceTypeName("RHQ Agent");
//rc.addFilterVersion("3.1");

var agent = ResourceManager.findResourcesByCriteria(rc);

assertTrue(agent.size() > 0, "No RHQ agents found!!");

//set the config properties for the operation
var config = new Configuration();
config.put(new PropertySimple("changesOnly", "true") );

//schedule the operation
var sched = OperationManager.scheduleResourceOperation(
        agent.get(0).id,
        "executeAvailabilityScan",
        0, // 0 means that the delay was skipped
        1,
        0, // this skips the repeat count
        10000000,
        config, 
        "test from cli"
        );

// check operation status
var res = new Resource(agent.get(0).id);
var history = res.waitForOperationResult(sched);
assertTrue(history.status == OperationRequestStatus.SUCCESS, "Operation status is " + history.status + " but success was expected!!");


