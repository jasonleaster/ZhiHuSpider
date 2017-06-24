package org.jasonleaster.spiderz.message.model;

/**
 * Author: jasonleaster
 * Date  : 2017/6/20
 * Email : jasonleaster@gmail.com
 * Description:
 */
public class MessageModel {

    private long messageId;

    private String message;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
