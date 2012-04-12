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
package org.jclouds.vcloud;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_SCHEMA;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.vcloud.domain.network.FenceMode;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for VCloud 1.0 API
 * 
 * @author Adrian Cole
 */
public class VCloudApiMetadata
      extends
      BaseComputeServiceApiMetadata<VCloudClient, VCloudAsyncClient, ComputeServiceContext<VCloudClient, VCloudAsyncClient>, VCloudApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public VCloudApiMetadata() {
      this(new Builder());
   }

   protected VCloudApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "1");
      properties.setProperty(PROPERTY_VCLOUD_XML_NAMESPACE,
            String.format("http://www.vmware.com/vcloud/v${%s}", PROPERTY_VCLOUD_VERSION_SCHEMA));
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 8 * 60 + "");
      properties.setProperty(PROPERTY_VCLOUD_XML_SCHEMA, "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "80");
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE, FenceMode.BRIDGED.toString());
      // TODO integrate this with the {@link ComputeTimeouts} instead of having
      // a single timeout for
      // everything.
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, 1200l * 1000l + "");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 300 + "");
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<VCloudClient, VCloudAsyncClient, ComputeServiceContext<VCloudClient, VCloudAsyncClient>, VCloudApiMetadata> {

      protected Builder() {
          id("vcloud")
         .name("VCloud 1.0 API")
         .identityName("User at Organization (user@org)")
         .credentialName("Password")
         .documentation(URI.create("http://www.vmware.com/support/pubs/vcd_pubs.html"))
         .version("1.0")
         .javaApi(VCloudClient.class, VCloudAsyncClient.class)
         .defaultProperties(VCloudApiMetadata.defaultProperties())
         .contextBuilder(TypeToken.of(VCloudContextBuilder.class));
      }

      @Override
      public VCloudApiMetadata build() {
         return new VCloudApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(VCloudApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}