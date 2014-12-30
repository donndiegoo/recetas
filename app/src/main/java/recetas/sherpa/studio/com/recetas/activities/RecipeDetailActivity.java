package recetas.sherpa.studio.com.recetas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.squareup.picasso.Picasso;

import java.io.File;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipeFile;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.fragments.RecipeDetailBaseFragment;
import recetas.sherpa.studio.com.recetas.fragments.RecipeDetailExternalAppFragment;
import recetas.sherpa.studio.com.recetas.fragments.RecipeDetailImagesFragment;
import recetas.sherpa.studio.com.recetas.fragments.RecipeDetailStepByStepFragment;
import recetas.sherpa.studio.com.recetas.fragments.RecipeDetailWebFragment;

public class RecipeDetailActivity extends ActionBarActivity {

    private static final String ARG_RECIPE_INDEX = "arg_recipe_index";

    private Recipe mRecipe;
    private int    mRecipeIndex;

    // Views
    ScrollView mScrollView;
    ImageView  mImage;
    RecipeDetailBaseFragment mFragment;

    public static void startActivity(Context context, int recipeIndex)
    {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_RECIPE_INDEX, recipeIndex);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mImage = (ImageView) findViewById(R.id.recipe_detail_image);

        if (savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                int recipeIndex = bundle.getInt(ARG_RECIPE_INDEX);
                mRecipe = RecipesManager.getInstance().getListReceipes().get(recipeIndex);
                mRecipeIndex = recipeIndex;

                getSupportActionBar().setTitle("");

                createRecipeFragment();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mScrollView.scrollTo(0,200);
        setPicture();

    }

    private void setPicture() {
        String picturePath = mRecipe.getFirstPicture();
        if(picturePath.length() > 0)
        {
            mImage.setVisibility(View.VISIBLE);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(this)
                    .load(new File(picturePath))
                    .tag(this)
                    .resize(width,height)
                    .centerInside()
                    .into(mImage);
        }
        else
        {
            mImage.setImageBitmap(null);
            mImage.setVisibility(View.GONE);
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.sample_content_fragment);
            frameLayout.setPadding(0,0,0,0);
        }
    }

    private void createRecipeFragment() {
        mFragment = null;

        if(mRecipe instanceof RecipeFile)
        {
            if(((RecipeFile) mRecipe).getFilePath().endsWith(".html"))
            {
                mFragment = RecipeDetailWebFragment.newInstance((RecipeFile) mRecipe);
            }
            else
            {
                mFragment = RecipeDetailExternalAppFragment.newInstance((RecipeFile) mRecipe);
            }
        }
        else if(mRecipe instanceof RecipeStepByStep)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFragment = RecipeDetailStepByStepFragment.newInstance((RecipeStepByStep) mRecipe);
        }
        else
        {
            mFragment = RecipeDetailImagesFragment.newInstance(mRecipe,mRecipeIndex);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.sample_content_fragment, mFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            return true;
        }
        else if (id == R.id.action_edit) {
            mFragment.startEditMode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
