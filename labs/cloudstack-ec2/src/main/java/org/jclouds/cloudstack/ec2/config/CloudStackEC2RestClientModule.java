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
package org.jclouds.cloudstack.ec2.config;


import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Scopes;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.jclouds.ec2.services.KeyPairAsyncClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ec2.services.WindowsAsyncClient;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.Provider;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.rest.ConfiguresRestClient;

import java.util.Map;


/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudStackEC2RestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>>builder()//
           .put(org.jclouds.ec2.services.AMIClient.class, org.jclouds.ec2.services.AMIAsyncClient.class)//
           .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
           .put(org.jclouds.ec2.services.InstanceClient.class, org.jclouds.ec2.services.InstanceAsyncClient.class)//
           .put(KeyPairClient.class, KeyPairAsyncClient.class)//
           .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
           .put(WindowsClient.class, WindowsAsyncClient.class)//
           .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
           .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
           .put(WindowsApi.class, WindowsAsyncApi.class)//
           .build();

   public CloudStackEC2RestClientModule() {
      super(TypeToken.of(EC2Client.class), TypeToken.of(EC2AsyncClient.class), DELEGATE_MAP);
   }


   /*@Override
   protected void configure() {
      super.configure();
      // override parsers, etc. here
      // ex.
      // bind(DescribeImagesResponseHandler.class).to(CloudStackDescribeImagesResponseHandler.class);
   }

}
=======
   }*/

   @Override
   protected void installLocations() {
      install(new LocationModule());
      bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
      bind(RegionIdToURISupplier.class).to(CloudStackEC2DescribeRegionsForRegionURIs.class).in(Scopes.SINGLETON);
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
      bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class).in(Scopes.SINGLETON);
      bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindRetryHandlers() {
      //Changing these to NEVER_RETRY as retrying may leads to unknown state
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).toInstance(HttpRetryHandler.NEVER_RETRY);
      //first instance creation takes lot of time in CloudStack
      //It is misinterpreting this as failure so changing it to NEVER_RETRY
      bind(IOExceptionRetryHandler.class).toInstance(IOExceptionRetryHandler.NEVER_RETRY);
   }


   private static class CloudStackEC2DescribeRegionsForRegionURIs extends org.jclouds.ec2.suppliers
           .DescribeRegionsForRegionURIs {
      private com.google.common.base.Supplier<java.net.URI> defaultURISupplier;

      @javax.inject.Inject
      public CloudStackEC2DescribeRegionsForRegionURIs(@Provider com.google.common.base
              .Supplier<java.net.URI> defaultURISupplier, EC2Client client) {
         super(client);
         this.defaultURISupplier = defaultURISupplier;
      }

      @Override
      public Map<String, com.google.common.base.Supplier<java.net.URI>> get() {
         return ImmutableMap.of("AmazonEC2", defaultURISupplier);
      }
   }
}

