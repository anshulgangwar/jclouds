package org.jclouds.aws.ec2.services;

import org.jclouds.ec2.services.ElasticIPAddressClientLiveTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/12/12
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "AWSEC2ElasticIPAddressClientLiveTest")
public class AWSEC2ElasticIPAddressClientLiveTest extends ElasticIPAddressClientLiveTest {
    public AWSEC2ElasticIPAddressClientLiveTest() {
        provider = "aws-ec2";
    }
}
