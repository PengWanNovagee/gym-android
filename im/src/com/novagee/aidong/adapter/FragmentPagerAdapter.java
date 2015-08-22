package com.novagee.aidong.adapter;

import java.util.List;

import com.novagee.aidong.fragment.BaseFragment;
import com.novagee.aidong.utils.DBug;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

	private List<BaseFragment> mFragList;

	public FragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragList) {
		super(fm);
		mFragList = fragList;
	}

	@Override
	public Fragment getItem(int position) {
		return mFragList.get(position);
	}

	@Override
	public int getCount() {
		return mFragList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragList.get(position).getTitle();
	}
	
	public int getBadgeCount(int position){
		return mFragList.get(position).getBadgeCount();
	}
}
