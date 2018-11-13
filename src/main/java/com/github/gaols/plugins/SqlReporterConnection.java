package com.github.gaols.plugins;

import java.sql.Connection;

public interface SqlReporterConnection {
    /**
     * The connection before proxied.
     */
    Connection getUnderlyingConnection();
}
