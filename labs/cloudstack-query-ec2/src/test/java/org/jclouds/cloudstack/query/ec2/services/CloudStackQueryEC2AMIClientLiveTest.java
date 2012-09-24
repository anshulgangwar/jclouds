package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.ec2.services.AMIClientLiveTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2AMIClientLiveTest")
public class CloudStackQueryEC2AMIClientLiveTest extends AMIClientLiveTest {
    public CloudStackQueryEC2AMIClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }
}
