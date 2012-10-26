package org.jclouds.cloudstack.aws.ec2;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2ProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import java.net.URI;
import java.util.Properties;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.*;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/16/12
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackAWSEC2ProviderMetadata extends AWSEC2ProviderMetadata {

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return builder().fromProviderMetadata(this);
    }

    public CloudStackAWSEC2ProviderMetadata() {
        super(builder());
    }

    public CloudStackAWSEC2ProviderMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        // sometimes, like in ec2, stop takes a very long time, perhaps
        // due to volume management. one example spent 2 minutes moving
        // from stopping->stopped state on an ec2 micro
        properties.setProperty(TIMEOUT_NODE_SUSPENDED, 120 * 1000 + "");
        properties.putAll(Region.regionProperties());
        // Amazon Linux, Amazon Windows, alestic, canonical, and rightscale
        properties.setProperty(PROPERTY_EC2_AMI_QUERY,
                "owner-id=137112412989,801119661308,063491364108,099720109477,411009282317;state=available;image-type=machine");
        // amis that work with the cluster instances
        properties.setProperty(PROPERTY_EC2_CC_REGIONS, Region.US_EAST_1 + "," + Region.US_WEST_2 + ","+ Region.EU_WEST_1);
        properties
                .setProperty(
                        PROPERTY_EC2_CC_AMI_QUERY,
                        "virtualization-type=hvm;architecture=x86_64;owner-id=137112412989,099720109477;hypervisor=xen;state=available;image-type=machine;root-device-type=ebs");
        properties.setProperty(TEMPLATE, "osFamily=AMZN_LINUX,os64Bit=true");
        return properties;
    }

    public static class Builder extends AWSEC2ProviderMetadata.Builder {

        protected Builder(){
            id("cloudstack-aws-ec2")
                    .name("Cloudstack specific  EC2 API")
                    .apiMetadata(new CloudStackAWSEC2ApiMetadata())
                    .endpoint("https://ec2.us-east-1.amazonaws.com")
                    .homepage(URI.create("http://aws.amazon.com/ec2"))
                    .console(URI.create("https://console.aws.amazon.com/ec2/home"))
                    .defaultProperties(AWSEC2ProviderMetadata.defaultProperties())
                    .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
                    .iso3166Codes("US-VA", "US-CA", "US-OR", "BR-SP", "IE", "SG", "JP-13");
        }

        @Override
        public AWSEC2ProviderMetadata build() {
            return new AWSEC2ProviderMetadata(this);
        }

        @Override
        public Builder fromProviderMetadata(
                ProviderMetadata in) {
            super.fromProviderMetadata(in);
            return this;
        }

    }
}
