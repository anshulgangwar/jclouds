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
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code EC2Client}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "InstanceClientLiveTest")
public class InstanceClientLiveTest extends BaseComputeServiceContextLiveTest {
   protected org.jclouds.predicates.RetryablePredicate<org.jclouds.ec2.domain.RunningInstance> runningTester;
   protected String defaultZone;
   protected String imageId;
   protected String region;
   private org.jclouds.ec2.domain.RunningInstance instance;
   private String instanceId;

   public InstanceClientLiveTest() {
      provider = "ec2";
   }

   protected EC2Client ec2Client;
   protected InstanceClient client;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
      client = ec2Client.getInstanceServices();
      runningTester = new org.jclouds.predicates.RetryablePredicate<org.jclouds.ec2.domain.RunningInstance>(new org.jclouds.ec2.predicates.InstanceStateRunning(ec2Client), 900, 5,
              java.util.concurrent.TimeUnit.SECONDS);
      Set<org.jclouds.ec2.domain.AvailabilityZoneInfo> allResults = ec2Client.getAvailabilityZoneAndRegionServices()
              .describeAvailabilityZonesInRegion(region);
      allResults.iterator().next();
      defaultZone = allResults.iterator().next().getZone();
      Set<? extends org.jclouds.ec2.domain.Image> allImageResults = ec2Client.getAMIServices().describeImagesInRegion(region);
      assertNotNull(allImageResults);
      assert allImageResults.size() >= 1 : allImageResults.size();
      java.util.Iterator<? extends org.jclouds.ec2.domain.Image> iterator = allImageResults.iterator();
      imageId = iterator.next().getId();
   }

   protected void setRegion(){
      region = "AmazonEC2";
   }

   @Test
   public void testDescribeInstances() {
      for (String region : ec2Client.getConfiguredRegions()) {
         Set<? extends Reservation<? extends RunningInstance>> allResults = client.describeInstancesInRegion(region);
         assertNotNull(allResults);
         assert allResults.size() >= 0 : allResults.size();
      }
   }

   @Test
   void testRunInstance() {
      Reservation<? extends RunningInstance> runningInstances = ec2Client.getInstanceServices()
              .runInstancesInRegion(region, defaultZone, imageId, 1, 1);
      instance = getOnlyElement(concat(runningInstances));
      instanceId = instance.getId();
      assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
      instance = (RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices()
              .describeInstancesInRegion(region, instanceId))));
   }

   @Test
   void testRunInstanceAsType() {
      String type = "m1.small";
      Reservation<? extends RunningInstance> runningInstances = client.runInstancesInRegion(region,
              defaultZone, imageId, 1, 1, new org.jclouds.ec2.options.RunInstancesOptions().asType(type));
      RunningInstance instance1 = getOnlyElement(concat(runningInstances));
      String instanceId1 = instance1.getId();
      assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
      client.terminateInstancesInRegion(region, instanceId1);
      String resultType = client.getInstanceTypeForInstanceInRegion(region, instanceId1);
      assertEquals(resultType, type, " instance type mismatch ");
   }
    /* Sailaja Mada - This is the testcase to Deploy a VM with an Invalid InstanceType */

    @Test
    void testRunInstanceAsInvalidType() {
        String type = "Invalidsmall";
        Reservation<? extends RunningInstance> runningInstances = client.runInstancesInRegion(region,
                defaultZone, imageId, 1, 1, new org.jclouds.ec2.options.RunInstancesOptions().asType(type));
        RunningInstance instance1 = getOnlyElement(concat(runningInstances));
        String instanceId1 = instance1.getId();
        assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
        client.terminateInstancesInRegion(region, instanceId1);
        String resultType = client.getInstanceTypeForInstanceInRegion(region, instanceId1);
        assertEquals(resultType, type, " instance type mismatch ");
    }



    /* Sailaja Mada - This is the testcase to Create a keypair using create_key_pair <key> */

    @Test
    void testCreateKeyPair() {
        String keypair1 = "jcloudsInstanceKey2";
        org.jclouds.ec2.domain.KeyPair keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, "jcloudsInstanceKey2");
        String keyPairname = keyPair.getKeyName();
        assertEquals(keyPair.getKeyName(), keypair1, " KeyPairs did not match ");
          }

    /* Sailaja Mada - This is the testcase to Create a keypair using create_key_pair <key> */

  /*  @Test(dependsOnMethods = "testCreateKeyPair")
    void testDeleteKeyPair() {
        org.jclouds.ec2.domain.KeyPair keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, "jcloudsInstanceKey2");
        String keyPairname = keyPair.getKeyName();
        ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
    }  */



   /*  @Test
    void testRunInstanceWithImageId() {
        Reservation<? extends RunningInstance> runningInstances = client.runInstancesInRegion(imageId,new org.jclouds.ec2.options.RunInstancesOptions().asImageId(imageId));
        RunningInstance instance1 = getOnlyElement(concat(runningInstances));
        String instanceId1 = instance1.getId();
        assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
        client.terminateInstancesInRegion(region, instanceId1);
     }     */

   /*@Test
void testRunInstanceWithSecurityGroup() {
String group1 = "jcloudsInstanceGroup1";
String group2 = "jcloudsInstanceGroup2";
ec2Client.getSecurityGroupServices().createSecurityGroupInRegion(region, group1, "For instances 1");
ec2Client.getSecurityGroupServices().createSecurityGroupInRegion(region, group2, "For instances 2");
Reservation<? extends RunningInstance> runningInstances = client.runInstancesInRegion(region,
defaultZone, imageId, 1, 1, new RunInstancesOptions().withSecurityGroups(group1, group2));
RunningInstance instance1 = getOnlyElement(concat(runningInstances));
String instanceId1 = instance1.getId();
assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
assertTrue(instance1.getGroupNames().contains(group1), "group 1 missing");
assertTrue(instance1.getGroupNames().contains(group2), "group 2 missing");
assertFalse(instance1.getGroupNames().contains(defaultZone), "deployed part of default security group");
client.terminateInstancesInRegion(region, instanceId1);
ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, group1);
ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, group2);
}*/

   @Test
   void testRunInstanceWithKeyPair() {
      org.jclouds.ec2.domain.KeyPair keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, "jcloudsInstanceKey1");
      Reservation<? extends RunningInstance> runningInstances = client.runInstancesInRegion(region,
              defaultZone, imageId, 1, 1, new org.jclouds.ec2.options.RunInstancesOptions().withKeyName(keyPair.getKeyName()));
      RunningInstance instance1 = getOnlyElement(concat(runningInstances));
      String instanceId1 = instance1.getId();
      assertTrue(runningTester.apply(instance1), instanceId1 + "didn't achieve the state running!");
      client.terminateInstancesInRegion(region, instanceId1);
      ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
   }

   @Test(dependsOnMethods = "testRunInstance")
   void testRebootInstance() {
      client.rebootInstancesInRegion(region, instanceId);
   }

   /*@Test(dependsOnMethods = "testRunInstance")
void testGetInstanceTypeForInstanceInRegion() {
String type = client.getInstanceTypeForInstanceInRegion(region, instanceId);
assertEquals(type,"m1.small"," instance type mismatch ");
}*/

   @Test(dependsOnMethods = "testRebootInstance")
   void testStopInstances() {
      Set<? extends org.jclouds.ec2.domain.InstanceStateChange> allresults = client.stopInstancesInRegion(region, false,
              instanceId);
      assertNotNull(allresults);
      assert allresults.size() == 1 : allresults.size();
      org.jclouds.ec2.domain.InstanceStateChange result = allresults.iterator().next();
      assertEquals(result.getInstanceId(), instanceId);
      assertEquals(result.getPreviousState(), org.jclouds.ec2.domain.InstanceState.RUNNING, " previous state not running ");
      org.jclouds.ec2.domain.InstanceState currentState = result.getCurrentState();
      assertTrue(currentState.equals(org.jclouds.ec2.domain.InstanceState.STOPPED) || currentState.equals(org.jclouds
              .ec2.domain.InstanceState.STOPPING),
              " current state not stopped or stopping ");
   }

   @Test(dependsOnMethods = "testStopInstances")
   void testStartInstances() {
      Set<? extends org.jclouds.ec2.domain.InstanceStateChange> allresults = client.startInstancesInRegion(region, instanceId);
      assertNotNull(allresults);
      assert allresults.size() == 1 : allresults.size();
      org.jclouds.ec2.domain.InstanceStateChange result = allresults.iterator().next();
      assertEquals(result.getInstanceId(), instanceId);
      assertEquals(result.getPreviousState(), org.jclouds.ec2.domain.InstanceState.STOPPED, " previous state not stopped ");
      org.jclouds.ec2.domain.InstanceState currentState = result.getCurrentState();
      assertTrue(currentState.equals(org.jclouds.ec2.domain.InstanceState.RUNNING) || currentState.equals(org.jclouds
              .ec2.domain.InstanceState.PENDING),
              " current state not stopped or stopping ");
   }

   @Test(dependsOnMethods = "testStartInstances")
   void testTerminateInstances() {
      Set<? extends org.jclouds.ec2.domain.InstanceStateChange> allresults = client.terminateInstancesInRegion(region, instanceId);
      assertNotNull(allresults);
      assert allresults.size() == 1 : allresults.size();
      org.jclouds.ec2.domain.InstanceStateChange result = allresults.iterator().next();
      assertEquals(result.getInstanceId(), instanceId);
      assertEquals(result.getPreviousState(), org.jclouds.ec2.domain.InstanceState.RUNNING, " previous state not running ");
      org.jclouds.ec2.domain.InstanceState currentState = result.getCurrentState();
      assertTrue(currentState.equals(org.jclouds.ec2.domain.InstanceState.SHUTTING_DOWN) || currentState.equals(org.jclouds.ec2.domain.InstanceState.TERMINATED),
              " current state not shutting down or terminated ");
   }
}
