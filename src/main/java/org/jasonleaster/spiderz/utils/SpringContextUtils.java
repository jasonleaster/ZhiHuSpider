package org.jasonleaster.spiderz.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Author: jasonleaster
 * Date  : 2017/6/9
 * Email : jasonleaster@gmail.com
 * Description:
 */
public class SpringContextUtils {

    private static final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/ApplicationContext.xml");

    public static ApplicationContext getContext(){
        return ctx;
    }

    public static Object getBean(String beanName){
        return ctx.getBean(beanName);
    }

}
