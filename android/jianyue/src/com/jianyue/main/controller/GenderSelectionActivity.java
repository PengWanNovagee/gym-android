package com.jianyue.main.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GenderSelectionActivity extends Activity{
	
	ImageView ivFemale , ivMale;
	String Nickname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gender_selection);
		
		Nickname = getIntent().getExtras().getString("Nickname");
		ivFemale = (ImageView)findViewById(R.id.ivFemale);
		ivMale = (ImageView)findViewById(R.id.ivMale);
		
		ivFemale.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(GenderSelectionActivity.this , TakePictureActivity.class);
				i.putExtra("Nickname", Nickname);
				i.putExtra("Gender", "F");
				i.putExtra("is_first_time", true);
				startActivity(i);
			}
		});
		
		ivMale.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(GenderSelectionActivity.this , TakePictureActivity.class);
				i.putExtra("Nickname", Nickname);
				i.putExtra("Gender", "M");
				i.putExtra("is_first_time", true);
				startActivity(i);
			}
		});
	}
}
