# JBoss ON Alert baselines

In this directory you can find 1 script and several setting files related. Currently we support only 
alert definitions based on `metricValueThreshold` condition type and `email` notification. So .. you can 
get notified by email when given metric value for given subset of resources reaches given value.

## When to use it?
 * when you have non-trivial (more than 3 agents) JBoss ON deployment
 * when you need to automate deployment of alert definitions based on resource selection 

### Resouce selection

By Default, JBoss ON allows you to define alerts on Resource level (UI, REST), on Resource Group level 
(UI) and even on Resource Type level (UI). Resource selection is a simple thing that basically finds 
resources based on selection criteria and set's up alert definition for individual resource.

An example:

    ("^server.*","RHQ Agent","JVM") # select all platfoms starting with `server` and find `JVM` child 
resource for each `RHQ Agent`

Each piece of selector is a regular expression and pieces are similar to xpath node selection, but in 
this case in JBoss ON inventory tree.

### alert_baselines.py

Python script reusing [RHQ Client package](../rhq) to simplify setting up alert definitions on JBoss ON 
environments

Usage:

    python alert_baselines.py --host <your RHQ Server> --baseline longrun.py

Result:

This will setup several alert definitions on `<your RHQ Server>`. For this case, those definitions live 
in [longrun.py](longrun.py) setting file. Take a look at [sample.py](sample.py) setting file for expected 
file format
