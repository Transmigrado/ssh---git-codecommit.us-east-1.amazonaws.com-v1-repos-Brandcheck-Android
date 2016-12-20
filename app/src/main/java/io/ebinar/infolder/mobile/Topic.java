package io.ebinar.infolder.mobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

import io.ebinar.infolder.R;
import io.ebinar.infolder.mobile.push.GCMTokenHelper;

/**
 * Creado Por jorgeacostaalvarado el 14-09-16.
 */
public class Topic {

    private GCMTokenHelper gcmTokenHelper;
    private String topicName;
    private Callback callback;
    private String app = "arn:aws:sns:us-east-1:389956195809:app/GCM/Brandcheck-Android";

    public Topic(Context context){
        gcmTokenHelper = new GCMTokenHelper(context, context.getResources().getString(R.string.GOOGLE_CLOUD_MESSAGING_SENDER_ID));
    }

    public void subscribeToTopic(String topicName, Callback callback){

        this.topicName = topicName.replace(":","-");
        this.callback = callback;

        asyncTask task = new asyncTask();
        task.execute();
    }

    private class asyncTask extends AsyncTask<String, Void, String> {

        AWSCredentials credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIAJA3VKAATD7NMAK6A";
            }

            @Override
            public String getAWSSecretKey() {
                return "EUGNF/gohBixZYFFAcGWVkA/rJk7wdBJfAU81j78";
            }
        };



        @Override
        protected String doInBackground(String... params) {

            gcmTokenHelper.updateGCMToken();
            String token = gcmTokenHelper.getGCMToken();

            AmazonSNSClient snsClient = new AmazonSNSClient(credentials);
            snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

            String topicArn = "arn:aws:sns:us-east-1:973480912605:" + topicName.replace(":","-");

            CreatePlatformEndpointRequest createEndpointRequest = new CreatePlatformEndpointRequest();

            createEndpointRequest.setPlatformApplicationArn(app);
            createEndpointRequest.setToken(token);

            CreatePlatformEndpointResult endpointResult = snsClient.createPlatformEndpoint(createEndpointRequest);

            Log.d("infolder",">> RESULT " + endpointResult.toString());
            /*
            SubscribeRequest subRequest = new SubscribeRequest(topicArn, "application", endpointResult.getEndpointArn());
            if(snsClient!= null && subRequest!=null) {
                try {
                    snsClient.subscribe(subRequest);
                }catch (Exception e){}
            }

            */
            return "";

        }

        protected void onPostExecute(String result) {

            if(callback != null){
                callback.done();
            }else{
                callback.error();
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    public interface Callback {
        void done();
        void error();
    }
}