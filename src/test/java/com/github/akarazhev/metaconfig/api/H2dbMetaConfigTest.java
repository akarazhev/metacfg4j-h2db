/* Copyright 2019 Andrey Karazhev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package com.github.akarazhev.metaconfig.api;

import com.github.akarazhev.metaconfig.Constants;
import com.github.akarazhev.metaconfig.UnitTest;
import com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools;
import com.github.akarazhev.metaconfig.engine.web.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.ARGS;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.CONFIG_NAME;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.TYPE;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.PASSWORD;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.URL;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("H2db meta config test")
final class H2dbMetaConfigTest extends UnitTest {
    private static H2dbMetaConfig h2dbMetaConfig;

    @BeforeAll
    static void beforeAll() {
        if (h2dbMetaConfig == null) {
            final Config dbServer = new Config.Builder(CONFIG_NAME,
                    Arrays.asList(
                            new Property.Builder(TYPE, "tcp").build(),
                            new Property.Builder(ARGS, "-tcp", "-tcpPort", "8043", "-ifNotExists").build())
            ).build();

            final Config connectionPool = new Config.Builder(ConnectionPools.Settings.CONFIG_NAME,
                    Arrays.asList(
                            new Property.Builder(URL, "jdbc:h2:tcp://localhost:8043/./data/metacfg4j").build(),
                            new Property.Builder(USER, "sa").build(),
                            new Property.Builder(PASSWORD, "sa").build())
            ).build();

            final Map<String, String> dataMapping = new HashMap<>();
            dataMapping.put(Constants.Mapping.CONFIGS_TABLE, "CONFIGS");
            dataMapping.put(Constants.Mapping.CONFIG_ATTRIBUTES_TABLE, "CONFIG_ATTRIBUTES");
            dataMapping.put(Constants.Mapping.PROPERTIES_TABLE, "PROPERTIES");
            dataMapping.put(Constants.Mapping.PROPERTY_ATTRIBUTES_TABLE, "PROPERTY_ATTRIBUTES");

            final Config webServer = new Config.Builder(Server.Settings.CONFIG_NAME,
                    Arrays.asList(
                            new Property.Builder(Server.Settings.HOSTNAME, "localhost").build(),
                            new Property.Builder(Server.Settings.PORT, 8000).build(),
                            new Property.Builder(Server.Settings.BACKLOG, 0).build(),
                            new Property.Builder(Server.Settings.KEY_STORE_FILE, "./data/metacfg4j.keystore").build(),
                            new Property.Builder(Server.Settings.ALIAS, "alias").build(),
                            new Property.Builder(Server.Settings.STORE_PASSWORD, "password").build(),
                            new Property.Builder(Server.Settings.KEY_PASSWORD, "password").build()))
                    .build();

            h2dbMetaConfig = new H2dbMetaConfig.Builder().
                    dbServer(dbServer).
                    connectionPool(connectionPool).
                    dataMapping(dataMapping).
                    webServer(webServer).
                    build();
        }
    }

    @AfterAll
    static void afterAll() throws IOException {
        if (h2dbMetaConfig != null) {
            h2dbMetaConfig.close();
            h2dbMetaConfig = null;
        }
    }

    @BeforeEach
    void beforeEach() {
        h2dbMetaConfig.update(Stream.of(getConfigWithSubProperties(FIRST_CONFIG),
                getConfigWithProperties(SECOND_CONFIG)));
        h2dbMetaConfig.addConsumer(null);
    }

    @AfterEach
    void afterEach() {
        h2dbMetaConfig.remove(Stream.of(FIRST_CONFIG, SECOND_CONFIG, NEW_CONFIG));
    }

    @Test
    @DisplayName("Build empty config")
    void buildEmptyConfig() {
        assertThrows(RuntimeException.class, () -> new H2dbMetaConfig.Builder().
                dbServer(new Config.Builder("dbServer", Collections.emptyList()).build()).
                webServer(new Config.Builder("webServer", Collections.emptyList()).build()).
                connectionPool(new Config.Builder("connectionPool", Collections.emptyList()).build()).
                build());
    }

    @Test
    @DisplayName("Get configs by empty names")
    void getByEmptyNames() {
        // Check test results
        assertEquals(0, h2dbMetaConfig.get(Stream.empty()).count());
    }

    @Test
    @DisplayName("Get configs by the not existed name")
    void getByNotExistedName() {
        // Check test results
        assertEquals(0, h2dbMetaConfig.get(Stream.of(NEW_CONFIG)).count());
    }

    @Test
    @DisplayName("Get configs by names")
    void getConfigsByNames() {
        assertEqualsConfigs(h2dbMetaConfig.get(Stream.of(FIRST_CONFIG, SECOND_CONFIG)).toArray(Config[]::new));
    }

    @Test
    @DisplayName("Get config names")
    void getNames() {
        assertEqualsNames(h2dbMetaConfig.getNames().toArray(String[]::new));
    }

    @Test
    @DisplayName("Get configs")
    void getConfigs() {
        assertEqualsNames(h2dbMetaConfig.get().toArray(Config[]::new));
    }

    @Test
    @DisplayName("Update a new config")
    void updateNewConfig() {
        final Optional<Config> newDbConfig =
                h2dbMetaConfig.update(Stream.of(getConfigWithProperties(NEW_CONFIG))).findFirst();
        // Check test results
        assertTrue(newDbConfig.isPresent());
        assertTrue(newDbConfig.get().getId() > 0);
    }

    @Test
    @DisplayName("Update an empty")
    void updateEmptyConfig() {
        // Check test results
        assertEquals(0, h2dbMetaConfig.update(Stream.empty()).count());
    }

    @Test
    @DisplayName("Update by the first config id")
    void updateConfigByFirstId() {
        final Optional<Config> firstConfig = h2dbMetaConfig.get(Stream.of(FIRST_CONFIG)).findFirst();
        // Check test results
        assertTrue(firstConfig.isPresent());
        final Config newConfig = new Config.Builder(NEW_CONFIG, Collections.emptyList()).
                id(firstConfig.get().getId()).
                build();
        Optional<Config> updatedDbConfig = h2dbMetaConfig.update(Stream.of(newConfig)).findFirst();
        assertTrue(updatedDbConfig.isPresent());
        assertTrue(updatedDbConfig.get().getId() > 0);
    }

    @Test
    @DisplayName("Update by the second config id")
    void updateConfigBySecondId() {
        final Optional<Config> secondConfig = h2dbMetaConfig.get(Stream.of(FIRST_CONFIG)).findFirst();
        // Check test results
        assertTrue(secondConfig.isPresent());
    }

    @Test
    @DisplayName("Optimistic locking error")
    void optimisticLockingError() {
        final Optional<Config> firstConfig = h2dbMetaConfig.get(Stream.of(FIRST_CONFIG)).findFirst();
        assertTrue(firstConfig.isPresent());
        final Config newConfig = new Config.Builder(firstConfig.get()).build();
        h2dbMetaConfig.update(Stream.of(newConfig));
        assertThrows(RuntimeException.class, () -> h2dbMetaConfig.update(Stream.of(newConfig)));
    }

    @Test
    @DisplayName("Remove configs by empty names")
    void removeByEmptyNames() {
        // Check test results
        assertEquals(0, h2dbMetaConfig.remove(Stream.empty()));
    }

    @Test
    @DisplayName("Remove configs by the not existed name")
    void removeByNotExistedName() {
        // Check test results
        assertEquals(0, h2dbMetaConfig.remove(Stream.of(NEW_CONFIG)));
    }

    @Test
    @DisplayName("Remove configs by names")
    void removeByNames() {
        // Check test results
        assertEquals(1, h2dbMetaConfig.remove(Stream.of(FIRST_CONFIG)));
    }

    @Test
    @DisplayName("Add a consumer for the config")
    void addConsumer() {
        final StringBuilder message = new StringBuilder();
        h2dbMetaConfig.addConsumer(config -> {
            if (FIRST_CONFIG.equals(config.getName())) {
                message.append(FIRST_CONFIG);
            }
        });
        // Check test results
        assertEquals(0, message.length());
    }

    @Test
    @DisplayName("Accept config by names")
    void acceptByNames() {
        h2dbMetaConfig.accept(Stream.of(FIRST_CONFIG));
    }

    @Test
    @DisplayName("Accept config by names")
    void acceptByDifferentNames() {
        final StringBuilder message = new StringBuilder();
        h2dbMetaConfig.addConsumer(config -> {
            if (FIRST_CONFIG.equals(config.getName())) {
                message.append(FIRST_CONFIG);
            }
        });
        // Check test results
        assertEquals(0, message.length());
    }

    private void assertEqualsConfigs(final Config[] configs) {
        // Check test results
        assertEquals(2, configs.length);
        final Config firstExpected = getConfigWithSubProperties(FIRST_CONFIG);
        final Config secondExpected = getConfigWithSubProperties(SECOND_CONFIG);
        assertEqualsConfig(firstExpected, configs[0]);
        assertEqualsProperty(firstExpected, configs[0]);
        assertEqualsConfig(secondExpected, configs[1]);
        assertEqualsProperty(secondExpected, configs[1]);
    }

    private void assertEqualsNames(final String[] names) {
        assertEquals(2, names.length);
        assertEquals(FIRST_CONFIG, names[0]);
        assertEquals(SECOND_CONFIG, names[1]);
    }

    private void assertEqualsNames(final Config[] configs) {
        assertEquals(2, configs.length);
        assertEquals(FIRST_CONFIG, configs[0].getName());
        assertEquals(SECOND_CONFIG, configs[1].getName());
    }
}
