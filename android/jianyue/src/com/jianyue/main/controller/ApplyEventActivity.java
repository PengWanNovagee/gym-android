package com.jianyue.main.controller;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianyue.DataTask.ApplyEventDataTask;
import com.jianyue.utils.ClassAPIResponse;
import com.jianyue.utils.CustomDialog;
import com.jianyue.utils.GlobalData;
import com.jianyue.utils.Internet_Check;
import com.jianyue.utils.SessionManager;
import com.jianyue.utils.StaticMethodsUtility;
import com.jianyue.webservices.WebElements;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ApplyEventActivity extends Activity {

	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String eventId;
	private String mobile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_event);
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.color.main_bg_color)
		.showImageForEmptyUri(R.color.main_bg_color).cacheInMemory()
		.cacheOnDisc().build();
		imageLoader = ImageLoader.getInstance();
		Intent intent = getIntent();
        eventId = intent.getStringExtra("id");
        TextView tvEventTitle = (TextView)findViewById(R.id.event_title);
		tvEventTitle.setText(intent.getStringExtra("title"));
		ImageView imageView = (ImageView) findViewById(R.id.event_pic);
		String pic = intent.getStringExtra("pic");
		if(!(pic == null || "".equals(pic))){
			System.out.println("event pic:"+pic);
			imageLoader.displayImage(pic, imageView,
					options, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(
								String imageUri, View view) {
						//	pb.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(
								String imageUri, View view,
								FailReason failReason) {
						//	pb.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(
								String imageUri, View view,
								Bitmap loadedImage) {
						//	pb.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(
								String imageUri, View view) {
						//	pb.setVisibility(View.GONE);
						}
					});	
				
			
		}
		TextView tvEventDescription = (TextView)findViewById(R.id.event_description);
		tvEventDescription.setText(intent.getStringExtra("description"));
		ImageView backImageView = (ImageView) findViewById(R.id.event_back);
		backImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView tvApplyEvent = (TextView)findViewById(R.id.apply_event_button);
		tvApplyEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				try{
					TelephonyManager phoneMgr=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
					mobile = phoneMgr.getLine1Number();
				}catch(Exception e){
					e.printStackTrace();
				}
				//如果无法获取手机号码，则让用户自己输入
				if(mobile == null || "".equals(mobile)){
					final EditText inputServer = new EditText(ApplyEventActivity.this);
					inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
					inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)}); 
					new CustomDialog.Builder(ApplyEventActivity.this).setTitle("请输入您的手机号码").setContentView(inputServer)
						     .setPositiveButton("确定",
						    		 new DialogInterface.OnClickListener() {
						             @Override
						             public void onClick(DialogInterface dialog, int which) {
						            	 mobile = inputServer.getText().toString();
						            	 dialog.dismiss();
						            	 applyEventWebservice();
						           }
						        })
						     .setNegativeButton("取消", 
						    		 new DialogInterface.OnClickListener() {
					             @Override
					             public void onClick(DialogInterface dialog, int which) {
					            	 dialog.dismiss();
					           }
					        }).create().show();
				}else{
					applyEventWebservice();
				}
				
				
				
		
			}
		});
        System.out.println("eventId:"+eventId);
	}

	private void applyEventWebservice() {
		final ClassAPIResponse apiResponse = new ClassAPIResponse();
		String append_url = "r/event/"+eventId+"/apply";
		ApplyEventDataTask task = new ApplyEventDataTask(ApplyEventActivity.this,
				apiResponse , append_url ) {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if (result.equals(GlobalData.FAIL)) {
					if (!Internet_Check
							.checkInternetConnection(ApplyEventActivity.this)) {
						StaticMethodsUtility.showNegativeToast(
								ApplyEventActivity.this,
								getResources().getString(R.string.no_internet));
					}
					return;
				}
				
				if(apiResponse.ack.equalsIgnoreCase("Success"))
				{
					StaticMethodsUtility.showNegativeToast(
							ApplyEventActivity.this,"预约成功!");
					finish();
					overridePendingTransition(R.anim.slide_down, R.anim.slide_down_out);
				}
				
			}
		};
		
		if (Internet_Check.checkInternetConnection(ApplyEventActivity.this)) {
			try {

				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					
					reqEntity.addPart(WebElements.FETCH_OR_APPLY_EVENT.UUID,
							new StringBody(SessionManager.getUUID(ApplyEventActivity.this)));
					reqEntity.addPart(WebElements.FETCH_OR_APPLY_EVENT.PHONE_NUMBER,
							new StringBody(mobile));
					reqEntity.addPart(WebElements.FETCH_OR_APPLY_EVENT.TIMESTAMP,
							new StringBody(String.valueOf(System.currentTimeMillis())));
					task.execute(reqEntity);
				} catch (Exception e) {
					e.printStackTrace();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			StaticMethodsUtility.showNegativeToast(ApplyEventActivity.this,
					getResources().getString(R.string.no_internet));
		}


	}
	

}
