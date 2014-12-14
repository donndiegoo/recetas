package recetas.sherpa.studio.com.recetas;

import android.app.Application;
import android.content.Context;

import java.io.File;

/**
 * Created by diego on 13/12/14.
 */
public class MyApplication extends Application {

    public static Context mGeneralContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mGeneralContext = this;

        File mydir = new File(getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY); //Creating an internal dir;
        if(!mydir.exists()) {
            mydir.mkdir();
        }
    }
}
