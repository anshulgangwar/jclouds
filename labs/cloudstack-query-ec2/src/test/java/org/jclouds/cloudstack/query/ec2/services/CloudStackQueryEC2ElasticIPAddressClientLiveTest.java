package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.ec2.services.ElasticIPAddressClientLiveTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2ElasticIPAddressClientLiveTest")
public class CloudStackQueryEC2ElasticIPAddressClientLiveTest extends ElasticIPAddressClientLiveTest {
    public CloudStackQueryEC2ElasticIPAddressClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }
}
