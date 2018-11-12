# jfinal-springtx-plugin

## 项目简介

JFinal的ActiveRecordPlugin操作数据库很方便，遗憾的是在和Spring一起使用的时候，无法参与
到Spring事务中。本插件就是让ActiveRecord也可以很方便的参与到Spring的声明式事务管理中。

## 使用说明

使用本项目提供的`SpringTxAwareActiveRecordPlugin`来代替JFinal的`ActiveRecordPlugin`。

```java
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class AppConfig {

    @Bean
    public ActiveRecordPlugin activeRecord(DataSource ds) {
        ActiveRecordPlugin activeRecord = new SpringTxAwareActiveRecordPlugin(ds);
        activeRecord.setShowSql(true);
        activeRecord.addMapping("Account", "id", Account.class); // add any mappings here
        activeRecord.start();
        return activeRecord;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

}
```

## 测试说明

* 将项目根目录下的db.sql导入到本地的数据库中；
* 修改数据库配置，src/test/resources目录下的conf.properties，使之指向你本地的数据库；
* 运行测试。
