package com.torvergata.mytotem.http;

import android.os.AsyncTask;
import android.os.Message;

public class AsyncHttp extends AsyncTask<Object, Void, String> {
    private String httpResponse;
        
    public AsyncHttp()
    {
    	this.httpResponse = "";
    }
    
	@Override
	protected String doInBackground(Object... params) {
		// TODO Auto-generated method stubs
		HttpRequestObject o = (HttpRequestObject)params[0];
		String response = o.execute();
		this.httpResponse = response;
		return response;
	}

    public String getResponse()
    {
    	return this.httpResponse;
    }
    
    @Override
    protected void onPostExecute(String result) {
    	
    	
    }
 }
