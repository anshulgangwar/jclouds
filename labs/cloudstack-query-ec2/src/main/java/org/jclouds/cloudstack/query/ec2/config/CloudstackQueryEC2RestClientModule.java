package org.jclouds.cloudstack.query.ec2.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Scopes;
import org.jclouds.cloudstack.query.ec2.suppliers.CloudstackQueryEC2DescribeRegionsForRegionURIs;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.ec2.services.*;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.*;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.rest.ConfiguresRestClient;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/18/12
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
@ConfiguresRestClient
public class CloudstackQueryEC2RestClientModule<S extends EC2Client, A extends EC2AsyncClient> extends EC2RestClientModule {
    public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(AMIClient.class, AMIAsyncClient.class)//
            .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
            .put(InstanceClient.class, InstanceAsyncClient.class)//
            .put(KeyPairClient.class, KeyPairAsyncClient.class)//
            .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
            .put(WindowsClient.class, WindowsAsyncClient.class)//
            .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
            .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
            .put(WindowsApi.class, WindowsAsyncApi.class)//
            .build();

    @SuppressWarnings("unchecked")
    public CloudstackQueryEC2RestClientModule() {
        super(TypeToken.class.cast(TypeToken.of(EC2Client.class)), TypeToken.class.cast(TypeToken.of(EC2AsyncClient.class)), DELEGATE_MAP);
    }

    protected CloudstackQueryEC2RestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
                                                 Map<Class<?>, Class<?>> sync2Async) {
        super(syncClientType, asyncClientType, sync2Async);
    }

    @Override
    protected void installLocations() {
        install(new LocationModule());
        bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
        bind(RegionIdToURISupplier.class).to(CloudstackQueryEC2DescribeRegionsForRegionURIs.class).in(Scopes.SINGLETON);
        bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
        bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class).in(Scopes.SINGLETON);
        bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
    }

}
