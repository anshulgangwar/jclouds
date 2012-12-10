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
package org.jclouds.cloudstack.ec2.services;

import org.jclouds.cloudstack.ec2.CloudStackEC2ApiMetadata;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.ec2.domain.*;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.services.InstanceClientLiveTest;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.*;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackInstanceClientLiveTest")
public class CloudStackEC2InstanceClientLiveTest extends InstanceClientLiveTest {
   private CloudStackEC2Client cloudstackEc2Client;
   private RetryablePredicate<RunningInstance> runningTester;
   private CloudStackEC2InstanceClient cloudstackClient;
   private String cloudstackDefaultZone;
   private String imageId;
   private String regionId = "AmazonEC2";
   private RunningInstance instance;
   private String instanceId;


   public CloudStackEC2InstanceClientLiveTest() {
      provider = "cloudstack-ec2";
   }

   @Override
   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      initializeContext();
      cloudstackEc2Client = view.unwrap(CloudStackEC2ApiMetadata.CONTEXT_TOKEN).getApi();
      runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(cloudstackEc2Client), 900, 5,
              TimeUnit.SECONDS);
      cloudstackClient = cloudstackEc2Client.getInstanceServices();
      Set<AvailabilityZoneInfo> allResults = cloudstackEc2Client.getAvailabilityZoneAndRegionServices()
              .describeAvailabilityZonesInRegion(regionId);
      allResults.iterator().next();
      cloudstackDefaultZone = allResults.iterator().next().getZone();
      Set<? extends Image> allImageResults = cloudstackEc2Client.getAMIServices().describeImagesInRegion(regionId);
      assertNotNull(allImageResults);
      assert allImageResults.size() >= 1 : allImageResults.size();
      Iterator<? extends Image> iterator = allImageResults.iterator();
      imageId = iterator.next().getId();
   }

   @Test
   void testDescribeInstances() {
      Set<? extends Reservation<? extends RunningInstance>> allResults = cloudstackClient.describeInstancesInRegion
              (regionId);
      assertNotNull(allResults);
      assert allResults.size() >= 0 : allResults.size();
   }

   @Test
   void testRunInstance() {
      Reservation<? extends RunningInstance> runningInstances = cloudstackEc2Client.getInstanceServices()
              .runInstancesInRegion(regionId, cloudstackDefaultZone, imageId, 1, 1);
      instance = getOnlyElement(concat(runningInstances));
      instanceId = instance.getId();
      assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
      instance = (RunningInstance) (getOnlyElement(concat(cloudstackEc2Client.getInstanceServices()
              .describeInstancesInRegion(regionId, instanceId))));
   }

   @Test
   void testRunInstanceAsType() {
      String type = "m1.small";
      Reservation<? extends RunningInstance> runningInstances = cloudstackClient.runInstancesInRegion(regionId,
              cloudstackDefaultZone, imageId, 1, 1, new RunInstancesOptions().asType(type));
      RunningInstance instance1 = getOnlyElement(concat(runningInstances));
      String instanceId1 = instance1.getId();
      assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
      cloudstackClient.terminateInstancesInRegion(regionId, instanceId1);
      String resultType = cloudstackClient.getInstanceTypeForInstanceInRegion(regionId, instanceId1);
      assertEquals(resultType, type, " instance type mismatch ");
   }

   @Test
   void testRunInstanceWithSecurityGroup() {
      String group1 = "jcloudsInstanceGroup1";
      String group2 = "jcloudsInstanceGroup2";
      cloudstackEc2Client.getSecurityGroupServices().createSecurityGroupInRegion(regionId, group1, "For instances 1");
      cloudstackEc2Client.getSecurityGroupServices().createSecurityGroupInRegion(regionId, group2, "For instances 2");
      Reservation<? extends RunningInstance> runningInstances = cloudstackClient.runInstancesInRegion(regionId,
              cloudstackDefaultZone, imageId, 1, 1, new RunInstancesOptions().withSecurityGroups(group1, group2));
      RunningInstance instance1 = getOnlyElement(concat(runningInstances));
      String instanceId1 = instance1.getId();
      assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
      assertTrue(instance1.getGroupNames().contains(group1), "group 1 missing");
      assertTrue(instance1.getGroupNames().contains(group2), "group 2 missing");
      assertFalse(instance1.getGroupNames().contains(cloudstackDefaultZone), "deployed part of default security group");
      cloudstackClient.terminateInstancesInRegion(regionId, instanceId1);
      cloudstackEc2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(regionId, group1);
      cloudstackEc2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(regionId, group2);
   }

   @Test
   void testRunInstanceWithKeyPair() {
      KeyPair keyPair = cloudstackEc2Client.getKeyPairServices().createKeyPairInRegion(regionId, "jcloudsInstanceKey1");
      Reservation<? extends RunningInstance> runningInstances = cloudstackClient.runInstancesInRegion(regionId,
              cloudstackDefaultZone, imageId, 1, 1, new RunInstancesOptions().withKeyName(keyPair.getKeyName()));
      RunningInstance instance1 = getOnlyElement(concat(runningInstances));
      String instanceId1 = instance1.getId();
      assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
      cloudstackClient.terminateInstancesInRegion(regionId, instanceId1);
      cloudstackEc2Client.getKeyPairServices().deleteKeyPairInRegion(regionId, keyPair.getKeyName());
   }


   @Test(dependsOnMethods = "testRunInstance")
   void testRebootInstance() {
      cloudstackClient.rebootInstancesInRegion(regionId, instanceId);
   }

   /*@Test(dependsOnMethods = "testRunInstance")
   void testGetInstanceTypeForInstanceInRegion() {
      String type = cloudstackClient.getInstanceTypeForInstanceInRegion(regionId, instanceId);
      assertEquals(type,"m1.small"," instance type mismatch ");
   }*/

   @Test(dependsOnMethods = "testRebootInstance")
   void testStopInstances() {
      Set<? extends InstanceStateChange> allresults = cloudstackClient.stopInstancesInRegion(regionId, false,
              instanceId);
      assertNotNull(allresults);
      assert allresults.size() == 1 : allresults.size();
      InstanceStateChange result = allresults.iterator().next();
      assertEquals(result.getInstanceId(), instanceId);
      assertEquals(result.getPreviousState(), InstanceState.RUNNING, " previous state not running ");
      assertEquals(result.getCurrentState(), InstanceState.STOPPING, " current state not stopping ");
   }

   @Test(dependsOnMethods = "testStopInstances")
   void testStartInstances() {
      Set<? extends InstanceStateChange> allresults = cloudstackClient.startInstancesInRegion(regionId, instanceId);
      assertNotNull(allresults);
      assert allresults.size() == 1 : allresults.size();
      InstanceStateChange result = allresults.iterator().next();
      assertEquals(result.getInstanceId(), instanceId);
      assertEquals(result.getPreviousState(), InstanceState.STOPPED, " previous state not stopped ");
      assertEquals(result.getCurrentState(), InstanceState.PENDING, " current state not pending ");
   }

   @Test(dependsOnMethods = "testStartInstances")
   void testTerminateInstances() {
      Set<? extends InstanceStateChange> allresults = cloudstackClient.terminateInstancesInRegion(regionId, instanceId);
      assertNotNull(allresults);
      assert allresults.size() == 1 : allresults.size();
      InstanceStateChange result = allresults.iterator().next();
      assertEquals(result.getInstanceId(), instanceId);
      assertEquals(result.getPreviousState(), InstanceState.RUNNING, " previous state not running ");
      assertEquals(result.getCurrentState(), InstanceState.SHUTTING_DOWN, " current state not shutting down ");
   }
}