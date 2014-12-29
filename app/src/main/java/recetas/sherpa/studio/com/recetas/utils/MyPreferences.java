package recetas.sherpa.studio.com.recetas.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class MyPreferences
{
	public static final String SETTINGS_NAME = "settings";
	private static final String LAST_RECIPES_QUERY = "recipes_hash";


	public static void setLastRecipesQueryDate(Context context, String date)
	{
		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(LAST_RECIPES_QUERY, date);
		editor.commit();
	}

	public static String getLastRecipesQueryDate(Context context)
	{
		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(LAST_RECIPES_QUERY, "2015/01/06 00:00:00");
	}
}
