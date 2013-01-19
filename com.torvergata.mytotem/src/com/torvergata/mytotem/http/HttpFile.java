package com.torvergata.mytotem.http;

import java.io.File;
import org.apache.http.client.HttpClient;


public class HttpFile {
	private String url;
	private File destination;
	private HttpClient httpClient;
	
	public HttpFile(String _url, HttpClient _httpClient, File _destination)
	{
		this.setUrl(_url);
		this.setHttpClient(_httpClient);
		this.setDestination(_destination);
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public File getDestination() {
		return destination;
	}

	public void setDestination(File destination) {
		this.destination = destination;
	}
}
