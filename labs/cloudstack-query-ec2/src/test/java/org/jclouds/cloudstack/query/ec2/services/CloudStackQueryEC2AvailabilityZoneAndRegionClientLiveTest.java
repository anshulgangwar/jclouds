package org.jclouds.cloudstack.query.ec2.services;

import org.jclouds.cloudstack.query.ec2.CloudStackQueryEC2ApiMetadata;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;

import static org.jclouds.ec2.options.DescribeAvailabilityZonesOptions.Builder.availabilityZones;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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
        client =  (view.unwrap(CloudStackQueryEC2ApiMetadata.CONTEXT_TOKEN).getApi()).getAvailabilityZoneAndRegionServices();
    }

    public void testDescribeAvailabilityZones() {

        Set<AvailabilityZoneInfo> allResults = client.describeAvailabilityZonesInRegion(null);
        assertNotNull(allResults);
        assert allResults.size() >= 1 : allResults.size();
        Iterator<AvailabilityZoneInfo> iterator = allResults.iterator();
        String id1 = iterator.next().getZone();
        Set<AvailabilityZoneInfo> oneResult = client.describeAvailabilityZonesInRegion(null,
                availabilityZones(id1));
        assertNotNull(oneResult);
        assertEquals(oneResult.size(), 1);
        iterator = allResults.iterator();
        assertEquals(iterator.next().getZone(), id1);

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
