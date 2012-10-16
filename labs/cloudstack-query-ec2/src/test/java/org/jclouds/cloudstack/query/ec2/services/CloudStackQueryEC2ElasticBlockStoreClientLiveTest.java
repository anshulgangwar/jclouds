package org.jclouds.cloudstack.query.ec2.services;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.cloudstack.query.ec2.CloudStackQueryEC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.*;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.ElasticBlockStoreClientLiveTest;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */

@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2ElasticBlockStoreClientLiveTest")
public class CloudStackQueryEC2ElasticBlockStoreClientLiveTest extends ElasticBlockStoreClientLiveTest {
    @Resource
    protected Logger logger = Logger.NULL;

    public CloudStackQueryEC2ElasticBlockStoreClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }


    private ElasticBlockStoreClient client;
    private EC2Client ec2Client;

    private String defaultZone;
    protected RetryablePredicate<RunningInstance> runningTester;
    protected String imageId;
    protected String instanceId;

    private String volumeId;
    private Snapshot snapshot;

    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        logger.error(" image id mera pehla "+imageId);


        initializeContext();
        ec2Client = view.unwrap(CloudStackQueryEC2ApiMetadata.CONTEXT_TOKEN).getApi();
        client = ec2Client.getElasticBlockStoreServices();
        /*AvailabilityZoneInfo info = Iterables.get(ec2Client.getAvailabilityZoneAndRegionServices()
                .describeAvailabilityZonesInRegion(null), 0);
          defaultRegion = checkNotNull(Strings.emptyToNull(info.getRegion()), "region of " + info);*/
        Set<AvailabilityZoneInfo> allResults = ec2Client.getAvailabilityZoneAndRegionServices().describeAvailabilityZonesInRegion(null);
        allResults.iterator().next();
        defaultZone = allResults.iterator().next().getZone();
        Set<? extends Image> allImageResults = ec2Client.getAMIServices().describeImagesInRegion(null);
        assertNotNull(allImageResults);
        assert allImageResults.size() >= 1 : allImageResults.size();
        Iterator<? extends Image> iterator = allImageResults.iterator();
        imageId = iterator.next().getId();
        logger.error(" image id mera "+imageId+ "  " +defaultZone);


        if (imageId != null) {
           // runInstance();
        }
    }




    @Test
    void testDescribeVolumes() {
        //TODO
        client.describeVolumesInRegion(null);
        //SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(null));
        //assertNotNull(allResults);
        /*if (allResults.size() >= 1) {
            Volume volume = allResults.last();
            SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(null, volume.getId()));
            assertNotNull(result);
            Volume compare = result.last();
            assertEquals(compare, volume);
        }*/

    }

    @Test
    void testCreateVolumeInAvailabilityZone() {
        Volume expected = client.createVolumeInAvailabilityZone(defaultZone, 1);
        assertNotNull(expected);
        assertEquals(expected.getAvailabilityZone(), defaultZone);

        this.volumeId = expected.getId();

        Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(null, expected.getId()));
        assertNotNull(result);
        assertEquals(result.size(), 2);
        Volume volume = result.iterator().next();
        assertEquals(volume.getId(), expected.getId());
    }


    @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
    void testCreateSnapshotInRegion() {
        Snapshot snapshot = client.createSnapshotInRegion(null, volumeId);
        Predicate<Snapshot> snapshotted = new RetryablePredicate<Snapshot>(new SnapshotCompleted(client), 600, 10,
                TimeUnit.SECONDS);
        assert snapshotted.apply(snapshot);

        Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(snapshot.getRegion(),
                snapshotIds(snapshot.getId())));

        assertEquals(result.getProgress(), 100);
        this.snapshot = result;
    }

    @Test(dependsOnMethods = "testCreateSnapshotInRegion")
    void testCreateVolumeFromSnapshotInAvailabilityZone() {
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
    void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
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

    @Test
    void testAttachVolumeInRegion() {
        // TODO: need an instance
    }

    @Test
    void testDetachVolumeInRegion() {
        // TODO: need an instance
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
    void testDeleteVolumeInRegion() {
        client.deleteVolumeInRegion(null, volumeId);
        Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(null, volumeId));
        assertEquals(result.size(), 1);
        Volume volume = result.iterator().next();
        assertEquals(volume.getStatus(), Volume.Status.DELETING);
    }

    @Test(dependsOnMethods = "testGetCreateVolumePermissionForSnapshot")
    void testDeleteSnapshotInRegion() {
        client.deleteSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
        assert client.describeSnapshotsInRegion(snapshot.getRegion(), snapshotIds(snapshot.getId())).size() == 0;
    }

}
