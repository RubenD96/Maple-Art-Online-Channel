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