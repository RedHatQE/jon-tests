RHQ alert tests in python
============================

This project contains alert-related tests by interacting with RHQ Server using it's REST Api. Those tests implement various 
scenarios (including HA) which in a nutshell 1) define on or more alert definitions 2) produce conditions so those alerts get 
fired 3) verify alerts. Tests are written for python 2.7 and are built on top of [RHQ REST Client] (rhq) package.

### Requirements
* **nosetests** 1.2.0  - ```yum install python-nose; easy_install --upgrade
nose```
* **requests** - python http client library ```easy_install requests```

If you do not have easy_install do ```yum install python-setuptools```


##### RHQ Server requirements

This tests require default admin user (rhqadmin:rhqadmin), at least 1 platform with RHQ Agent imported to inventory.

### Execution

Please note that python cannot read your *~/automation.properties*, all input variables must be passed as environment variables

Export environment variable with a hostname of your RHQ instance or
comma-separated hostnames if testing HA

        export RHQ_HOSTS=<your host>
        export RHQ_HOSTS=<host1>,<host2>,<host3>


### Execute all tests and get xunit/junit report

        nosetests --with-xunit

### Execute a single test/group
Have test class or method with annotated with ```@attr('tag')```

        nosetests -a tag

Please refer [nosetests documentation](http://nose.readthedocs.org/en/latest/usage.html#selecting-tests) for other options.

### Enable @blockedBy decorator
You can enable Bugzilla checker. This checker goes to [Redhat Bugzilla](https://bugzilla.redhat.com) and checks for status of BUG 
when test method is annotated by *@blockedBy('Bug ID')*. If
given bug is still
 *under development* test method gets skipped. Without this check such test would most likely fail.

        export BZCHECK=true

### Test results

Some tests may fail or get skipped, because requirements have not been met
