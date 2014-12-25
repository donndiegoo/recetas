package recetas.sherpa.studio.com.recetas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.adapters.FullScreenImageAdapter;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;

public class ImageGalleryActivity extends Activity {

    private static final String ARG_RECIPE_INDEX = "arg_recipe_index";

    private ViewPager mViewPager;
    private FullScreenImageAdapter mAdapter;

    private Recipe mRecipe;

    public static void startActivity(Context context, int recipeIndex)
    {
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_RECIPE_INDEX, recipeIndex);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            int recipeIndex = bundle.getInt(ARG_RECIPE_INDEX);
            mRecipe = RecipesManager.getInstance().getListReceipes().get(recipeIndex);
        }


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new FullScreenImageAdapter(this, mRecipe.getListPictures());
        mViewPager.setAdapter(mAdapter);
    }

}
