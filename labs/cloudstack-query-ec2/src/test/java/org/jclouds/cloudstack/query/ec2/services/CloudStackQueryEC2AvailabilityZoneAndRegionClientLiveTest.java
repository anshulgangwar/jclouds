package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.cloudstack.query.ec2.CloudStackQueryEC2ApiMetadata;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2AvailabilityZoneAndRegionClientLiveTest")
public class CloudStackQueryEC2AvailabilityZoneAndRegionClientLiveTest extends AvailabilityZoneAndRegionClientLiveTest {
    public CloudStackQueryEC2AvailabilityZoneAndRegionClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }

    private AvailabilityZoneAndRegionClient client;

    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setupContext() {
        super.setupContext();
        super.setupContext();
        client =  (view.unwrap(CloudStackQueryEC2ApiMetadata.CONTEXT_TOKEN).getApi()).getAvailabilityZoneAndRegionServices();
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
