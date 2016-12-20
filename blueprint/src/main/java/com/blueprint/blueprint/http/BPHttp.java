package com.blueprint.blueprint.http;

import android.os.AsyncTask;
import android.util.Log;

import com.blueprint.blueprint.object.BPObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Creado por jorgeacostaalvarado on 12-03-15.
 */
public class BPHttp {

    private DownloadServiceTask task;
    private ArrayList<BeeRequestCallback> callbacks;
    private HashMap<String,String> headers;


    private BPHttpType mode= BPHttpType.GET;
    private String postMessage = null;
    private byte[] bytes = null;

    public BPHttp(){
        task = new DownloadServiceTask();
        callbacks = new ArrayList<>();
        headers = new HashMap<>();
    }

    public void requestURL(String url){
        task.execute(url);
    }

    public void requestURL(String url, BPHttpType mode){
        requestURL(url);
        this.mode = mode;
    }

    public int addCallback(BeeRequestCallback callback){
        callbacks.add(callback);
        return callbacks.size();
    }

    public BeeRequestCallback getCallback(int index){
        return callbacks.get(index);
    }

    public void addHeader(String key,String value){
       headers.put(key,value);

    }

    public void setEntity(String postMessage){
        this.postMessage = postMessage;
    }

    private void doneAllCallbacks(BPObject data){
        for(int i = 0; i < callbacks.size();i++){
            callbacks.get(i).done(data);
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    private class DownloadServiceTask extends AsyncTask<String, Void, String> {


        BPObject data = new BPObject();

        @Override
        protected String doInBackground(String... params) {

            try{

                DefaultHttpClient defaultClient =  new DefaultHttpClient();


                HttpResponse httpResponse = null;


                switch (mode){
                    case POST:
                        HttpPost httpPostRequest = new HttpPost(params[0]);

                        if(postMessage!=null) {

                            ByteArrayEntity entity = new ByteArrayEntity(postMessage.getBytes("UTF8"));
                            entity.setContentType("application/json");
                            httpPostRequest.setEntity(entity);
                        } else if(bytes!=null){
                            ByteArrayEntity entity = new ByteArrayEntity(bytes);
                            entity.setContentType("image/jpeg");
                            httpPostRequest.setEntity(entity);

                        }


                        putHeaders(httpPostRequest);

                        httpResponse = defaultClient.execute(httpPostRequest);

                        break;
                    case PUT:
                        HttpPut httpPutRequest = new HttpPut(params[0]);

                        if(postMessage!=null) {
                            httpPutRequest.setEntity(new ByteArrayEntity(postMessage.getBytes("UTF8")));
                        }

                        putHeaders(httpPutRequest);
                        httpResponse = defaultClient.execute(httpPutRequest);
                        break;
                    case GET:
                        HttpGet httpGetRequest = new HttpGet(params[0]);
                        putHeaders(httpGetRequest);
                        httpResponse  = defaultClient.execute(httpGetRequest);
                        break;
                }



                int responseCode= httpResponse.getStatusLine().getStatusCode();
                data.setInt("responseCode", responseCode);

                if(httpResponse.getEntity()==null){
                    return "{\"data\":\"entity null\"}";
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                String aux;

                while ((aux = reader.readLine()) != null) {
                    builder.append(aux);
                }

                Log.d("brandcheck",">> " +  builder.toString());

                return builder.toString();

            } catch(Exception e){

                Log.d("brandcheck",">> " +  e.toString());
                return "";
            }



        }

        void putHeaders(HttpRequestBase requestBase){
            Iterator it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry)it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                requestBase.addHeader(key,value);
            }
        }

        protected void onPostExecute(String result) {





            if(result!=null){
                data.setString("data",result);
                doneAllCallbacks(data);
            }else{
                data.setString("data",result);
                doneAllCallbacks(data);
            }

            Log.d("brandcheck","data : " + data.toString());
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    public interface BeeRequestCallback{
        void done(BPObject data);
        void doneBytes(byte[] data);
    }

}
