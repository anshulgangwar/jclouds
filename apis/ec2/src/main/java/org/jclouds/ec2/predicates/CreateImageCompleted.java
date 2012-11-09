package org.jclouds.ec2.predicates;

import com.google.common.base.Predicate;
import org.jclouds.javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/31/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateImageCompleted implements Predicate<String> {

    @Override
    public boolean apply(@Nullable String input) {
        return false;
    }
}
