package com.torvergata.mytotem.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;


public class HttpRequestObject {
	private String destinationUrl;
	private String httpRequestMethod;
	private MultipartEntity entity_multipart;
	private List<NameValuePair> entity;
	private String httpRequestResponse;
	private boolean isMultipart;
	
	private HttpClient httpClient;
	HttpContext localContext;

	public HttpRequestObject(String destinationUrl, String httpRequestMethod)
	{
		this.destinationUrl = destinationUrl;
		this.httpRequestMethod = httpRequestMethod;
		this.entity_multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		this.entity = new ArrayList<NameValuePair>(2);
		this.httpClient = new DefaultHttpClient();
		this.localContext = new BasicHttpContext();
		this.httpClient = new DefaultHttpClient();
		this.isMultipart = false;
	}

	public HttpRequestObject()
	{
		this.entity_multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		this.entity = new ArrayList<NameValuePair>(2);
		this.httpClient = new DefaultHttpClient();
		this.localContext = new BasicHttpContext();
		this.isMultipart = false;
	}
	
	public HttpRequestObject(String destinationUrl, String httpRequestMethod, List<NameValuePair> paramsList, boolean isMultipart)
	{
		this.isMultipart = isMultipart;
		this.destinationUrl = destinationUrl;
		this.httpRequestMethod = httpRequestMethod;
		this.entity = new ArrayList<NameValuePair>(2);
		this.entity_multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		for(int i=0; i<paramsList.size(); i++)
		{
			if(isMultipart)
				addParam(paramsList.get(i).getName(), paramsList.get(i).getValue());
			else
				entity.add(new BasicNameValuePair(paramsList.get(i).getName(), paramsList.get(i).getValue()));
		}
		
	}
	
	public void addHttpClient(HttpClient hc)
	{
		this.httpClient = hc;
	}
	
	public HttpClient getHttpClient()
	{
		return httpClient;
	}
	
	public void addParam(String paramName, String paramValue, boolean isAFile)
	{
		if(isAFile)
		{
			if(isMultipart)
				entity_multipart.addPart(paramName, new FileBody(new File (paramValue))); // In this case @paramValue is a file path
		}
		else
		{
			try {
				if(isMultipart)
					entity_multipart.addPart(paramName, new StringBody(paramValue)); // In this case @paramValue is just a string :)
				else
					entity.add(new BasicNameValuePair(paramName, paramValue));
			} catch (UnsupportedEncodingException e) {
				Log.v("Errore", "Unsupported");
				e.printStackTrace();
			} 
		}
	}
	
	public void addParam(String paramName, String paramValue)
	{
		addParam(paramName, paramValue, false);
	}
	
	public void setURL(String url)
	{
		this.destinationUrl = url;
	}
	
	public void setMethod(String method)
	{
		this.httpRequestMethod = method;
	}

	/*
	 * Requires Class fields already filled with correct values
	 */

	public String execute(boolean resetConnectionHandler)
	{
		if(resetConnectionHandler)
		{
			this.httpRequestMethod = new String(); 
			this.localContext = new BasicHttpContext();
			this.httpClient = new DefaultHttpClient();
		}
		
		Log.v("http execute", "url = " + destinationUrl);
		Log.v("http execute", "method = " + httpRequestMethod);
		HttpResponse response = null;
		
		if(httpRequestMethod.toUpperCase().matches("POST"))
		{
			// Method = POST
			HttpPost http = new HttpPost(destinationUrl);

			
			try {
				if(isMultipart)
					http.setEntity(entity_multipart);
				else
					http.setEntity(new UrlEncodedFormEntity(entity));

	            response = httpClient.execute(http, localContext);
				this.httpRequestResponse = inputStreamToString(response.getEntity().getContent()).toString();			
			} catch (Exception e) {
				Log.v("Exception", "# Error: " + e.toString());
				e.printStackTrace();
			}
		}
		else if(httpRequestMethod.toUpperCase().matches("GET"))
		{
			// Method = GET
			HttpGet http = new HttpGet(destinationUrl);
			try {
				response = httpClient.execute(http);
				this.httpRequestResponse = inputStreamToString(response.getEntity().getContent()).toString();
				return httpRequestResponse;
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				Log.v("Exception", "#3 Error: " + e1.toString());
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.v("Exception", "#4 Error: " + e1.toString());
				e1.printStackTrace();
			}
		}
		
		return this.httpRequestResponse;
	}
	
	public String execute()
	{
		return execute(false);
	}

    public String getResponse()
    {
    	return this.httpRequestResponse;
    }
    
    /*
     * So I don't need more libs imports (like apache stuff)
     */
    public static String inputStreamToString(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
    }

	public boolean isMultipart() {
		return isMultipart;
	}

	public void setMultipart(boolean isMultipart) {
		this.isMultipart = isMultipart;
	}
	
	public void purgeParamsList()
	{		
		if(isMultipart)
			entity_multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		else
			entity = new ArrayList<NameValuePair>(2);
	}
}
