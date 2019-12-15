package com.wangxs.cglib;

import com.wangxs.proxy.IProducer;
import com.wangxs.proxy.Producer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 模拟一个消费者
 */
public class Client {
    public static void main(String[] args) {
        final Producer producer = new Producer(); //匿名内部类访问外部成员变量时，要求外部成员变量是最终的，要用final修饰

        /**
         * 动态代理：
         *  特点：字节码随用随创建，随用随加载
         *  作用：不修改源码的基础上对方法（生产商）增强
         *  分类：
         *      基于接口的动态代理
         *      基于子类的动态代理
         *  基于接口的动态代理：
         *      涉及的类：Proxy
         *      提供者：JDK官方
         *  如何创建代理对象：
         *      使用Proxy类中的newProxyInstance方法
         *  创建代理对象的要求：
         *      被代理类最少实现一个接口，如果没有则不能使用
         *  newProxyInstance方法的参数：
         *      ClassLoader:
         *          它是用于加载代理对象字节码的。和被代理对象（工厂）使用相同的的类加载器。是固定写法。
         *      Class[]: 字节码数组
         *          它是用于让代理对象和被代理对象有相同方法。
         *      InvocationHandler:用于提供增强的代码
         *          它是让我们写如何代理。我们一般都是写一个该接口的实现类，通常情况下都是匿名内部类，但是不必须。
         *          此接口的实现类都是谁用谁写。
         */


//        //创建动态代理，用proxyProducer来接收
//        IProducer proxyProducer = (IProducer) Proxy.newProxyInstance(producer.getClass().getClassLoader(),
//                producer.getClass().getInterfaces(),
//                new InvocationHandler() {
//                    /**
//                     * 作用：执行被代理对象的任何接口方法都会经过该方法，有拦截的作用
//                     * 方法参数的含义：
//                     * @param proxy ：代理对象的引用
//                     * @param method：当前执行的方法
//                     * @param args  ：当前执行方法所需的参数
//                     * @return      ：和被代理对象方法有相同的返回值
//                     * @throws Throwable
//                     */
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                        //提供增强代码
//                        Object returnValue = null;
//                        //1.获取方法执行的参数
//                        Float money = (Float) args[0];//我们当前的方法只有一个参数
//                        //2.判断当前方法是不是销售
//                        if ("saleProduct".equals(method.getName())) {
//                            returnValue = method.invoke(producer, money*0.8F);
//                        }
//                        return returnValue;
//                    }
//                });
//        proxyProducer.saleProduct(10000f);// 给厂商钱

        /**
         * 动态代理：
         *  特点：字节码随用随创建，随用随加载
         *  作用：不修改源码的基础上对方法（生产商）增强
         *  基于子类的动态代理：
         *      涉及的类：Proxy
         *      提供者：第三方cglib库
         *  如何创建代理对象：
         *      使用Enhancer类中的create方法
         *  创建代理对象的要求：
         *      被代理类不能是最终类（最终类值得是被final修饰的类，不可继承的类）
         *  create方法的参数：
         *      Class:字节码
         *          它是用于指定被代理对象的字节码。
         *
         *      Callback：用于提供增强的代码
         *          它是让我们写如何代理，我们一般都是些一个该接口的实现类，通常情况下都是匿名内部类，但不是必须的。
         *          此接口的实现类都是谁用谁写。
         *          我们一般写的都是该接口的子接口实现类：MethodInterceptor(方法拦截)
         *
         */

            Producer cglibProducer = (Producer)Enhancer.create(producer.getClass(), new MethodInterceptor() {

                /**
                 * 执行被代理对象的任何方法都会经过该方法
                 * @param proxy
                 * @param method
                 * @param args
                 *          以上三个参数和基于接口的动态代理中invoke方法的参数是一样的
                 * @param methodProxy ：当前执行方法的代理对象
                 * @return
                 * @throws Throwable
                 */
                @Override
                public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                    //提供增强代码
                    Object returnValue = null;
                    //1.获取方法执行的参数
                    Float money = (Float) args[0];//我们当前的方法只有一个参数
                    //2.判断当前方法是不是销售
                    if ("saleProduct".equals(method.getName())) {
                        returnValue = method.invoke(producer, money*0.8F);
                    }
                    return returnValue;
                }
            });
            cglibProducer.saleProduct(12000F);
    }
}