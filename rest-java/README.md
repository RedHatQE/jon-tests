# RHQ REST API java automation

This project contains several REST tests written in java.

##### RHQ Server requirements

This tests require default admin user (rhqadmin:rhqadmin) or another admin user by specifying system properties, at least 1 platform imported to inventory.

##### General requirements

Generally, you need a properly configured environment (have JDK (1.6+), setup JAVA_HOME, have **java** and **gradle** binaries  on $PATH)

### Execution
  We recommend putting all required system properties to *$HOME/automation.properties*, which gets loaded at runtime. Optinally test runtime loads 
*$HOME/log.properties* where you can configure logging.

#### Required properties
  * **jon.server.url** - http(s) url of your RHQ instance (for exammple http://localhost:7080)


#### Run!
  You can execute automation from this folder by running

  ```gradle clean test```

  or from root of this repository by running

  ```gradle clean rest-java:test```


### Execute another suite

  ```gradle clean test -Dtestng.suite=src/test/resources/testng.xml```

