package org.jclouds.cloudstack.aws.ec2.compute;

import com.google.inject.ImplementedBy;
import org.jclouds.cloudstack.aws.ec2.compute.internal.CloudStackAWSEC2ComputeServiceContextImpl;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/19/12
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
@ImplementedBy(CloudStackAWSEC2ComputeServiceContextImpl.class)
public interface CloudStackAWSEC2ComputeServiceContext {
    @Override
    org.jclouds.cloudstack.aws.ec2.compute.CloudStackAWSEC2ComputeService getComputeService();
}
