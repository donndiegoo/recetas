package recetas.sherpa.studio.com.recetas;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by diego on 13/12/14.
 */
public class MyApplication extends Application {

    public static Context mGeneralContext;

    public static Map<String,String> mHashMap;

    @Override
    public void onCreate() {
        super.onCreate();

        mGeneralContext = this;

        File mydir = new File(getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY); //Creating an internal dir;
        if(!mydir.exists()) {
            mydir.mkdir();
        }

        restoreHashMap();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        storeHashMap();
    }

    private void restoreHashMap()
    {
        File hashFile = new File(getFilesDir().getAbsolutePath() + "/" + Constants.HASH_FILE);
        mHashMap = new TreeMap();
        if(hashFile.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(hashFile);

                if (inputStream != null) {
                    InputStreamReader streamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(streamReader);

                    String line;
                    String key = null;
                    while (( line = bufferedReader.readLine()) != null) {
                        if(key == null)
                        {
                            key = line;
                        }
                        else
                        {
                            mHashMap.put(key,line);
                            key = null;
                        }
                    }
                }
                inputStream.close(); //close the file
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    public static void storeHashMap()
    {
        try
        {
            FileWriter fileWriter = new FileWriter(mGeneralContext.getFilesDir().getAbsolutePath() + "/" + Constants.HASH_FILE);

            String separator = "";

            for(Map.Entry<String,String> entry : mHashMap.entrySet())
            {
                fileWriter.write(separator + entry.getKey() + "\n" + entry.getValue());
                separator = "\n";
            }

            fileWriter.close();
        }
        catch(Exception e)
        {

        }
    }

    public static String getHashFile(String fileName)
    {
        String hash = "";
        if(mHashMap.containsKey(fileName))
        {
            hash = mHashMap.get(fileName);
        }
        return hash;
    }

    public static void setHashFile(String fileName, String hash)
    {
       mHashMap.put(fileName,hash);
    }
}
