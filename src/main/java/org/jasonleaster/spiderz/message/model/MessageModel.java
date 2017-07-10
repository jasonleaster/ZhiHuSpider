package org.jasonleaster.spiderz.message.model;

import lombok.Data;

/**
 * Author: jasonleaster
 * Date  : 2017/6/20
 * Email : jasonleaster@gmail.com
 * Description:
 */
@Data
public class MessageModel {

    /**
     * 消息id
     */
    private long messageId;

    /**
     * 消息内容
     */
    private String message;
}
