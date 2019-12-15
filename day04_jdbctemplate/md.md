JdbcTemplateDemo1里面：

`        //准备数据源：spring的内置数据源
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");//设置内容
        ds.setUrl("jdbc:mysql://localhost:3306/eesy?useUnicode=true&characterEncoding=utf8");
        ds.setUsername("root");
        ds.setPassword("wxs950518");`

可以用spring来配置:
 1. 创建bean.xml:
 
`    <!--配置JdbcTemplate-->
     <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
         <property name="dataSource" ref="dataSource"></property>
     </bean>
     <!-- 配置数据源-->
     <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
         <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
         <property name="url" value="jdbc:mysql://localhost:3306/eesy?useUnicode=true&amp;characterEncoding=utf8"></property>
         <property name="username" value="root"></property>
         <property name="password" value="wxs950518"></property>
     </bean>`
     
     `public static void main(String[] args) {
         //1.获取容器
         ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
         //2.获取对象
         JdbcTemplate jt = ac.getBean("jdbcTemplate",JdbcTemplate.class);
         //3.执行操作
         jt.execute("insert into account(name,money)values('ddd',2222)");`
         
         