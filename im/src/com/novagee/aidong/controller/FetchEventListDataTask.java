package com.novagee.aidong.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.novagee.aidong.model.Event;
import com.novagee.aidong.utils.Constant;
import com.novagee.aidong.utils.webservices.APIResponse;
import com.novagee.aidong.utils.webservices.CallWebService;
import com.novagee.aidong.utils.webservices.WebElements;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class FetchEventListDataTask extends AsyncTask<String, Void, String>
{
	private Context context;
	private APIResponse apiResponse;
	private String append_url;
	private ProgressDialog mDialog;
	private List<Event> events_list;
	private boolean is_refresh = false;

	public FetchEventListDataTask(Context context, APIResponse apiResponse , String append_url , List<Event> events_list , boolean is_refresh)
	{
		this.context = context;
		this.apiResponse = apiResponse;
		this.append_url = append_url;
		this.events_list = events_list;
		this.is_refresh = is_refresh;
	}

	protected void onPreExecute()
	{
		if(!is_refresh)
		{
			/*
			mDialog = new ProgressDialog(context);
			mDialog.setMessage("获取活动数据...  ");
			mDialog.setCancelable(false);
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
			*/
		}
	}

	protected String doInBackground(String... params)
	{
		try
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//	nameValuePairs.add(new BasicNameValuePair(WebElements.NEARBYPEOPLE.UUID, SessionManager.getUUID(context)));
		//	nameValuePairs.add(new BasicNameValuePair(WebElements.NEARBYPEOPLE.PAGENUMBER, page_no));
		//	nameValuePairs.add(new BasicNameValuePair(WebElements.NEARBYPEOPLE.PAGESIZE, page_size));
			nameValuePairs.add(new BasicNameValuePair(WebElements.NEARBYPEOPLE.TIMESTAMP, String.valueOf(System.currentTimeMillis())));
			String result = CallWebService.Webserice_Call_Json(nameValuePairs , append_url);
			Log.d("result", result);

			if (result != null)
			{
				parceJsonResponse(result);
			}
			return Constant.SUCCESS;
		} catch (Exception e)
		{
			e.printStackTrace();
			return Constant.FAIL;
		}
	}

	private void parceJsonResponse(String result)
	{
		try {
			JSONObject j_result = new JSONObject(result);
			apiResponse.ack = j_result.getString("ack");
			if(apiResponse.ack.equalsIgnoreCase("Success"))
			{
			//	JSONObject j_obj = j_result.getJSONObject("object");
			//	apiResponse.count = j_obj.getString("count");
				events_list.clear();
				JSONArray j_frineds = j_result.getJSONArray("object");
				for(int i = 0 ; i < j_frineds.length() ; i++)
				{
					JSONObject j_obj1 = j_frineds.getJSONObject(i);
					Event obj = new Event();
					obj.setId(j_obj1.getString("id"));
					obj.setTitle(j_obj1.getString("title"));
					obj.setDescription(j_obj1.getString("description").replace("\\n", ""));
					obj.setPic(j_obj1.getString("pic"));
					obj.setStarttime(j_obj1.getString("starttime"));
					obj.setEndtime(j_obj1.getString("endtime"));
					obj.setAddress(j_obj1.getString("address"));	
					obj.setFee(j_obj1.getString("fee"));
					events_list.add(obj);
				}
			}
			else
			{
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onPostExecute(String result)
	{
		if(!is_refresh)
		{/*
			try
			{
				mDialog.dismiss();
			} catch (Exception e)
			{
	
			}*/
		}
	}

}
