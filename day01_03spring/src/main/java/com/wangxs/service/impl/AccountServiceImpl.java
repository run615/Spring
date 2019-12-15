package com.wangxs.service.impl;

import com.wangxs.dao.IAccountDao;
import com.wangxs.dao.impl.AccountDaoImpl;

import com.wangxs.service.IAccountService;

/**
 * 账户的业务层实现类,业务实现层这里需要调用持久层
 */

public class AccountServiceImpl implements IAccountService {

    private IAccountDao accountDao = new AccountDaoImpl();

    public void  saveAccount(){
        accountDao.saveAccount();
    }
}
