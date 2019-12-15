完善account案例
    在服务接口里添加一个转账transfer接口
 
细节：
1.     /**
        * 根据名称查询账户
        * @param accountName
        * @return 如果有唯一的结果就返回，如果没有结果就返回null
        *          如果结果集超过一个就抛出异常
        */
       Account findAccountByName(String accountName);
       
                   public void transfer(String sourceName, String targetName, Float money) {
                       //1.根据名称查询转出账户
                       Account source = accountDao.findAccountByName(sourceName);
                       //2.根据名称查询转入账户
                       Account target = accountDao.findAccountByName(targetName);
                       //3.转出账户减钱
                       source.setMoney(source.getMoney()-money);
                       //4.转入账户加钱
                       target.setMoney(target.getMoney()+money);
                       //5.更新转出账户
                       accountDao.updateAccount(source);
                       //6.更新转入账户
                       accountDao.updateAccount(target);
                   }
                   
                   
2.步骤1、2、5、6 都是会获取一个连接，每个连接都有一个独立事务，每个事物只要成功就会提交，但是如果1、2、5都成功，但是6失败。会导致系统出bug。
所以要把这4个连接当做一次connection。要成功就一起成功，有一个连接失败就全部失败。
解决办法：
创建utils.Connectionutils.java这个类，使用THreadLocal对象把Connection和当前线程绑定，从而使一个线程中只有一个能控制事务的对象。
                             `public Connection getThreadConnection(){
                                    try {
                                        // 1.先从ThreadLocal上获取
                                        Connection conn = tl.get();
                                        // 2.判断当前线程上是否有连接
                                        if(conn == null){
                                                // 3.从数据源中获取一个连接，并且存入到ThreadLocal中
                                            conn = datasource.getConnection();
                                            tl.set(conn);
                                        }
                                        //4. 返回当前线程上的连接
                                        return conn;
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }`
创建和事务管理相关的工具类，它包含了开启事务，提交事务，回滚事务和释放连接的utils.TransactionManager类
细节：
    事务管理的release释放资源时，并不是真正的把连接关了，而是把连接还回连接池中，线程用完了也是还回线程池中，而不是真正的关了。
    其实线程中是绑着一个连接的，当我们把连接关闭，线程还回池中时，线程上是有连接的，只不过此时的连接已经关闭了，当我们下次再获取线程判断上面有无连接是，
    获取的结果一定是有，但是这个连接已经不能用了，因为被close过了。
    所以我们需要再全部用完后，把线程和连接进行解绑的操作，虽然java工程不涉及这个问题，但是在javaee上还是需要的
        解决办法：
                    /**
                     * 把连接和线程解绑
                     */
                    public void removeConnection(){
                        tl.remove();
                    }
                    
dao在执行时，已经有connection的支持，并且queryrunner由于在配置过程中没有注入数据源，从而实现功能中它不会从数据源中拿连接，
通过工具类的方式，把连接和线程进行绑定，并且编写了事务管理。接下来最后要做的就是，把新建的依赖都注入好，首先配置的是配置Connection的工具类 ConnectionUtils