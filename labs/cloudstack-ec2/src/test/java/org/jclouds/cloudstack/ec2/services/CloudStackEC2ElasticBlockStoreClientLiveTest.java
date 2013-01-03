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

import org.jclouds.ec2.services.ElasticBlockStoreClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2ElasticBlockStoreClientLiveTest")
public class CloudStackEC2ElasticBlockStoreClientLiveTest extends ElasticBlockStoreClientLiveTest {

   public CloudStackEC2ElasticBlockStoreClientLiveTest() {
      provider = "cloudstack-ec2";
   }

   /*@Override
   @Test(dependsOnMethods = "testAttachVolumeInRegion")
   public void testCreateSnapshotInRegion() {
      super.testCreateSnapshotInRegion();
   }
*/
   @Override
   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testGetCreateVolumePermissionForSnapshot() {
      throw new org.testng.SkipException("Not supported in CloudStack");
   }

   /*@Override
   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
      throw new org.testng.SkipException("Not supported in CloudStack");
   }*/
}