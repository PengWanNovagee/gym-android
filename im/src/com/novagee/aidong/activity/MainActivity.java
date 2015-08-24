package com.novagee.aidong.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import com.novagee.aidong.adapter.FragmentPagerAdapter;
import com.novagee.aidong.fragment.BaseFragment;
import com.novagee.aidong.fragment.ChatListFragment;
import com.novagee.aidong.fragment.EventFragment;
import com.novagee.aidong.fragment.ExploreFragment;
import com.novagee.aidong.fragment.FriendListFragment;
import com.novagee.aidong.fragment.SettingFragment;
import com.novagee.aidong.im.controller.IMManager;
import com.novagee.aidong.im.controller.IMManager.GetUnReadedMessageCountCallback;
import com.novagee.aidong.im.model.Chat;
import com.novagee.aidong.im.model.Message;
import com.novagee.aidong.im.model.Topic;
import com.novagee.aidong.utils.Constant;
import com.novagee.aidong.utils.DBug;
import com.novagee.aidong.utils.Utils;
import com.novagee.aidong.view.AppBar;
import com.novagee.aidong.view.SlidingTabLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.novagee.aidong.R;

public class MainActivity extends BaseActivity implements Observer{
	
	
	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;
	private AppBar mAppbar;
	
	private ChatListFragment mChatListFragment;
	private FriendListFragment mFriendListFragment;
	private ExploreFragment mExploreFragment;
	private SettingFragment mSettingFragment;
	private EventFragment mEventFragment;
	
	private List<BaseFragment> fragList;
	
	private boolean doubleBackToExistPressedOnce = false;
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener(){
		public void onPageScrollStateChanged(int arg0) {}
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		@Override
		public void onPageSelected(int location) {
			BaseFragment frag = fragList.get(location);
			frag.onViewShown();
			
			mAppbar.initLayout();
			mAppbar.getLogoView().setImageResource(R.drawable.menu_logo);
			if(frag instanceof ChatListFragment){
				onChatListFragmentShown((ChatListFragment)frag);
			}else if(frag instanceof FriendListFragment){
				onFriendListFragmentShown((FriendListFragment)frag);
			}else if(frag instanceof ExploreFragment){
				mAppbar.getMenuItemView1().setVisibility(View.GONE);
			}else if(frag instanceof SettingFragment){
				mAppbar.getMenuItemView1().setVisibility(View.GONE);
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		IMManager.getInstance(this).addObserver(this);
		setContentView(R.layout.activity_main);
		checkBundle();
		initView();
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
		checkBundle();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(mOnPageChangeListener!=null){
			mOnPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
		}
		updateTabBadge();
	}
	
	private void checkBundle(){
		if(getIntent().hasExtra("payload")){
			String payload = getIntent().getStringExtra("payload");
			String alert = null; 
			
			Chat chat = null;
			try {
				JSONObject json = new JSONObject(payload);
				alert = json.getJSONObject("android").getString("alert");
				if(json.has("topic_id")){
					Topic topic = new Topic();
					topic.topicId = json.getString("topic_id");
					topic = topic.getFromTable();
					chat = IMManager.getInstance(this).addChat(topic);
				}else if(json.has("from")){
					chat = IMManager.getInstance(this).addChat(json.getString("from"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(alert!=null && (alert.contains(getString(R.string.anlive_push_call)) || alert.contains(getString(R.string.anlive_push_video_call)))){
				DBug.e("###push",alert+"?");
			}else{
				if(chat!=null){
					Intent i = new Intent(this,ChatActivity.class);
					Bundle b = new Bundle();
					b.putSerializable(Constant.INTENT_EXTRA_KEY_CHAT, chat);
					i.putExtras(b);
					startActivity(i);
				}
			}
		}
	}
	
	private void initView(){
		mChatListFragment = new ChatListFragment(getString(R.string.tab_title_chat));
		mFriendListFragment = new FriendListFragment(getString(R.string.tab_title_friend));
		mExploreFragment = new ExploreFragment(getString(R.string.tab_title_explore));
		mSettingFragment = new SettingFragment(getString(R.string.tab_title_setting));
		mEventFragment = new EventFragment(getString(R.string.tab_title_event));

		mAppbar = (AppBar)findViewById(R.id.toolbar);
		mAppbar.getLogoView().setImageResource(R.drawable.menu_logo);
		
		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		fragList = new ArrayList<BaseFragment>();
		fragList.add(mChatListFragment);
		fragList.add(mFriendListFragment);
		fragList.add(mExploreFragment);
		fragList.add(mEventFragment);
		fragList.add(mSettingFragment);
		FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),fragList);
		mViewPager.setAdapter(mFragmentPagerAdapter);
		
		mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setViewPager(mViewPager);
		mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
		    @Override
		    public int getIndicatorColor(int position) {
		        return getResources().getColor(R.color.no13);
		    }
		});
		mSlidingTabLayout.setOnPageChangeListener(mOnPageChangeListener);
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				mOnPageChangeListener.onPageSelected(0);
			}
		}, 300);
	}
	
	private void onChatListFragmentShown(final ChatListFragment frag){
		//modified by seeyet,2015/08/23 增加添加好友
		final Context context = this;
		mAppbar.getMenuItemView1().setVisibility(View.VISIBLE);
		mAppbar.getMenuItemView1().setImageResource(R.drawable.menu_search);
		mAppbar.getMenuItemView1().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mAppbar.getMenuItemView1().setVisibility(View.GONE);
				mAppbar.getMenuItemView2().setVisibility(View.GONE);
				mAppbar.getLogoView().setImageResource(R.drawable.menu_back);
				RelativeLayout.LayoutParams rlpLogo = (LayoutParams) mAppbar.getLogoView().getLayoutParams();
				rlpLogo.width = Utils.px2Dp(v.getContext(), 56);
				rlpLogo.height = Utils.px2Dp(v.getContext(), 56);
				rlpLogo.leftMargin = 0;
				mAppbar.getLogoView().setLayoutParams(rlpLogo);
				mAppbar.getLogoView().setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						RelativeLayout.LayoutParams rlpLogo = (LayoutParams) mAppbar.getLogoView().getLayoutParams();
						rlpLogo.width = Utils.px2Dp(v.getContext(), 54);
						rlpLogo.height = Utils.px2Dp(v.getContext(), 26);
						rlpLogo.leftMargin = Utils.px2Dp(v.getContext(), 16);
						mAppbar.getLogoView().setLayoutParams(rlpLogo);
						mAppbar.getLogoView().setImageResource(R.drawable.menu_logo);
						mAppbar.getEditText().setVisibility(View.GONE);
						mAppbar.getMenuItemView1().setVisibility(View.VISIBLE);
						mAppbar.getMenuItemView2().setVisibility(View.VISIBLE);
						mAppbar.getEditText().setText("");
					}
				});
				mAppbar.getEditText().setVisibility(View.VISIBLE);
				mAppbar.getEditText().requestFocus();
				mAppbar.getEditText().setHint(R.string.friend_list_search);
				mAppbar.getEditText().addTextChangedListener(new TextWatcher() {
				    @Override
				    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
						frag.filterList(cs.toString());
				    }
				    @Override
				    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
				    public void afterTextChanged(Editable arg0) { }
				});
			}
		});
		
		mAppbar.getMenuItemView2().setVisibility(View.VISIBLE);
		mAppbar.getMenuItemView2().setImageResource(R.drawable.menu_add);
		mAppbar.getMenuItemView2().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context,SearchUserActivity.class);
				context.startActivity(i);
			}
		});
	
	}
	
	private void onFriendListFragmentShown(final FriendListFragment frag){
		//modified by seeyet,2015/08/23 增加添加好友
		final Context context = this;
		mAppbar.getMenuItemView1().setVisibility(View.VISIBLE);
		mAppbar.getMenuItemView1().setImageResource(R.drawable.menu_search);
		mAppbar.getMenuItemView1().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mAppbar.getMenuItemView1().setVisibility(View.GONE);
				mAppbar.getMenuItemView2().setVisibility(View.GONE);
				mAppbar.getLogoView().setImageResource(R.drawable.menu_back);
				RelativeLayout.LayoutParams rlpLogo = (LayoutParams) mAppbar.getLogoView().getLayoutParams();
				rlpLogo.width = Utils.px2Dp(v.getContext(), 56);
				rlpLogo.height = Utils.px2Dp(v.getContext(), 56);
				rlpLogo.leftMargin = 0;
				mAppbar.getLogoView().setLayoutParams(rlpLogo);
				mAppbar.getLogoView().setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						RelativeLayout.LayoutParams rlpLogo = (LayoutParams) mAppbar.getLogoView().getLayoutParams();
						rlpLogo.width = Utils.px2Dp(v.getContext(), 54);
						rlpLogo.height = Utils.px2Dp(v.getContext(), 26);
						rlpLogo.leftMargin = Utils.px2Dp(v.getContext(), 16);
						mAppbar.getLogoView().setLayoutParams(rlpLogo);
						mAppbar.getLogoView().setImageResource(R.drawable.menu_logo);
						mAppbar.getEditText().setVisibility(View.GONE);
						mAppbar.getMenuItemView1().setVisibility(View.VISIBLE);
						mAppbar.getMenuItemView2().setVisibility(View.VISIBLE);
						mAppbar.getEditText().setText("");
					}
				});
				mAppbar.getEditText().setVisibility(View.VISIBLE);
				mAppbar.getEditText().requestFocus();
				mAppbar.getEditText().setHint(R.string.friend_list_search);
				mAppbar.getEditText().addTextChangedListener(new TextWatcher() {
				    @Override
				    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
						frag.filterList(cs.toString());
				    }
				    @Override
				    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
				    public void afterTextChanged(Editable arg0) { }
				});
			}
		});
		
		mAppbar.getMenuItemView2().setVisibility(View.VISIBLE);
		mAppbar.getMenuItemView2().setImageResource(R.drawable.menu_add);
		mAppbar.getMenuItemView2().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context,SearchUserActivity.class);
				context.startActivity(i);
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		Handler h = new Handler();
		Runnable r =new Runnable(){
			@Override
			public void run() {
				doubleBackToExistPressedOnce = false;
			}
		};
		if(!doubleBackToExistPressedOnce){
			doubleBackToExistPressedOnce = true;
			Toast.makeText(this,getString(R.string.general_press_again_to_exit),Toast.LENGTH_SHORT).show();
			h.postDelayed(r, 2000);
		}else{
			h.removeCallbacks(r);
			super.onBackPressed();
		}
	}

	public void updateTabBadge(){
	//	mExploreFragment.setBadgeCount(mExploreFragment.getLikeCount());
		IMManager.getInstance(this).getUnReadMessageCount(new GetUnReadedMessageCountCallback(){
			@Override
			public void onFinish(int count) {
				mChatListFragment.setBadgeCount(count);
			}
		});
	}
	
	@Override
	public void update(final Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if(data instanceof Message){
					Message msgData = (Message)data;
					if(!msgData.readed){
						int badgeCount = mChatListFragment.getBadgeCount();
						mChatListFragment.setBadgeCount(++badgeCount);
					}
					mChatListFragment.update(observable, data);
					
				}else if(data instanceof IMManager.UpdateType && ((IMManager.UpdateType)data).equals(IMManager.UpdateType.Topic)){
					mChatListFragment.update(observable, data);
					
				}else if(data instanceof IMManager.UpdateType && ((IMManager.UpdateType)data).equals(IMManager.UpdateType.Like)){
		//			mExploreFragment.notifyLike();
		//			mExploreFragment.setBadgeCount(mExploreFragment.getLikeCount());
		//			DBug.e("mExploreFragment.getLikeCount()",mExploreFragment.getLikeCount()+"?");
		//			DBug.e("mExploreFragment.badgeCount",mExploreFragment.getBadgeCount()+"?");
				}
				mSlidingTabLayout.refreshAllTab();
			}
		});
	}
}
