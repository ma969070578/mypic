package com.my.activity.sq.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amima.app.MyApp;
import com.my.util.SystemOut;
import com.my.util.Tools;

public class UserBean {
	private String id;
	private String avatar;
	private String loginName;
	private String mobile;
	private String lastLogin;
	private String workStatus = "0";
	private String userName;
	private String dn;
	private String confNo;
	private String expire;
	private int type = 1; // 1用户 2医生

	private ArrayList<SqInfoBean> listSq;

	private String errorInfo;
	String userKey = "";

	String communityId;
	String communityName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getConfNo() {
		return confNo;
	}

	public void setConfNo(String confNo) {
		this.confNo = confNo;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getCommunityId() {
		return communityId;
	}

	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public ArrayList<SqInfoBean> getListSq() {
		return listSq;
	}

	public void setListSq(ArrayList<SqInfoBean> listSq) {
		this.listSq = listSq;
	}
	
	
	

	public static String showKey(String userKey) {
		if (userKey == null)
			return "";
		if (userKey.length() < 8)
			return userKey;
		return userKey.substring(0, 4) + "***"
				+ userKey.substring(userKey.length() - 4);
	}

	public String showKey() {
		return showKey(userKey);
	}

	public static UserBean getUser(String content) throws JSONException {
		UserBean muser = new UserBean();
		if (Tools.isNull(content)) {
			return null;
		}
		muser.setUserKey(MyApp.userKey);

		JSONObject jsonobject1 = new JSONObject(content);

		if (jsonobject1 != null) {

			String code = jsonobject1.optString("code");
			if ("-1".equals(code)) {
				String error = jsonobject1.optString("error");
				muser.setErrorInfo(error);

				return muser;
			} else if ("0".equals(code)) {

				String data = jsonobject1.optString("data");
				JSONObject jsonObject = new JSONObject(data);
				if (jsonObject != null) {

					muser.setId(Tools.JSONString(jsonObject, "id"));
					muser.setLoginName(Tools.JSONString(jsonObject, "loginName"));

					muser.setLastLogin(Tools
							.JSONString(jsonObject, "lastLogin"));
					muser.setUserName(Tools.JSONString(jsonObject, "userName"));
					muser.setWorkStatus(Tools.JSONString(jsonObject,
							"workStatus"));
					muser.setDn(Tools.JSONString(jsonObject, "dn"));
					muser.setConfNo(Tools.JSONString(jsonObject, "confNo"));
					String murl=Tools.JSONString(jsonObject,"avatar");
					if(!Tools.isNull(murl))
					muser.setAvatar(Tools.getUrl(murl));

					ArrayList<SqInfoBean> mInfoList = new ArrayList<SqInfoBean>();
					String communityList = Tools.JSONString(
							jsonObject, "communityList");
					SystemOut.out("communityList" + communityList);

					JSONArray marray = jsonObject.optJSONArray("communityList");
					if (marray != null) {
						int length = marray.length();
						for (int i = 0; i < length; i++) {
							JSONObject mobject = (JSONObject) marray.get(i);
							SqInfoBean minfo = new SqInfoBean();
							minfo.setId(Tools.JSONString(mobject, "id"));
							minfo.setName(Tools.JSONString(mobject, "name"));
							minfo.setLongitude(Tools.JSONString(mobject,
									"longitude"));
							minfo.setLatitude(Tools.JSONString(mobject,
									"latitude"));
							mInfoList.add(minfo);
						}
					}
					muser.setListSq(mInfoList);

				}
				return muser;

			}

		}

		return muser;
	}

 
}
