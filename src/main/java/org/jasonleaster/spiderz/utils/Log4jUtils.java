package org.jasonleaster.spiderz.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;

/**
 * Author: jasonleaster
 * Date  : 2017/6/19
 * Email : jasonleaster@gmail.com
 * Description:
 *    主要任务就是从 /config/log4j.properteis 加载配置初始化 log4j
 */
public class Log4jUtils {

    public static synchronized void InitLog4jConfig() {

        // 从配置文件中读取配置信息
        Properties props = Resources.getResourceAsProperties("log4j.properties");

        PropertyConfigurator.configure(props);//装入log4j配置信息
    }
}
