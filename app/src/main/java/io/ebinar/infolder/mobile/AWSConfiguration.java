package io.ebinar.infolder.mobile;

import com.amazonaws.regions.Regions;

public class AWSConfiguration {


    public static final String AWS_MOBILEHUB_USER_AGENT = "MobileHub feaf5e55-5bca-426f-a074-249fe78c0dc6 aws-my-sample-app-android-v0.8";

    public static final Regions AMAZON_COGNITO_REGION = Regions.fromName("us-east-1");
    public static final String AMAZON_COGNITO_IDENTITY_POOL_ID = "us-east-1:803d169f-0de3-4dd8-9318-2d33079a6772";

    public static final String GOOGLE_CLOUD_MESSAGING_API_KEY = "AIzaSyCwkYmydfipsXTV7mvZ7juQnnQ8Pqn-oZY";
    public static final String GOOGLE_CLOUD_MESSAGING_SENDER_ID = "359976135321";

    public static final String AMAZON_SNS_PLATFORM_APPLICATION_ARN = "arn:aws:sns:us-east-1:973480912605:app/GCM/Dal";
    public static final Regions AMAZON_SNS_REGION = Regions.fromName("us-east-1");

    public static final String AMAZON_SNS_DEFAULT_TOPIC_ARN = "arn:aws:sns:us-east-1:973480912605:Test";

    public static final String[] AMAZON_SNS_TOPIC_ARNS = {"arn:aws:sns:us-east-1:973480912605:Test"};
}
