package com.github.gaols.plugins.model;

public class Account extends BaseAccount<Account> {
    public static Account dao = new Account();

    public Account findByName(String name) {
        return dao.findFirst("select * from Account where name=?", name);
    }
}
