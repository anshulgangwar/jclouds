package org.jclouds.cloudstack.query.ec2.domain;

import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/3/12
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackQueryEC2Reservation<T extends RunningInstance> extends Reservation {
    public CloudStackQueryEC2Reservation(String region, Iterable<String> groupNames, Iterable instances, @Nullable String ownerId, @Nullable String requesterId, @Nullable String reservationId) {
        super("AmazonEC2", groupNames, instances, ownerId, requesterId, reservationId);
    }
}
