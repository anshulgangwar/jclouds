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

import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.services.AMIClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2AMIClientLiveTest")
public class CloudStackEC2AMIClientLiveTest extends AMIClientLiveTest {
   private String defaultZone = null;
   private RunningInstance instance;
   private String instanceId;
   private String createdImageId;

   public CloudStackEC2AMIClientLiveTest() {
      provider = "cloudstack-ec2";
   }

   @Override
   protected Properties setupProperties() {
      return super.setupProperties();
   }

   @Override
   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setupContext();
      if (imageId != null) {
         runInstance();
      }
   }

   private void runInstance() {
      Reservation<? extends RunningInstance> runningInstances = ec2Client.getInstanceServices()
              .runInstancesInRegion(
                      regionId, defaultZone, imageId, 1, 1);
      instance = getOnlyElement(concat(runningInstances));
      instanceId = instance.getId();
      assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
      instance = (RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices()
              .describeInstancesInRegion(regionId,
                      instanceId))));
   }

   public void verifyDescribeImagesResult(String region, Set<? extends Image> allResults) {
      assertNotNull(allResults);
      assert allResults.size() >= 1 : allResults.size();
      Iterator<? extends Image> iterator = allResults.iterator();
      String id1 = iterator.next().getId();
      Set<? extends Image> twoResults = client.describeImagesInRegion(null, imageIds(id1));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      iterator = twoResults.iterator();
      assertEquals(iterator.next().getId(), id1);
   }

   @Test
   public void testCreateImage() {
      ec2Client.getInstanceServices().stopInstancesInRegion(regionId, false, instanceId);
      createdImageId = client.createImageInRegion(regionId, "jclouds-cloudstack-ec2", instanceId);
   }

   @Test(dependsOnMethods = "testCreateImage")
   void testDeregisterImageInRegion() {
      client.deregisterImageInRegion(regionId, createdImageId);
   }



   @Override
   @Test
   public void testCreateAndListEBSBackedImage() throws Exception {
      throw new org.testng.SkipException("it is done differently in CloudStack ");
   }

   @Override
   public void testGetLaunchPermissionForImage() {
      super.testGetLaunchPermissionForImage();
   }

   @Override
   @AfterClass(groups = {"integration", "live"})
   protected void tearDownContext() {
      for (String imageId : imagesToDeregister){
         client.deregisterImageInRegion(regionId, imageId);
      }
      for (String snapshotId : snapshotsToDelete){
         ec2Client.getElasticBlockStoreServices().deleteSnapshotInRegion(regionId, snapshotId);
      }
      if (instanceId != null) {
         ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
      }
      super.tearDownContext();
   }
}