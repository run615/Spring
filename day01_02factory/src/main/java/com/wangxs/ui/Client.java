package com.wangxs.ui;


import com.wangxs.factory.BeanFactory;
import com.wangxs.service.IAccountService;
import com.wangxs.service.impl.AccountServiceImpl;

public class Client {

    public static void main(String[] args) {
//        IAccountService as = new AccountServiceImpl();
        for(int i=0;i<5;i++) {
            IAccountService as = (IAccountService) BeanFactory.getBean("accountService");
            System.out.println(as);
            as.saveAccount();
        }

    }
}
