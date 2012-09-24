package org.jclouds.ec2.config;

import com.google.common.reflect.TypeToken;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 9/13/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedEC2RestClientModule<S extends EC2Client, A extends EC2AsyncClient> extends EC2RestClientModule {
    public ExtendedEC2RestClientModule() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected ExtendedEC2RestClientModule(TypeToken syncClientType, TypeToken asyncClientType, Map<Class<?>, Class<?>> sync2Async) {
        super(syncClientType, asyncClientType, sync2Async);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void installLocations() {
        super.installLocations();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
