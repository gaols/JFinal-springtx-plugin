package com.github.gaols.plugins;

import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.activerecord.cache.ICache;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;

import javax.sql.DataSource;
import java.lang.reflect.Field;

public class SpringTxAwareConfig extends Config {

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
            Field method = Config.class.getDeclaredField("transactionLevel");
            method.setAccessible(true);
            method.set(this, transactionLevel);
        } catch ( IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
