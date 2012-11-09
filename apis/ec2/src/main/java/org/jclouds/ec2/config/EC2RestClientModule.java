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
package org.jclouds.ec2.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Scopes;
import org.jclouds.aws.config.WithZonesFormSigningRestClientModule;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.ec2.services.*;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.ec2.suppliers.ExtendededDescribeRegionsForRegionURIs;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.*;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.rest.ConfiguresRestClient;

import java.util.Map;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole (EDIT: Nick Terry nterry@familysearch.org)
 */
@ConfiguresRestClient
public class EC2RestClientModule<S extends EC2Client, A extends EC2AsyncClient> extends
         WithZonesFormSigningRestClientModule<S, A> {
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
   public EC2RestClientModule() {
      super(TypeToken.class.cast(TypeToken.of(EC2Client.class)), TypeToken.class.cast(TypeToken.of(EC2AsyncClient.class)), DELEGATE_MAP);
   }

   protected EC2RestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }
   
   @Override
   protected void installLocations() {
      install(new LocationModule());
      bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
      bind(RegionIdToURISupplier.class).to(ExtendededDescribeRegionsForRegionURIs.class).in(Scopes.SINGLETON);
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
      bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class).in(Scopes.SINGLETON);
      bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
   }

    @Override
    protected void bindRetryHandlers() {
       super.bindRetryHandlers();
       bind(HttpRetryHandler.class).annotatedWith(ServerError.class).toInstance(HttpRetryHandler.NEVER_RETRY);
    }
}
