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

import org.jclouds.Constants;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RegisterImageOptions;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code AMIClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class AMIClientLiveTest extends BaseComputeServiceContextLiveTest {
    @Resource
    protected Logger logger = Logger.NULL;
    private TemplateBuilderSpec ebsTemplate;
    private String defaultZone = null;
    private RunningInstance instance;
    private String instanceId;
    private RetryablePredicate<String> ImageTester;

    public AMIClientLiveTest() {
        provider = "ec2";
    }

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();
        overrides.put(Constants.PROPERTY_MAX_RETRIES, "0");
        overrides.put(Constants.PROPERTY_REQUEST_TIMEOUT,"9000000");
        overrides.put(Constants.PROPERTY_SO_TIMEOUT,"9000000");
        overrides.put(Constants.PROPERTY_CONNECTION_TIMEOUT,"9000000");

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
    protected String createdImageId;
    protected String registeredImageId;

    @Override
    @BeforeClass(groups = {"integration", "live"})
    public void setupContext() {
        super.setupContext();
        ec2Client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi();
        runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(ec2Client), 600, 5,
                TimeUnit.SECONDS);
        /*ImageTester = new RetryablePredicate<String>(new InstanceStateRunning(ec2Client), 600, 5,
                TimeUnit.SECONDS);*/


        client = ec2Client.getAMIServices();
        if (ebsTemplate != null) {
            Template template = view.getComputeService().templateBuilder().from(ebsTemplate).build();
            regionId = "AmazonEC2";
            imageId = template.getImage().getProviderId();
            for (Image image : client.describeImagesInRegion(regionId)) {
                if (ebsBackedImageName.equals(image.getName()))
                    client.deregisterImageInRegion(regionId, image.getId());
            }
        }

        if (imageId != null) {
            runInstance();
        }
    }

    private void runInstance() {
         try {
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

        } catch (Exception e) {
            if (instanceId != null) {
                ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
            }
        }
    }

    public void testDescribeImageNotExists() {
        assertEquals(client.describeImagesInRegion(null, imageIds("ami-cdf819a3")).size(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDescribeImageBadId() {
        client.describeImagesInRegion(null, imageIds("asdaasdsa"));
    }

    @Test
    public void testDescribeImages() {
        // for (String region : ec2Client.getConfiguredRegions()) {
        Set<? extends Image> allResults = client.describeImagesInRegion(null);
        assertNotNull(allResults);
        assert allResults.size() >= 1 : allResults.size();
        Iterator<? extends Image> iterator = allResults.iterator();
        String id1 = iterator.next().getId();
        //String id2 = iterator.next().getId();
        Set<? extends Image> twoResults = client.describeImagesInRegion(null, imageIds(id1));
        assertNotNull(twoResults);
        assertEquals(twoResults.size(), 1);
        iterator = twoResults.iterator();
        assertEquals(iterator.next().getId(), id1);
        //assertEquals(iterator.next().getId(), id2);
        // }
    }



    @Test
    public void testRegisterImage(){

        RegisterImageOptions options = new RegisterImageOptions();
        options.asArchitecture("VHD:zone1:CentOS:Xenserver");
      registeredImageId = client.registerImageFromManifestInRegion(regionId,"imageRegister",
              "http://10.102.123.240/cloudstack/templates/centos56-x86_64.vhd.bz2",
              new RegisterImageOptions().asArchitecture("VHD:zone1:CentOS:Xenserver")) ;
    }



    @Test
    public void testCreateImage(){
        ec2Client.getInstanceServices().stopInstancesInRegion(regionId,false,instanceId);
        logger.error(" image creation anshul 1");

        createdImageId = client.createImageInRegion(regionId,"Centosanshul",instanceId);

        logger.error(" image creation anshul 2");

    }

    @Test(dependsOnMethods = "testCreateImage")
    void testDeregisterImageInRegion(){
        client.deregisterImageInRegion(regionId, createdImageId);
    }

    @Test
    void testGetLaunchPermissionForImageInRegion(){
         client.getLaunchPermissionForImageInRegion(regionId, imageId);
    }

    /*@Test
    void testResetLaunchPermissionsOnImageInRegion(){
        client.resetLaunchPermissionsOnImageInRegion(regionId, imageId);
    }*/



    /*@Test
    public void testCreateAndListEBSBackedImage() throws Exception {
       Snapshot snapshot = createSnapshot();

       // List of images before...
       int sizeBefore = client.describeImagesInRegion(regionId).size();

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
       assertEquals(after, sizeBefore + 1);
    }*//*


    // Fires up an instance, finds its root volume ID, takes a snapshot, then
    // terminates the instance.
    *//*private Snapshot createSnapshot() throws RunNodesException {
          logger.error(" snapshot error anshul 1");
          String instanceId = null;
          try {
              logger.error(" snapshot error anshul 21") ;
              Reservation<? extends RunningInstance> runningInstances = ec2Client.getInstanceServices().runInstancesInRegion(
                      regionId, null, imageId, 1, 1);;
              logger.error(" snapshot error anshul 22 " + runningInstances);
              RunningInstance instance = getOnlyElement(concat(runningInstances));
              logger.error(" snapshot error anshul 23");
             instanceId = instance.getId();
              logger.error(" snapshot error anshul 3");
             assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
              logger.error(" snapshot error anshul 4");



             instance =(RunningInstance) (getOnlyElement(concat(ec2Client.getInstanceServices().describeInstancesInRegion(regionId,
                   instanceId))));
              logger.error(" snapshot error anshul 5");
             BlockDevice device = instance.getEbsBlockDevices().get("/dev/sda1");
             assertNotNull(device, "device: /dev/sda1 not present on: " + instance);
             Snapshot snapshot = ec2Client.getElasticBlockStoreServices().createSnapshotInRegion(regionId,
                   device.getVolumeId());
              logger.error(" snapshot error anshul 6");
             snapshotsToDelete.add(snapshot.getId());
              logger.error(" snapshot error anshul 7");
             return snapshot;
          } finally {
             if (instanceId != null)
                ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
          }
       }
    */

/*

     @Test(dependsOnMethods = "testCreateAndListEBSBackedImage")
    public void testGetLaunchPermissionForImage() {
       client.getLaunchPermissionForImageInRegion(regionId, ebsBackedImageId);
    }
*/

    @Override
    @AfterClass(groups = {"integration", "live"})
    protected void tearDownContext() {
        for (String imageId : imagesToDeregister)
            client.deregisterImageInRegion(regionId, imageId);
        for (String snapshotId : snapshotsToDelete)
            ec2Client.getElasticBlockStoreServices().deleteSnapshotInRegion(regionId, snapshotId);

        if (instanceId != null) {
            ec2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
        }
        super.tearDownContext();
    }

    /*public Reservation<? extends RunningInstance> runningInstances() {
        return ec2Client.getInstanceServices().runInstancesInRegion(
                regionId, null, imageId, 1, 1);
    }*/
}
