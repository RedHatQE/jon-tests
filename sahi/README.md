# RHQ SAHI automation
We're currently running sahi on linux only and with firefox only, although some other browsers and OS might be supported by sahi.
 RHQ Server requirements

##### RHQ Server requirements
This tests require default admin user (rhqadmin:rhqadmin), at least 1 platform with RHQ Agent imported to inventory.

##### General requirements
Generally, you need a properly configured environment (have JDK (1.6+), setup JAVA_HOME, have **java** and **gradle** binaries  on $PATH)
 
##### Sahi requirements
* download & unzip [sahi](http://sourceforge.net/projects/sahi/files/) 
* run sahi proxy - go to $SAHI_HOME/bin and simply run ./sahi.sh
* configure your browser proxy - (i.e. for firefox:Edit->Preferences->Advanced->Network->Settings->Manual proxy configuration )
* by default sahi proxy listens on port '''9999'''
* if you are firefox user, create a new firefox profile called '''sahi''' (see [http://support.mozilla.org/en-US/kb/profile-manager-create-and-remove-firefox-profiles Use the Profile Manager to create and remove Firefox profiles]), so yo'll be able to run both instances at the same time (see [http://turbulentsky.com/how-to-run-multiple-firefox-profiles.html How to Run Multiple Firefox Profiles Simultaneously])

### Execution
  We recommend putting all required system properties to *$HOME/automation.properties*, which gets loaded at runtime. Optinally test runtime loads *$HOME/log.properties* where you can configure logging.

#### System properties
  * **jon.sahi.base.dir** (Required) - a path to your SAHI installation
  * **jon.server.url** (Required) - an url to JON server (i.e. http://10.34.131.88:7080)
  * **jon.browser.opt** (Optional) - additional options passed to firefox process (use -P sahi to run sahi firefox profile) 
  * **jon.browser.path** (Optional) - a path to your firefox binary 
  * **jon.agent.name** - agent name which appears in RHQ inventory (mostly same as jon.agent.host)
  * **jon.agent.user** - user which runs RHQ Agent
  * **jon.agent.password** - password to agent machine

  If you run this tests on local machine you need to provide your password. Sad but true.

#### Ready? Go!
  You can execute automation from this folder by running

  ```gradle clean test -Djon.sahi.base.dir=/home/user/Work/sahi -Djon.server.url=http://10.34.131.88:7080```

  or from root of this repository by running

  ```gradle clean sahi:test -Djon.sahi.base.dir=/home/user/Work/sahi -Djon.server.url=http://10.34.131.88:7080```


##### Execute another suite
  ```gradle clean test -Dtestng.suite=src/test/resources/testng.xml -Djon.sahi.base.dir=/home/user/Work/sahi -Djon.server.url=http://10.34.131.88:7080```

##### Execute with given firefox profile
```gradle clean test -Djon.sahi.base.dir=/home/user/Work/sahi -Djon.server.url=http://10.34.131.88:7080 -Djon.browser.opt="-P sahi -no-remote"```
