package org.jclouds.cloudstack.aws.ec2;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeServiceContext;
import org.jclouds.aws.ec2.compute.config.AWSEC2ComputeServiceContextModule;
import org.jclouds.aws.ec2.config.AWSEC2RestClientModule;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;

import java.util.Properties;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/12/12
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */

public class CloudStackAWSEC2ApiMetadata extends AWSEC2ApiMetadata {
    /** The serialVersionUID */
    private static final long serialVersionUID = 3060225665040763827L;

    private static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return builder().fromApiMetadata(this);
    }

    public CloudStackAWSEC2ApiMetadata() {
        this(builder());
    }

    protected CloudStackAWSEC2ApiMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        Properties properties = EC2ApiMetadata.defaultProperties();
        properties.remove(PROPERTY_EC2_AMI_OWNERS);
        // auth fail sometimes happens in EC2, as the rc.local script that injects the
        // authorized key executes after ssh has started.
        properties.setProperty("jclouds.ssh.max-retries", "7");

        properties.setProperty("jclouds.ssh.retry-auth", "true");
        properties.setProperty(PROPERTY_EC2_GENERATE_INSTANCE_NAMES, "true");
        return properties;
    }

    public static class Builder extends AWSEC2ApiMetadata.Builder {
        protected Builder(){
            super();
            id("cloudstack-aws-ec2")
                    .version(AWSEC2AsyncClient.VERSION)
                    .name("Cloudstack specific  EC2 API")
                    .view(AWSEC2ComputeServiceContext.class)
                    .context(CONTEXT_TOKEN)
                    .defaultProperties(AWSEC2ApiMetadata.defaultProperties())
                    .defaultModules(ImmutableSet.<Class<? extends Module>>of(AWSEC2RestClientModule.class, EC2ResolveImagesModule.class, AWSEC2ComputeServiceContextModule.class));
        }

        @Override
        public CloudStackAWSEC2ApiMetadata build() {
            return new CloudStackAWSEC2ApiMetadata(this);
        }

        @Override
        public Builder fromApiMetadata(ApiMetadata in) {
            super.fromApiMetadata(in);
            return this;
        }
    }


}
