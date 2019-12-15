package com.wangxs.service.impl;


import com.wangxs.dao.IAccountDao;
import com.wangxs.domian.Account;
import com.wangxs.service.IAccountService;

import java.util.List;

/**
 * 账户的业务层实现类
 * 这里需要添加未实现的方法
 */
public class AccountServiceImpl implements IAccountService{

    private IAccountDao accountDao;//业务层要调用持久层

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public List<Account> findAllAccount() {
        return accountDao.findAllAccount();
    }

    public Account findAccountById(Integer accountId) {
        return accountDao.findAccountById(accountId);
    }

    public void saveAccount(Account account) {
        accountDao.saveAccount(account);
    }

    public void updateAccount(Account account) {
        accountDao.updateAccount(account);
    }

    public void deleteAccount(Integer accountId) {
        accountDao.deleteAccount(accountId);
    }
}
