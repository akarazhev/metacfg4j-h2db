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
import org.h2.jdbc.JdbcSQLNonTransientConnectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Db server test")
final class DbServerTest extends UnitTest {

    @Test
    @DisplayName("Start")
    void start() throws Exception {
        final DbServer dbServer = DbServers.newServer().start();
        assertGetSchema();
        dbServer.stop();
    }

    @Test
    @DisplayName("Stop")
    void stop() throws Exception {
        final DbServer dbServer = DbServers.newServer().start();
        assertGetSchema();
        dbServer.stop();
        assertThrows(JdbcSQLNonTransientConnectionException.class, this::assertGetSchema);
    }
}
