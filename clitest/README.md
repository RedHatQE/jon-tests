# RHQ CLI automation

CLI Automation focuses on testing RHQ CLI. RHQ CLI is a client supporting mutliple scripting languages, we're focused only on javascript. In most cases we reuse [rhqapi.js] (https://github.com/rhq-project/samples/tree/master/cli/rhqapi) (A JS library sitting on top of remote API exposed to CLI environment). Note that tests may even **break** your RHQ installation.

##### RHQ Server requirements

This tests require default admin user (rhqadmin:rhqadmin), at least 1 platform with RHQ Agent imported to inventory.

##### General requirements

Generally, you need a properly configured environment (have JDK (1.6+), setup JAVA_HOME, have **java** and **gradle** binaries  on $PATH)

### Execution
  We recommend putting all required system properties to *$HOME/automation.properties*, which gets loaded at runtime. Optinally test runtime loads *$HOME/log.properties* where you can configure logging.

#### Required properties
  * **jon.server.host** - hostname of your JBoss ON/ RHQ server
  * **jon.server.user** - user which runs RHQ
  * **jon.server.password** - password to server machine
  * **jon.agent.host** - hostname of  your agent machine
  * **jon.agent.name** - agent name which appears in RHQ inventory (mostly same as jon.agent.host)
  * **jon.agent.user** - user which runs RHQ Agent
  * **jon.agent.password** - password to agent machine

  If you run this tests on local machine you need to provide your password. Sad but true.

#### Optional properties & environment variables

Below environment variables are typed wih CAPITALS, properties are normal.

* **automation.propertiesfile** - path to your automation.properties (overrides default location)
* **jon.server.log.path** -  this property defines where to find a server log. The server log is then copied to the local system and stored to build/logs directory.
* **CLI_AGENT_BIN_SH** - path to RHQ CLI binary. Use if you want to run custom version. If not defined, CLI is downloaded from server at test runtime.
* **RHQ_CLI_JAVA_HOME** - java home for RHQ CLI. Use if you want to run it with different JVM. If not defined, JAVA_HOME environment variable is used. 
* **REPORT_ENGINE_PROPERTY_FILE** - path to [Report Engine](https://github.com/jkandasa/report-engine) client property file

#### Ready? Go!
  You can execute automation from this folder by running

  ```gradle clean test```

  or from root of this repository by running

  ```gradle clean clitest:test```


### Execute another suite

  ```gradle clean test -Dtestng.suite=src/test/resources/testng.xml```

