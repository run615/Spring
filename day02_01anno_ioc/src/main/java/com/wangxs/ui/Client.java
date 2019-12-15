package com.wangxs.ui;

import com.wangxs.dao.IAccountDao;
import com.wangxs.service.IAccountService;
import com.wangxs.service.impl.AccountServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 模拟一个表现层，用于调用业务层
 */
public class Client {

    public static void main(String[] args) {
        //1.获取核心容器对象
//        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        //这里需要修改一下，手动销毁容器，因为如果把一个子类看成父类型，就只能调用父类型的方法！
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");


        //2.根据id获取Bean对象
        IAccountService as  = (IAccountService)ac.getBean("accountServiceImpl");
//        System.out.println(as);
//
//        IAccountDao adao  = (IAccountDao) ac.getBean("accountDao","accountDao.class");
//        System.out.println(adao);
        as.saveAccount();
        ac.close();


    }
}
