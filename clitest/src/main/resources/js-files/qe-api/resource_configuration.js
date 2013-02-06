/* this example shows how to configure a resource
* 1. we'll configure RHQ Agent resource - setting up boolean, String and Number properties
* 2. we'll configure RHQ Agent/rhq-agent-env.sh resource, we'll add new environment variable to array
* 3. we'll configure RHQ Agent/rhq-agent-env.sh resource, we'll remove environment variable from array
*/


// Step 1 - obtain rhqagent resource

var platform = agent

var agents = resources.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent",parentResourceName:platform});
assertTrue(agents.length>0,"At least one RHQ Agent resource was found in inventory");
var agent = agents[0];

var agentConfig = agent.getConfiguration();

// assert correct value types were retrieved
assertTrue(typeof agentConfig["rhq.agent.server-auto-detection"] == "boolean","Boolean value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == "number","Integer value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.data-directory"] == "string","String value type has been retrieved");

// adjust configuration values
agentConfig["rhq.agent.server-auto-detection"] = true;
agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] = 70000;
agentConfig["rhq.agent.data-directory"] = "data2";

agent.updateConfiguration(agentConfig);

agentConfig = agent.getConfiguration();

println(typeof agentConfig["rhq.agent.server-auto-detection"]);
println(typeof agentConfig["rhq.agent.wait-for-server-at-startup-msecs"]);
println(typeof agentConfig["rhq.agent.data-directory"]);
println(typeof agentConfig["rhq.communications.connector.security.keystore.type"]);

//assert correct value types were retrieved
assertTrue(typeof agentConfig["rhq.agent.server-auto-detection"] == "boolean","Boolean value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == "number","Integer value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.data-directory"] == "string","String value type has been retrieved");
assertTrue(agentConfig["rhq.communications.connector.security.keystore.type"] == null,"Null has been retrieved");

// assert configuration was updated
assertTrue(agentConfig["rhq.agent.server-auto-detection"] == true,"Boolean config value has been updated");
assertTrue(agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == 70000,"Integer config value has been updated");
assertTrue(agentConfig["rhq.agent.data-directory"] == "data2","String config value has been updated");


// Step 2

//obtain any RHQ Agent/rhq-agent-env.sh resource
var agentEnvs = Inventory.find({name:"rhq-agent-env.sh",resourceTypeName:"Environment Setup Script"});
assertTrue(agentEnvs.length>0,"At least one Agent's environment setup script resource was found in inventory");
var script = agentEnvs[0];

var scriptConfig = script.getConfiguration();
// put new env. variables 
scriptConfig["environmentVariables"] = [{"name":"FOO","value":"BAR"},{"name":"VARIABLE","value":"IT'S VALUE"}];
script.updateConfiguration(scriptConfig);

scriptConfig = script.getConfiguration();

// assert retrieved values and value types
assertTrue(scriptConfig["environmentVariables"] instanceof Array,"Configuration was updated by array object");
assertTrue(scriptConfig["environmentVariables"].length == 2,"Configuration was updated  - two items are present");
assertTrue(typeof scriptConfig["environmentVariables"][0]["name"] == "string","Configuration was updated  - first item's name is STRING type");
assertTrue(scriptConfig["environmentVariables"][0]["name"] == "FOO","Configuration was updated  - first item's name is FOO");
assertTrue(typeof scriptConfig["environmentVariables"][0]["value"] == "string","Configuration was updated  - first item's value is STRING type");
assertTrue(scriptConfig["environmentVariables"][0]["value"] == "BAR","Configuration was updated  - first item's name is BAR");

// Step 3
scriptConfig["environmentVariables"]=[];

script.updateConfiguration(scriptConfig);
scriptConfig = script.getConfiguration();

assertTrue(scriptConfig["environmentVariables"] instanceof Array,"Configuration was updated by array object");
assertTrue(scriptConfig["environmentVariables"].length == 0,"Configuration was updated  - two items are present");

// TODO test passing invalid configurations