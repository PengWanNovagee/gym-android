package com.jianyue.webservices;

public class WebElements {
	
	public final static String UUID = "uuid";
	public final static String RECEIVER = "receiver";
	public final static String MESSAGEID = "messageId";
	public final static String FRIENDID = "friendId";

	public final static class SIGNUP {
		public final static String UUID = "uuid";
		public final static String NAME = "name";
		public final static String LAT = "lat";
		public final static String LNG = "lng";
		public final static String GENDER = "gender";
		public final static String INTERESTIN = "interestIn";
		public final static String DEVICETYPE = "deviceType";
		public final static String DEVICETOKEN = "deviceToken";
//		public final static String DEVICETYPE = "deviceType";
		public final static String PIC = "pic";
		public final static String WIDTH = "width";
		public final static String HEIGHT = "height";
	}
	
	public final static class NEARBYPEOPLE {
		public final static String UUID = "uuid";
		public final static String LAT = "lat";
		public final static String LNG = "lng";
		public final static String INTERESTIN = "interestIn";
		public final static String PAGENUMBER = "pageNumber";
		public final static String PAGESIZE = "pageSize";
		public final static String TIMESTAMP = "timestamp";
	}
	
	public final static class SENDMESSAGE {
		public final static String UUID = "uuid";
		public final static String RECEIVER = "receiver";
		public final static String CONTENT = "content";
		public final static String PIC = "pic";
		public final static String WIDTH = "width";
		public final static String HEIGHT = "height";
		public final static String TIMESTAMP = "timestamp";
		public final static String MESSAGEID = "messageId";
	}
	
	public final static class FETCH_CHAT_OR_FRIEND {
		public final static String UUID = "uuid";
		public final static String PAGENUMBER = "pageNumber";
		public final static String PAGESIZE = "pageSize";
		public final static String TIMESTAMP = "timestamp";
	}
	
	public final static class FETCH_OR_APPLY_EVENT{
		public final static String UUID = "uuid";
		public final static String PHONE_NUMBER= "mobile";
		public final static String TIMESTAMP = "timestamp";
	}
}