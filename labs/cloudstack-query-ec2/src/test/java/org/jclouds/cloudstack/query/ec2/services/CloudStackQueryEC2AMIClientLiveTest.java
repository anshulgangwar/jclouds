package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.cloudstack.query.ec2.CloudStackQueryEC2ApiMetadata;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.ec2.services.AMIClientLiveTest;
import org.jclouds.logging.ConsoleLogger;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2AMIClientLiveTest")
public class CloudStackQueryEC2AMIClientLiveTest extends AMIClientLiveTest {
    Logger s = new ConsoleLogger();
    public CloudStackQueryEC2AMIClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }

    private TemplateBuilderSpec ebsTemplate;

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();

        String ebsSpec = checkNotNull(setIfTestSystemPropertyPresent(overrides, provider + ".ebs-template"), provider
                + ".ebs-template");
        ebsTemplate = TemplateBuilderSpec.parse(ebsSpec);
        return overrides;

    }

    protected EC2Client ec2Client;
    protected AMIClient client;

    protected RetryablePredicate<RunningInstance> runningTester;

    protected Set<String> imagesToDeregister = newHashSet();
    protected Set<String> snapshotsToDelete = newHashSet();
    protected String regionId;
    protected String ebsBackedImageId;
    protected String ebsBackedImageName = "jcloudstest1";
    protected String imageId;

    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setupContext() {
        initializeContext();
        ec2Client = view.unwrap(CloudStackQueryEC2ApiMetadata.CONTEXT_TOKEN).getApi();
        /*runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(ec2Client), 2400, 5,
                TimeUnit.SECONDS);*/
        client = ec2Client.getAMIServices();
/*
        if (ebsTemplate != null) {
            Template template = view.getComputeService().templateBuilder().from(ebsTemplate).build();
            regionId = null;
            imageId = template.getImage().getProviderId();
            for (Image image : client.describeImagesInRegion(regionId)) {
                if (ebsBackedImageName.equals(image.getName()))
                    client.deregisterImageInRegion(regionId, image.getId());
            }
        }*/

    }

    public void testDescribeImageNotExists() {
        assertEquals(client.describeImagesInRegion(null, imageIds("cdf819a3")).size(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDescribeImageBadId() {
        client.describeImagesInRegion(null, imageIds("asdaasdsa"));
    }

    @Test
    public void testDescribeImages() {
        //for (String region : ec2Client.getConfiguredRegions()) {
        Set<? extends Image> allResults = client.describeImagesInRegion(null);
        assertNotNull(allResults);
        assert allResults.size() >= 1 : allResults.size();
        Iterator<? extends Image> iterator = allResults.iterator();
        String id1 = iterator.next().getId();
        //String id2 = iterator.next().getId();
        Set<? extends Image> oneResults = client.describeImagesInRegion(null, imageIds(id1));
        assertNotNull(oneResults);
        assertEquals(oneResults.size(), 1);
        iterator = oneResults.iterator();
        assertEquals(iterator.next().getId(), id1);
       // assertEquals(iterator.next().getId(), id2);
       // }
    }

    @Test
    public void testCreateAndListEBSBackedImage() throws Exception {
       //Snapshot snapshot = createSnapshot();

       // List of images before...
      /* int sizeBefore = client.describeImagesInRegion(regionId).size();

       // Register a new image...
       ebsBackedImageId = client.registerUnixImageBackedByEbsInRegion(regionId, ebsBackedImageName, snapshot.getId(),
             addNewBlockDevice("/dev/sda2", "myvirtual", 1).withDescription("adrian"));
       imagesToDeregister.add(ebsBackedImageId);
       final Image ebsBackedImage = getOnlyElement(client.describeImagesInRegion(regionId, imageIds(ebsBackedImageId)));
       assertEquals(ebsBackedImage.getName(), ebsBackedImageName);
       assertEquals(ebsBackedImage.getImageType(), Image.ImageType.MACHINE);
       assertEquals(ebsBackedImage.getRootDeviceType(), RootDeviceType.EBS);
       assertEquals(ebsBackedImage.getRootDeviceName(), "/dev/sda1");
       assertEquals(ebsBackedImage.getDescription(), "adrian");
       assertEquals(
             ebsBackedImage.getEbsBlockDevices().entrySet(),
             ImmutableMap.of("/dev/sda1", new Image.EbsBlockDevice(snapshot.getId(), snapshot.getVolumeSize(), true),
                     "/dev/sda2", new Image.EbsBlockDevice(null, 1, false)).entrySet());

       // List of images after - should be one larger than before
       int after = client.describeImagesInRegion(regionId).size();
       assertEquals(after, sizeBefore + 1);*/
    }

    // Fires up an instance, finds its root volume ID, takes a snapshot, then
    // terminates the instance.
   /* private Snapshot createSnapshot() throws RunNodesException {

       String instanceId = null;
        try {
            Reservation<? extends RunningInstance> reservation = ec2Client.getInstanceServices().runInstancesInRegion(null, null, imageId, 1, 1);
           *//**//* RunningInstance instance = getOnlyElement(concat(ec2Client.getInstanceServices().runInstancesInRegion(
                    null, null, imageId, 1, 1)));
            *//**//*
           // instanceId = instance.getId();

           // assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");

            *//**//*instance =(RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices().describeInstancesInRegion(regionId,
                    instanceId))));
            BlockDevice device = instance.getEbsBlockDevices().get("/dev/sda1");
            assertNotNull(device, "device: /dev/sda1 not present on: " + instance);
            Snapshot snapshot = ec2Client.getElasticBlockStoreServices().createSnapshotInRegion(regionId,
                    device.getVolumeId());
            snapshotsToDelete.add(snapshot.getId());*//**//*
            return null;
        } finally {
            if (instanceId != null)
                ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
        }
    }*/

    @Test(dependsOnMethods = "testCreateAndListEBSBackedImage")
    public void testGetLaunchPermissionForImage() {
     //  client.getLaunchPermissionForImageInRegion(regionId, ebsBackedImageId);
    }

    @Override
    @AfterClass(groups = { "integration", "live" })
    protected void tearDownContext() {
        for (String imageId : imagesToDeregister)
            client.deregisterImageInRegion(regionId, imageId);
        for (String snapshotId : snapshotsToDelete)
            ec2Client.getElasticBlockStoreServices().deleteSnapshotInRegion(regionId, snapshotId);
        super.tearDownContext();
    }

}
