package recetas.sherpa.studio.com.recetas.utils.googledrive;
/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;

import java.util.ArrayList;
import java.util.List;

import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;

/**
 * An abstract activity that handles authorization and connection to the Drive
 * services.
 */
public abstract class GoogleDriveBaseActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleDriveBaseActivity";

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    /**
     * DriveId of an existing folder to be used as a parent folder in
     * folder operations samples.
     */
    public static final String EXISTING_FOLDER_ID = "Recetas";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION) {
            mResolvingError = false;
            if(resultCode == RESULT_OK)
            {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }


    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        showMessage("GoogleApiClient connected");

        DriveFolder rootFolder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
        rootFolder.listChildren(getGoogleApiClient()).setResultCallback(childrenRetrievedCallback);

        //MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
        //        .setTitle("New folder").build();
        //Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
        //        getGoogleApiClient(), changeSet).setResultCallback(folderCreatedCallback);

    }


    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            mResolvingError = true;
        }
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    private void transformBufferIntoRecipes(MetadataBufferResult buffer)
    {
        List<Recipe> listRecipes = new ArrayList<Recipe>(buffer.getMetadataBuffer().getCount());

        for(Metadata metadata : buffer.getMetadataBuffer())
        {
            Recipe recipe = new Recipe(metadata.getTitle());
            listRecipes.add(recipe);
        }

        RecipesManager.getInstance().setListReceipes(listRecipes);
        onLoadFinished();
    }

    public void onLoadFinished()
    {
        showMessage("Load finished");
    }

    ResultCallback<MetadataBufferResult> childrenRetrievedCallback = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }

                    for(Metadata metadata : result.getMetadataBuffer())
                    {
                        String title = metadata.getTitle();
                        if(title.equals("Recetas"))
                        {
                            return;
                        }
                    }

                }
            };
}