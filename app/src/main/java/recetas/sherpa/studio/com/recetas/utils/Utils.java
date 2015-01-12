package recetas.sherpa.studio.com.recetas.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import recetas.sherpa.studio.com.recetas.Constants;

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

    public static void saveBitmap(Bitmap bitmap, String path) throws Exception {
        boolean error = false;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                //TODO error
            }
            finally {
                if(error)
                {
                    throw new Exception();
                }
            }
        }
    }
}
