package com.jianyue.main.controller;

import java.io.File;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.jianyue.DataTask.SignupDataTask;
import com.jianyue.utils.ClassAPIResponse;
import com.jianyue.utils.GlobalData;
import com.jianyue.utils.Internet_Check;
import com.jianyue.utils.SessionManager;
import com.jianyue.utils.StaticMethodsUtility;
import com.jianyue.webservices.WebElements;

public class SettingActivity extends Activity{
	
	Button btnMale , btnFemale , btnOther;
	ImageView ivGender , ivOk , ivPrefCamera , ivPrefFriendList;
	
	String Nickname , Gender , insterestedIn = "M";
	
	String width = "" , height = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pref_screen);
		
		Log.d("log_tag", "Setting screen entered");
		
		Nickname = getIntent().getExtras().getString("Nickname");
		Gender = getIntent().getExtras().getString("Gender");
		
		btnMale = (Button)findViewById(R.id.btnMale);
		btnFemale = (Button)findViewById(R.id.btnFemale);
		btnOther = (Button)findViewById(R.id.btnOther);
		ivGender = (ImageView)findViewById(R.id.ivGender);
		ivOk = (ImageView)findViewById(R.id.ivOk);
		ivPrefCamera = (ImageView)findViewById(R.id.ivPrefCamera);
		ivPrefFriendList = (ImageView)findViewById(R.id.ivPrefFriendList);
		
		ivPrefCamera.setVisibility(View.GONE);
		ivPrefFriendList.setVisibility(View.GONE);

		btnMale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				insterestedIn = "M";
				ivGender.setBackgroundResource(R.drawable.gender_m);
			}
		});

		btnFemale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				insterestedIn = "F";
				ivGender.setBackgroundResource(R.drawable.gender_f);
			}
		});

		btnOther.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				insterestedIn = "O";
				ivGender.setBackgroundResource(R.drawable.gender_o);
			}
		});
		
		/*ivPrefCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SettingActivity.this,TakePictureActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_up,R.anim.slide_up_out);
			}
		});*/
		
		ivOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(!GlobalData.sd_path.equalsIgnoreCase(""))
				{
					if(GlobalData.bmp != null)
					{
						width = String.valueOf(GlobalData.bmp.getWidth());
						height = String.valueOf(GlobalData.bmp.getHeight());
					}
					requestSignupWebservice();
				}
				else
				{
					StaticMethodsUtility.showNegativeToast(SettingActivity.this, "Please select picture");
				}
			}
		});
	}
	
	private void requestSignupWebservice() {

		final ClassAPIResponse apiResponse = new ClassAPIResponse();

		SignupDataTask task = new SignupDataTask(SettingActivity.this,
				apiResponse , "r/user/signin" ) {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if (result.equals(GlobalData.FAIL)) {
					if (!Internet_Check
							.checkInternetConnection(SettingActivity.this)) {
						StaticMethodsUtility.showNegativeToast(
								SettingActivity.this,
								getResources().getString(R.string.no_internet));
					}
					return;
				}
				
				if(apiResponse.ack.equalsIgnoreCase("Success"))
				{
					finish();
					Intent i = new Intent(SettingActivity.this , MainActivity.class);
					startActivity(i);
				}
				
			}
		};

		if (Internet_Check.checkInternetConnection(SettingActivity.this)) {
			try {

				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					
					reqEntity.addPart(WebElements.SIGNUP.NAME,
							new StringBody(Nickname));
					reqEntity.addPart(WebElements.SIGNUP.GENDER,
							new StringBody(Gender));
					reqEntity.addPart(WebElements.SIGNUP.INTERESTIN,
							new StringBody(insterestedIn));
					reqEntity.addPart(WebElements.SIGNUP.DEVICETYPE, new StringBody("android"));
					Log.d("log_tag", "Insterested in  " + insterestedIn);
					Log.d("LATLONG", "Location  "+SessionManager.getLatitude(SettingActivity.this) + "  " + SessionManager.getLongitude(SettingActivity.this));
					if(SessionManager.getLatitude(SettingActivity.this).equalsIgnoreCase(""))
					{
						reqEntity.addPart(WebElements.SIGNUP.LAT,
								new StringBody("22.959991"));
						reqEntity.addPart(WebElements.SIGNUP.LNG,
								new StringBody("72.909516"));
					}
					else
					{
						reqEntity.addPart(WebElements.SIGNUP.LAT,
								new StringBody(SessionManager.getLatitude(SettingActivity.this)));
						reqEntity.addPart(WebElements.SIGNUP.LNG,
								new StringBody(SessionManager.getLongitude(SettingActivity.this)));
					}
					reqEntity.addPart(WebElements.SIGNUP.DEVICETOKEN,
							new StringBody(""));
					reqEntity.addPart(WebElements.SIGNUP.HEIGHT,
							new StringBody(height));
					reqEntity.addPart(WebElements.SIGNUP.WIDTH,
							new StringBody(width));
					reqEntity.addPart(WebElements.SIGNUP.PIC ,
							new FileBody((new File(GlobalData.sd_path))));
					task.execute(reqEntity);
				} catch (Exception e) {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			StaticMethodsUtility.showNegativeToast(SettingActivity.this,
					getResources().getString(R.string.no_internet));
		}
	}
}
