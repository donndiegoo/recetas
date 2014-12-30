package recetas.sherpa.studio.com.recetas.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by diego on 29/12/14.
 */
public class Utils {

    public static void deleteFile(String folderPath)
    {
        File dir = new File(folderPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteFile(folderPath + "/" + children[i]);
            }
            boolean deleted = dir.delete();
            Log.d("Utils", "deleted " + folderPath);
        }
        else
        {
            boolean deleted = dir.delete();
            Log.d("Utils", "deleted " + folderPath);
        }
    }
}
