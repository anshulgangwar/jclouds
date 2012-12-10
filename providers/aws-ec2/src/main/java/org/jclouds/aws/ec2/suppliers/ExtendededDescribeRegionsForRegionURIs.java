package org.jclouds.aws.ec2.suppliers;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.suppliers.DescribeRegionsForRegionURIs;
import org.jclouds.util.Suppliers2;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/13/12
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtendededDescribeRegionsForRegionURIs extends DescribeRegionsForRegionURIs {

    private final AvailabilityZoneAndRegionClient client;

    @Inject
    public ExtendededDescribeRegionsForRegionURIs(EC2Client client) {
        super(client);
        this.client = client.getAvailabilityZoneAndRegionServices();
    }

    @Override
    public Map<String, Supplier<URI>> get() {
        Map<String, URI> regionToUris = null;
        try {
            regionToUris = ImmutableMap.of("AmazonEC2", new URI("http://10.147.39.67:7080/awsapi"));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return Maps.transformValues(regionToUris, Suppliers2.<URI>ofInstanceFunction());
    }
}

