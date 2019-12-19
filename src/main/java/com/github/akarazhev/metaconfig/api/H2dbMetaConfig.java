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

import com.github.akarazhev.metaconfig.engine.db.DbServer;
import com.github.akarazhev.metaconfig.engine.db.DbServers;
import com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPool;
import com.github.akarazhev.metaconfig.engine.db.pool.ConnectionPools;
import com.github.akarazhev.metaconfig.extension.Validator;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.akarazhev.metaconfig.Constants.Messages.META_CONFIG_ERROR;

/**
 * The core configuration class that provides the functionality.
 */
public final class H2dbMetaConfig implements ConfigService, Closeable {
    private final DbServer dbServer;
    private final ConnectionPool connectionPool;
    private final MetaConfig metaConfig;

    private H2dbMetaConfig(final DbServer dbServer, final ConnectionPool connectionPool, final MetaConfig metaConfig) {
        this.dbServer = dbServer;
        this.connectionPool = connectionPool;
        this.metaConfig = metaConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Config> update(final Stream<Config> stream) {
        return metaConfig.update(stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> getNames() {
        return metaConfig.getNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Config> get() {
        return metaConfig.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Config> get(final Stream<String> stream) {
        return metaConfig.get(stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remove(final Stream<String> stream) {
        return metaConfig.remove(stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(final Stream<String> stream) {
        metaConfig.accept(stream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addConsumer(final Consumer<Config> consumer) {
        metaConfig.addConsumer(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // Stop the meta configuration
        if (metaConfig != null) {
            metaConfig.close();
        }
        // Close the connection pool
        if (connectionPool != null) {
            connectionPool.close();
        }
        // Stop the database server
        if (dbServer != null) {
            dbServer.stop();
        }
    }

    /**
     * Wraps and builds the instance of the core configuration class.
     */
    public final static class Builder {
        private Config dbConfig;
        private Config webConfig;
        private Config poolConfig;
        private Map<String, String> dataMapping;

        /**
         * Constructs the core configuration class with the configuration of a db server.
         *
         * @param config a configuration of a db server.
         * @return a builder of the core configuration class.
         */
        public Builder dbServer(final Config config) {
            this.dbConfig = Validator.of(config).get();
            return this;
        }

        /**
         * Constructs the core configuration class with the configuration of a connection pool.
         *
         * @param config a configuration a connection pool.
         * @return a builder of the core configuration class.
         */
        public Builder connectionPool(final Config config) {
            this.poolConfig = Validator.of(config).get();
            return this;
        }

        /**
         * Constructs the core configuration class with the custom mapping.
         *
         * @param mapping a table mapping.
         * @return a builder of the core configuration class.
         */
        public Builder dataMapping(final Map<String, String> mapping) {
            this.dataMapping = Validator.of(mapping).get();
            return this;
        }

        /**
         * Constructs the core configuration class with the configuration of a web server.
         *
         * @param config a configuration a web server.
         * @return a builder of the core configuration class.
         */
        public Builder webServer(final Config config) {
            this.webConfig = Validator.of(config).get();
            return this;
        }

        /**
         * Builds the core configuration class with parameters.
         *
         * @return a builder of the core configuration class.
         */
        public H2dbMetaConfig build() {
            try {
                // Init the DB server
                final DbServer dbServer = dbConfig != null ?
                        DbServers.newServer(dbConfig).start() :
                        DbServers.newServer().start();
                // Init the connection pool and the data source
                final ConnectionPool connectionPool = poolConfig != null ?
                        ConnectionPools.newPool(poolConfig) :
                        ConnectionPools.newPool();
                final DataSource dataSource = connectionPool.getDataSource();
                // Init the data mapping
                final Map<String, String> mapping = dataMapping != null ? dataMapping : new HashMap<>();
                // Create the main instance
                final MetaConfig metaConfig = webConfig != null ?
                        new MetaConfig.Builder().webServer(webConfig).
                                dataMapping(mapping).dataSource(dataSource).build() :
                        new MetaConfig.Builder().defaultConfig().
                                dataMapping(mapping).dataSource(dataSource).build();
                return new H2dbMetaConfig(dbServer, connectionPool, metaConfig);
            } catch (final Exception e) {
                throw new RuntimeException(META_CONFIG_ERROR, e);
            }
        }
    }
}
