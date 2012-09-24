package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.ec2.services.ElasticBlockStoreClientLiveTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */

@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2ElasticBlockStoreClientLiveTest")
public class CloudStackQueryEC2ElasticBlockStoreClientLiveTest extends ElasticBlockStoreClientLiveTest {
    public CloudStackQueryEC2ElasticBlockStoreClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }

}
