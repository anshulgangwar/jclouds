package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.ec2.services.InstanceClientLiveTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2InstanceClientLiveTest")
public class CloudStackQueryEC2InstanceClientLiveTest extends InstanceClientLiveTest {
    public CloudStackQueryEC2InstanceClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }
}
