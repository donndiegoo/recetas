package recetas.sherpa.studio.com.recetas;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.dd.CircularProgressButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.utils.Utils;

/**
 * Created by diego on 13/12/14.
 */
public class MyApplication extends Application {

    public static Context mGeneralContext;

    public static Map<String,String> mHashMap;

    public static String mFolderSelected = "Recetas Familia";

    @Override
    public void onCreate() {
        super.onCreate();

        mGeneralContext = this;

        String baseDirectory = MyApplication.getRecipesBaseDirecotry();

        File baseDirectoryFile = new File(baseDirectory);
        if(!baseDirectoryFile.exists())
        {
            baseDirectoryFile.mkdir();
        }

        File mydir = new File(baseDirectory + "/" + mFolderSelected); //Creating an internal dir;
        if(!mydir.exists()) {
            mydir.mkdir();
        }

        cleanRecipesCache();

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


    public static void removeHashTable() {
        mHashMap.clear();
        storeHashMap();
    }

    public static String getRecipesBaseDirecotry()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mGeneralContext.getResources().getString(R.string.app_name);
    }

    public static boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mGeneralContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo =
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public static View showProgressView(Activity context, View mProgresView)
    {
        if(mProgresView == null)
        {
            mProgresView = LayoutInflater.from(context).inflate(R.layout.include_progress_view,null);
            CircularProgressButton circluarProgress = (CircularProgressButton) mProgresView.findViewById(R.id.auth_button);
            circluarProgress.setIndeterminateProgressMode(true);
            circluarProgress.setProgress(50);

            ViewGroup rootView = ((ViewGroup) context.getWindow().getDecorView().findViewById(android.R.id.content));
            rootView.addView(mProgresView);
        }

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(500);
        animation1.setFillAfter(true);
        mProgresView.startAnimation(animation1);



        return mProgresView;
    }

    public static void hideProgressView(View progressView)
    {
        if(progressView != null)
        {
            AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
            animation1.setDuration(500);
            animation1.setFillAfter(true);
            progressView.startAnimation(animation1);
        }

    }

    public static void cleanAllRecipies()
    {
        cleanRecipesCache();
        String baseDirectory = getRecipesBaseDirecotry();

        String recipesFolderPath = baseDirectory + "/" + mFolderSelected;
        Utils.deleteFile(recipesFolderPath);
        File mydir = new File(recipesFolderPath);
        mydir.mkdir();

        MyApplication.removeHashTable();
        RecipesManager.getInstance().cleanListRecipes();
    }

    public static void cleanRecipesCache() {
        String baseDirectory = MyApplication.getRecipesBaseDirecotry();

        String recipesFolderPathTemp = baseDirectory + "/" + mFolderSelected + "_aux";
        String recipesFolderPathTemp2 = baseDirectory + "/" + mFolderSelected + "_aux_2";

        Utils.deleteFile(recipesFolderPathTemp);
        Utils.deleteFile(recipesFolderPathTemp2);
    }
}
