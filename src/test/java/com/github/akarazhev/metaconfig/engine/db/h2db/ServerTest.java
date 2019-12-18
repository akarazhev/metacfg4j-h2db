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
package com.github.akarazhev.metaconfig.engine.db.h2db;

import com.github.akarazhev.metaconfig.UnitTest;
import com.github.akarazhev.metaconfig.api.Config;
import com.github.akarazhev.metaconfig.api.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.ARGS;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.ARGS_VALUE;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.CONFIG_NAME;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.TYPE;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.TYPE_PG;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.TYPE_WEB;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Server test")
final class ServerTest extends UnitTest {

    @Test
    @DisplayName("Settings constructor")
    void settingsConstructor() throws Exception {
        assertPrivate(Server.Settings.class);
    }

    @Test
    @DisplayName("Create default server")
    void createDefaultServer() throws SQLException {
        final Server server = new Server();
        assertNotNull(server);
    }

    @Test
    @DisplayName("Create pg config server")
    void createPgConfigServer() throws SQLException {
        final Config config = new Config.Builder(CONFIG_NAME, Arrays.asList(new Property.Builder(TYPE, TYPE_PG).build(),
                new Property.Builder(ARGS, ARGS_VALUE).build())).build();

        final Server server = new Server(config);
        assertNotNull(server);
    }

    @Test
    @DisplayName("Create web config server")
    void createWebConfigServer() throws SQLException {
        final Config config = new Config.Builder(CONFIG_NAME, Arrays.asList(new Property.Builder(TYPE, TYPE_WEB).build(),
                new Property.Builder(ARGS, ARGS_VALUE).build())).build();

        final Server server = new Server(config);
        assertNotNull(server);
    }
}
