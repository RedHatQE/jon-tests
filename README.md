# JBoss ON automation

Welcome to Jboss ON automation repository. You can find RHQ or JBoss ON related automation frameworks and integration tests here.

There are several projects:

* **sahi** - sahi framework & JBoss ON Core automation
* **clitest** - cli framework & JBoss ON CLI automation
* **common** - module with common stuff 
* **rest-java** - JBoss ON REST API automation written in java
* **rest-python** - JBoss ON REST API automation written in python
* **rest-groovy** - JBoss ON REST API automation written in groovy
* ...

For more details about each project, go to it's source folder and check README.

#### How do I run all this?
First you need to build it. Get latest [gradle] (http://gradle.org), navigate to the root of this repository and run:
```
gradle clean install
```

#### How do I reuse this framework?

Take a look at [clojars.org] (http://clojars.org/jon-automation) 

#### How do I contribute? 
Great, we're happy for that. Just fork our repo and send pull request. If you are using Eclipse IDE, you might run:
```
gradle eclipse
```
which generates all eclipse project files required for successfull import.

#### License
GPL License

