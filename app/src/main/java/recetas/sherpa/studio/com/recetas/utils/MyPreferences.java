package recetas.sherpa.studio.com.recetas.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class MyPreferences
{
	public static final String SETTINGS_NAME = "settings";
	private static final String TELEPHONE_NUMBER = "telephone_number";
	private static final String MEETS_ID = "meets_id";
	private static final String TELEPHONE_PREFIX = "telephone_prefix";
	private static final String PUSH_TOKEN = "push_token";
	private static final String ACTIVATION_TOKEN = "activation_token";
	private static final String LAST_UPDATE = "last_update";
	private static final String CIPHER_KEY = "cipher_key";
	private static final String UNCAUGHT_EXCEPTIONS_TIMESTAMP = "uncaught_exception_timestamp";
	private static final String UNCAUGHT_EXCEPTIONS_COUNTER = "uncaught_exception_counter";
	private static final String REGISTRATION_STEP = "registration_step";
	private static final String PROFILE_NAME = "profile_name";
	private static final String WAY_ENABLED = "way_enabled";
	private static final String FIRST_RUN = "first_run";
	private static final String LAST_PROFILES_REQUEST = "last_profiles_request";
	private static final String SHOW_INVITE_CONTACTS_DIALOG = "show_invite_contacts_dialog";
	private static final String APP_VERSION = "app_version";
	private static final String LAST_CONTACTS_REQUEST = "last_contacts_request";
	private static final String LAST_HISTORY_PLAN_INDEX = "last_history_plan_index";
	// Facebook synchronization
	private static final String FACEBOOK_ID = "facebook_id";
	private static final String FACEBOOK_CONTACTS = "facebook_contacts_synchronization";
	private static final String FACEBOOK_ACTIVITY = "facebook_activity_synchronization";
	private static final String FACEBOOK_GENDER = "facebook_gender";
	private static final String FACEBOOK_NAME = "facebook_name";
	private static final String FACEBOOK_EMAIL = "facebook_email";
	private static final String FACEBOOK_BIRTHDAY = "facebook_birthday";
	// FourSquare preferences
	private static final String FOURSQUARE_VENUES = "foursquare_venues";

	public static void setTelephoneNumber(Context context, String telephoneNumber)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(TELEPHONE_NUMBER, telephoneNumber);
		editor.commit();
	}

	public static String getTelephoneNumber(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(TELEPHONE_NUMBER, null);
	}

	public static void setTelephonePrefix(Context context, String telephonePrefix)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(TELEPHONE_PREFIX, telephonePrefix);
		editor.commit();
	}

	public static String getMeetsId(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(MEETS_ID, null);
	}

	public static void setMeetsId(Context context, String meetsId)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(MEETS_ID, meetsId);
		editor.commit();
	}

	public static String getTelephonePrefix(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(TELEPHONE_PREFIX, "");
	}

	public static void setPushToken(Context context, String pushToken)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(PUSH_TOKEN, pushToken);
		editor.commit();
	}

	public static String getPushToken(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(PUSH_TOKEN, "");
	}

	public static void setActivationToken(Context context, String activationToken)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(ACTIVATION_TOKEN, activationToken);
		editor.commit();
	}

	public static String getActivationToken(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(ACTIVATION_TOKEN, null);
	}

	public static void setLastUpdate(Context context, String lastUpdate)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		// Convert to TimeZone Zero
		lastUpdate = DateUtils.convertToGMTZero(lastUpdate);
		editor.putString(LAST_UPDATE, lastUpdate);
		editor.commit();
	}

	public static String getLastContactsRequest(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(LAST_CONTACTS_REQUEST, "1970-01-01");
	}

	public static void setLastContactsRequest(Context context, String lastRequest)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(LAST_CONTACTS_REQUEST, lastRequest);
		editor.commit();
	}

	public static String getLastUpdate(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(LAST_UPDATE, "1970-01-01 00:00:00");
	}

	public static void setLastProfilesRequest(Context context, String lastUpdate)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		// Convert to TimeZone Zero
		lastUpdate = DateUtils.convertToGMTZero(lastUpdate);
		editor.putString(LAST_PROFILES_REQUEST, lastUpdate);
		editor.commit();
	}

	public static String getLastProfilesRequest(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(LAST_PROFILES_REQUEST, "1970-01-01 00:00:00");
	}
	
	public static void setLastHistoryPlanIndex(Context context, int index)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(LAST_HISTORY_PLAN_INDEX, index);
		editor.commit();
	}

	public static int getLastHistoryPlanIndex(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getInt(LAST_HISTORY_PLAN_INDEX, 0);
	}

	public static void setCipherKey(Context context, String cipherKey)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(CIPHER_KEY, cipherKey);
		editor.commit();
	}

	public static String getCipherKey(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(CIPHER_KEY, "");
	}

	public static void setUncaughtExceptionTimestamp(Context context, long timestamp)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putLong(UNCAUGHT_EXCEPTIONS_TIMESTAMP, timestamp);
		editor.commit();
	}

	public static long getUncaughtExceptionTimestamp(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getLong(UNCAUGHT_EXCEPTIONS_TIMESTAMP, System.currentTimeMillis());
	}

	public static void setUncaughtExceptionCounter(Context context, int counter)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(UNCAUGHT_EXCEPTIONS_COUNTER, counter);
		editor.commit();
	}

	public static int getUncaughtExceptionCounter(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getInt(UNCAUGHT_EXCEPTIONS_COUNTER, 0);
	}

	public static void setRegistrationStep(Context context, int stepNumber)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(REGISTRATION_STEP, stepNumber);
		editor.commit();
	}

	public static int getRegistrationStep(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getInt(REGISTRATION_STEP, 1);
	}

	public static String getProfileName(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(PROFILE_NAME, null);
	}

	public static void setProfileName(Context context, String profileName)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(PROFILE_NAME, profileName);
		editor.commit();
	}

	public static void setWayEnabled(Context context, boolean value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(WAY_ENABLED, value);
		editor.commit();
	}

	public static boolean isWayEnabled(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(WAY_ENABLED, true);
	}

	public static void setFirstRun(Context context, boolean value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(FIRST_RUN, value);
		editor.commit();
	}

	public static boolean isFirstRun(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(FIRST_RUN, true);
	}

	public static void setShowInviteContactsDialog(Context context, boolean value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(SHOW_INVITE_CONTACTS_DIALOG, value);
		editor.commit();
	}

	public static boolean isShowInviteContactsDialog(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(SHOW_INVITE_CONTACTS_DIALOG, true);
	}

	public static void setFacebookId(Context context, String value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_ID, value);
		editor.commit();
	}

	public static String getFacebookId(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(FACEBOOK_ID, "");
	}

	public static void setFacebookGender(Context context, String value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_GENDER, value);
		editor.commit();
	}

	public static String getFacebookGender(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(FACEBOOK_GENDER, "");
	}

	public static void setFacebookName(Context context, String value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_NAME, value);
		editor.commit();
	}

	public static String getFacebookName(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(FACEBOOK_NAME, "");
	}
	
	public static void setFacebookEmail(Context context, String value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_EMAIL, value);
		editor.commit();
	}

	public static String getFacebookEmail(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(FACEBOOK_EMAIL, "");
	}

	public static void setFacebookBirthday(Context context, String value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(FACEBOOK_BIRTHDAY, value);
		editor.commit();
	}

	public static String getFacebookBirthday(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(FACEBOOK_BIRTHDAY, "");
	}
	
	public static void setFacebookActivitySynchronization(Context context, boolean value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(FACEBOOK_ACTIVITY, value);
		editor.commit();
	}

	public static boolean isFacebookActivitySynchronization(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(FACEBOOK_ACTIVITY, false);
	}

	public static void setFacebookContactsSynchronization(Context context, boolean value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(FACEBOOK_CONTACTS, value);
		editor.commit();
	}
	
	public static boolean isFourSquareEnabled(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(FOURSQUARE_VENUES, true);
	}

	public static void setFourSquareEnabled(Context context, boolean value)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(FOURSQUARE_VENUES, value);
		editor.commit();
	}

	public static boolean isFacebookContactsSynchronization(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(FACEBOOK_CONTACTS, false);
	}
	
	public static int getAppVersion(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getInt(APP_VERSION, 0);
	}

	public static void setAppVersion(Context context, int appVersion)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
}
