# rhq remote java API examples

There are 2 sub-modules

 * rhq-client - uses RemoteClient class provided by RHQ to access remote EJBs
 * ejb-client - uses JNDI lookup of remote EJBs

# Usage

```
mvn package -DskipTests
mvn exec:java -Drhq.server.host=<your RHQ server host>
```

```
mvn test -Drhq.server.host=<your RHQ server host>
```
