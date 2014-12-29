package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.etsy.android.grid.StaggeredGridView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.activities.RecipeCreateActivityPictures;
import recetas.sherpa.studio.com.recetas.activities.RecipeCreateActivityStepByStep;
import recetas.sherpa.studio.com.recetas.activities.RecipeDetailActivity;
import recetas.sherpa.studio.com.recetas.adapters.RecipesAdapter;
import recetas.sherpa.studio.com.recetas.utils.DropboxManager;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.widgets.FloatingActinButtons.FloatingActionsMenu;
import recetas.sherpa.studio.com.recetas.widgets.FloatingActinButtons.FloatingActionsMenuButtonListener;


/**
 * Created by diego on 11/12/14.
 */
public class RecipesFragment extends Fragment implements FloatingActionsMenuButtonListener, Observer {

    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "RecipesFragment";

    private FrameLayout mBlurImage;

    private View                    mRootView;
    private StaggeredGridView       mListView;
    private FloatingActionsMenu     mAddButton;

    private List<Recipe>            mListRecipes;
    private  RecipesAdapter         mAdapter;

    public RecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecipesManager.getInstance().loadRecipesFromCache();
        mListRecipes = RecipesManager.getInstance().getListReceipes();

        mListView = (StaggeredGridView) mRootView.findViewById(R.id.grid_view);
        mAdapter = new RecipesAdapter(getActivity(),R.layout.item_recipe,mListRecipes);
        mListView.setAdapter(mAdapter);

        mRootView.findViewById(R.id.container);
        mBlurImage = (FrameLayout) mRootView.findViewById(R.id.blur_image);
        mAddButton = (FloatingActionsMenu) mRootView.findViewById(R.id.multiple_actions);
        mAddButton.setListener(this);

        mRootView.findViewById(R.id.add_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecipeFromFile();
            }
        });

        mRootView.findViewById(R.id.add_manually).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecipeStepByStep();
            }
        });

        mRootView.findViewById(R.id.add_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecipeFromCamera();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecipeDetailActivity.startActivity(getActivity(), position);
            }
        });

        DropboxManager.getInstance().addObserver(this);

        return mRootView;
    }

    @Override
    public void onToggle() {
        if(mAddButton.isExpanded())
        {
            mBlurImage.setVisibility(View.VISIBLE);
            AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.9f);
            animation1.setDuration(500);
            animation1.setFillAfter(true);
            mBlurImage.startAnimation(animation1);

            // mBlurImage.setAlpha(0.85f);
        }
        else
        {
            AlphaAnimation animation1 = new AlphaAnimation(0.85f, 0.0f);
            animation1.setDuration(500);
            animation1.setFillAfter(true);
            mBlurImage.startAnimation(animation1);
        }
    }

    private void createRecipeFromFile() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void createRecipeFromCamera()
    {
        RecipeCreateActivityPictures.startActivity(getActivity());
    }

    private void createRecipeStepByStep() {
        RecipeCreateActivityStepByStep.startActivity(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        Log.d(TAG, "update recipes");

        boolean changed = (boolean) data;

        Log.d(TAG, "Recipes changed? " + changed);

        if(changed)
        {
            if(mListRecipes.isEmpty())
            {
                Log.d(TAG, "List empty");
                RecipesManager.getInstance().loadRecipesFromCache();
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                Log.d(TAG, "List not empty, show snackbar");

                SnackbarManager.show(
                        Snackbar.with(getActivity().getApplicationContext()) // context
                                .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                                .text("Hay nuevas recetas. Deseas mostrar los cambios?") // text to display
                                .actionLabel("Aceptar") // action button label
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {

                                        RecipesManager.getInstance().loadRecipesFromCache();
                                        mAdapter.notifyDataSetChanged();

                                    }
                                }) // action button's ActionClickListener
                                .swipeToDismiss(true)
                        , this.getActivity()); // activity where it is displayed
            }


        }
    }
}
