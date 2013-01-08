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

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
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
   protected RetryablePredicate<RunningInstance> runningTester;
   protected String defaultRegion;
   protected String defaultZone;
   protected RunningInstance instance;
   protected String instanceId;
   protected String publicIp;


   public ElasticIPAddressClientLiveTest() {
      provider = "ec2";
   }

   private EC2Client ec2Client;
   private ElasticIPAddressClient client;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
      client = ec2Client.getElasticIPAddressServices();
      runningTester = new RetryablePredicate<RunningInstance>(new org.jclouds.ec2.predicates.InstanceStateRunning
              (ec2Client), 900, 5, TimeUnit.SECONDS);
      AvailabilityZoneInfo info = Iterables.get(ec2Client.getAvailabilityZoneAndRegionServices()
              .describeAvailabilityZonesInRegion(defaultRegion), 0);
      defaultRegion = checkNotNull(Strings.emptyToNull(info.getRegion()), "region of " + info);
      defaultZone = checkNotNull(Strings.emptyToNull(info.getZone()), "zone of " + info);
   }

   private void runInstance() {
      String imageId = ec2Client.getAMIServices().describeImagesInRegion(defaultRegion).iterator().next().getId();
      if (imageId != null) {
         Reservation<? extends RunningInstance> runningInstances = ec2Client.getInstanceServices().runInstancesInRegion
                 (defaultRegion, defaultZone, imageId, 1, 1);
         instance = getOnlyElement(concat(runningInstances));
         instanceId = instance.getId();
         assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
         instance = (RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices().describeInstancesInRegion
                 (defaultRegion, instanceId))));
      }
   }

   @Test
   void testDescribeAddresses() {
      for (String region : ec2Client.getConfiguredRegions()) {
         SortedSet<PublicIpInstanceIdPair> allResults = Sets.newTreeSet(client.describeAddressesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            PublicIpInstanceIdPair pair = allResults.last();
            SortedSet<PublicIpInstanceIdPair> result = Sets.newTreeSet(client.describeAddressesInRegion(region, pair
                    .getPublicIp()));
            assertNotNull(result);
            PublicIpInstanceIdPair compare = result.last();
            assertEquals(compare, pair);
         }
      }
   }

   @Test
   void testAllocateAddressInRegion() {
      publicIp = client.allocateAddressInRegion(defaultRegion);
   }

   @Test(dependsOnMethods = "testAllocateAddressInRegion")
   void testAssociateAddressInRegion() {
      runInstance();
      client.associateAddressInRegion(defaultRegion, publicIp, instanceId);
   }

   @Test(dependsOnMethods = "testAssociateAddressInRegion")
   void testDisassociateAddressInRegion() {
      client.disassociateAddressInRegion(defaultRegion, publicIp);
   }

   @Test(dependsOnMethods = "testDisassociateAddressInRegion")
   void testReleaseAddressInRegion() {
      client.releaseAddressInRegion(defaultRegion, publicIp);
   }

   @Override
   @AfterClass(groups = {"integration", "live"})
   protected void tearDownContext() {
      if (instanceId != null) {
         ec2Client.getInstanceServices().terminateInstancesInRegion(defaultRegion, instanceId);
      }
      super.tearDownContext();
   }
}
