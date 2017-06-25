package org.jasonleaster.spiderz.message.dao;

import java.util.List;
import org.jasonleaster.spiderz.message.model.MessageModel;
import org.springframework.stereotype.Repository;

/**
 * Author: jasonleaster
 * Date  : 2017/6/20
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Repository("messageHandlerDAO")
public interface IMessageHandlerDAO {

    /**
     * 创建一个数据库表，用于为消息队列 #queueName 存放消息
     */
    int createMessageQueue(String queueName);

    /**
     * 插入消息
     */
    int insertMessages(String queueName, List<String> messages);

    /**
     * 删除消息
     * 两种不同的入参，本质上都是通过消息id删除消息
     */
    int deleteMessages(String queueName, List<MessageModel> messages);
    int deleteMessagesById(String queueName, List<Integer> messageIds);

    /**
     * 获取counts条消息
     * @param counts 消息的数目
     * @return 消息
     */
    List<String> selectMessages(String queueName, int counts);
}
