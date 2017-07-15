package org.jasonleaster.spiderz.message;

import java.util.ArrayList;
import java.util.List;
import org.jasonleaster.spiderz.message.model.MessageModel;
import org.jasonleaster.spiderz.utils.SpringContextUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: jasonleaster
 * Date  : 2017/7/15
 * Email : jasonleaster@gmail.com
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/ApplicationContext.xml"})
public class IMessageHandlerTest {

    private IMessageHandler messageHandler;

    private String defaultQueueName = "testQueue";

    @Before
    public void setUp() throws Exception {
        messageHandler = SpringContextUtils.getBean("messageHandler", IMessageHandler.class);
    }

    @Test
    public void queueMessages() throws Exception {
        String testMessage = "testMessage";
        List<MessageModel> messages = new ArrayList<>();
        messages.add(new MessageModel(testMessage));
        messageHandler.queueMessages(defaultQueueName, messages);
    }

    @Test
    public void dequeueMessage() throws Exception {

    }

    @Test
    public void dequeueMessages() throws Exception {

    }

}