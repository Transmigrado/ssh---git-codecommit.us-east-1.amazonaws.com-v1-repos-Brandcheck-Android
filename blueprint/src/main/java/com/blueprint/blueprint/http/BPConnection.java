package com.blueprint.blueprint.http;

import android.os.AsyncTask;
import android.util.Log;

import com.blueprint.blueprint.object.BPObject;

import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creado Por jorgeacostaalvarado el 02-05-16.
 */
public class BPConnection {

    public enum MethodType {
        GET,
        POST,
        PUT,
        DELETE;

        @Override
        public String toString() {
            switch(this) {
                case GET: return "GET";
                case POST: return "POST";
                case DELETE: return "DELETE";
                case PUT: return "PUT";
                default: throw new IllegalArgumentException();
            }
        }
    }

    private MethodType method = MethodType.GET;
    private Task task;
    private Callback callback;
    private HashMap<String,String> properties = new HashMap<>();
    private JSONObject body = null;

    public BPConnection(){
        task = new Task();
    }

    public void setBody(JSONObject body){
        this.body = body;
    }

    public void request(String url,Callback callback){
        task.execute(url);
        this.callback = callback;
    }

    public void setMethod(MethodType method){
        this.method = method;
    }
    public void addRequestProperty(String key,String value)
    {
        properties.put(key, value);
    }


    private class Task extends AsyncTask<String, Void, byte[]> {

        @Override
        protected byte[] doInBackground(String... params) {

            try{


                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Iterator it = properties.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry e = (Map.Entry)it.next();
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    conn.addRequestProperty(key,value);
                }

                conn.setDoInput(true);
                conn.setDoOutput(true);


                byte[] outputInBytes = body.toString().getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write( outputInBytes );
                os.close();


                conn.setRequestMethod("POST");


                InputStream in = conn.getInputStream();


                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    if(in != null){

                        ByteArrayOutputStream contentStream = readSourceContent(in);
                        byte[] byteArr =   contentStream.toByteArray();
                        return byteArr;
                    }
                }



                return new byte[0];

            } catch(Exception e){
                Log.d("cpech",">> " + e.toString());
                return new byte[0];
            }


        }

        public  ByteArrayOutputStream readSourceContent(InputStream inputStream) throws IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            int nextChar;
            try {
                while ((nextChar = inputStream.read()) != -1) {
                    outputStream.write(nextChar);
                }
                outputStream.flush();
            } catch (IOException e) {
                Log.d("cpech",">> " + e.toString());
                throw new IOException("Exception occurred while reading content", e);
            }

            return outputStream;
        }


        public  void putHeaders(HttpRequestBase requestBase){
            /*
            Iterator it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry)it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                requestBase.addHeader(key,value);
            }
            */
        }

        protected void onPostExecute(byte[] result) {

            if(callback!=null){
                callback.done(result);
            }

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    public interface Callback{
        void done(byte[] result);
    }
}
