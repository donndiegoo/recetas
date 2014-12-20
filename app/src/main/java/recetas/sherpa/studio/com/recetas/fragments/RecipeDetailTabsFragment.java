package recetas.sherpa.studio.com.recetas.fragments;

import android.os.Bundle;
import android.view.View;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.adapters.RecipeDetailTabsAdapter;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;
import recetas.sherpa.studio.com.recetas.widgets.SlidingTab.SlidingTabLayout;
import recetas.sherpa.studio.com.recetas.widgets.SlidingTab.SlidingTabsBasicFragment;

/**
 * Created by diego on 15/12/14.
 */
public class RecipeDetailTabsFragment extends SlidingTabsBasicFragment{



    public void setRecipe(RecipeStepByStep recipe)
    {
        mRecipe = recipe;
    }


    public static RecipeDetailTabsFragment newInstance(RecipeStepByStep recipe)
    {
        RecipeDetailTabsFragment fragment = new RecipeDetailTabsFragment();
        fragment.setRecipe(recipe);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecipeDetailTabsAdapter adapter = new RecipeDetailTabsAdapter(getActivity(), (RecipeStepByStep) mRecipe);
        SlidingTabLayout.TabColorizer colorizer = new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getActivity().getResources().getColor(R.color.white);
            }

            @Override
            public int getDividerColor(int position) {
                return getActivity().getResources().getColor(R.color.theme_color);
            }
        };

        configureSlidingTab(adapter,colorizer);
    }
}
