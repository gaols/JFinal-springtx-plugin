package com.gaols.plugins;

import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.cache.ICache;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SpringTxAwareConfig extends Config {
    public SpringTxAwareConfig(String name, DataSource dataSource, Dialect dialect, boolean showSql, boolean devMode, int transactionLevel, IContainerFactory containerFactory, ICache cache) {
        super(name, dataSource, dialect, showSql, devMode, transactionLevel, containerFactory, cache);
    }

    public SpringTxAwareConfig(String name, DataSource dataSource) {
        super(name, dataSource);
    }

    public SpringTxAwareConfig(String name, DataSource dataSource, Dialect dialect) {
        super(name, dataSource, dialect);
    }

    public SpringTxAwareConfig(String name, DataSource dataSource, int transactionLevel) {
        super(name, dataSource, new MysqlDialect());
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
        if (isShowSql()) {
            connection = new com.gaols.plugins.SqlReporter(connection).getConnection();
        }
        return connection;
    }

    @Override
    public boolean isInTransaction() {
        throw new UnsupportedOperationException("isInTransaction is not supported");
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
        return !DataSourceUtils.isConnectionTransactional(conn, getDataSource());
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
