# metacfg4j-h2db

The `metacfg4j-h2db` project is an abstraction that builds upon the `metacfg4j` library that can be used as the solution by creating a business abstraction or 
may extend an existed implementation to provide such software solutions as: various configuration (application, user's and etc.), CRUD services, DSL, MVP.
It uses the embedded H2 Database Engine (https://www.h2database.com/html/main.html).

## Architecture

This is a high-level abstraction based on the low-level API. It has been written without frameworks and delivered with an additional dependency:

 &#8658; H2 Database Engine (https://www.h2database.com/html/main.html)<br/>

See the `metacfg4j` project for more information: (https://github.com/akarazhev/metacfg4j)
  
## Usage

### Basic Usage

Add a maven dependency into your project:
```xml
<dependency>
    <groupId>com.github.akarazhev.metacfg</groupId>
    <artifactId>metacfg4j-h2db</artifactId>
    <version>1.0</version>
</dependency>
```
Instantiate the meta configuration class in your project with the default configuration:
```java
public MetaConfig metaConfig() {
    return new MetaConfig.Builder().defaultConfig().build();
}
```

### Advanced Usage

You can instantiate the meta configuration with the custom configuration:
```java
public MetaConfig metaConfig() {
    // Create the DB server config
    final Config dbServer = new Config.Builder(Server.Settings.CONFIG_NAME,
        Arrays.asList(
                new Property.Builder(Settings.TYPE, Settings.TYPE_TCP).build(),
                new Property.Builder(Settings.ARGS, "-tcp", "-tcpPort", "8043").build())).
        build();
    // Create the connection pool config
    final Config connectionPool = new Config.Builder(ConnectionPools.Settings.CONFIG_NAME,
        Arrays.asList(
                new Property.Builder(Settings.URL, "jdbc:h2:./data/metacfg4j").build(),
                new Property.Builder(Settings.USER, "sa").build(),
                new Property.Builder(Settings.PASSWORD, "sa").build())).
        build();
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
    return new MetaConfig.Builder().
        dbServer(dbServer).
        connectionPool(connectionPool).
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