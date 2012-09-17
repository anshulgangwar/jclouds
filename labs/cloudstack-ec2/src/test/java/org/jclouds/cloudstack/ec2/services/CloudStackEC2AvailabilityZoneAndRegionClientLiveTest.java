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
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2AvailabilityZoneAndRegionClientLiveTest")
public class CloudStackEC2AvailabilityZoneAndRegionClientLiveTest extends AvailabilityZoneAndRegionClientLiveTest {
   public CloudStackEC2AvailabilityZoneAndRegionClientLiveTest() {
      provider = "cloudstack-ec2";
   }

    private AvailabilityZoneAndRegionClient client;

    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setupContext() {
        super.setupContext();
        super.setupContext();
        client =  (view.unwrap(CloudStackEC2ApiMetadata.CONTEXT_TOKEN).getApi()).getAvailabilityZoneAndRegionServices();
    }

    public void testDescribeAvailabilityZones() {

            client.describeAvailabilityZonesInRegion(null);



    }

    public void testDescribeRegions() {
        /*SortedMap<String, URI> allResults = Maps.newTreeMap();
        allResults.putAll(client.describeRegions());
        assertNotNull(allResults);
        assert allResults.size() >= 1 : allResults.size();
        Iterator<Map.Entry<String, URI>> iterator = allResults.entrySet().iterator();
        String r1 = iterator.next().getKey();
        SortedMap<String, URI> oneResult = Maps.newTreeMap();
        oneResult.putAll(client.describeRegions(regions(r1)));
        assertNotNull(oneResult);
        assertEquals(oneResult.size(), 1);
        iterator = oneResult.entrySet().iterator();
        assertEquals(iterator.next().getKey(), r1);*/
    }

}
