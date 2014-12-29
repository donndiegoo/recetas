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

import com.dd.CircularProgressButton;

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

        String baseDirectory = MyApplication.getRecipesBaseDirecotry();

        File mydir = new File(baseDirectory + "/" + Constants.RECIPES_DIRECTORY); //Creating an internal dir;
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

    public static String getRecipesBaseDirecotry()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            mProgresView = LayoutInflater.from(context).inflate(R.layout.fragment_dropbox,null);
            CircularProgressButton circluarProgress = (CircularProgressButton) mProgresView.findViewById(R.id.auth_button);
            circluarProgress.setIndeterminateProgressMode(true);
            circluarProgress.setProgress(50);

            ViewGroup rootView = ((ViewGroup) context.getWindow().getDecorView().findViewById(android.R.id.content));
            rootView.addView(mProgresView);
        }

        mProgresView.setVisibility(View.VISIBLE);



        return mProgresView;
    }

    public static void hideProgressView(View progressView)
    {
        if(progressView != null)
            progressView.setVisibility(View.GONE);
    }
}
