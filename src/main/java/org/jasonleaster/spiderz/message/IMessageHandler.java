package org.jasonleaster.spiderz.message;

import java.util.List;

/**
 * Author: jasonleaster
 * Date  : 2017/6/20
 * Email : jasonleaster@gmail.com
 * Description:
 *  消息处理接口
 */
public interface IMessageHandler {

    // 消息入队
    void queueMessages(String queueName, List<String> messages);

    // 消息出队
    String dequeueMessage(String queueName);

    // 消息出队
    List<String> dequeueMessages(String queuename);
}
