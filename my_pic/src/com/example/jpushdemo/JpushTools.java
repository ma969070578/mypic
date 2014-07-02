package com.example.jpushdemo;

import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.amima.app.MyApp;
import com.my.util.Constants;
import com.my.util.SharedPreTools;
import com.my.util.SystemOut;
import com.my.util.Tools;

public class JpushTools {
	private static final String TAG = "JpushTools";

	/**
	 * alias null 此次调用不设置此值。（注：不是指的字符串"null"） "" （空字符串）表示取消之前的设置。
	 * 每次调用设置有效的别名，覆盖之前的设置。 有效的别名组成：字母（区分大小写）、数字、下划线、汉字。 限制：alias 命名长度限制为 40
	 * 字节。（判断长度需采用UTF-8编码） tags null 此次调用不设置此值。（注：不是指的字符串"null"）
	 * 空数组或列表表示取消之前的设置。 每次调用至少设置一个 tag，覆盖之前的设置，不是新增。
	 * 有效的标签组成：字母（区分大小写）、数字、下划线、汉字。 限制：每个 tag 命名长度限制为 40 字节，最多支持设置 100 个
	 * tag，但总长度不得超过1K字节。（判断长度需采用UTF-8编码） 单个设备最多支持设置 100 个 tag。App 全局 tag 数量无限制。
	 * callback 在 TagAliasCallback 的 gotResult 方法，返回对应的参数 alias,
	 * tags。并返回对应的状态码：0为成功，其他返回码请参考错误码定义。
	 * 
	 * @param context
	 * @param alias
	 * @param tags
	 *            以逗号分隔
	 * @param callback
	 */

	public static void setAliasAndTags(Context context, String alias,
			String tag, TagAliasCallback callback) {

		if (!Tools.isNull(alias) && !ExampleUtil.isValidTagAndAlias(alias)) {
			SystemOut.out("格式不对");
			return;
		}
	 
        	tag="0";
         		
    	Set<String> tagSet = new LinkedHashSet<String>();
		if (!Tools.isNull(tag)) {
			// ","隔开的多个 转换成 Set
			String[] sArray = tag.split(",");
		
			for (String sTagItme : sArray) {
				if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {

					SystemOut.out("格式不对");
//					return;
				}
				tagSet.add(sTagItme);
			}

		}
		
		if(!Tools.isNull(alias)&&alias.equals("0")){
			return;
		}

		JPushInterface.setAliasAndTags(context, alias,tagSet, callback);
		

	}

	public static TagAliasCallback caallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			// TODO Auto-generated method stub
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success, alias = " + alias
						+ "; tags = " + tags;
				//	Log.i(TAG, logs);
	 
				break;

			default:
				logs = "Failed with errorCode = " + code + " alias = " + alias
						+ "; tags = " + tags;
				//Log.e(TAG, logs);
			}
			if (Tools.isDebug) {

				ExampleUtil.showToast(logs, MyApp.getInstance()
						.getApplicationContext());
			}
		}
	};

	/**
	 * 初始化操作
	 * 
	 * @param context
	 */
	public static void initPush(Context context) {
		JPushInterface.init(context);
	}

	/**
	 * 调用了本 API 后，JPush 推送服务完全被停止。具体表现为： JPush Service 不在后台运行 收不到推送消息
	 * JPushInterface.init 这个初始化方法调用，不能初始化 JPush 使得可以收到推送 极光推送所有的其他 API 调用都无效
	 * 
	 * @param context
	 */
	public static void stopPush(Context context) {
		JPushInterface.stopPush(context);
	}

	/**
	 * 调用了此 API 后，极光推送完全恢复正常工作。 s
	 * 
	 * @param context
	 */
	public static void resumePush(Context context) {
		JPushInterface.resumePush(context);
	}

	/**
	 * 用来检查 Push Service 是否已经被停止
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPushStopped(Context context) {
		return JPushInterface.isPushStopped(context);
	}

	/**
	 * 此 API 提供清除通知的功能，包括：清除所有 JPush 展现的通知（不包括非 JPush SDK 展现的）
	 * 
	 * @param context
	 */
	public static void clearAllNotifications(Context context) {
		JPushInterface.clearAllNotifications(context);
	}

	public static void clearNotificationById(Context context, int notificationId) {
		JPushInterface.clearNotificationById(context, notificationId);
	}

	/**
	 * 所谓保留最近的，意思是，如果有新的通知到达，之前列表里最老的那条会被移除。 例如，设置为保留最近 5 条通知。假设已经有 5 条显示在通知栏，当第
	 * 6 条到达时，第 1 条将会被移除。
	 * 
	 * @param context
	 * @param maxNum
	 */
	public static void setLatestNotifactionNumber(Context context, int maxNum) {
		JPushInterface.setLatestNotifactionNumber(context, maxNum);
	}

	/**
	 * Context context 应用的ApplicationContext Set<Integer> days
	 * 0表示星期天，1表示星期一，以此类推。 （7天制，Set集合里面的int范围为0到6） Sdk1.2.9 –
	 * 新功能:set的值为null,则任何时间都可以收到消息和通知，set的size为0，则表示任何时间都收不到消息和通知. int startHour
	 * 允许推送的开始时间 （24小时制：startHour的范围为0到23） int endHour
	 * 允许推送的结束时间（24小时制：endHour的范围为0到23）
	 */
	public static void setPushTime(Context context, Set<Integer> weekDays,
			int startHour, int endHour) {
		JPushInterface.setPushTime(context, weekDays, startHour, endHour);
	}

	/**
	 * 默认情况下用户在收到推送通知时，客户端可能会有震动，响铃等提示。但用户在睡觉、开会等时间点希望为 "免打扰" 模式，也是静音时段的概念。
	 * 开发者可以调用此 API 来设置静音时段。 如果在该时间段内收到消息，则：不会有铃声和震动。 Context context
	 * 应用的ApplicationContext int startHour 静音时段的开始时间 - 小时 （24小时制，范围：0~23 ） int
	 * startMinute 静音时段的开始时间 - 分钟（范围：0~59 ） int endHour 静音时段的结束时间 - 小时
	 * （24小时制，范围：0~23 ） int endMinute 静音时段的结束时间 - 分钟（范围：0~59 ）
	 */

	public static void setSilenceTime(Context context, int startHour,
			int startMinute, int endHour, int endMinute) {
		JPushInterface.setSilenceTime(context, startHour, startMinute, endHour,
				endMinute);
	}

}
