# JFinal-springtx-plugin

## 概述

JFinal的ActiveRecordPlugin操作数据库很方便，遗憾的是在和Spring一起使用的时候，无法参与到Spring事务中。本插件就是让ActiveRecord也可以很方便的参与到Spring的声明式事务管理中。

## 使用说明

1. 引入依赖

```xml
<dependency>
    <groupId>com.github.gaols</groupId>
    <artifactId>jfinal-springtx-plugin</artifactId>
    <version>2.0.1</version>
</dependency>
```

**注意：本插件依赖JFinal，请自行引入合适的版本。**

2. 唯一需要改变的地方就是使用本项目提供的`SpringTxAwareActiveRecordPlugin`来代替JFinal的`ActiveRecordPlugin`。
当然我默认你的Spring项目已经开启了声明式事务管理。

先来看下在Spring项目中如何集成JFinal的ActiveRecordPlugin：

```java
@Configuration
public class AppConfig {
    @Bean
    public ActiveRecordPlugin activeRecord(DataSource ds) {
        ActiveRecordPlugin activeRecord = new ActiveRecordPlugin(ds);
        activeRecord.setShowSql(true);
        // 根据需要决定是否要加入mapping
        // activeRecord.addMapping("Account", "id", Account.class);
        activeRecord.start();
        return activeRecord;
    }
}
```

通过上面简单的配置，我们就可以在Spring项目中使用JFinal的ActiveRecord了。

```java
public class AccountService {
    // 开启声明式事务
    @Transactional(rollbackFor = Exception.class)
    public void transfer(int fromAccount, int toAccount, int amount) {
        Db.update("update account set balance=balance+? where id=?", amount, toAccount);
        Db.update("update account set balance=balance-? where id=?", amount, fromAccount);
        if (somethingUnmet) {
            // 转账条件不满足，例如：金额不足，则抛出异常。
            throw new YourBussinessException("transfer failed");
        }
    }
}
```

到目前为止，一切看起来都很顺利，但是当转账条件不满足，抛出一个`YourBussinessException`异常的时候，你会发现事务没有回滚！
是的，ActiveRecordPlugin默认不支持Spring事物，这个时候你要做的仅仅是使用本项目提供的**SpringTxAwareActiveRecordPlugin**代替
ActiveRecordPlugin即可。

```java
@Configuration
public class AppConfig {
    @Bean
    public ActiveRecordPlugin activeRecord(DataSource ds) {
        ActiveRecordPlugin activeRecord = new SpringTxAwareActiveRecordPlugin(ds);
        activeRecord.setShowSql(true);
        activeRecord.addMapping("Account", "id", Account.class); // add any mappings here
        activeRecord.start();
        return activeRecord;
    }
}
```

## 测试说明

* 将项目根目录下的db.sql导入到本地的数据库中；
* 修改数据库配置，src/test/resources目录下的conf.properties，使之指向你本地的数据库；
* 运行测试。

## 使用说明

* 目前只支持JFinal-2.2以上，之前的版本暂不支持；
* 由于使用了Spring事务管理，所以不要使用JFinal自带的事务管理；
* 确保开启事务的数据源和提供给JFinal的数据源是同一个。
