package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.ec2.services.KeyPairClientLiveTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2KeyPairClientLiveTest")
public class CloudStackQueryEC2KeyPairClientLiveTest extends KeyPairClientLiveTest {
    public CloudStackQueryEC2KeyPairClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }
}
