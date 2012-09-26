package org.jclouds.cloudstack.query.ec2.services;

import com.google.common.collect.Sets;
import org.jclouds.cloudstack.query.ec2.CloudStackQueryEC2ApiMetadata;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.services.KeyPairClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.SortedSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackQueryEC2KeyPairClientLiveTest")
public class CloudStackQueryEC2KeyPairClientLiveTest extends KeyPairClientLiveTest {


    private KeyPairClient client;

    public CloudStackQueryEC2KeyPairClientLiveTest() {
        provider = "cloudstack-query-ec2";
    }

    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setupContext() {
        super.setupContext();

        client =  (view.unwrap(CloudStackQueryEC2ApiMetadata.CONTEXT_TOKEN).getApi()).getKeyPairServices();
    }

    @Test
    void testDescribeKeyPairs() {

            SortedSet<KeyPair> allResults = Sets.newTreeSet(client.describeKeyPairsInRegion(null));
            assertNotNull(allResults);
            if (allResults.size() >= 1) {
                KeyPair pair = allResults.last();
                SortedSet<KeyPair> result = Sets.newTreeSet(client.describeKeyPairsInRegion(null, pair.getKeyName()));
                assertNotNull(result);
                KeyPair compare = result.last();
                assertEquals(compare, pair);
            }

    }

    public static final String PREFIX = System.getProperty("user.name") + "-cloudstack-query-ec2";

    @Test
    void testCreateKeyPair() {
        String keyName = PREFIX + "1";
        try {
            client.deleteKeyPairInRegion(null, keyName);
        } catch (Exception e) {

        }
        //client.deleteKeyPairInRegion(null, keyName);

        KeyPair result = client.createKeyPairInRegion(null, keyName);
        assertNotNull(result);
        assertNotNull(result.getKeyMaterial());
        assertNotNull(result.getSha1OfPrivateKey());
        assertEquals(result.getKeyName(), keyName);

        Set<KeyPair> twoResults = Sets.newLinkedHashSet(client.describeKeyPairsInRegion(null, keyName));
        assertNotNull(twoResults);
        assertEquals(twoResults.size(), 1);
        KeyPair listPair = twoResults.iterator().next();
        assertEquals(listPair.getKeyName(), result.getKeyName());
        assertEquals(listPair.getSha1OfPrivateKey(), result.getSha1OfPrivateKey());
    }

}
