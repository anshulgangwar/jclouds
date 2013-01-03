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

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code ElasticBlockStoreClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ElasticBlockStoreClientLiveTest")
public class ElasticBlockStoreClientLiveTest extends BaseComputeServiceContextLiveTest {
   private org.jclouds.ec2.domain.RunningInstance instance;
   private String instanceId;
   private RetryablePredicate<RunningInstance> runningTester;


   public ElasticBlockStoreClientLiveTest() {
      provider = "ec2";
   }

   private EC2Client ec2Client;
   private ElasticBlockStoreClient client;

   private String defaultRegion;
   private String defaultZone;

   private String volumeId;
   private Snapshot snapshot;
   private String imageId;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
      client = ec2Client.getElasticBlockStoreServices();
      runningTester = new RetryablePredicate<org.jclouds.ec2.domain.RunningInstance>(new org.jclouds.ec2.predicates.InstanceStateRunning(ec2Client), 900, 5,
              TimeUnit.SECONDS);
      AvailabilityZoneInfo info = Iterables.get(ec2Client.getAvailabilityZoneAndRegionServices()
            .describeAvailabilityZonesInRegion(defaultRegion), 0);
      defaultRegion = checkNotNull(Strings.emptyToNull(info.getRegion()), "region of " + info);
      defaultZone = checkNotNull(Strings.emptyToNull(info.getZone()), "zone of " + info);
      imageId = ec2Client.getAMIServices().describeImagesInRegion(defaultRegion).iterator().next().getId();
      if (imageId != null) {
         runInstance();
      }
   }

   private void runInstance() {
      org.jclouds.ec2.domain.Reservation<? extends org.jclouds.ec2.domain.RunningInstance> runningInstances = ec2Client.getInstanceServices()
              .runInstancesInRegion(
                      defaultRegion, defaultZone, imageId, 1, 1);
      instance = getOnlyElement(concat(runningInstances));
      instanceId = instance.getId();
      assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
      instance = (org.jclouds.ec2.domain.RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices()
              .describeInstancesInRegion(defaultRegion,
                      instanceId))));
   }


   @Test
   public void testDescribeVolumes() {
      for (String region : ec2Client.getConfiguredRegions()) {
         SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Volume volume = allResults.last();
            SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(region, volume.getId()));
            assertNotNull(result);
            Volume compare = result.last();
            assertEquals(compare, volume);
         }
      }
   }

   @Test
   public void testCreateVolumeInAvailabilityZone() {
      Volume expected = client.createVolumeInAvailabilityZone(defaultZone, 1);
      assertNotNull(expected);
      assertEquals(expected.getAvailabilityZone(), defaultZone);

      this.volumeId = expected.getId();

      Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(defaultRegion, expected.getId()));
      assertNotNull(result);
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getId(), expected.getId());
   }

   @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
   public void testCreateSnapshotInRegion() {
      Snapshot snapshot = client.createSnapshotInRegion(defaultRegion, volumeId);
      Predicate<Snapshot> snapshotted = new RetryablePredicate<Snapshot>(new SnapshotCompleted(client), 600, 10,
            TimeUnit.SECONDS);
      assert snapshotted.apply(snapshot);

      Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(snapshot.getRegion(),
            snapshotIds(snapshot.getId())));

      assertEquals(result.getProgress(), 100);
      this.snapshot = result;
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testCreateVolumeFromSnapshotInAvailabilityZone() {
      Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(defaultZone, snapshot.getId());
      assertNotNull(volume);

      Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(client), 600, 10,
            TimeUnit.SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(snapshot.getRegion(), volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), defaultZone);
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(snapshot.getRegion(), volume.getId());
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
      Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(defaultZone, 2, snapshot.getId());
      assertNotNull(volume);

      Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(client), 600, 10,
            TimeUnit.SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(snapshot.getRegion(), volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), defaultZone);
      assertEquals(volume.getSize(), 2);
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(snapshot.getRegion(), volume.getId());
   }

   @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
   public void testAttachVolumeInRegion() {
      client.attachVolumeInRegion(defaultRegion, volumeId, instanceId, "/dev/sdh");
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testDetachVolumeInRegion() {
      client.detachVolumeInRegion(defaultRegion, volumeId, false);
   }

   @Test
   public void testDescribeSnapshots() {
      for (String region : ec2Client.getConfiguredRegions()) {
         SortedSet<Snapshot> allResults = Sets.newTreeSet(client.describeSnapshotsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Snapshot snapshot = allResults.last();
            Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(region,
                  snapshotIds(snapshot.getId())));
            assertNotNull(result);
            assertEquals(result, snapshot);
         }
      }
   }

   @Test(enabled = false)
   public void testAddCreateVolumePermissionsToSnapshot() {
      // TODO client.addCreateVolumePermissionsToSnapshotInRegion(defaultRegion,
      // userIds,
      // userGroups,
      // snapshotId);
   }

   @Test(enabled = false)
   public void testRemoveCreateVolumePermissionsFromSnapshot() {
      // TODO
      // client.removeCreateVolumePermissionsFromSnapshotInRegion(defaultRegion,
      // userIds,
      // userGroups,
      // snapshotId);
   }

   @Test(enabled = false)
   public void testResetCreateVolumePermissionsOnSnapshot() {
      // TODO
      // client.resetCreateVolumePermissionsOnSnapshotInRegion(defaultRegion,
      // snapshotId);
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testGetCreateVolumePermissionForSnapshot() {
      client.getCreateVolumePermissionForSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testDeleteVolumeInRegion() {
      client.deleteVolumeInRegion(defaultRegion, volumeId);
      Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(defaultRegion, volumeId));
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getStatus(), Volume.Status.DELETING);
      volumeId = null;
   }

   @Test(dependsOnMethods = "testGetCreateVolumePermissionForSnapshot")
   public void testDeleteSnapshotInRegion() {
      client.deleteSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
      assert client.describeSnapshotsInRegion(snapshot.getRegion(), snapshotIds(snapshot.getId())).size() == 0;
      snapshot = null;
   }

   @Override
   @org.testng.annotations.AfterClass(groups = {"integration", "live"})
   protected void tearDownContext() {
      if (instanceId != null) {
         ec2Client.getInstanceServices().terminateInstancesInRegion(defaultRegion, instanceId);
      }
      if (volumeId != null) {
         client.deleteVolumeInRegion(defaultRegion, volumeId);
      }
      if (snapshot != null) {
         client.deleteSnapshotInRegion(defaultRegion, snapshot.getId());
      }
      super.tearDownContext();
   }
}