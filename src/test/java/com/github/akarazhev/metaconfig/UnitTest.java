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

import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.CONFIG_NAME;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.PASSWORD;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.PASSWORD_VALUE;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.URL;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.USER;
import static com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools.Settings.USER_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitTest {

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
                new Property.Builder(USER, USER_VALUE).build(),
                new Property.Builder(PASSWORD, PASSWORD_VALUE).build())).build();

        final ConnectionPool connectionPool = ConnectionPools.newPool(config);
        final Connection connection = connectionPool.getDataSource().getConnection();
        // add application code here
        assertEquals("PUBLIC", connection.getSchema());
        connection.close();
        connectionPool.close();
    }
}
