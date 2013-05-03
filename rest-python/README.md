# RHQ REST API tests written in python

Those tests are written for python 2.7
# Requirements
 * nosetests 1.2.0  - ```yum install python-nose; easy_install --upgrade nose```, 
 * requests - python http client library ```easy_install requests```
If you do not have easy_install do ```yum install python-setuptools```
 * proboscis - python test framework providing similar features as TestNG
 ```easy_install proboscis```

# Execution
Please note that python cannot read your ~/automation.properties, all
input variables must be passed as environment variables

```export RHQ_TARGET=<your host>```

## Execute all tests and get xunit/junit report

```python run.py --with-xunit```

## Execute a single class 

```nosetests status.py:StatusTest --verbosity 2```
