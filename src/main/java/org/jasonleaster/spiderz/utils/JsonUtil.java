package org.jasonleaster.spiderz.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: jasonleaster
 * Date  : 2017/5/10
 * Email : jasonleaster@gmail.com
 */
public class JsonUtil {

    private final static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object)
    {
        String value = "";
        try {
            value = mapper.writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            logger.error("Failed to transform an object into json string");
        }
        return value;
    }

    public static Object toObject(String jsonStr, Class clazz)
    {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.error("Failed to transform an json string into object");
        }
        return null;
    }
}
