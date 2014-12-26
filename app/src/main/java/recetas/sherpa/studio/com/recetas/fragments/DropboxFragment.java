package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import recetas.sherpa.studio.com.recetas.Constants;
import recetas.sherpa.studio.com.recetas.MyApplication;
import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.helpers.DropboxListener;
import recetas.sherpa.studio.com.recetas.helpers.TaskDropboxListener;
import recetas.sherpa.studio.com.recetas.utils.MyPreferences;

/**
 * Created by diego on 13/12/14.
 */
public class DropboxFragment extends Fragment implements TaskDropboxListener {
    private static final String TAG = "DropboxFragment";


    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    // You don't need to change these, leave them alone.
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    private DropboxAPI<AndroidAuthSession>  mApi;
    private Entry                           mRecipesContent;

    private String mApiKey;
    private String mApiSecret;

    private boolean mLoggedIn;

    // Android widgets
    private View   mRootView;
    private CircularProgressButton mSubmit;

    private AsyncTask mAsyncTaskRecipes;
    private AsyncTask mAsyncTaskChanges;
    private DropboxListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApiKey              = getActivity().getResources().getString(R.string.dropbox_api_key);
        mApiSecret           = getActivity().getResources().getString(R.string.dropbox_api_secret);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_dropbox, container, false);

        mSubmit = (CircularProgressButton) mRootView.findViewById(R.id.auth_button);
        mSubmit.setIndeterminateProgressMode(true);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // This logs you out if you're logged in, or vice versa
                if (mLoggedIn) {
                    getRecipes(mRecipesContent);
                } else {
                    doLogin();
                }
            }
        });

        // Display the proper UI state if logged in or not
        mLoggedIn = mApi.getSession().isLinked();
        if(!mLoggedIn)
        {
            doLogin();
        }
        else
        {
            checkRecipesChanges();
        }

        return mRootView;
    }

    private void doLogin() {
        mSubmit.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
        // Start the remote authentication
        if (USE_OAUTH1) {
            mApi.getSession().startAuthentication(getActivity());
        } else {
            mApi.getSession().startOAuth2Authentication(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();
                // Store it locally in our app for later use
                storeAuth(session);
                mLoggedIn = true;
                checkRecipesChanges();
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAsyncTaskRecipes != null && mAsyncTaskRecipes.getStatus() == AsyncTask.Status.RUNNING){
            mAsyncTaskRecipes.cancel(true);
        }
        if(mAsyncTaskChanges != null && mAsyncTaskChanges.getStatus() == AsyncTask.Status.RUNNING)
        {
            mAsyncTaskChanges.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setListener(DropboxListener listener)
    {
        mListener = listener;
    }

    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        mLoggedIn = false;
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
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
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(mApiKey, mApiSecret);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private void checkRecipesChanges()
    {
        if(RecipesManager.getInstance().getListReceipes().isEmpty())
        {
           mRootView.setVisibility(View.VISIBLE);
        }
        else
        {
            mRootView.setVisibility(View.INVISIBLE);
        }

        mAsyncTaskChanges = new TaskChanges(this);
        mAsyncTaskChanges.execute();
    }

    @Override
    public void onChangesTaskFinished(Object... result) {
        if(result.length > 0) // Recipes did change
        {
            mAsyncTaskRecipes = new TaskRecipes(DropboxFragment.this, (Entry) result[0]);
            mAsyncTaskRecipes.execute();
        }
        else
        {
            mSubmit.setProgress(100);
            refreshRecipes(false);
        }
    }

    @Override
    public void onRecipesTaskFinsihted(boolean result) {
        if(result)
        {
            refreshRecipes(true);
        }
        else
        {
            //TODO show error
            mSubmit.setProgress(-1);
        }
    }

    private void refreshRecipes(boolean changed)
    {
       mRootView.setVisibility(View.INVISIBLE);
        if(mListener != null)
        {
            mListener.onRecipesLoaded(changed);
        }
    }



    private void getRecipes(Entry recipesContent)
    {
        mAsyncTaskRecipes = new TaskRecipes(this,recipesContent);
        mAsyncTaskRecipes.execute();
    }

    private void moveTemporaryRecipesToRealRecipes(List<String> listFilesNotModified) {
        String recipesFolderPath = getActivity().getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY;
        String recipesFolderPathTemp = getActivity().getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY + "_aux";
        String recipesFolderPathTemp2 = getActivity().getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY + "_aux_2";

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

    private class TaskChanges extends AsyncTask<Object,Object,Object>
    {
        private TaskDropboxListener mListener;
        private boolean             mHasChanged;

        public TaskChanges(TaskDropboxListener listener) {

            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHasChanged = false;
            mSubmit.setProgress(50); // Start progress
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

        private TaskDropboxListener mListener;
        private Entry mRecipesContent;

        public TaskRecipes(TaskDropboxListener listener, Entry recipesContent) {
            mListener = listener;
            mRecipesContent = recipesContent;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSubmit.setProgress(50); // Start progress
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            boolean result = true;
            List<String> listFilesNotModified = new ArrayList<>();

            try{

                String recipesFolderNameLocal = getActivity().getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY + "_aux";
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

                for (Entry recipeFile : mRecipesContent.contents) {
                    if (recipeFile.isDir) {

                        Log.d(TAG,"Getting metadata from: " + recipeFile.fileName());

                        Entry recipeContent = mApi.metadata(recipeFile.path, 1000, null, true, null);

                        Log.d(TAG,"metadata arrived");

                        boolean recipeHasChanged = hasRecipesChanged(recipeFile.fileName(), recipeContent.hash);
                        if(recipeHasChanged) {

                            File recipeDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.fileName());
                            recipeDirectory.mkdir();

                            for (Entry recipeElement : recipeContent.contents) {
                                if (recipeElement.isDir && recipeElement.fileName().equals("Imagenes")) {
                                    Entry imagesContent = mApi.metadata(recipeElement.path, 1000, null, true, null);

                                    if (imagesContent.contents.size() > 0) {
                                        File imagesDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.fileName() + "/" + "Imagenes");
                                        imagesDirectory.mkdir();

                                        for (Entry imageElement : imagesContent.contents) {
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
                result = false;
            }
            catch(FileNotFoundException e)
            {
                Log.d(TAG,e.getMessage());
                result = false;
            }
            catch (IOException e)
            {
                Log.d(TAG,e.getMessage());
                result = false;
            }
            catch (Exception e)
            {
                Log.d(TAG,e.getMessage());
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(mListener != null)
            {
                mListener.onRecipesTaskFinsihted(result);
            }
        }
    }
}
