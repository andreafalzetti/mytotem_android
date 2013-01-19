package com.torvergata.mytotem.http;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncDownloader extends AsyncTask<String, Integer, String> {
 @Override
 protected String doInBackground(String... sUrl) {
	 
	 
     try {    	 
    	 Log.v("Downloader", "Source: " + sUrl[0]);
    	 Log.v("Downloader", "Destin: " +sUrl[1]+"/" + sUrl[2]);
         URL url = new URL(sUrl[0]);
         URLConnection connection = url.openConnection();
         connection.connect();
         // this will be useful so that you can show a typical 0-100% progress bar
         int fileLength = connection.getContentLength();
         Log.v("Downloader", "Total Length: " +fileLength);

         // download the file
         InputStream iii = url.openStream();
         InputStream input = new BufferedInputStream(iii);
         OutputStream output = new FileOutputStream(sUrl[1]+"/" + sUrl[2]);

         byte data[] = new byte[1024];
         long total = 0;
         int count = -9999;
         while ((count = input.read(data)) != -1) {
        	 
             total += count;
             Log.v("Publish", "totale = " + (int) (total * 100 / fileLength));
             // publishing the progress....
             publishProgress((int) (total * 100 / fileLength));
             output.write(data, 0, count);
         }

         output.flush();
         output.close();
         input.close();
     } catch (Exception e) {
    	 e.printStackTrace();
    	 Log.v("Error", "Exeption -> " + e.toString());
     }
     return null;
 }
 
}
 
