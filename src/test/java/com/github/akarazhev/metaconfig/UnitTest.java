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
package com.github.akarazhev.metaconfig;

import com.github.akarazhev.metaconfig.api.Config;
import com.github.akarazhev.metaconfig.api.Property;
import com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPool;
import com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.CONFIG_NAME;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.PASSWORD;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.URL;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitTest {
    protected static final String FIRST_CONFIG = "The First Config";
    protected static final String SECOND_CONFIG = "The Second Config";
    protected static final String NEW_CONFIG = "New Config";

    protected <T> void assertPrivate(Class<T> clazz) throws NoSuchMethodException {
        final Constructor constructor = clazz.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
        constructor.setAccessible(false);
    }

    protected void assertGetSchema() throws Exception {
        final Config config = new Config.Builder(CONFIG_NAME, Arrays.asList(
                new Property.Builder(URL, "jdbc:h2:tcp://localhost:8043/./data/metacfg4j").build(),
                new Property.Builder(USER, "sa").build(),
                new Property.Builder(PASSWORD, "sa").build())).build();

        final ConnectionPool connectionPool = ConnectionPools.newPool(config);
        final Connection connection = connectionPool.getDataSource().getConnection();
        assertEquals("PUBLIC", connection.getSchema());
        connection.close();
        connectionPool.close();
    }

    protected Config getConfigWithSubProperties(final String name) {
        final Property firstSubProperty = new Property.Builder("Sub-Property-1", "Sub-Value-1").
                attribute("key_1", "value_1").build();
        final Property secondSubProperty = new Property.Builder("Sub-Property-2", "Sub-Value-2").
                attribute("key_2", "value_2").build();
        final Property thirdSubProperty = new Property.Builder("Sub-Property-3", "Sub-Value-3").
                attribute("key_3", "value_3").build();
        final Property property = new Property.Builder("Property", "Value").
                caption("Caption").
                description("Description").
                attribute("key", "value").
                property(new String[0], firstSubProperty).
                property(new String[]{"Sub-Property-1"}, secondSubProperty).
                property(new String[]{"Sub-Property-1", "Sub-Property-2"}, thirdSubProperty).
                build();

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("key_1", "value_1");
        attributes.put("key_2", "value_2");
        attributes.put("key_3", "value_3");

        return new Config.Builder(name, Collections.singletonList(property)).attributes(attributes).build();
    }

    protected Config getConfigWithProperties(final String name) {
        final Property firstProperty = new Property.Builder("Property-1", "Value-1").
                attribute("key_1", "value_1").build();
        final Property secondProperty = new Property.Builder("Property-2", "Value-2").
                attribute("key_2", "value_2").build();
        final Property thirdProperty = new Property.Builder("Property-3", "Value-3").
                attribute("key_3", "value_3").build();
        final Property property = new Property.Builder("Property", "Value").
                caption("Caption").
                description("Description").
                attribute("key", "value").
                property(new String[0], firstProperty).
                property(new String[0], secondProperty).
                property(new String[0], thirdProperty).
                build();

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("key_1", "value_1");
        attributes.put("key_2", "value_2");
        attributes.put("key_3", "value_3");

        return new Config.Builder(name, Collections.singletonList(property)).attributes(attributes).build();
    }

    protected void assertEqualsConfig(final Config expected, final Config actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getVersion(), actual.getVersion());

        assertTrue(actual.getAttributes().isPresent());
        assertTrue(expected.getAttributes().isPresent());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

    protected void assertEqualsProperty(final Config expectedConfig, final Config actualConfig) {
        final Optional<Property> expectedProperty = expectedConfig.getProperty("Property");
        assertTrue(expectedProperty.isPresent());
        final Property expected = expectedProperty.get();
        final Optional<Property> actualProperty = actualConfig.getProperty("Property");
        assertTrue(actualProperty.isPresent());
        final Property actual = actualProperty.get();

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCaption(), actual.getCaption());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getValue(), actual.getValue());

        assertTrue(actual.getAttributes().isPresent());
        assertTrue(expected.getAttributes().isPresent());
        assertEquals(expected.getAttributes(), actual.getAttributes());
    }

}
