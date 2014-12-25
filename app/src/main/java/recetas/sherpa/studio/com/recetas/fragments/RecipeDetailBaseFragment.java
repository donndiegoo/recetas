package recetas.sherpa.studio.com.recetas.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.Recipe;

/**
 * Created by diego on 17/12/14.
 */
public class RecipeDetailBaseFragment extends Fragment{

    protected Recipe mRecipe;
    protected int    mRecipeIndex;

    // View
    protected TextView mToolbarTitle;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbarTitle =         (TextView) view.findViewById(R.id.toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
       mToolbarTitle.setText(mRecipe.getTitle());
    }

    public void startEditMode()
    {

    }

    public void saveRecipe()
    {

    }

}
