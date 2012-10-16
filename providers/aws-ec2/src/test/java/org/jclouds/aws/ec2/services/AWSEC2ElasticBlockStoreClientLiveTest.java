package org.jclouds.aws.ec2.services;

import org.jclouds.ec2.services.ElasticBlockStoreClientLiveTest;
import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/12/12
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "AWSEC2ElasticBlockStoreClientLiveTest")
public class AWSEC2ElasticBlockStoreClientLiveTest extends ElasticBlockStoreClientLiveTest {
    @Resource
    protected Logger logger = Logger.NULL;

    public AWSEC2ElasticBlockStoreClientLiveTest() {
        provider = "aws-ec2";
    }
}
