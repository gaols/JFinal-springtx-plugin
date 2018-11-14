package com.github.gaols.plugins;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;
import java.lang.reflect.Field;

public class SpringTxAwareActiveRecordPlugin extends ActiveRecordPlugin {

    public SpringTxAwareActiveRecordPlugin(String configName, DataSource dataSource, int transactionLevel) {
        this(new SpringTxAwareConfig(configName, wrapDataSource(dataSource), transactionLevel));
    }

    private static DataSource wrapDataSource(DataSource dataSource) {
        return new SpringTxAwareDataSource(dataSource);
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
        try {
            DataSource ds = config.getDataSource();
            Field field = ActiveRecordPlugin.class.getDeclaredField("dataSource");
            field.setAccessible(true);
            field.set(this, ds);
        } catch ( IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public ActiveRecordPlugin setShowSql(boolean showSql) {
        try {
            Field field = ActiveRecordPlugin.class.getDeclaredField("dataSource");
            field.setAccessible(true);
            SpringTxAwareDataSource ds = (SpringTxAwareDataSource) field.get(this);
            ds.setShowSql(showSql);
            super.setShowSql(false); // always disable JFinal's
        } catch ( IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
