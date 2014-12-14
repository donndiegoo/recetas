package recetas.sherpa.studio.com.recetas.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.fragments.RecipeDetailWebView;

public class RecipeDetailActivity extends ActionBarActivity {

    private static final String ARG_RECIPE_INDEX = "arg_recipe_index";

    private int mRecipeIndex;

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

        if (savedInstanceState == null) {

            Bundle bundle = getIntent().getExtras();
            if(bundle != null)
            {
                mRecipeIndex = bundle.getInt(ARG_RECIPE_INDEX);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                RecipeDetailWebView fragment = RecipeDetailWebView.newInstance(mRecipeIndex);

                //SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
                transaction.replace(R.id.sample_content_fragment, fragment);
                transaction.commit();
            }
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
