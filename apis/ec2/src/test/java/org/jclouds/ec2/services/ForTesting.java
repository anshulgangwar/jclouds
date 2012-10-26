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
        System.setProperty("ec2.identity","a4U6DaFOv7JTWFlEhwn_im3q2KbNTTmPPR-im0qiGWpGTwL_ULHDTwLEjxXEMinv38GgaiKv80N4TEHuzVisbQ");
        System.setProperty("ec2.credential","wR_gX6iGvhz5CHtbcewI5n3RZCu4jAuYXK3HzOq1r-FGZyxEorI07kgeX_mYTOiLtkWz8ti9ozsJLfAqOZTXYQ");
        System.setProperty("ec2.endpoint","http://10.102.125.213:7080/awsapi");
        System.setProperty("ec2.api-version","2012-08-15");
        System.setProperty("test.ec2.ebs-template","hardwareId=m1.small,imageId=AmazonEC2/3a5ee1b9-1017-4d64-85ac-b29be64a744a");
        System.setProperty("ec2.ebs-template","hardwareId=m1.small,imageId=AmazonEC2/3a5ee1b9-1017-4d64-85ac-b29be64a744a");
/*

        AMIClientLiveTest ebs = new AMIClientLiveTest();
        ebs.setupContext();
        System.out.println(ebs.runningInstances());
*/


    }

}
