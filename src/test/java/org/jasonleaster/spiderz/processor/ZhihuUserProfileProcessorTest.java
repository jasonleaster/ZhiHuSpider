package org.jasonleaster.spiderz.processor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jasonleaster.spiderz.model.UserProfileInfo;
import org.jasonleaster.spiderz.service.UserProfileInfoService;
import org.jasonleaster.spiderz.utils.JsonUtil;
import org.jasonleaster.spiderz.utils.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: jasonleaster
 * Date  : 2017/6/10
 * Email : jasonleaster@gmail.com
 * Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/ApplicationContext.xml"})
public class ZhihuUserProfileProcessorTest {

    @Autowired
    private ZhihuUserProfileProcessor profileProcessor;

    @Autowired
    private UserProfileInfoService userProfileInfoService;

    @Test
    public void process() throws Exception {

        try {
            File inputFile = Resources.getResourceAsFile("userProfileDemo.json");
            FileInputStream fis = new FileInputStream(inputFile);
            byte[] data = new byte[(int) inputFile.length()];
            fis.read(data);
            fis.close();

            String jsonStr = new String(data, "utf-8");
            UserProfileInfo userProfileInfo = (UserProfileInfo) JsonUtil.toObject(jsonStr, UserProfileInfo.class);

            if (userProfileInfo == null) {
                System.out.println("Error");
                return;
            }

            List<UserProfileInfo> userProfileInfos = new ArrayList<>();
            userProfileInfos.add(userProfileInfo);
            userProfileInfoService.saveUsersProfileInfo(userProfileInfos);

        } catch (IOException e){

        }
    }

}