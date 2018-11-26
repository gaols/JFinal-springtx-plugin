package com.github.gaols.plugins;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SpringTxAwareDataSource implements DataSource {

    private final DataSource ds;
    private volatile boolean showSql = false;

    SpringTxAwareDataSource(DataSource ds) {
        this.ds = ds;
    }

    public void setShowSql(boolean isShowSql) {
        this.showSql = isShowSql;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new SqlReporter( DataSourceUtils.getConnection(this.ds), this.showSql).getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return ds.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ds.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return ds.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        ds.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        ds.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return ds.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return ds.getParentLogger();
    }
}
