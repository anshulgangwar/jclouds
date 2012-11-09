package org.jclouds.ec2.services;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 10/11/12
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
*/


public class ForTesting {
    public static void main(String args[]) {
        System.setProperty("ec2.identity","9JvjfjIFLfAZN6zY4Yt430CqxaDXGknXlSK6PHqCKlgtr6N2k-mdseSNcEGSrBcjphE3ejvpRZNL2K65mBwQ0w");
        System.setProperty("ec2.credential","JPPin_6_nK8huTKxF0E40WF8GNOyNSdpFeAh_aYIycPydLiUeax5bm2MIR0jynezLhm_4uXQjqIZorUxExyU6g");
        System.setProperty("ec2.endpoint","http://10.102.125.216:7080/awsapi");
        System.setProperty("ec2.api-version","2012-08-15");
        System.setProperty("test.ec2.ebs-template","hardwareId=m1.small,imageId=AmazonEC2/11dc70e3-1dfa-41b0-9bd5-af0d929baec8");
        System.setProperty("ec2.ebs-template","hardwareId=m1.small,imageId=AmazonEC2/11dc70e3-1dfa-41b0-9bd5-af0d929baec8");


        /*AMIClientLiveTest ebs = new AMIClientLiveTest();
        ebs.setupContext();*/
       // System.out.println(ebs.runningInstances());



    }

}
