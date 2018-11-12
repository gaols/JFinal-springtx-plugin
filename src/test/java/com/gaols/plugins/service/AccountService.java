package com.gaols.plugins.service;

import com.gaols.plugins.model.Account;
import com.jfinal.plugin.activerecord.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    public Account findByName(String name) {
        return Account.dao.findFirst("select * from Account where name=?", name);
    }

    public int getBalance(String name) {
        return findByName(name).getBalance();
    }

    @Transactional
    public Account findByNameTx(String name) {
        return Account.dao.findFirst("select * from Account where name=?", name);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(String from, String to, int amount) {
        Db.update("update Account set balance=balance+? where name=?", amount, to);
        Db.update("update Account set balance=balance-? where name=?", amount, from);

        if ("here".equals(from)) {
            throw new IllegalArgumentException("here从来是只进不出");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchInc(int amount) {
        String sql = "update Account set balance=balance+? where name=?";
        Object[][] params = {{amount, "gaols"}, {amount, "here"}};
        Db.batch(sql, params, 20);
        if (amount < 5) {
            throw new IllegalArgumentException("more balance required");
        }
    }

}
