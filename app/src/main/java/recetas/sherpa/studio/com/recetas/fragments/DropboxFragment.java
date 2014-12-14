package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import recetas.sherpa.studio.com.recetas.Constants;
import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.activities.DropboxListener;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.utils.MyPreferences;

/**
 * Created by diego on 13/12/14.
 */
public class DropboxFragment extends Fragment{
    private static final String TAG = "DropboxFragment";


    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    // You don't need to change these, leave them alone.
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;
    private static final int MAX_NUMBER_RETRIES = 2;

    private DropboxAPI<AndroidAuthSession> mApi;

    private String mApiKey;
    private String mApiSecret;

    private int mNumberRetries;
    private boolean mLoggedIn;
    private boolean mRecipesLoaded;

    // Android widgets
    private View   mRootView;
    private CircularProgressButton mSubmit;

    private DropboxListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNumberRetries = 0;

        mApiKey = getActivity().getResources().getString(R.string.dropbox_api_key);
        mApiSecret = getActivity().getResources().getString(R.string.dropbox_api_secret);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        if(mLoggedIn)
        {
            getRecipes();
        }

        checkAppKeySetup();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_dropbox, container, false);

        mSubmit = (CircularProgressButton) mRootView.findViewById(R.id.auth_button);
        mSubmit.setIndeterminateProgressMode(true);
        // Display the proper UI state if logged in or not
        setLoggedIn(mApi.getSession().isLinked());

        mSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // This logs you out if you're logged in, or vice versa
                if (mLoggedIn) {

                    if(mRecipesLoaded || mNumberRetries == MAX_NUMBER_RETRIES)
                    {
                        if(mListener != null)
                        {
                            mListener.onRecipesLoaded();
                        }
                    }
                    else
                    {
                        mNumberRetries++;
                        getRecipes();
                    }


                } else {
                    mSubmit.setProgress(50); // set progress > 0 & < 100 to display indeterminate progress
                    // Start the remote authentication
                    if (USE_OAUTH1) {
                        mApi.getSession().startAuthentication(getActivity());
                    } else {
                        mApi.getSession().startOAuth2Authentication(getActivity());
                    }
                }
            }
        });



        return mRootView;
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
                setLoggedIn(true);
                getRecipes();
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
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
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        if (loggedIn) {
            //mSubmit.setText(getActivity().getResources().getString(R.string.dropbox_unlink));

        } else {
            //mSubmit.setText(getActivity().getResources().getString(R.string.dropbox_syncrhonize));
        }
    }

    private void checkAppKeySetup() {

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + mApiKey;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getActivity().getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
        }
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

    public void getRecipes()
    {
        (new AsyncTask<Void,Void,Boolean>(){

            private boolean mResult;
            List<Recipe> mListRecipes;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSubmit.setProgress(50); // Start progress
                mResult = true;
                mRecipesLoaded = false;
                mListRecipes = new ArrayList<Recipe>();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try{
                    RecipesManager.getInstance().loadRecipesFromCache(); // Load cache just in case

                    String recipesFolderNameLocal = getActivity().getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY + "_aux";
                    String recipesFolderNameRemote = "/" + Constants.RECIPES_DIRECTORY;

                    File file = new File(recipesFolderNameLocal);
                    if(file.exists()){
                        deleteFile(recipesFolderNameLocal);
                    }
                    file.mkdir();

                    Entry recipesContent = mApi.metadata(recipesFolderNameRemote, 1000, null, true, null);

                    boolean recipesHasChanged = hasRecipesChanged(recipesContent.hash); // Check hash
                    if(recipesHasChanged) {
                        for (Entry recipeFile : recipesContent.contents) {
                            Recipe recipe = new Recipe();
                            recipe.setTitle(recipeFile.fileName());

                            if (recipeFile.isDir) {
                                File recipeDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.fileName());
                                recipeDirectory.mkdir();
                                recipe.setPath(recipeDirectory.getAbsolutePath());

                                Entry recipeContent = mApi.metadata(recipeFile.path, 1000, null, true, null);

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
                                                    recipe.addPicture(imagePath);
                                                }
                                            }
                                        }
                                    } else if (!recipeElement.isDir) {
                                        String filePath = recipesFolderNameLocal + "/" + recipeFile.fileName() + "/" + recipeElement.fileName();
                                        FileOutputStream outputStream = new FileOutputStream(filePath);
                                        DropboxAPI.DropboxFileInfo info = mApi.getFile(recipesFolderNameRemote + "/" + recipeFile.fileName() + "/" + recipeElement.fileName(), null, outputStream, null);
                                        outputStream.close();
                                        //TODO change this after
                                        recipe.setPath(filePath);
                                    }
                                }
                            } else {
                                String filePath = recipesFolderNameLocal + "/" + recipeFile.fileName();
                                FileOutputStream outputStream = new FileOutputStream(filePath);
                                DropboxAPI.DropboxFileInfo info = mApi.getFile(recipesFolderNameRemote + "/" + recipeFile.fileName(), null, outputStream, null);
                                outputStream.close();
                                recipe.setPath(filePath);
                            }

                            mListRecipes.add(recipe);
                        }

                        RecipesManager.getInstance().setListReceipes(mListRecipes);
                        if(mListRecipes.size() > 0)
                        {
                            MyPreferences.setRecipesHash(getActivity(),recipesContent.hash);
                            moveTemporaryRecipesToRealRecipes();
                        }

                        return mResult;

                    }
                    // Recipes did not change since last call
                    else
                    {
                        return mResult;
                    }
                }
                catch(DropboxException e)
                {
                    Log.d(TAG,e.getMessage());
                    mResult = false;
                }
                catch(FileNotFoundException e)
                {
                    Log.d(TAG,e.getMessage());
                    mResult = false;
                }
                catch (IOException e)
                {
                    Log.d(TAG,e.getMessage());
                    mResult = false;
                }
                catch (Exception e)
                {
                    Log.d(TAG,e.getMessage());
                    mResult = false;
                }

                return mResult;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                mSubmit.setProgress(result ? 100 : -1);
                mRecipesLoaded = result;
            }
        }).execute();
    }

    private void moveTemporaryRecipesToRealRecipes() {
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
                deleteFile(recipesFolderPathTemp);
                deleteFile(recipesFolderPathTemp2);
            }
        } // Rename original folder to temp2

    }

    private boolean hasRecipesChanged(String hash) {
        String lastHash = MyPreferences.getRecipesHash(getActivity());
        return !hash.equals(lastHash);
    }

    public void deleteFile(String folderPath)
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

}
