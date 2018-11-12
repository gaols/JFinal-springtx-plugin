package com.gaols.plugins;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.activerecord.cache.ICache;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SpringTxAwareConfig extends Config {

    private static final Log log = Log.getLog(SpringTxAwareConfig.class);

    public SpringTxAwareConfig(String name, DataSource dataSource, Dialect dialect, boolean showSql, boolean devMode, int transactionLevel, IContainerFactory containerFactory, ICache cache) {
        super(name, dataSource, dialect, showSql, devMode, transactionLevel, containerFactory, cache);
    }

    public SpringTxAwareConfig(String name, DataSource dataSource) {
        this(name, dataSource, new MysqlDialect());
    }

    public SpringTxAwareConfig(String name, DataSource dataSource, Dialect dialect) {
        this(name, dataSource, dialect, false, false, DbKit.DEFAULT_TRANSACTION_LEVEL, IContainerFactory.defaultContainerFactory, new EhCache());
    }

    public SpringTxAwareConfig(String name, DataSource dataSource, int transactionLevel) {
        this(name, dataSource, new MysqlDialect());
        try {
            Method method = Config.class.getDeclaredMethod("setTransactionLevel", int.class);
            method.setAccessible(true);
            method.invoke(this, transactionLevel);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() {
        Connection connection = DataSourceUtils.getConnection(getDataSource());
        return isShowSql() ? new SqlReporter(connection).getConnection() : connection;
    }

    @Override
    public boolean isInTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }

    @Override
    public void close(Connection conn) {
        smartCloseConn(conn);
    }

    @Override
    public void close(ResultSet rs, Statement st, Connection conn) {
        if (rs != null) {
            JdbcUtils.closeResultSet(rs);
        }
        if (st != null) {
            JdbcUtils.closeStatement(st);
        }
        smartCloseConn(conn);
    }

    @Override
    public void close(Statement st, Connection conn) {
        if (st != null) {
            JdbcUtils.closeStatement(st);
        }
        smartCloseConn(conn);
    }

    private boolean shouldCloseConn(Connection conn) {
        if (conn == null) {
            return false;
        }

        if (conn instanceof SqlReporterConnection) {
            conn = ((SqlReporterConnection) conn).getUnderlyingConnection();
        }

        boolean isTransactional = DataSourceUtils.isConnectionTransactional(conn, getDataSource());
        boolean ret = !isTransactional;
        log.debug("should close conn: " + ret);
        return ret;
    }

    private void smartCloseConn(Connection conn) {
        if (shouldCloseConn(conn)) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new ActiveRecordException(e);
            }
        }
    }

}
