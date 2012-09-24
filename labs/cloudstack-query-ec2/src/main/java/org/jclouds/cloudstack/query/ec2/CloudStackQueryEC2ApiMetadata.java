package org.jclouds.cloudstack.query.ec2;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;

import java.net.URI;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/17/12
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackQueryEC2ApiMetadata extends EC2ApiMetadata{
    /** The serialVersionUID */
    private static final long serialVersionUID = 3060225665040763827L;

    private static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return builder().fromApiMetadata(this);
    }

    public CloudStackQueryEC2ApiMetadata() {
        this(builder());
    }

    protected CloudStackQueryEC2ApiMetadata(Builder builder) {
        super(builder);
    }

    public static Properties defaultProperties() {
        Properties properties = EC2ApiMetadata.defaultProperties();
        return properties;
    }

    public static class Builder extends EC2ApiMetadata.Builder {
        protected Builder() {
            super(EC2Client.class, EC2AsyncClient.class);
            id("cloudstack-query-ec2")
                    .name("CloudStackEC2 (EC2 clone) API")
                    .version("2010-11-15")
                    .defaultEndpoint("http://10.147.39.67:7080/awsapi")
                    .documentation(URI.create("http://docs.cloudstack.org/CloudBridge_Documentation"))
                    .defaultProperties(CloudStackQueryEC2ApiMetadata.defaultProperties());
        }

        @Override
        public CloudStackQueryEC2ApiMetadata build() {
            return new CloudStackQueryEC2ApiMetadata(this);
        }

        @Override
        public Builder fromApiMetadata(ApiMetadata in) {
            super.fromApiMetadata(in);
            return this;
        }
    }

}
