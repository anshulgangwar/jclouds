package org.jclouds.cloudstack.query.ec2;

import org.jclouds.compute.internal.BaseComputeServiceApiMetadataTest;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/17/12
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "unit", testName = "CloudStackQueryEC2ApiMetadataTest")
public class CloudStackQueryEC2ApiMetadataTest extends BaseComputeServiceApiMetadataTest {

    public CloudStackQueryEC2ApiMetadataTest() {
        super(new CloudStackQueryEC2ApiMetadata());
    }
}
