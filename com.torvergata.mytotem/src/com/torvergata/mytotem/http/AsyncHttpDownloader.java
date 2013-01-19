package com.torvergata.mytotem.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpDownloader extends AsyncTask<HttpFile, Integer, String> {
 @Override
 protected String doInBackground(HttpFile... general) {
	 
	 
     try {    	 
    	// Log.v("Downloader", "Source: " + sUrl[0]);
    	// Log.v("Downloader", "Destin: " +sUrl[1]+"/" + sUrl[2]);
    	 String url = general[0].getUrl();
    	 File destination = general[0].getDestination();
    	 HttpClient httpClient = general[0].getHttpClient();
    	 
    	 FileOutputStream f = new FileOutputStream(destination);
      	//HttpClient httpclient = new DefaultHttpClient();
      	HttpGet httpget = new HttpGet(url);
      	HttpResponse xresponse = httpClient.execute(httpget);
      	HttpEntity entity = xresponse.getEntity();
      	if (entity != null) {
      	   // long len = entity.getContentLength();
      	    InputStream inputStream = entity.getContent();
      	    // write the file to whether you want it.
      	   byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = inputStream.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
            }
            f.close();
      	}
      	
     } catch (Exception e) {
    	 e.printStackTrace();
    	 Log.v("Error", "Exeption -> " + e.toString());
     }
     return null;
 }
 
}
 


/*

*/