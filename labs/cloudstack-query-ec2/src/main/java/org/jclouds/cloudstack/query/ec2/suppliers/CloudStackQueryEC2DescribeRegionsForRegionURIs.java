package org.jclouds.cloudstack.query.ec2.suppliers;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.suppliers.DescribeRegionsForRegionURIs;
import org.jclouds.location.Provider;

import javax.inject.Inject;
import java.net.URI;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackQueryEC2DescribeRegionsForRegionURIs extends DescribeRegionsForRegionURIs {

    private final AvailabilityZoneAndRegionClient client;
    private Supplier<URI> defaultURISupplier;

    @Inject
    public CloudStackQueryEC2DescribeRegionsForRegionURIs(@Provider Supplier<URI> defaultURISupplier, EC2Client client) {
        super(client);
        this.client = client.getAvailabilityZoneAndRegionServices();
        this.defaultURISupplier = defaultURISupplier;
    }

    @Override
    public Map<String, Supplier<URI>> get() {
        Map<String, Supplier<URI>> regionToUris = null;
        regionToUris = ImmutableMap.of("AmazonEC2", defaultURISupplier);

        return regionToUris;
    }

}
