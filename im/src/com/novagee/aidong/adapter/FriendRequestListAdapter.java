package com.novagee.aidong.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.arrownock.exception.ArrownockException;
import com.arrownock.social.IAnSocialCallback;
import com.novagee.aidong.activity.SearchUserActivity;
import com.novagee.aidong.controller.UserManager;
import com.novagee.aidong.controller.UserManager.FetchFriendCallback;
import com.novagee.aidong.controller.UserManager.FetchFriendRequestCallback;
import com.novagee.aidong.controller.UserManager.FetchUserCallback;
import com.novagee.aidong.im.controller.IMManager;
import com.novagee.aidong.im.controller.IMManager.FetchLocalTopicCallback;
import com.novagee.aidong.im.model.Topic;
import com.novagee.aidong.im.model.TopicMember;
import com.novagee.aidong.model.Friend;
import com.novagee.aidong.model.FriendRequest;
import com.novagee.aidong.model.User;
import com.novagee.aidong.utils.DBug;
import com.novagee.aidong.utils.Utils;
import com.novagee.aidong.view.UserListItem;

import com.novagee.aidong.R;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FriendRequestListAdapter extends BaseAdapter {
	private List<FriendRequest> data;
	private Context ct;
	
	public FriendRequestListAdapter(Context ct){
		this.ct = ct;
		data = new ArrayList<FriendRequest>();
	}
	
	public void applyData(List<FriendRequest> requests){
		data.clear();
		data.addAll(requests);
		notifyDataSetChanged();
		
	}
	
	public void fetchRemoteData(boolean fillLocalDataFirst){
		if(fillLocalDataFirst){
			fillLocalData();
		}
		UserManager.getInstance(ct).fetchFriendRequest(new  IAnSocialCallback(){
			@Override
			public void onFailure(JSONObject arg0) {
			}

			@Override
			public void onSuccess(JSONObject arg0) {
				DBug.e("fetchFriendRequest", arg0.toString());
				fillLocalData();
			}
		});
	}
	
	public void fillLocalData(){
		UserManager.getInstance(ct).getLocalFriendRequest(new FetchFriendRequestCallback() {
			@Override
			public void onFinish(List<FriendRequest> data) {
				applyData(data);
			}
		});
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public FriendRequest getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendRequestListItem view = (FriendRequestListItem) convertView;
		if (convertView == null) {
			view = new FriendRequestListItem(parent.getContext());
		}
		
		view.setData(data.get(position));
		
		return view;
	}
	
	public class FriendRequestListItem extends UserListItem{
		private TextView textFriendStatus;
		private FrameLayout btnApprove,btnReject;
		private LinearLayout viewBtn;
		public FriendRequestListItem(Context ct) {
			super(ct);

			setLayoutParams(new AbsListView.LayoutParams(-1,Utils.px2Dp(ct, 56)));
			
			textFriendStatus = new TextView(ct);
			textFriendStatus.setTextColor(ct.getResources().getColor(R.color.no8));
			textFriendStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
			int padding = 0;
			textFriendStatus.setPadding(padding, padding, padding, padding);
			RelativeLayout.LayoutParams rlpTextFS = new RelativeLayout.LayoutParams(-2,-2);
			rlpTextFS.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rlpTextFS.addRule(RelativeLayout.CENTER_VERTICAL);
			rlpTextFS.rightMargin = Utils.px2Dp(ct, 16);
			addView(textFriendStatus,rlpTextFS);
			textFriendStatus.setVisibility(View.GONE);
			
			viewBtn = new LinearLayout(ct);
			viewBtn.setOrientation(LinearLayout.HORIZONTAL);
			viewBtn.setVisibility(View.GONE);
			
			btnApprove = new FrameLayout(ct);
			ImageView imgApprove = new ImageView(ct);
			imgApprove.setImageResource(R.drawable.friend_accept);
			btnApprove.addView(imgApprove,new FrameLayout.LayoutParams(Utils.px2Dp(ct, 24),Utils.px2Dp(ct, 24),Gravity.CENTER));
			viewBtn.addView(btnApprove,new LinearLayout.LayoutParams(Utils.px2Dp(ct, 40),-1));

			btnReject = new FrameLayout(ct);
			ImageView imgReject = new ImageView(ct);
			imgReject.setImageResource(R.drawable.friend_decline);
			btnReject.addView(imgReject,new FrameLayout.LayoutParams(Utils.px2Dp(ct, 24),Utils.px2Dp(ct, 24),Gravity.CENTER));
			viewBtn.addView(btnReject,new LinearLayout.LayoutParams(Utils.px2Dp(ct, 40),-1));
			
			addView(viewBtn,rlpTextFS);
		}
		
		public void setData(final FriendRequest request){
			setIcon(request.user().userPhotoUrl, R.drawable.friend_default);
			setName(request.user().userName);
			
			textFriendStatus.setVisibility(View.GONE);
			viewBtn.setVisibility(View.GONE);
			if(request.status.equals(FriendRequest.STATUS_APPROVED)){
				textFriendStatus.setVisibility(View.VISIBLE);
				textFriendStatus.setText(R.string.friend_request_status_approved);
			}else if(request.status.equals(FriendRequest.STATUS_REJECTED)){
				textFriendStatus.setVisibility(View.VISIBLE);
				textFriendStatus.setText(R.string.friend_request_status_rejected);
			}else if(request.status.equals(FriendRequest.STATUS_PENDING)){
				viewBtn.setVisibility(View.VISIBLE);
				btnApprove.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						UserManager.getInstance(ct).approveFriendRequest(request, new IAnSocialCallback(){
							@Override
							public void onFailure(JSONObject arg0) {
								DBug.e("approveFriendRequest.onFailure", arg0.toString());
							}
							@Override
							public void onSuccess(JSONObject arg0) {
								fetchRemoteData(false);
							}
						});
					}
				});
				btnReject.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						UserManager.getInstance(ct).rejectFriendRequest(request, new IAnSocialCallback(){
							@Override
							public void onFailure(JSONObject arg0) {
								DBug.e("approveFriendRequest.onFailure", arg0.toString());
							}
							@Override
							public void onSuccess(JSONObject arg0) {
								fetchRemoteData(false);
							}
						});
					}
				});
			}
		}
	}
}
