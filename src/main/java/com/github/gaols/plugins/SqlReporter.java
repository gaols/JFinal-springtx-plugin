package com.github.gaols.plugins;

import com.jfinal.log.Log;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Provide a proxy connection for JFinal.
 */
public class SqlReporter implements InvocationHandler, SqlReporterConnection {

    private final Connection conn;
    private static final Log log = Log.getLog(SqlReporter.class);
    private final boolean showSql;

    SqlReporter(Connection conn, boolean showSql) {
        this.conn = conn;
        this.showSql = showSql;
    }

    @SuppressWarnings("rawtypes")
    Connection getConnection() {
        Class clazz = conn.getClass();
        return (Connection) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{Connection.class, SqlReporterConnection.class}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        try {
            if (showSql && "prepareStatement".equals(methodName)) {
                String info = "Sql: " + args[0];
                log.info(info);
            } else if ("close".equals(methodName)) {
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    return null;
                }
            } else if("commit".equals(methodName)) {
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    return null;
                }
            } else if ("rollback".equals(methodName)) {
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    return null;
                }
            }

            return method.invoke(conn, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
