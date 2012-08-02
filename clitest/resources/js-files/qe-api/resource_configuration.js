/* this example shows how to configure a resource
* 1. we'll configure RHQ Agent resource - setting up boolean, String and Number properties
* 2. we'll configure /etc/hosts resource, we'll add new host item to array
* 3. we'll configure /etc/hosts resource, we'll add remove host item to array
* 4. we'll configure GRUB resource, we'll add a new property to map
* 5. we'll configure GRUB resource, we'll remove a property from map
*/

var agents = Inventory.find({name:"RHQ Agent",resourceTypeName:"RHQ Agent"});
assertTrue(agents.length>0,"At least one RHQ Agent resource was found in inventory");
var agent = agents[0];

var agentConfig = agent.getConfiguration();

assertTrue(typeof agentConfig["rhq.agent.server-auto-detection"] == "boolean","Boolean value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == "number","Integer value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.data-directory"] == "string","String value type has been retrieved");

agentConfig["rhq.agent.server-auto-detection"] = true;
agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] = 70000;
agentConfig["rhq.agent.data-directory"] = "data2";
agent.updateConfiguration(agentConfig);

agentConfig = agent.getConfiguration();

assertTrue(typeof agentConfig["rhq.agent.server-auto-detection"] == "boolean","Boolean value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == "number","Integer value type has been retrieved");
assertTrue(typeof agentConfig["rhq.agent.data-directory"] == "string","String value type has been retrieved");

assertTrue(agentConfig["rhq.agent.server-auto-detection"] == true,"Boolean config value has been updated");
assertTrue(agentConfig["rhq.agent.wait-for-server-at-startup-msecs"] == 70000,"Integer config value has been updated");
assertTrue(agentConfig["rhq.agent.data-directory"] == "data2","String config value has been updated");

// TODO test maps and arrays
//var hosts = Inventory.find({name:"Hosts File",resourceTypeName:"Hosts File"});
//assertTrue(hosts.length>0,"At least one Hosts File resource was found in inventory");
//var host = hosts[0];

//var hostConfig = host.getConfiguration();
//host.updateConfiguration(hostConfig);
