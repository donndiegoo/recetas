package recetas.sherpa.studio.com.recetas.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class MyPreferences
{
	public static final String SETTINGS_NAME = "settings";
	private static final String RECIPES_HASH = "recipes_hash";

//	public static void setRecipesHash(Context context, String hash)
//	{
//		Editor editor = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).edit();
//		editor.putString(RECIPES_HASH, hash);
//		editor.commit();
//	}
//
//	public static String getRecipesHash(Context context)
//	{
//		return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE).getString(RECIPES_HASH, "");
//	}
}
