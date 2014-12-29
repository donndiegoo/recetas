package recetas.sherpa.studio.com.recetas.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import recetas.sherpa.studio.com.recetas.Constants;
import recetas.sherpa.studio.com.recetas.MyApplication;
import recetas.sherpa.studio.com.recetas.R;

/**
 * Created by diego on 27/12/14.
 */
public class DropboxManager implements DropboxListenerTask {

    private enum  TYPE_TASK
    {
        TASK_NONE,
        TASK_LOAD_RECIPES,
        TASK_UPLOAD_RECIPE
    }


    private static DropboxManager               mInstance;

    private static final String                 TAG = "DropboxFragment";


    // You don't need to change these, leave them alone.
    private static final String                 ACCOUNT_PREFS_NAME = "prefs";
    private static final String                 ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String                 ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean                USE_OAUTH1 = false;

    private DropboxAPI<AndroidAuthSession>      mApi;
    private DropboxAPI.Entry                    mRecipesContent;

    private String                              mApiKey;
    private String                              mApiSecret;

    private AsyncTask                           mAsyncTaskRecipes;
    private AsyncTask                           mAsyncTaskChanges;

    private DropboxListener                     mListener;
    private Activity                            mContext;

    private TYPE_TASK                           mTypeTask;

    private View                                mProgressView;




    private DropboxManager()
    {
        mApiKey              = MyApplication.mGeneralContext.getResources().getString(R.string.dropbox_api_key);
        mApiSecret           = MyApplication.mGeneralContext.getResources().getString(R.string.dropbox_api_secret);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

        mTypeTask = TYPE_TASK.TASK_NONE;
    }

    public static DropboxManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new DropboxManager();
        }

        return mInstance;
    }

    /****************************************************************************************************************
     * PUBLIC METHODS
     ***************************************************************************************************************/

    public void setListener(DropboxListener listener)
    {
        mListener = listener;
    }

    public void setContext(Activity context) {

        if(mContext != null) {
            ViewGroup rootView = ((ViewGroup) mContext.getWindow().getDecorView().findViewById(android.R.id.content));
            rootView.removeView(mProgressView);
        }

        mContext = context;
        mProgressView = LayoutInflater.from(mContext).inflate(R.layout.fragment_dropbox,null);
        mProgressView.setVisibility(View.GONE);
        CircularProgressButton circluarProgress = (CircularProgressButton) mProgressView.findViewById(R.id.auth_button);
        circluarProgress.setIndeterminateProgressMode(true);
        circluarProgress.setProgress(50);

        ViewGroup rootView = ((ViewGroup) mContext.getWindow().getDecorView().findViewById(android.R.id.content));
        rootView.addView(mProgressView);
    }

    public void loadRecipes(Context context)
    {
        showProgress();
        mTypeTask = TYPE_TASK.TASK_LOAD_RECIPES;
        doLogin(context);
    }



    /*****************************************************************************************************************
        DROPBOX AUTH
     *****************************************************************************************************************/

    private void doLogin(Context context) {
        AndroidAuthSession session = mApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();
                // Store it locally in our app for later use
                storeAuth(session);

                if(mTypeTask == TYPE_TASK.TASK_LOAD_RECIPES)
                {
                    mAsyncTaskChanges = new TaskChanges(this);
                    mAsyncTaskChanges.execute();
                }
                else if(mTypeTask == TYPE_TASK.TASK_UPLOAD_RECIPE)
                {

                }

            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox: " + e.getLocalizedMessage());
                hideProgress();
            }
        }
        else {
            // Start the remote authentication
            if (USE_OAUTH1) {
                mApi.getSession().startAuthentication(context);
            } else {
                mApi.getSession().startOAuth2Authentication(context);
            }
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(mApiKey, mApiSecret);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = MyApplication.mGeneralContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = MyApplication.mGeneralContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = MyApplication.mGeneralContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = MyApplication.mGeneralContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }



    /*****************************************************************************************************************
     DROPBOX GET RECIPES
     *****************************************************************************************************************/

    @Override
    public void onChangesTaskFinished(Object... result) {
        if(result.length > 0) // Recipes did change
        {
            mAsyncTaskRecipes = new TaskRecipes(DropboxManager.this, (DropboxAPI.Entry) result[0]);
            mAsyncTaskRecipes.execute();
        }
        else
        {
            refreshRecipes(false);
        }
    }

    @Override
    public void onRecipesTaskFinsihed(boolean result) {
        refreshRecipes(result);
    }

    private void refreshRecipes(boolean changed)
    {
        if(mListener != null)
        {
            mListener.onRecipesLoaded(changed);
        }
        mTypeTask = null;

        hideProgress();
    }



    private class TaskChanges extends AsyncTask<Object,Object,Object>
    {
        private DropboxListenerTask mListener;
        private boolean             mHasChanged;

        public TaskChanges(DropboxListenerTask listener) {

            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHasChanged = false;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String recipesFolderNameRemote = "/" + Constants.RECIPES_DIRECTORY;
                mRecipesContent = mApi.metadata(recipesFolderNameRemote, 1000, null, true, null);
                //mHasChanged = hasRecipesChanged(Constants.RECIPES_DIRECTORY, mRecipesContent.hash); //FIXME delete Task and add behaviour inside TaskRescipes
                mHasChanged = true;
            }
            catch(DropboxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(mHasChanged)
            {
                mListener.onChangesTaskFinished(mRecipesContent);
            }
            else{
                mListener.onChangesTaskFinished();
            }

        }
    }

    private class TaskRecipes extends AsyncTask<Object,Object,Boolean> {

        private DropboxListenerTask mListener;
        private DropboxAPI.Entry mRecipesContent;

        public TaskRecipes(DropboxListenerTask listener, DropboxAPI.Entry recipesContent) {
            mListener = listener;
            mRecipesContent = recipesContent;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            boolean recipesChanged = false;
            List<String> listFilesNotModified = new ArrayList<>();

            try{

                String baseDirectory = MyApplication.getRecipesBaseDirecotry();

                String recipesFolderNameLocal = baseDirectory + "/" + Constants.RECIPES_DIRECTORY + "_aux";
                String recipesFolderNameRemote = "/" + Constants.RECIPES_DIRECTORY;

                File file = new File(recipesFolderNameLocal);
                if(file.exists()){
                    deleteFile(recipesFolderNameLocal);
                }
                file.mkdir();

                if(mRecipesContent == null)
                {
                    mRecipesContent = mApi.metadata(recipesFolderNameRemote, 1000, null, true, null);
                }

                for (DropboxAPI.Entry recipeFile : mRecipesContent.contents) {
                    if (recipeFile.isDir) {

                        Log.d(TAG,"Getting metadata from: " + recipeFile.fileName());

                        DropboxAPI.Entry recipeContent = mApi.metadata(recipeFile.path, 1000, null, true, null);

                        Log.d(TAG,"metadata arrived");

                        boolean recipeHasChanged = hasRecipesChanged(recipeFile.fileName(), recipeContent.hash);
                        if(recipeHasChanged) {
                            recipesChanged = true;

                            File recipeDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.fileName());
                            recipeDirectory.mkdir();

                            for (DropboxAPI.Entry recipeElement : recipeContent.contents) {
                                if (recipeElement.isDir && recipeElement.fileName().equals("Imagenes")) {
                                    DropboxAPI.Entry imagesContent = mApi.metadata(recipeElement.path, 1000, null, true, null);

                                    if (imagesContent.contents.size() > 0) {
                                        File imagesDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.fileName() + "/" + "Imagenes");
                                        imagesDirectory.mkdir();

                                        for (DropboxAPI.Entry imageElement : imagesContent.contents) {
                                            if (!imageElement.isDir) {
                                                String imagePath = recipesFolderNameLocal + "/" + recipeFile.fileName() + "/Imagenes/" + imageElement.fileName();
                                                FileOutputStream outputStream = new FileOutputStream(imagePath);
                                                DropboxAPI.DropboxFileInfo info = mApi.getFile(recipesFolderNameRemote + "/" + recipeFile.fileName() + "/Imagenes/" + imageElement.fileName(), null, outputStream, null);
                                                outputStream.close();
                                            }
                                        }
                                    }
                                } else if (!recipeElement.isDir) {
                                    String filePath = recipesFolderNameLocal + "/" + recipeFile.fileName() + "/" + recipeElement.fileName();
                                    FileOutputStream outputStream = new FileOutputStream(filePath);
                                    DropboxAPI.DropboxFileInfo info = mApi.getFile(recipesFolderNameRemote + "/" + recipeFile.fileName() + "/" + recipeElement.fileName(), null, outputStream, null);
                                    outputStream.close();
                                }
                            }
                            MyApplication.setHashFile(recipeFile.fileName(),recipeContent.hash);
                        }
                        else
                        {
                            listFilesNotModified.add(recipeFile.fileName());
                        }
                    }
                }

                MyApplication.setHashFile(mRecipesContent.fileName(),mRecipesContent.hash);
                MyApplication.storeHashMap();
                moveTemporaryRecipesToRealRecipes(listFilesNotModified);

            }
            catch(DropboxException e)
            {
                Log.d(TAG,e.getMessage());
                recipesChanged = false;
                showErrorMessage();
            }
            catch(FileNotFoundException e)
            {
                Log.d(TAG,e.getMessage());
                recipesChanged = false;
                showErrorMessage();
            }
            catch (IOException e)
            {
                Log.d(TAG,e.getMessage());
                recipesChanged = false;
                showErrorMessage();
            }
            catch (Exception e)
            {
                Log.d(TAG,e.getMessage());
                recipesChanged = false;
                showErrorMessage();
            }

            return recipesChanged;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(mListener != null)
            {
                mListener.onRecipesTaskFinsihed(result);
            }
        }
    }


    private class TaskUploadRecipe extends AsyncTask<Recipe,Object,Object>{


        @Override
        protected Object doInBackground(Recipe... params) {

            Recipe recipe = params[0];

            try
            {
                // 1. Create Recipe directory
                File localRecipeDirectory = new File(recipe.getRecipeDirectoryPathLocal());

                FileInputStream inputStream = new FileInputStream(localRecipeDirectory);
                DropboxAPI.Entry response = mApi.putFile(recipe.getRecipeDirectoryPathRemote(), inputStream,
                        localRecipeDirectory.length(), null, null);

               // if(response.rev)

            }
            catch (IOException e)
            {

            }
            catch (DropboxException e)
            {

            }
            catch(Exception e)
            {

            }




            return null;
        }
    }



    /*****************************************************************************************************************
     PRIVATE METHODS
     *****************************************************************************************************************/

    private void showToast(String msg) {
        Toast error = Toast.makeText(MyApplication.mGeneralContext, msg, Toast.LENGTH_LONG);
        error.show();
    }

    private boolean hasRecipesChanged(String path, String hash) {
        String lastHash = MyApplication.getHashFile(path);
        return !hash.equals(lastHash);
    }

    private void deleteFile(String folderPath)
    {
        File dir = new File(folderPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteFile(children[i]);
            }
            dir.delete();
        }
        else
        {
            dir.delete();
        }
    }

    private void moveTemporaryRecipesToRealRecipes(List<String> listFilesNotModified) {

        String baseDirectory = MyApplication.getRecipesBaseDirecotry();

        String recipesFolderPath = baseDirectory + "/" + Constants.RECIPES_DIRECTORY;
        String recipesFolderPathTemp = baseDirectory + "/" + Constants.RECIPES_DIRECTORY + "_aux";
        String recipesFolderPathTemp2 = baseDirectory + "/" + Constants.RECIPES_DIRECTORY + "_aux_2";

        File recipesFolder = new File(recipesFolderPath);
        File recipesFolderTemp = new File(recipesFolderPathTemp);
        File recipesFolderTemp2 = new File(recipesFolderPathTemp2);
        recipesFolderTemp2.mkdir();

        if(recipesFolder.renameTo(recipesFolderTemp2)){
            recipesFolder = new File(recipesFolderPath); // reset recipesFolder to original folder
            if(recipesFolderTemp.renameTo(recipesFolder)) // Reneme temp folder to oringal one)
            {
                for(String name : listFilesNotModified)
                {
                    File fileOrigin = new File(recipesFolderPathTemp2 + "/" + name);
                    File fileDestin = new File(recipesFolderPath + "/" + name);
                    boolean renamed = fileOrigin.renameTo(fileDestin);
                    Log.d(TAG, "file: " + name + " moved? " + renamed);
                }

                deleteFile(recipesFolderPathTemp);
                deleteFile(recipesFolderPathTemp2);
            }
        } // Rename original folder to temp2

    }

    private void showErrorMessage() {
        Toast.makeText(MyApplication.mGeneralContext,"Ups! Ha habido un error de conexion, Tus recetas no se han actualizado pero no pasa nada, lo haran la proxima vez ;)",Toast.LENGTH_LONG).show();
    }

    private void showProgress()
    {
        mProgressView.setVisibility(View.VISIBLE);
    }

    private void hideProgress()
    {
        mProgressView.setVisibility(View.GONE);
    }

}
