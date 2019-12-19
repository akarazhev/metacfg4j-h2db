# metacfg4j-h2db

[![Build Status](https://travis-ci.com/akarazhev/metacfg4j.svg?branch=master)](https://travis-ci.com/akarazhev/metacfg4j)
[![codecov.io](http://codecov.io/github/akarazhev/metacfg4j/coverage.svg?branch=master)](http://codecov.io/github/akarazhev/metacfg4j?branch=master)

The `metacfg4j-h2db` project is an abstraction that builds upon the `metacfg4j` library that can be used as the solution by creating a business abstraction or 
may extend an existed implementation to provide such software solutions as: various configuration (application, user's and etc.), CRUD services, DSL, MVP.
It uses the embedded H2 Database Engine (https://www.h2database.com/html/main.html).

## Architecture

This is a high-level abstraction based on the low-level API. It has been written without frameworks and delivered with an additional dependency:

 &#8658; H2 Database Engine (https://www.h2database.com/html/main.html)<br/>

See the `metacfg4j` project for the more information: (https://github.com/akarazhev/metacfg4j)
  
## Configuration and Usage

### Basic Configuration

Add a maven dependency into your project:
```xml
<dependency>
    <groupId>com.github.akarazhev.metacfg</groupId>
    <artifactId>metacfg4j-h2db</artifactId>
    <version>1.1</version>
</dependency>
```
Instantiate the meta configuration class in your project with the default configuration:
```java
public H2dbMetaConfig h2dbMetaConfig() {
    return new H2dbMetaConfig.Builder().build();
}
```

### Advanced Usage

You can instantiate the meta configuration with the custom configuration:
```java
public H2dbMetaConfig h2dbMetaConfig() {
    // Create the DB server config
    final Config dbServer = new Config.Builder(Server.Settings.CONFIG_NAME,
            Arrays.asList(
                    new Property.Builder(Settings.TYPE, "tcp").build(),
                    new Property.Builder(Settings.ARGS, "-tcp", "-tcpPort", "8043", "-ifNotExists").build())).
            build();
    // Create the connection pool config
    final Config connectionPool = new Config.Builder(ConnectionPools.Settings.CONFIG_NAME,
            Arrays.asList(
                    new Property.Builder(Settings.URL, "jdbc:h2:tcp://localhost:8043/./data/metacfg4j").build(),
                    new Property.Builder(Settings.USER, "sa").build(),
                    new Property.Builder(Settings.PASSWORD, "sa").build())).
            build();
    // Create the custom data mapping
    final Map<String, String> dataMapping = new HashMap<>();
    dataMapping.put(Constants.Mapping.CONFIGS_TABLE, "CONFIGS");
    dataMapping.put(Constants.Mapping.CONFIG_ATTRIBUTES_TABLE, "CONFIG_ATTRIBUTES");
    dataMapping.put(Constants.Mapping.PROPERTIES_TABLE, "PROPERTIES");
    dataMapping.put(Constants.Mapping.PROPERTY_ATTRIBUTES_TABLE, "PROPERTY_ATTRIBUTES");
    // Create the web server config
    final Config webServer = new Config.Builder(Server.Settings.CONFIG_NAME,
            Arrays.asList(
                    new Property.Builder(Settings.HOSTNAME, "localhost").build(),
                    new Property.Builder(Settings.PORT, 8000).build(),
                    new Property.Builder(Settings.BACKLOG, 0).build(),
                    new Property.Builder(Settings.KEY_STORE_FILE, "./data/metacfg4j.keystore").build(),
                    new Property.Builder(Settings.ALIAS, "alias").build(),
                    new Property.Builder(Settings.STORE_PASSWORD, "password").build(),
                    new Property.Builder(Settings.KEY_PASSWORD, "password").build()))
            .build();
    // Create the meta configuration
    return new H2dbMetaConfig.Builder().
            dbServer(dbServer).
            connectionPool(connectionPool).
            dataMapping(dataMapping).
            webServer(webServer).
            build();
}
```
NOTE: you need to call the close method in the end of processing.

## Build Requirements

 &#8658; Java 8+ <br/>
 &#8658; Maven 3.6+ <br/>
 
 ## Contribution
 
Contribution is always welcome. Please perform changes and submit pull requests from the `dev` branch instead of `master`.  
Please set your editor to use spaces instead of tabs, and adhere to the apparent style of the code you are editing.