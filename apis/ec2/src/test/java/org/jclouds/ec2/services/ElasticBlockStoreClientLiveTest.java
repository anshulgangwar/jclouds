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
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.*;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code ElasticBlockStoreClient}
 *
 * @author Adrian Cole
 */

@Test(groups = "live", singleThreaded = true, testName = "ElasticBlockStoreClientLiveTest")
public class ElasticBlockStoreClientLiveTest extends BaseComputeServiceContextLiveTest {
    @Resource
    protected Logger logger = Logger.NULL;
    private RetryablePredicate<RunningInstance> runningTester;
    private String instanceId = null;
    private String regionId = "AmazonEC2";
    private RunningInstance instance;
    private Volume volumeSnapshot;

    public ElasticBlockStoreClientLiveTest() {
        provider = "ec2";
    }

    private EC2Client ec2Client;
    private ElasticBlockStoreClient client;

    private String defaultRegion;
    private String defaultZone;

    protected String imageId;
    private String volumeId;
    private Snapshot snapshot;

    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        super.setupContext();
        ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
        runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(ec2Client), 900, 5,
                TimeUnit.SECONDS);
        client = ec2Client.getElasticBlockStoreServices();
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
    void testDescribeVolumes() {
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
    void testCreateVolumeInAvailabilityZone() {
        Volume expected = client.createVolumeInAvailabilityZone(defaultZone, 1);
        assertNotNull(expected);
        assertEquals(expected.getAvailabilityZone(), defaultZone);

        this.volumeId = expected.getId();

        Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(defaultRegion, expected.getId()));
        assertNotNull(result);
        logger.error(" result me kya hai " + result);
        assertEquals(result.size(), 1);
        Volume volume = result.iterator().next();
        assertEquals(volume.getId(), expected.getId());
    }

    @Test(dependsOnMethods = "testAttachVolumeInRegion")
    void testCreateSnapshotInRegion() {
        Snapshot snapshot = client.createSnapshotInRegion(defaultRegion, volumeId);
        Predicate<Snapshot> snapshotted = new RetryablePredicate<Snapshot>(new SnapshotCompleted(client), 900, 10,
                TimeUnit.SECONDS);
        assert snapshotted.apply(snapshot);

        Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(snapshot.getRegion(),
                snapshotIds(snapshot.getId())));

        assertEquals(result.getProgress(), 100);
        this.snapshot = result;
    }

    @Test(dependsOnMethods = "testCreateSnapshotInRegion")
    void testCreateVolumeFromSnapshotInAvailabilityZone() {
        volumeSnapshot = client.createVolumeFromSnapshotInAvailabilityZone(defaultZone, snapshot.getId());
        assertNotNull(volumeSnapshot);

        Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(client), 600, 10,
                TimeUnit.SECONDS);
        assert availabile.apply(volumeSnapshot);

        Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(regionId, volumeSnapshot.getId()));
        logger.error(" snapshot from volume error 1");
        assertEquals(volumeSnapshot.getId(), result.getId());
        logger.error(" snapshot from volume error 2");
       // assertEquals(volumeSnapshot.getSnapshotId(), snapshot.getId());
        logger.error(" snapshot from volume error 3");
       // assertEquals(volumeSnapshot.getAvailabilityZone(), defaultZone);
        logger.error(" snapshot from volume error 4");
        //assertEquals(result.getStatus(), Volume.Status.AVAILABLE);
        logger.error(" snapshot from volume error 5");

        client.deleteVolumeInRegion(regionId, volumeSnapshot.getId());
        volumeSnapshot = null;
    }


/*
@Test(dependsOnMethods = "testCreateSnapshotInRegion")
    void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
        Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(defaultZone, 2, snapshot.getId());
        assertNotNull(volume);

        Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(client), 600, 10,
                TimeUnit.SECONDS);
        assert availabile.apply(volume);

        Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(snapshot.getRegion(), volume.getId()));
        assertEquals(volume.getId(), result.getId());
        //assertEquals(volume.getSnapshotId(), snapshot.getId());
        assertEquals(volume.getAvailabilityZone(), defaultZone);
        assertEquals(volume.getSize(), 2);
        //assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

        client.deleteVolumeInRegion(snapshot.getRegion(), volume.getId());
    }
*/


    @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
    void testAttachVolumeInRegion() {
        logger.error(" volumeid kya hai idhar  " + volumeId);
        client.attachVolumeInRegion(regionId, volumeId, instanceId, "/dev/sdh");

    }

    @Test(dependsOnMethods = "testCreateSnapshotInRegion")
    void testDetachVolumeInRegion() {
        client.detachVolumeInRegion(regionId, volumeId, false);
    }

    @Test
    void testDescribeSnapshots() {
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


/*@Test(enabled = false)
    public void testAddCreateVolumePermissionsToSnapshot() {
        // TODO client.addCreateVolumePermissionsToSnapshotInRegion(defaultRegion,
        // userIds,
        // userGroups,
        // snapshotId);
    }*//*


    */
/*@Test(enabled = false)
    public void testRemoveCreateVolumePermissionsFromSnapshot() {
        // TODO
        // client.removeCreateVolumePermissionsFromSnapshotInRegion(defaultRegion,
        // userIds,
        // userGroups,
        // snapshotId);
    }*//*


    */
/*@Test(enabled = false)
    public void testResetCreateVolumePermissionsOnSnapshot() {
        // TODO
        // client.resetCreateVolumePermissionsOnSnapshotInRegion(defaultRegion,
        // snapshotId);
    }*//*


    */
/*@Test(dependsOnMethods = "testCreateSnapshotInRegion")
    public void testGetCreateVolumePermissionForSnapshot() {
        client.getCreateVolumePermissionForSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
    }*/


    @Test(dependsOnMethods = "testDetachVolumeInRegion")
    void testDeleteVolumeInRegion() {
        client.deleteVolumeInRegion(defaultRegion, volumeId);
        volumeId = null;

/*Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(defaultRegion, volumeId));
        assertEquals(result.size(), 1);
        Volume volume = result.iterator().next();
        assertEquals(volume.getStatus(), Volume.Status.DELETING);*/

    }


/*@Test(dependsOnMethods = "testDeleteVolumeInRegion")
    void testTerminateInstance() {
        if (instanceId != null) {
            ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
        }
    }*/


    @Test(dependsOnMethods = "testCreateVolumeFromSnapshotInAvailabilityZone")
    void testDeleteSnapshotInRegion() {
        client.deleteSnapshotInRegion(regionId, snapshot.getId());
        assert client.describeSnapshotsInRegion(regionId, snapshotIds(snapshot.getId())).size() == 0;
        snapshot = null;
    }

    @Override
    @AfterClass(groups = {"integration", "live"})
    protected void tearDownContext() {
        if (instanceId != null) {
            ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
        }

        if(volumeId != null){
            client.deleteVolumeInRegion(defaultRegion, volumeId);
        }

        if(snapshot != null){
            client.deleteSnapshotInRegion(regionId, snapshot.getId());
        }

        if(volumeSnapshot != null){
            client.deleteVolumeInRegion(regionId, volumeSnapshot.getId());
        }

        super.tearDownContext();
    }

}

