package com.github.gaols.plugins;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;

public class SpringTxAwareActiveRecordPlugin extends ActiveRecordPlugin {
    public SpringTxAwareActiveRecordPlugin(String configName, DataSource dataSource, int transactionLevel) {
        this(new SpringTxAwareConfig(configName, dataSource, transactionLevel));
    }

    public SpringTxAwareActiveRecordPlugin(DataSource dataSource) {
        this(DbKit.MAIN_CONFIG_NAME, dataSource);
    }

    public SpringTxAwareActiveRecordPlugin(String configName, DataSource dataSource) {
        this(configName, dataSource, DbKit.DEFAULT_TRANSACTION_LEVEL);
    }

    public SpringTxAwareActiveRecordPlugin(DataSource dataSource, int transactionLevel) {
        this(DbKit.MAIN_CONFIG_NAME, dataSource, transactionLevel);
    }

    public SpringTxAwareActiveRecordPlugin(Config config) {
        super(config);
    }

    public SpringTxAwareActiveRecordPlugin(String configName, IDataSourceProvider dataSourceProvider, int transactionLevel) {
        this(new SpringTxAwareConfig(configName, providerDataSource(dataSourceProvider), transactionLevel));
    }

    private static DataSource providerDataSource(IDataSourceProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("IDataSourceProvider should not be null");
        }
        return provider.getDataSource();
    }

    public SpringTxAwareActiveRecordPlugin(IDataSourceProvider dataSourceProvider) {
        this(DbKit.MAIN_CONFIG_NAME, dataSourceProvider);
    }

    public SpringTxAwareActiveRecordPlugin(String configName, IDataSourceProvider dataSourceProvider) {
        this(configName, dataSourceProvider, 4);
    }

    public SpringTxAwareActiveRecordPlugin(IDataSourceProvider dataSourceProvider, int transactionLevel) {
        this(DbKit.MAIN_CONFIG_NAME, dataSourceProvider, transactionLevel);
    }
}
