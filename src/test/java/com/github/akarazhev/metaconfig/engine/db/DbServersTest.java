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
package com.github.akarazhev.metaconfig.engine.db;

import com.github.akarazhev.metaconfig.UnitTest;
import com.github.akarazhev.metaconfig.api.Config;
import com.github.akarazhev.metaconfig.api.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.ARGS;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.CONFIG_NAME;
import static com.github.akarazhev.metaconfig.engine.db.h2db.Server.Settings.TYPE;

@DisplayName("Db servers test")
final class DbServersTest extends UnitTest {

    @Test
    @DisplayName("Db servers constructor")
    void dbServersConstructor() throws Exception {
        assertPrivate(DbServers.class);
    }

    @Test
    @DisplayName("Start")
    void start() throws Exception {
        final DbServer dbServer = DbServers.newServer().start();
        assertGetSchema();
        dbServer.stop();
    }

    @Test
    @DisplayName("Start with the config")
    void startWithConfig() throws Exception {
        final Config config = new Config.Builder(CONFIG_NAME, Arrays.asList(
                new Property.Builder(TYPE, "tcp").build(),
                new Property.Builder(ARGS, "-tcp", "-tcpPort", "8043", "-ifNotExists").build())
        ).build();

        final DbServer dbServer = DbServers.newServer(config).start();
        assertGetSchema();
        dbServer.stop();
    }
}
