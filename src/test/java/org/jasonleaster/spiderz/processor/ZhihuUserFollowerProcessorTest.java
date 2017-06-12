package org.jasonleaster.spiderz.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.jasonleaster.spiderz.model.RelationShipPaginationModel;
import org.jasonleaster.spiderz.model.UserRelationship;
import org.jasonleaster.spiderz.service.UserRelationshipService;
import org.jasonleaster.spiderz.url.UrlFactory;
import org.jasonleaster.spiderz.utils.JsonUtil;
import org.jasonleaster.spiderz.utils.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant.Method;

/**
 * Author: jasonleaster
 * Date  : 2017/6/2
 * Email : jasonleaster@gmail.com
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/ApplicationContext.xml"})
public class ZhihuUserFollowerProcessorTest {

    @Autowired
    private ZhihuUserFollowersProcessor processor;

    @Autowired
    private UserRelationshipService relationshipService;

    @Test
    public void test() {
        String urlToken = "Sweets07";
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setUrl(UrlFactory.getRestfulFollowerAPI(urlToken, 0, 20));

        Spider spiderForUserFollowing = Spider.create(processor);

        spiderForUserFollowing.addRequest(request);
        spiderForUserFollowing.thread(1).run();
    }

    @Test
    public void test2(){
        try {
            File inputFile = Resources.getResourceAsFile("userRelationshipDemo.json");
            FileInputStream fis = new FileInputStream(inputFile);
            byte[] data = new byte[(int) inputFile.length()];
            fis.read(data);
            fis.close();

            String jsonStr = new String(data, "utf-8");
            RelationShipPaginationModel relationship = (RelationShipPaginationModel)
                JsonUtil.toObject(jsonStr, RelationShipPaginationModel.class);
            List<UserRelationship> relationships = relationship.getData();

            if (relationships == null) {
                System.out.println("Error");
                return;
            }

            for (UserRelationship iter : relationships){
                iter.setFromUrlToken("Sweets07");
                iter.setFromUserId("2528c0d822893dd5112b57b813ed47e7");
            }

            relationshipService.saveUserRelationships(relationships);
        }catch (IOException e){

        }


    }

}