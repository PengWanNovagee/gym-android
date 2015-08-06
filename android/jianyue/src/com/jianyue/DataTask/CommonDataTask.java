package com.jianyue.DataTask;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jianyue.utils.ClassAPIResponse;
import com.jianyue.utils.GlobalData;
import com.jianyue.utils.SessionManager;
import com.jianyue.webservices.CallWebService;
import com.jianyue.webservices.WebElements;

public class CommonDataTask extends AsyncTask<String, Void, String> {
	private Context context;
	private ClassAPIResponse apiResponse;
	private String append_url, id , message;
	private ProgressDialog mDialog;
	private int flag = 0;

	public CommonDataTask(Context context, ClassAPIResponse apiResponse,
			String append_url, String id , int flag , String message) {
		this.context = context;
		this.apiResponse = apiResponse;
		this.append_url = append_url;
		this.message = message;
		this.flag = flag;
		this.id = id;
	}

	protected void onPreExecute() {
		mDialog = new ProgressDialog(context);
		mDialog.setMessage(message+"...  ");
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.show();
	}

	protected String doInBackground(String... params) {
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(
					WebElements.UUID, SessionManager.getUUID(context)));
			if(flag == 0)
			{
				nameValuePairs.add(new BasicNameValuePair(
						WebElements.RECEIVER, id));
			}
			else if(flag == 1)
			{
				nameValuePairs.add(new BasicNameValuePair(
						WebElements.MESSAGEID, id));
			}
			else
			{
				nameValuePairs.add(new BasicNameValuePair(
						WebElements.FRIENDID, id));
			}
			String result = CallWebService.Webserice_Call_Json_Post(nameValuePairs,
					append_url);
			Log.d("result", result);

			if (result != null) {
				parceJsonResponse(result);
			}
			return GlobalData.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return GlobalData.FAIL;
		}
	}

	private void parceJsonResponse(String result) {
		try {
			JSONObject j_result = new JSONObject(result);
			apiResponse.ack = j_result.getString("ack");
			if (apiResponse.ack.equalsIgnoreCase("Success")) {
				
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPostExecute(String result) {
			try {
				mDialog.dismiss();
			} catch (Exception e) {

		}
	}

}
