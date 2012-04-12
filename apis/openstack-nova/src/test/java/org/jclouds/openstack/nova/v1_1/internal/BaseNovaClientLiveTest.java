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
package org.jclouds.openstack.nova.v1_1.internal;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.openstack.nova.v1_1.NovaAsyncClient;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NovaClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseNovaClientLiveTest
      extends
      BaseComputeServiceContextLiveTest<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>> {

   public BaseNovaClientLiveTest() {
      provider = "openstack-nova";
   }

   protected RestContext<NovaClient, NovaAsyncClient> novaContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      novaContext = context.getProviderSpecificContext();
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (novaContext != null)
         novaContext.close();
   }

}