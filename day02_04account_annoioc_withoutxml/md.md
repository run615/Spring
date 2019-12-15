现在要解决的就是注解IOC还有xml的问题，要把xml给舍弃掉！
首先思路就是要有和xml相同功能的注解。

**创建一个配置类SpringConfiguration.java，作用和beam.xml一样。**
学习新的注解：
@Configuration
     作用：指定当前类是一个配置类
     细节：当配置类作为AnnotationConfigApplicationContext对象创建的参数时，该注解可以不写。
     
@ComponentScan
     作用：用于通过注解指定spring在创建容器时要扫描的包
     属性：
       value：它和basePackages的作用是一样的，都是用于指定创建容器时要扫描的包。
              我们使用此注解就等同于在bean.xml中配置了的:
              `<context:component-scan base-package="com.wangxs"></context:component-scan>`
     细节：1.base-package的value要填类路径（com.wangxs）；
          2.base-package的标准写法应该是数组类型，写成{"com.wangxs"},如果注解的属性有且只有一个值，如果是数组类型就可以把括号拿走啦；
          3.不写base-package也是可以的。
     此时：bean.xml中的`<context:component-scan base-package="com.wangxs"></context:component-scan>`可以删除了。
     
**接下来搞dbutils.QueryRunner,也是在SpringConfiguration这个配置类中创建注解。**
`    /**
     * 用于创建一个QueryRunner对象
     * @param dataSource
     * @return 
     */
    @Bean(name = "runner")//此注解用于把当前方法的返回值作为bean对象存入spring的ioc容器中
    public QueryRunner createQueryRunner(DataSource dataSource){
        return new QueryRunner(dataSource);
    }`
 @Bean
       作用：用于把当前方法的返回值作为bean对象存入spring的ioc容器中
       属性:
           name:用于指定bean的id。当不写时，默认值是当前方法的名称,及id是createQueryRunner，value是QueryRunner(dataSource)
       细节：
           当我们使用注解配置方法时，如果方法有参数，spring框架会去容器中查找有没有可用的bean对象。(举例：dataSource)
           查找的方式和Autowired注解的作用是一样的
           
       `/**
        * 创建数据源对象
        * @return
        */
       @Bean(name = "datasource")
       public DataSource createDataSource(){
           try {
               ComboPooledDataSource ds = new ComboPooledDataSource();
               ds.setDriverClass("com.mysql.jdbc.Drive");
               ds.setJdbcUrl("jdbc:mysql://localhost:3306/eesy?useUnicode=true&amp;characterEncoding=utf8");
               ds.setUser("root");
               ds.setPassword("1234");
               return ds;
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
       }`
       
  此时就已经都已经修改完毕，可以删除bean.xml文件啦。
  接着在测试类中把获取容器`ApplicationContext ac = new ClassPathXmlApplicationContext("beam.xml");`
  修改为`ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);`
  
 现在有几个小细节：
 1. @Configuration 写或不写在测试时都能正常运行，当配置类作为AnnotationConfigApplicationContext对象创建的参数时，该注解可以不写。
 
  @Import
      作用：用于导入其他的配置类
      属性：
          value：用于指定其他配置类的字节码。
                  当我们使用Import的注解之后，有Import注解的类就父配置类，而导入的都是子配置类
                  
 2. 不能把jdbc那一块写死了，重新写一个JdbcConfig的类。
  @PropertySource
      作用：用于指定properties文件的位置
      属性：
          value：指定文件的名称和路径。
                  关键字：classpath，表示类路径下
                  
                  
                  
#最后内容就是spring整合junit代码
由于前期好几个的测试功能中的获取容器和得到业务层代码太过冗余，所以需要用@before方法：
                    `    private ApplicationContext ac;
                        private IAccountService as;
                        @Before
                        public void init(){
                            //1.获取容器
                             ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);
                            //2.得到业务层对象
                             as = ac.getBean("accountService", IAccountService.class);
                        }
                        @Test
                        public void testFindAll() {
                            //3.执行方法
                            List<Account> accounts = as.findAllAccount();
                            for (Account account : accounts) {
                                System.out.println(account);
                            }
                        }`
但是这样的操作会让测试工程师很为难，他们或许不懂的这些骚操作，只会找带@Test的代码做测试。
所以我们得按流程进行分析：
            1、应用程序的入口
                main方法
            2、junit单元测试中，没有main方法也能执行
                junit集成了一个main方法
                该方法就会判断当前测试类中哪些方法有 @Test注解
                junit就让有Test注解的方法执行
            3、junit不会管我们是否采用spring框架
                在执行测试方法时，junit根本不知道我们是不是使用了spring框架
                所以就算写@Autowired注解也不会为我们读取配置文件/配置类创建spring核心容器
            4、由以上三点可知
                当测试方法执行时，没有Ioc容器，就算写了Autowired注解，也无法实现注入 
                
                
解决方法：
 * 使用Junit单元测试：测试我们的配置
 * Spring整合junit的配置
 *      1、导入spring整合junit的spring-context的jar(坐标)
 *      2、使用Junit提供的一个注解把原有的main方法替换了，替换成spring提供的
 *             @Runwith
 *      3、告知spring的运行器，spring和ioc创建是基于xml还是注解的，并且说明位置
 *          @ContextConfiguration
 *                  locations：指定xml文件的位置，加上classpath关键字，表示在类路径下
 *                  classes：指定注解类所在地位置
 *
 *   当我们使用spring 5.x版本的时候，要求junit的jar必须是4.12及以上                       
                        
