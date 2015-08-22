package com.novagee.aidong.utils.webservices;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class CallWebService
{
	private static String URL = "http://123.56.155.120/jy/";

	public static String convertStringToURL(String str) {
        String myString = null;
        myString = str.replace("%", "%25");
        myString = myString.replace("`", "%60");
        myString = myString.replace("~", "%7E");
        myString = myString.replace("^", "%5E");
        myString = myString.replace("+", "%2B");
        myString = myString.replace("-", "%2D");
        myString = myString.replace("_", "%5F");
        myString = myString.replace("*", "%2A");
        myString = myString.replace(",", "%2C");
        myString = myString.replace(".", "%2E");
        myString = myString.replace("\'", "%27");
        myString = myString.replace(" ", "%20");
        myString = myString.replace(";", "%3B");
        myString = myString.replace("=", "%3D");
        myString = myString.replace("?", "%3F");
        myString = myString.replace("@", "%40");
        myString = myString.replace("\t", "%%%");
        myString = myString.replace("$", "%24");
        myString = myString.replace("#", "%23");
        myString = myString.replace("<", "%3C");
        myString = myString.replace(">", "%3E");
        myString = myString.replace("\n", "@@@");
        myString = myString.replace("(", "%28");
        myString = myString.replace(")", "%29");
        myString = myString.replace("{", "%7B");
        myString = myString.replace("}", "%7D");
        myString = myString.replace("[", "%5B");
        myString = myString.replace("]", "%5D");
        myString = myString.replace("!", "%21");
        myString = myString.replace("&", "%26");
        myString = myString.replace("\"", "%22");
        myString = myString.replace("\\", "%68");
        myString = myString.replace("|", "%7C");
        return myString;
    }
	
	public static String Webserice_Call_Json_Multipart(MultipartEntity reqEntity , String Append_Url) throws Exception
	{
		InputStream is = null;
		String result = null;

		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL + Append_Url);

			Log.v("log", "URL: " + URL);
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			//is = entity.getContent();
			result = EntityUtils.toString(entity);
			Log.d("Result", result);

		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	return result;
	}
	
	public static String Webserice_Call_Json(ArrayList<NameValuePair> nameValuePairs , String Append_Url) throws Exception
	{
		InputStream is = null;
		String result = null;
		 
		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
			String url = URL + Append_Url + "?"+paramString;
			Log.d("URL", url);
			HttpGet httpget = new HttpGet(url);

			Log.v("log", "URL: " + URL);
//			Log.v("log", "JSON: " + jsonObject);

			//HttpParams httpParameters = new BasicHttpParams();
			int timeoutSocket = 30000;
			HttpConnectionParams.setSoTimeout(httpclient.getParams(), timeoutSocket);
			
//			httpclient.setParams(httpParameters);
//			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("data", jsonObject.toString()));
			Log.d("dsg", new UrlEncodedFormEntity(nameValuePairs).toString() + "");
			//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			//is = entity.getContent();
			result = EntityUtils.toString(entity);

		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return result;
	}
	
	public static String Webserice_Call_Json_Post(ArrayList<NameValuePair> nameValuePairs , String Append_Url) throws Exception
	{
		InputStream is = null;
		String result = null;
		 
		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(URL + Append_Url);

			Log.v("log", "URL: " + URL);
			//Log.v("log", "JSON: " + jsonObject);

			//HttpParams httpParameters = new BasicHttpParams();
			int timeoutSocket = 30000;
			HttpConnectionParams.setSoTimeout(httpclient.getParams(), timeoutSocket);
			
//			httpclient.setParams(httpParameters);
//			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("data", jsonObject.toString()));
//			Log.d("dsg", new UrlEncodedFormEntity(nameValuePairs).toString() + "");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			//is = entity.getContent();
			result = EntityUtils.toString(entity);

		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return result;
	}
}