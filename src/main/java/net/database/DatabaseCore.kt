package net.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import constants.ServerConstants
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object DatabaseCore {

    private val m_pDataSource: HikariDataSource
    val connection: DSLContext get() = DSL.using(m_pDataSource, SQLDialect.MARIADB)

    init {
        val pConfig = HikariConfig()
        pConfig.driverClassName = "com.mysql.cj.jdbc.Driver"
        pConfig.jdbcUrl = ServerConstants.DB_URL
        pConfig.username = ServerConstants.DB_USER
        pConfig.password = ServerConstants.DB_PASS
        pConfig.maximumPoolSize = 20
        pConfig.leakDetectionThreshold = 15 * 1000L
        pConfig.maxLifetime = 595 * 1000L
        pConfig.idleTimeout = 0 // disabled
        pConfig.addDataSourceProperty("autoReconnect", "true")
        pConfig.addDataSourceProperty("cachePrepStmts", true)
        pConfig.addDataSourceProperty("cacheResultSetMetadata", "true")
        pConfig.addDataSourceProperty("cacheServerConfiguration", "true")
        pConfig.addDataSourceProperty("elideSetAutoCommits", "true")
        pConfig.addDataSourceProperty("maintainTimeStats", "false")
        pConfig.addDataSourceProperty("prepStmtCacheSize", 250)
        pConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        pConfig.addDataSourceProperty("rewriteBatchedStatements", "true")
        pConfig.addDataSourceProperty("useLocalSessionState", "true")
        pConfig.addDataSourceProperty("useServerPrepStmts", "false")
        m_pDataSource = HikariDataSource(pConfig)
    }
}

/*
package net.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import constants.ServerConstants;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class DatabaseCore {

    public DatabaseCore() {
        initialize();
    }

    public static void initialize() {
        final var pConfig = new HikariConfig();
        pConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        pConfig.setJdbcUrl(ServerConstants.DB_URL);
        pConfig.setUsername(ServerConstants.DB_USER);
        pConfig.setPassword(ServerConstants.DB_PASS);
        pConfig.setMaximumPoolSize(20);
        pConfig.setLeakDetectionThreshold(15 * 1000L);
        pConfig.setMaxLifetime(595 * 1000L);
        pConfig.setIdleTimeout(0); // disabled

        pConfig.addDataSourceProperty("autoReconnect", "true");
        pConfig.addDataSourceProperty("cachePrepStmts", true);
        pConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        pConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        pConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        pConfig.addDataSourceProperty("maintainTimeStats", "false");
        pConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        pConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        pConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        pConfig.addDataSourceProperty("useLocalSessionState", "true");
        pConfig.addDataSourceProperty("useServerPrepStmts", "false");

        m_pDataSource = new HikariDataSource(pConfig);
    }

    private static HikariDataSource m_pDataSource;

    public static DSLContext getConnection() {
        return DSL.using(m_pDataSource, SQLDialect.MARIADB);
    }
}

 */