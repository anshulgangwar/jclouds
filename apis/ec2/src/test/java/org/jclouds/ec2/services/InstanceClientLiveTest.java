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

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code EC2Client}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "InstanceClientLiveTest")
public class InstanceClientLiveTest extends BaseComputeServiceContextLiveTest {
    @Resource
    protected Logger logger = Logger.NULL;

    private RetryablePredicate<RunningInstance> runningTester;
    private String defaultZone;
    private String imageId;
    private EC2Client ec2Client;
    private InstanceClient client;
    private String regionId = "AmazonEC2";
    private RunningInstance instance;
    private String instanceId;


    public InstanceClientLiveTest() {
        provider = "ec2";
    }


    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        super.setupContext();
        ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
        client = ec2Client.getInstanceServices();
        runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(ec2Client), 600, 5,
                TimeUnit.SECONDS);
        Set<AvailabilityZoneInfo> allResults = ec2Client.getAvailabilityZoneAndRegionServices().describeAvailabilityZonesInRegion(null);
        allResults.iterator().next();
        defaultZone = allResults.iterator().next().getZone();
        Set<? extends Image> allImageResults = ec2Client.getAMIServices().describeImagesInRegion(null);
        assertNotNull(allImageResults);
        assert allImageResults.size() >= 1 : allImageResults.size();
        Iterator<? extends Image> iterator = allImageResults.iterator();
        imageId = iterator.next().getId();
    }

    @Test
    void testDescribeInstances() {

        Set<? extends Reservation<? extends RunningInstance>> allResults = client.describeInstancesInRegion(regionId);
        assertNotNull(allResults);
        assert allResults.size() >= 0 : allResults.size();

    }

    @Test
    void testRunInstance() {
        try {
            logger.error(" instance error anshul 21");
            Reservation<? extends RunningInstance> runningInstances = ec2Client.getInstanceServices().runInstancesInRegion(
                    regionId, null, imageId, 1, 1);
            logger.error(" instance error anshul 22 " + runningInstances);
            instance = getOnlyElement(concat(runningInstances));
            logger.error(" instance error anshul 23");
            instanceId = instance.getId();
            logger.error(" instance error anshul 3");
            assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
            logger.error(" instance error anshul 4");
            instance = (RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices().describeInstancesInRegion(regionId,
                    instanceId))));
        } catch (Exception e) {
            if (instanceId != null) {
                ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
            }
        }

    }

    @Test(dependsOnMethods = "testRunInstance")
    void testRebootInstance() {
        logger.error(" yahan reboot error 1");
        if (instanceId != null) {
            logger.error(" yahan reboot error 2");
            client.rebootInstancesInRegion(regionId, instanceId);
            logger.error(" yahan reboot error 3");
        }
    }

    @Test(dependsOnMethods = "testRebootInstance")
    void testStopInstances() {
       client.stopInstancesInRegion(regionId,false,instanceId);
    }

    @Test(dependsOnMethods = "testStopInstances")
    void testStartInstances() {
       client.startInstancesInRegion(regionId,instanceId);
    }

    @Test(dependsOnMethods = "testStartInstances")
    void testTerminateInstances() {
        client.terminateInstancesInRegion(regionId,instanceId);
    }


}
