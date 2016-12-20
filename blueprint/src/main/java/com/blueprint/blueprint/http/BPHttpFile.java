package com.blueprint.blueprint.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Creado Por jorgeacostaalvarado el 27-12-15.
 */
public class BPHttpFile extends BPHttp {


    private DownloadFileServiceTask taskFile;

    @Override
    public void requestURL(String url){
        taskFile = new DownloadFileServiceTask();
        taskFile.execute(url);
    }

    private class DownloadFileServiceTask extends AsyncTask<String, Void, byte[]> {

        @Override
        protected byte[] doInBackground(String... params) {

            try{


                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("x-api-key","QCFM6DpLdO2RkSOnNsfk9a8hXgjGuAiQ7YXYiHXh");


                InputStream in = conn.getInputStream();




                if(in != null){

                    ByteArrayOutputStream contentStream = readSourceContent(in);
                    byte[] byteArr =   contentStream.toByteArray();
                    return byteArr;
                }


                return new byte[0];

            } catch(Exception e){
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
                throw new IOException("Exception occurred while reading content", e);
            }

            return outputStream;
        }


        void putHeaders(HttpRequestBase requestBase){
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

            getCallback(0).doneBytes(result);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }


}
