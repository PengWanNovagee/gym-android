package com.jianyue.main.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.fireking.app.imagelib.entity.ImageBean;
import org.fireking.app.imagelib.tools.Config;
import org.fireking.app.imagelib.widget.PicSelectActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.jianyue.DataTask.SendMessageDataTask;
import com.jianyue.utils.ClassAPIResponse;
import com.jianyue.utils.GlobalData;
import com.jianyue.utils.Internet_Check;
import com.jianyue.utils.SessionManager;
import com.jianyue.utils.StaticMethodsUtility;
import com.jianyue.webservices.WebElements;

public class SendMessageActivity extends Activity {

	private CameraPreview mPreview;
	private FLASH_MODE flash_mode = FLASH_MODE.AUTO;
	private ImageView ivCamaraBtn, ivClose;
	private EditText etMessage;

	public static Activity activity;
	
	private String width = "", height = "" , receiver = "";
	private boolean reply_mode = true;
	private boolean initFirst = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.send_message);
		activity = this;
		receiver = getIntent().getExtras().getString("receiver");
		reply_mode = getIntent().getExtras().getBoolean("reply_mode");
		initObjects();
	}

	private void initObjects() {
		etMessage = (EditText)findViewById(R.id.etMessage);
		ivClose = (ImageView) findViewById(R.id.ivClose);
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				backManage();
			}
		});
		ivCamaraBtn = (ImageView) findViewById(R.id.ivCamaraBtn);
		ivCamaraBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					if(etMessage.getText().toString().trim().length() > 0)
					{
						/*
						if (mPreview == null) {
							mPreview = new CameraPreview(SendMessageActivity.this,
									flash_mode);
							FrameLayout preview = (FrameLayout) findViewById(R.id.flCameraPreview);
							preview.removeAllViews();
							preview.addView(mPreview);
							if (!mPreview.isCameraLoad()) {
								Toast.makeText(SendMessageActivity.this,
										"Can't load camera!", Toast.LENGTH_SHORT)
										.show();
								backManage();
							}
							initFirst = true;
						}else{
							initFirst = false;
						}*/
						Intent intent = new Intent(SendMessageActivity.this,
				    			PicSelectActivity.class);
				    	Config.setLimit(1);
				    	startActivityForResult(intent, 0x123);
						
					}
					else
					{
						StaticMethodsUtility.showNegativeToast(SendMessageActivity.this, "Enter message");
					}
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if(etMessage.getText().toString().trim().length() > 0 && initFirst == false)
					{
						if (mPreview == null) {
							mPreview = new CameraPreview(SendMessageActivity.this,
									flash_mode);
							FrameLayout preview = (FrameLayout) findViewById(R.id.flCameraPreview);
							preview.removeAllViews();
							preview.addView(mPreview);
							if (!mPreview.isCameraLoad()) {
								Toast.makeText(SendMessageActivity.this,
										"Can't load camera!", Toast.LENGTH_SHORT)
										.show();
								backManage();
							}
						}
						takePicture();
					}
					break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mPreview != null) {
			mPreview.onPause();
			mPreview = null;
		}
		ivCamaraBtn.setEnabled(false);
	}

	private void takePicture() {
		Log.i("log_tag", "takePicture");
		mPreview.takePicture(null, null, photoCallback);
	}

	private Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(final byte[] data, final Camera camera) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			GlobalData.bmp = null;
			GlobalData.bmp = (mPreview.getBitmapFromBuffer(data,
					dm.widthPixels, dm.heightPixels));
			GlobalData.sd_path = getFilePathFromSDCARD(GlobalData.bmp);
//			backManage();
			requestSendMessageWebservice();
		}
	};

	private void backManage() {
		finish();
		overridePendingTransition(R.anim.slide_down, R.anim.slide_down_out);
	}

	@Override
	public void onBackPressed() {
		backManage();
	}

	public String getFilePathFromSDCARD(Bitmap image) {
		
		File folder = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			folder = new File(Environment.getExternalStorageDirectory(),
					"/Demo/");
			if (!folder.isDirectory()) {
				folder.mkdir();
			}
		} else {
			folder = getDir("Demo", Context.MODE_WORLD_READABLE);
		}
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(SendMessageActivity.this);
		int image_no = pref.getInt("Image_No", 1);
		String fname = "Demo_" + image_no + ".png";
		File myDir = new File(folder.toString());
		image_no++;
		Editor edit = pref.edit();
		edit.putInt("Image_No", image_no);
		edit.commit();
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			image.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * String fileName = "temp.jpg"; File f = new
		 * File(Environment.getExternalStorageDirectory(), "/saved_images"); f =
		 * new File(f, fileName);
		 */

		return file.getAbsolutePath();
	}
	
	private void requestSendMessageWebservice() {

		if(GlobalData.bmp != null)
		{
			width = String.valueOf(GlobalData.bmp.getWidth());
			height = String.valueOf(GlobalData.bmp.getHeight());
		}
		
		final ClassAPIResponse apiResponse = new ClassAPIResponse();
		String append_url = "";
		if(reply_mode)
		{
			append_url = "r/message/reply";
		}
		else
		{
			append_url = "r/message/send";
		}
		SendMessageDataTask task = new SendMessageDataTask(SendMessageActivity.this,
				apiResponse , append_url ) {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if (result.equals(GlobalData.FAIL)) {
					if (!Internet_Check
							.checkInternetConnection(SendMessageActivity.this)) {
						StaticMethodsUtility.showNegativeToast(
								SendMessageActivity.this,
								getResources().getString(R.string.no_internet));
					}
					return;
				}
				
				if(apiResponse.ack.equalsIgnoreCase("Success"))
				{
//					finish();
//					Intent i = new Intent(SendMessageActivity.this , MainActivity.class);
//					startActivity(i);
				}
				
			}
		};

		if (Internet_Check.checkInternetConnection(SendMessageActivity.this)) {
			try {

				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				try {
					
					reqEntity.addPart(WebElements.SENDMESSAGE.UUID,
							new StringBody(SessionManager.getUUID(SendMessageActivity.this)));
					if(reply_mode)
					{
						reqEntity.addPart(WebElements.SENDMESSAGE.MESSAGEID,
								new StringBody(receiver));
					}
					else
					{
						reqEntity.addPart(WebElements.SENDMESSAGE.RECEIVER,
								new StringBody(receiver));
					}
					reqEntity.addPart(WebElements.SENDMESSAGE.CONTENT,
							new StringBody(etMessage.getText().toString().trim()));
					reqEntity.addPart(WebElements.SENDMESSAGE.TIMESTAMP,
							new StringBody(String.valueOf(System.currentTimeMillis())));
					reqEntity.addPart(WebElements.SENDMESSAGE.HEIGHT,
							new StringBody(height));
					reqEntity.addPart(WebElements.SENDMESSAGE.WIDTH,
							new StringBody(width));
					reqEntity.addPart(WebElements.SENDMESSAGE.PIC ,
							new FileBody((new File(GlobalData.sd_path))));
					task.execute(reqEntity);
					backManage();
				} catch (Exception e) {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			StaticMethodsUtility.showNegativeToast(SendMessageActivity.this,
					getResources().getString(R.string.no_internet));
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x123 && resultCode == RESULT_OK) {
			Intent intent = data;
			List<ImageBean> images = (List<ImageBean>) intent.getSerializableExtra("images");
			if(images.size() > 0){
				GlobalData.bmp = null;
				GlobalData.bmp = BitmapFactory.decodeFile(images.get(0).path) ;
				GlobalData.sd_path = images.get(0).path;
				requestSendMessageWebservice();
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
