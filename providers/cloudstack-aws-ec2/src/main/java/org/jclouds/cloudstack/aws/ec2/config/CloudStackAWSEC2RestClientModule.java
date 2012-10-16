package org.jclouds.cloudstack.aws.ec2.config;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;
import org.jclouds.aws.ec2.config.AWSEC2RestClientModule;
import org.jclouds.aws.ec2.services.*;
import org.jclouds.cloudstack.aws.ec2.suppliers.CloudStackAWSEC2DescribeRegionsForRegionURIs;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.ec2.services.*;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.*;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/12/12
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackAWSEC2RestClientModule extends AWSEC2RestClientModule {

    public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(AWSAMIClient.class, AWSAMIAsyncClient.class)//
            .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
            .put(AWSInstanceClient.class, AWSInstanceAsyncClient.class)//
            .put(AWSKeyPairClient.class, AWSKeyPairAsyncClient.class)//
            .put(AWSSecurityGroupClient.class, AWSSecurityGroupAsyncClient.class)//
            .put(PlacementGroupClient.class, PlacementGroupAsyncClient.class)//
            .put(MonitoringClient.class, MonitoringAsyncClient.class)//
            .put(WindowsClient.class, WindowsAsyncClient.class)//
            .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
            .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
            .put(SpotInstanceClient.class, SpotInstanceAsyncClient.class)//
            .put(TagClient.class, TagAsyncClient.class)//
            .put(WindowsApi.class, WindowsAsyncApi.class)//
            .build();

    @Override
    protected void installLocations() {
        install(new LocationModule());
        bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
        bind(RegionIdToURISupplier.class).to(CloudStackAWSEC2DescribeRegionsForRegionURIs.class).in(Scopes.SINGLETON);
        bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
        bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class).in(Scopes.SINGLETON);
        bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
    }



}
