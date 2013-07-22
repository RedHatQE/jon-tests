# RHQ REST API tests in python

Tests are written for python 2.7
### Requirements
* **nosetests** 1.2.0  - ```yum install python-nose; easy_install --upgrade
nose``` 
* **requests** - python http client library ```easy_install requests```
* **proboscis** - python test framework providing similar features as TestNG
    ```easy_install proboscis```

If you do not have easy_install do ```yum install python-setuptools```

##### RHQ Server requirements

This tests require default admin user (rhqadmin:rhqadmin), at least 1 platform with RHQ Agent imported to inventory.

### Execution

Please note that python cannot read your *~/automation.properties*, all input variables must be passed as environment variables

Export environment variable with a hostname of your RHQ instance

```export RHQ_HOST=<your host>```

### Execute all tests and get xunit/junit report
```python run.py --with-xunit```

### Execute a single test/group 
Have test class or method with annotated with ```@test(groups=['mytest'])```

```python run.py --group=mytest --verbosity 2```

### Enable @blockedBy decorator
You can enable Bugzilla checker. This checker goes to [Redhat Bugzilla](https://bugzilla.redhat.com) and checks for status of BUG when test method is annotated by *@blockedBy('Bug ID')*. If 
given bug is still
 *under development* test method gets skipped. Without this check such test would most likely fail.

```export BZCHECKER=true```

### Test results

Some tests may fail or get skipped, because requirements have not been met
