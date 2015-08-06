package com.jianyue.main.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jianyue.utils.ClassContactPhone;

public class PhoneContactActivity extends Activity {

//	ArkListView lvContacts;
	ListView lvContacts;
	ArrayList<ClassContactPhone> contactPhoneEntities;
	ArrayList<ClassContactPhone> sorted_phone_contact = new ArrayList<ClassContactPhone>();
	ImageView ivClose;

	//SectionComposerAdapter sectionComposerAdapter;
	
	ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_contacts);
		lvContacts = (ListView) findViewById(R.id.lvContacts);
//		lvContacts.setPinnedHeaderView(LayoutInflater.from(PhoneContactActivity.this)
//				.inflate(R.layout.header_section_list, lvContacts, false));
//		lvContacts.setFastScrollEnabled(true);
		ivClose = (ImageView) findViewById(R.id.ivClose);

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				backManage();
			}
		});
		
		new GetPhoneContactDataTask().execute();
	}

	public class GetPhoneContactDataTask extends AsyncTask<String, Void, String> {
		protected void onPreExecute() {
			mDialog = new ProgressDialog(PhoneContactActivity.this);
			mDialog.setMessage("Retriving Contacts...  ");
			mDialog.setCancelable(false);
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}

		protected String doInBackground(String... params) {
			getPhoneContacts();
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				mDialog.dismiss();
			} catch (Exception e) {

			}
//			sectionComposerAdapter = new SectionComposerAdapter();
//			lvContacts.setAdapter(sectionComposerAdapter);
			MyListAdapter adp = new MyListAdapter(PhoneContactActivity.this, contactPhoneEntities);
			lvContacts.setAdapter(adp);
		}
	}

	public void getPhoneContacts() {
		contactPhoneEntities = new ArrayList<ClassContactPhone>();
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		ClassContactPhone entity;
		while (cursor.moveToNext()) {
			entity = new ClassContactPhone();
			entity.setName(cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

			long long1 = cursor.getLong(cursor.getColumnIndex("_id"));
			entity.setId(String.valueOf(long1));
			Cursor emails = getContentResolver().query(Email.CONTENT_URI, null,
					Email.CONTACT_ID + " = " + long1, null, null);
			while (emails.moveToNext()) {
				entity.setEmail(emails.getString(emails
						.getColumnIndex(Email.DATA)));
				break;
			}
			emails.close();

			if (Integer
					.parseInt(cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				Cursor pCur = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?",
						new String[] { String.valueOf(long1) }, null);
				while (pCur.moveToNext()) {
					entity.setContact(pCur.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					break;
				}
				pCur.close();
			}

			Uri person = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, long1);
			Uri withAppendedPath = Uri.withAppendedPath(person,
					ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
			entity.setImageUri(withAppendedPath);
			contactPhoneEntities.add(entity);
			Log.d("Name", contactPhoneEntities.get(contactPhoneEntities.size() - 1).getName());
		}
		cursor.close();
		Collections.sort(contactPhoneEntities, new CustomComparator());
	}

	public class CustomComparator implements Comparator<ClassContactPhone> {
		@Override
		public int compare(ClassContactPhone o1, ClassContactPhone o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	private class CustomClass {
		String header;

		ArrayList<String> arrayList;
	}

	/*private class SectionComposerAdapter extends AmazingAdapter implements
			SectionIndexer {

		private class ViewHolder {
			TextView tvContact;
		}

		private LayoutInflater inflater;

		// private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		private ArrayList<CustomClass> all = populateList();

		public SectionComposerAdapter() {
			super();
			inflater = LayoutInflater.from(PhoneContactActivity.this);
		}

		@Override
		public void notifyDataSetChanged() {
			all = populateList();
			super.notifyDataSetChanged();
		}

		private ArrayList<CustomClass> populateList() {
			ArrayList<CustomClass> list = new ArrayList<CustomClass>();
			sorted_phone_contact.clear();
			for (int i = 0; i < mSections.length(); i++) {
				CustomClass element = new CustomClass();
				element.header = mSections.charAt(i) + "";
				element.arrayList = new ArrayList<String>();

				for (ClassContactPhone contact : contactPhoneEntities) {

					if (contact.getName().toString().trim()
							.startsWith(element.header)) {
						Log.d("Sorted", contact.getName().toString());
						sorted_phone_contact.add(contact);
					}
				}
				list.add(element);
			}

			return list;
		}

		@Override
		public int getCount() {
			int res = 0;
			for (int i = 0; i < all.size(); i++) {
				res += all.get(i).arrayList.size();
			}
			return res;
		}

		@Override
		public String getItem(int position) {
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (position >= c && position < c + all.get(i).arrayList.size()) {
					return all.get(i).arrayList.get(position - c);
				}
				c += all.get(i).arrayList.size();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected void onNextPageRequested(int page) {
		}

		@Override
		protected void bindSectionHeader(View view, int position,
				boolean displaySectionHeader) {
			if (displaySectionHeader) {
				view.findViewById(R.id.header).setVisibility(View.VISIBLE);
				TextView lSectionTitle = (TextView) view
						.findViewById(R.id.header);
				lSectionTitle
						.setText(getSections()[getSectionForPosition(position)]);
				// lSectionTitle.setTypeface(tf_bold);
			} else {
				view.findViewById(R.id.header).setVisibility(View.GONE);
			}
		}

		@Override
		public View getAmazingView(int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.row_contact, parent,
						false);

				holder.tvContact = (TextView) convertView
						.findViewById(R.id.tvContact);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tvContact.setText(getItem(position));
			// holder.tvSaint.setTypeface(tf_bold);
			return convertView;
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			TextView lSectionHeader = (TextView) header;
			lSectionHeader
					.setText(getSections()[getSectionForPosition(position)]);
			// lSectionHeader.setBackgroundColor(0xEFCECECE);
			// lSectionHeader.setTextColor(alpha << 90 | (0x000000));
		}

		@Override
		public int getPositionForSection(int section) {
			if (section < 0)
				section = 0;
			if (section >= all.size())
				section = all.size() - 1;
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (section == i) {
					return c;
				}
				c += all.get(i).arrayList.size();
			}

			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							String name = getItem(j);
							if (name != null && !name.equals("")) {
								if (StringMatcher.match(
										String.valueOf(name.charAt(0)),
										String.valueOf(k)))
									return j;
							}

						}
					} else {
						String name = getItem(j);
						if (name != null && !name.equals("")) {
							if (StringMatcher.match(
									String.valueOf(name.charAt(0)),
									String.valueOf(mSections.charAt(i))))
								return j;
						}

					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			int c = 0;
			for (int i = 0; i < all.size(); i++) {
				if (position >= c && position < c + all.get(i).arrayList.size()) {
					return i;
				}
				c += all.get(i).arrayList.size();
			}
			return -1;
		}

		@Override
		public String[] getSections() {
			String[] res = new String[all.size()];
			for (int i = 0; i < all.size(); i++) {
				res[i] = all.get(i).header;
			}
			return res;
		}

	}
*/
	private void backManage() {
		finish();
		overridePendingTransition(R.anim.slide_down, R.anim.slide_down_out);
	}

	@Override
	public void onBackPressed() {
		backManage();
	}
	
	public class MyListAdapter extends ArrayAdapter<ClassContactPhone> {
		class ViewHolder {
			public TextView tvContact;
		}

		private final Activity context;

		public MyListAdapter(Activity context, ArrayList<ClassContactPhone> list) {
			super(context, R.layout.row_contact, list);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.row_contact, null);
				final ViewHolder viewHolder = new ViewHolder();
				
				viewHolder.tvContact = (TextView) rowView
						.findViewById(R.id.tvContact);
				
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.tvContact.setText(contactPhoneEntities.get(position).getName());

			return rowView;
		}
	}
	

}
