#  用于梳理Spring知识点

Spring是分层的Java EE应用全栈轻量级开源框架，以IoC（Inverse Of Control反转控制）和AOP（Aspect Oriented Programming:面向切面编程）为内核，
提供了展现层SpringMVC和持久层SpringJDBC 等企业级应用技术。

**Spring的优势在于**：
    _方便解耦，简化开发_
        通过Spring提供的IoC容器，将对象间的依赖关系交由Spring进行控制。
    _AOP编程的支持_
    通过Spring的AOP功能，方便进行面向切面的编程，许多不容易用OOP实现的功能可以通过AOP来应对。
    tips：OOP是面向对象编程
    _声明式事务的支持_
    _方便程序的测试_
    _方便集成优秀框架_
     
## 首先是**day01_01JDBC**:
一开始由程序间的耦合来引入解耦这个概念，在实际开发中应该做到编辑期不依赖，运行时才依赖。

举了两个例子：
    1.业务层调用持久层，并且此时业务层在依赖持久层的接口和实现类。但是如果此时没有持久层的实现类，编译将不能通过。
    2.早期在JDBC操作的时候会注册驱动时，会使用`DriverManager.registerDriver(new com.mysql.jdbc.Driver());`
      但是利用`Class.forName("com.mysql.jdbc.Driver");`就能实现了类之间解耦，此时的`"com.mysql.jdbc.Driver"`只是一个字符串。
      这样写的好处是，我们的类中不再依赖具体的驱动类，
      不过同时也产生了一个新的问题。mysql驱动的全限定类名字符串是在java类中写死了。一旦要改还是要修改源码。
      解决这个问题的方法就是使用配置文件配置。

## **day01_02factory**:
_工厂模式解耦_：
在实际开发中我们可以把三层的对象都使用配置文件（bean.properties）配置起来：
当启动服务器应用加载的时候，让一个类中的方法通过读取配置文件，把这些对象创建出来并存起来。（q1）在接下来的使用的时候，直接拿过来用就好了。
那么，这个读取配置文件，创建和获取三层对象的类就是工厂（q2）。

q1：因为有很多对象，所以要找集合存起来，那么就有Map和List供选择。如果有查找需求就用Map。
    在应用加载的时候，创建一个Map，用于存放三层对象<IAccountService、IAccountDao、Client>。
    于是这个Map就称之为**容器**。
    
q2：工厂（BeanFactory）就是负责给我们从容器中获取指定对象的类。
    那么现在，我们在获取对象的时候，就不需要采用主动new的方式，而是向工厂要，被动的等着工厂为我们查找或者创建对象。
    这种被动的方式获取对象的思想就是控制反转IoC。

_解释三层对象的概念_：    
            IAccountService 是业务层的接口，负责操作账户。 模拟保存一名用户。
            AccountServiceImpl 用来继承IAccountService，它是账务的业务层实现类。里面要添加保存用户的方法。
                                根据三层架构，业务层需要调用持久层，所以需要定义一个IAccountDao类，并new一个新的对象accountDao
            
            IAccountDao是账户的持久层接口。模拟保存一名用户。
            AccountDaoImpl 用来继承IAccountDao ，是持久层的实现类。里面要添加保存用户的方法。
                           在操作的时候，直接打印。
            
            Client 模拟一个表现层，用来调用业务层。
                    里面调用BeanFactory去对bean.properties做处理，来降低类耦合。这就是工厂模式解耦！
                    
_定义一个Beanfactory的类：_
    Bean：在计算机英语中，有可重用组件的含义。
    JavaBean：用java语言编写的可重用组件。（javabean >  实体类）
    要想查找或创建service和dao对象，
        第一个：需要一个配置文件（bean.properties）来配置我们的service和dao，
               配置的内容格式：唯一标识=全限定类名（key=value) ,<全限定名=项目名+包名+类名>
        第二个：通过读取配置文件中配置的内容，使用了`Class.forName(beanPath).newInstance()；`反射创建对象。         
          
   _这里工厂模式（Beanfactory）会产生了一个问题_：
   会产生多例的对象-<对象会被创建多次，执行效率没有单例对象高。>
   但是我们只想要单例，因为单例只被创建一次，从而类中的成员也就只会初始化一次。
                        
   考虑此问题的背景：
   由于BeanFactory在创建对象的时候，使用了newInstance()。每次它都会调用默认构造函数创建对象。
   我们只需要调用它一次即可，于是就要调用完之后立即存储起来，不然由于java的垃圾机制，长时间不用就会被回收。

   解决方法：(**工厂模式解耦的升级版**)
   调用完后立即存储起来。
   首先需要在BeanFactory里用容器来存储（定义一个Map，用于存放我们要创建的对象，即为容器）。
   接下来在静态代码块中，不仅要得到properties，还要实例化容器，并取出配置文件中所有的Key。keys方法返回枚举类型。
   遍历枚举，取出每个Key，根据Key获取value，并通过反射创建对象。最后把key和value存入容器中。

## **day01_03spring**:
使用Spring的IoC解决程序耦合，搭建Spring基于xml Ioc的开发环境。

先要创建一个**xml文件（beam.xml）**,让Spring管理资源，在配置文件中配置service和dao。
        格式如下：
        `<!-- bean标签:用于配置让spring创建对象，并且存入ioc容器之中
        id 属性:对象的唯一标识。   class 属性:反射时指定要创建对象的全限定类名
        -->`
        内容如下：
        `<!-- 配置 service -->
        <bean id="accountService" class="com.wangxs.service.impl.AccountServiceImpl"> </bean>
        <!-- 配置 dao -->
        <bean id="accountDao" class="com.wangxs.dao.impl.AccountDaoImpl"></bean>`

_在Client.java中测试配置是否成功_：
        模拟一个表现层，用于调用业务层。
        获取spring的Ioc核心容器，并根据id获取对象，把spring创建好的容器beans取出来。
    
   整个过程变得非常简单，和工厂模式几乎一样，只是读取配置文件、创建对象并存入map中的过程全部让Spring干了。
   我们只需要创建配置文件，打开配置信息交给spring，并且获取对象，得到核心容器，再根据id取出对象即可。

   **细节：**
   **获取核心容器<ApplicationContext>的三个常用实现类**：
        ClassPathXmlApplicationContext：它可以加载类路径下的配置文件bean.xml，要求配置文件必须在类路径下。不在的话，加载不了。(更常用)
        FileSystemXmlApplicationContext：它可以加载磁盘任意路径下的配置文件(必须有访问权限）
        AnnotationConfigApplicationContext：它是用于读取注解创建容器的。
   **核心容器的两个接口引发出的问题：**
        _ApplicationContext_:     单例对象适用              采用此接口
        它在构建核心容器时，创建对象采取的策略是采用立即加载的方式。也就是说，只要一读取完配置文件马上就创建配置文件中配置的对象。
        _BeanFactory_:            多例对象使用
        它在构建核心容器时，创建对象采取的策略是采用延迟加载的方式。也就是说，什么时候根据id获取对象了，什么时候才真正的创建对象。
    
   **By the way，BeanFactory 才是 Spring 容器中的顶层接口。 ApplicationContext 是它的子接口**。
   
   
## **day01_04bean**:
解决的问题是：如何利用ApplicationContext来自己判断是多例还是单例呢？

这一节主要讲bean标签和管理对象的细节，一共有两个bean(一个service、一个dao)

_bean标签的作用_:用于将配置对象让 spring 来创建和管理。
                默认情况下它调用的是类中的无参构造函数。如果没有无参构造函数则不能创建成功。 
_属性_:
    id:给对象在容器中提供一个唯一标识。用于获取对象。 
    class:指定类的全限定类名。用于反射创建对象。默认情况下调用无参构造函数。 
    scope:指定对象的作用范围。
            * singleton :默认值，单例的.
            * prototype :多例的.
            * request :WEB项目中,Spring创建一个Bean的对象,将对象存入到request域中. 
            * session :WEB项目中,Spring创建一个Bean的对象,将对象存入到session域中. * global session :WEB项目中,应用在Portlet环境.如果没有Portlet环境那么
                       globalSession 相当于 session.
    init-method:指定类中的初始化方法名称。 
    destroy-method:指定类中销毁方法名称。

**spring对bean的管理细节
        1.创建bean的三种方式
        2.bean对象的作用范围
        3.bean对象的生命周期**
        
                _1.创建bean的三种方式：_
                    <!-- 第一种方式：使用默认构造函数创建。
                            在spring的配置文件中使用bean标签，配以id和class属性之后，且没有其他属性和标签时。
                            采用的就是默认构造函数创建bean对象，此时如果类中没有默认构造函数，则对象无法创建。
                
                    <bean id="accountService" class="com.wangxs.service.impl.AccountServiceImpl"></bean>
                    -->
                
                    <!-- 第二种方式： 使用实例工厂中的方法创建对象（使用某个类中的方法创建对象，并存入spring容器）
                                    此种方式是：
                                        先把工厂的创建交给Spring来管理，然后在使用工厂的bean来调用里面的方法。
                                        
                    <bean id="instanceFactory" class="com.wangxs.factory.InstanceFactory"></bean>
                    <bean id="accountService" factory-bean="instanceFactory" factory-method="getAccountService"></bean>
                                            factory-bean:指定哪一个是工厂例  factory—method：指定哪个方法来获取对象
                
                
                    <!- 第三种方式：使用静态工厂中的静态方法创建对象（使用某个类中的静态方法创建对象，并存入spring容器，让spring管理)
                                    此种方式是:
                                        使用 StaticFactory 类中的静态方法 createAccountService 创建对象，并存入 spring 容器
                                        id 属性:指定 bean 的 id，用于从容器中获取 
                                        class 属性:指定静态工厂的全限定类名 
                                        factory-method 属性:指定生产对象的静态方法
                        
                    <bean id="accountService"
                          class="com.itheima.factory.StaticFactory" 
                          factory-method="createAccountService"></bean>
                    
                _2.bean对象的生命周期:_
                        单例对象:scope="singleton"
                            一个应用只有一个对象的实例。它的作用范围就是整个引用。
                            出生：当容器创建时对象出生
                            活着：只要容器还在，对象一直活着
                            死亡：容器销毁，对象消亡
                            总结：单例对象的生命周期和容器相同
                            
                        多例对象:scope="prototype"
                            出生：当我们使用对象时spring框架为我们创建
                            活着：对象只要是在使用过程中就一直活着。
                            死亡：当对象长时间不用，且没有别的对象引用时，由Java的垃圾回收器回收。所以不会立即销毁。           
                        
                        <bean id="accountService" class="com.wangxs.service.impl.AccountServiceImpl"
                              scope="singleton" init-method="init" destroy-method="destroy"></bean>
                                    init-method:初始化方法 
                                    destroy-method：销毁方法，需要在 AccountServiceImpl手动定义方法
            
            
                _3.bean的作用范围调整_
                        bean标签的scope属性：
                            作用：用于指定bean的作用范围
                            取值： 常用的就是单例的和多例的
                                singleton：单例的（默认值）
                                prototype：多例的
                                request：作用于web应用的请求范围
                                session：作用于web应用的会话范围
                                global-session：作用于集群环境的会话范围（全局会话范围），当不是集群环境时，它就是session
                
                    <bean id="accountService" class="com.wangxs.service.impl.AccountServiceImpl" scope="prototype"></bean>


## **day01_05di**:   
_依赖注入:Dependency Injection。它是spring框架核心ioc的具体实现。_ 
        我们的程序在编写时，通过控制反转，把对象的创建交给了 spring，但是代码中不可能出现没有依赖的情况。
        ioc 解耦只是降低他们的依赖关系，但不会消除。例如:我们的业务层仍会调用持久层的方法。 
        那这种业务层和持久层的依赖关系，在使用 spring 之后，就让 spring 来维护了。
        简单的说，就是坐等框架把持久层对象传入业务层，而不用我们自己去获取。
        实现类之间依赖使用Spring的注入来实现依赖关系的维护。
        
_依赖关系的管理：_
         在当前类需要用到其他类的对象，由spring为我们提供，我们只需要在配置文件中说明。
_依赖关系的维护：_
         就称之为依赖注入。
         
依赖注入：
        能注入的数据：有三类
            基本类型和String
            其他bean类型（在配置文件中或者注解配置过的bean）
            复杂类型/集合类型
        **注入的方式：有三种**
            第一种：使用构造函数提供
            第二种：使用set方法提供
            第三种：使用注解提供
                        _构造函数注入：_ 
                                顾名思义：就是使用类中的构造函数，给成员变量赋值。注意，赋值的操作不是我们自己做的，而是通过配置的方式，让spring框架来为我们注入。
                                        `public class AccountServiceImpl implements IAccountService {
                                               private String name; 
                                                private Integer age; 
                                                private Date birthday;
                                        public AccountServiceImpl(String name, Integer age, Date birthday) { 
                                                this.name = name;
                                                this.age = age;
                                                this.birthday = birthday;`
                                要求：类中需要提供一个对应参数列表的构造函数
                                使用的标签:constructor-arg
                                标签出现的位置：bean标签的内部
                                标签中的属性
                                    type：用于指定要注入的数据的数据类型，该数据类型也是构造函数中某个或某些参数的类型
                                    index：用于指定要注入的数据给构造函数中指定索引位置的参数赋值。索引的位置是从0开始
                                    name：用于指定给构造函数中指定名称的参数赋值                                     <常用的>
                                    =============以上三个用于指定给构造函数中哪个参数赋值，下面两个指的是赋什么值的===============
                                    value：用于提供基本类型和String类型的数据
                                    ref：用于指定其他的bean类型数据。它指的就是在spring的Ioc核心容器中出现过的bean对象
                                _优势_：
                                    在获取bean对象时，注入数据是必须的操作，否则对象无法创建成功。
                                _弊端_：
                                    改变了bean对象的实例化方式，使我们在创建对象时，如果用不到这些数据，也必须提供。
                                    **所以我们一般用set方法注入！**
                                    `<bean
                                            id="accountService" class="com.wangxs.service.impl.AccountServiceImpl">
                                        <!--<constructor-arg type="java.lang.String" value="test"></constructor-arg>
                                        type用于指定要注入的数据的数据类型，现在就是String，
                                        value就会把test的值注入到构造函数中是String类型的成员，但是如果有两个String成员就会出现问题啦-->
                                            <constructor-arg name="name" value="test"></constructor-arg>
                                            <constructor-arg name="age" value="18"></constructor-arg>
                                            <constructor-arg name="birthday" ref="now"></constructor-arg>
                                    </bean>
                                        <!-- 配置一个日期对象 -->
                                        <bean id="now" class="java.util.Date"></bean>`
                        _set方法注入_    **更常用的方式**
                                顾名思义：就是在类中提供需要注入成员的set的方法
                                        `public class AccountServiceImpl implements IAccountService {
                                            private String name;
                                            private Integer age; 
                                            private Date birthday;
                                         public void setName(String name) { 
                                            this.name = name;
                                            }
                                         public void setAge(Integer age) {
                                            this.age = age; 
                                            }
                                         public void setBirthday(Date birthday) {
                                            this.birthday = birthday;
                                            }`
                                涉及的标签：property
                                出现的位置：bean标签的内部
                                标签的属性
                                    name：用于指定注入时所调用的set方法名称
                                    value：用于提供基本类型和String类型的数据
                                    ref：用于指定其他的bean类型数据。它指的就是在spring的Ioc核心容器中出现过的bean对象
                                优势：
                                    创建对象时没有明确的限制，可以直接使用默认构造函数
                                弊端：
                                    如果有某个成员必须有值，则获取对象是有可能set方法没有执行。
                                `<bean id="accountService2" class="com.wangxs.service.impl.AccountServiceImpl2">
                                    <property name="name" value="TEST" ></property>
                                    <property name="age" value="21"></property>
                                    <property name="birthday" ref="now"></property>
                                </bean>`
        **复杂类型的注入/集合类型的注入**
                   用于给List结构集合注入的标签：
                       list array set
                   用于给Map结构集合注入的标签:
                       map  props
                   结构相同，标签可以互换
                `<bean id="accountService3" class="com.wangxs.service.impl.AccountServiceImpl3">
                    <property name="myStrs">
                        <set>
                            <value>AAA</value>
                            <value>BBB</value>
                            <value>CCC</value>
                        </set>
                    </property>
                    <property name="myList">
                        <array>
                            <value>AAA</value>
                            <value>BBB</value>
                            <value>CCC</value>
                        </array>
                    </property>
                    <property name="mySet">
                        <list>
                            <value>AAA</value>
                            <value>BBB</value>
                            <value>CCC</value>
                        </list>
                    </property>
                    <property name="myMap">
                        <props>
                            <prop key="testC">ccc</prop>
                            <prop key="testD">ddd</prop>
                        </props>
                    </property>
                    <property name="myProps">
                        <map>
                            <entry key="testA" value="aaa"></entry>
                            <entry key="testB">
                                <value>BBB</value>
                            </entry>
                        </map>
                    </property>
                </bean>`
                

第二天课程：
##day02_01anno_ioc
#基于注解的IOC配置
   学习基于注解的 IoC 配置，大家脑海里首先得有一个认知，即注解配置和 xml 配置要实现的功能都是一样的，都是要降低程序间的耦合。只是配置的形式不一样。
   关于实际的开发中到底使用 xml 还是注解，每家公司有着不同的使用习惯。所以这两种配置方式我们都需要掌握。
   我们在讲解注解配置时，采用上一章节的案例，把 spring 的 xml 配置内容改为使用注解逐步实现。

#虽然是基于注解，但是依然要创建xml配置文件(bean.xml)并开启对注解的支持。
   而且得在beam里添加一个配置，告知Spring在创建容器时要扫描的包，当它在扫描包的时候就会扫描里面的注解。
   其中配置所需要的标签，不是在beans的约束中，而是一个名称为contexts的名称空间和约束中。接着我们就得导入，导入方式就是在配置环境中复制：
                `<beans xmlns="http://www.springframework.org/schema/beans"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xmlns:context="http://www.springframework.org/schema/context"
                       xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context.xsd">
                </beans>`
   这时候我们就可以通过`<context:component-scan base-package="com.wangxs"></context:component-scan>`
   让Spring扫描com.wangxs下的所有子包所有类上或接口上的注解。回过头来，这时候系统它在解析配置文件时就会知道我们在哪儿写了注解。

#需要学习的几类注解：

##用于创建对象的：
1. 首先学习的是@component 注解，它的作用是把当前类对象存入spring容器中。
   也就是说在service上写一个@component，就会把哪个类反射创建一个对象然后存入spring容器中。
   但是有个问题，spring容器是一个map结构，有key-value。存这个对象，那么这个对象就是key。可是value是啥呢？
   它的属性是用于指定bean的id。当我们不写时，它的默认值是当前类名，且首字母改小写。
   其中最关键的一步是，
   回想一下昨天的知识点，当我们解析配置文件时就会创建容器并创建对象，但是在beam.xml文件里没有定义对象。
   因为系统它在解析配置文件时还不知道我们在哪儿写了注解。于是我们就得在利用jar包aop。
   而且得在beam里添加一个配置，告知Spring在创建容器时要扫描的包，当它在扫描包的时候就会扫描里面的注解。
   
   对比xml，注解比较简单，只需要写上注解和标签就可以了。另外如果要指定id，就按照这种写法。@component(value="accountService"),其中这个value名称可以不写，就写双引号内部的。
        
2.  接着介绍  @Controller：一般用在表现层
             @Service：一般用在业务层
             @Repository：一般用在持久层
    以上三个注解他们的作用和属性与Component是一模一样，是Component的子类。他们三个是spring框架为我们提供明确的三层使用的注解，使我们的三层对象更加清晰。
    **细节：如果注解中有且只有一个属性要赋值时，且名称是 value，value 在赋值是可以不写。**  
             
##用于注入数据的：
###相当于 <property name="" ref="">       <property name="" value="">
      
3.  @Autowired:
        作用：自动按照类型注入。只要容器中有唯一的一个bean对象类型和要注入的变量类型匹配，就可以注入成功
             如果ioc容器中没有任何bean的类型和要注入的变量类型匹配，则报错。
             如果Ioc容器中有多个类型匹配时：只要有一个一样，就算注入成功，如果没有一个一样就报错。这里引入Qualifier注入。
        出现位置：可以是变量上，也可以是方法上
        细节：在使用注解注入时，set方法就不是必须的了，可以省略

4.  @Qualifier:
        作用：在@Autowired自动按照类中注入的基础之上再按照名称注入。
        属性：
            value：用于指定注入bean的id。
        缺点：必须搭配Autowired使用，太繁琐啦，所以引入注解Resource
        细节：它在给类成员注入时不能单独使用，必须和@Autowired一起使用。但是在给方法参数注入时可以独立使用。

5.  @Resource：
        作用：直接按照bean的id注入。它可以独立使用
        属性：
            name：用于指定bean的id。

   以上三个注入都只能注入其他bean类型的数据，而基本类型和String类型无法使用上述注解实现。该如何注入基本类型和String类型就引出@value啦！
       另外，集合类型的注入只能通过XML来实现。                                                
      
6.  @Value注解
         作用：用于注入基本类型和String类型的数据
         属性：
                value：用于指定数据的值。它可以使用spring中SpEL(也就是spring的el表达式）
                SpEL的写法：${表达式}
                el表达式就是通过${key}取出配置文件中的value，也是键值对的一种取值方式  
                
##用于改变作用范围的：
###相当于:<bean id="" class="" scope="">   
        
7.  @Scope
        作用：用于指定bean的作用范围
        属性：
        value：指定范围的取值。常用取值：singleton prototype
        
##和生命周期相关的:(了解)
###相当于:<bean id="" class="" init-method="" destroy-method="" />
他们的作用就和在bean标签中使用init-method和destroy-methode的作用是一样的

8.   @PreDestroy
         作用：用于指定销毁方法
     @PostConstruct
         作用：用于指定初始化方法
         
         
**#写到此处，基于注解的 IoC 配置已经完成，但是发现了一个问题:我们依然离不开 spring的xml配置文件，那么能不能不写这个 bean.xml，所有配置都用注解来实现呢?**
**当然选择哪种配置的原则是简化开发和配置方便，而非追求某种技术。**

##day02_02account_xmlioc
可以发现，之所以现在离不开 xml 配置文件，是因为我们有一句很关键的配置:
<!-- 告知spring框架在，读取配置文件，创建容器时，扫描注解，依据注解创建对象，并存入容器中 --> 
`<context:component-scan base-package="com.wangxs"></context:component-scan> `
如果他要也能用注解配置，那么我们就离脱离 xml 文件又进了一步。

另外，数据源和QueryRunner的配置也需要靠注解来实现。
`    <!--配置QueryRunner-->
    <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
        <!--注入数据源-->
        <constructor-arg name="ds" ref="dataSource"></constructor-arg>
    </bean>
    <!-- 配置数据源 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!--连接数据库的必备信息-->
        <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/eesy?useUnicode=true&amp;characterEncoding=utf8"></property>
        <property name="user" value="root"></property>
        <property name="password" value="wxs950518"></property>
    </bean>`

_细节：_
queryRunner也需要在beam这个Spring配置文件里注入，
注入时，数据源可以实现配置，并且把连接数据库的信息注入进来，如果queryRunner是单例对象当面对多个dao使用时有可能出现线程的安全问题。

**补充：QueryRunner是一个封装了jdbc的一个工具类,执行数据库操作的。**

现在service和dao都配到dao的配置文件中，可以使用Spring的ioc来实现。


