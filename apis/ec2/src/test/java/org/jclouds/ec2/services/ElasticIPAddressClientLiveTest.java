/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ec2.services;

import com.google.common.collect.Sets;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.*;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code ElasticIPAddressClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ElasticIPAddressClientLiveTest")
public class ElasticIPAddressClientLiveTest extends BaseComputeServiceContextLiveTest {
    @Resource
    protected Logger logger = Logger.NULL;
    private String regionId = "AmazonEC2";
    private String publicIp;
    private String instanceId;
    private String defaultZone;
    private String imageId;
    private RunningInstance instance;
    private RetryablePredicate<RunningInstance> runningTester;


    public ElasticIPAddressClientLiveTest() {
        provider = "ec2";
    }

    private EC2Client ec2Client;
    private ElasticIPAddressClient client;

    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        super.setupContext();
        ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
        client = ec2Client.getElasticIPAddressServices();
        runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(ec2Client), 900, 5,
                TimeUnit.SECONDS);
        Set<AvailabilityZoneInfo> allResults = ec2Client.getAvailabilityZoneAndRegionServices().describeAvailabilityZonesInRegion(null);
        allResults.iterator().next();
        defaultZone = allResults.iterator().next().getZone();
        Set<? extends Image> allImageResults = ec2Client.getAMIServices().describeImagesInRegion(null);
        assertNotNull(allImageResults);
        assert allImageResults.size() >= 1 : allImageResults.size();
        Iterator<? extends Image> iterator = allImageResults.iterator();
        imageId = iterator.next().getId();
        if (imageId != null) {
            runInstance();
        }
    }


    private void runInstance() {

/* try {*/

        logger.error(" snapshot error anshul 21");
        Reservation<? extends RunningInstance> runningInstances = ec2Client.getInstanceServices().runInstancesInRegion(
                regionId, defaultZone, imageId, 1, 1);
        logger.error(" snapshot error anshul 22 " + runningInstances);
        instance = getOnlyElement(concat(runningInstances));
        logger.error(" snapshot error anshul 23");
        instanceId = instance.getId();
        logger.error(" snapshot error anshul 3");
        assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
        logger.error(" snapshot error anshul 4");


        instance = (RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices().describeInstancesInRegion(regionId,
                instanceId))));
        logger.error(" snapshot error anshul 54");


/*} catch (Exception e) {
            if (instanceId != null) {
                ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
            }
        }*/


    }

    @Test
    void testDescribeAddresses() {

        SortedSet<PublicIpInstanceIdPair> allResults = Sets.newTreeSet(client.describeAddressesInRegion(regionId));
        assertNotNull(allResults);
        if (allResults.size() >= 1) {
            PublicIpInstanceIdPair pair = allResults.last();
            SortedSet<PublicIpInstanceIdPair> result = Sets.newTreeSet(client.describeAddressesInRegion(regionId, pair
                    .getPublicIp()));
            assertNotNull(result);
            PublicIpInstanceIdPair compare = result.last();
            assertEquals(compare, pair);
        }

    }

    @Test
    void testAllocateAddressInRegion() {
        publicIp = client.allocateAddressInRegion(regionId);
    }

    @Test(dependsOnMethods = "testAllocateAddressInRegion")
    void testAssociateAddressInRegion() {
        client.associateAddressInRegion(regionId, publicIp, instanceId);
    }

    @Test(dependsOnMethods = "testAssociateAddressInRegion")
    void testDisassociateAddressInRegion() {
        client.disassociateAddressInRegion(regionId,publicIp);
    }

    @Test(dependsOnMethods = "testDisassociateAddressInRegion")
    void testReleaseAddressInRegion() {
        client.releaseAddressInRegion(regionId,publicIp);
    }

}
