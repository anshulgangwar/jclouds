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
package org.jclouds.cloudstack.ec2;

import org.jclouds.cloudstack.ec2.services.CloudStackAMIClient;
import org.jclouds.cloudstack.ec2.services.CloudStackEC2InstanceClient;
import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.EC2Client;
import org.jclouds.rest.annotations.Delegate;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to EC2 services.
 *
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface CloudStackEC2Client extends EC2Client {

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   CloudStackAMIClient getAMIServices();

   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   CloudStackEC2InstanceClient getInstanceServices();
}