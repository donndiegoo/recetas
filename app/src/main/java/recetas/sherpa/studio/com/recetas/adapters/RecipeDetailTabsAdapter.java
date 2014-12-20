package recetas.sherpa.studio.com.recetas.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipeStepByStep;

/**
 * Created by diego on 15/12/14.
 */
public class RecipeDetailTabsAdapter extends PagerAdapter{

    private static final String LOG_TAG = "RecipeDetailTabsAdapter";
    private static final int PAGE_INGRIDIENTS = 0;
    private static final int PAGE_INSTRUCTIONS = 1;

    private final Context mContext;
    private RecipeStepByStep mRecipe;


    public RecipeDetailTabsAdapter(Context context, RecipeStepByStep recipe)
    {
        mContext = context;
        mRecipe = recipe;
    }

    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * @return true java.lang.Stringif the value returned from {@link #instantiateItem(android.view.ViewGroup, int)} is the
     * same object as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
     */
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    /**
     * Return the title of the item at {@code position}. This is important as what this method
     * returns is what is displayed in the {@link recetas.sherpa.studio.com.recetas.widgets.SlidingTab.SlidingTabLayout}.
     * <p>
     * Here we construct one using the position value, but for real application the title should
     * refer to the item's contents.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return mContext.getResources().getString(R.string.step_by_step_ingridients);
        else
            return mContext.getResources().getString(R.string.step_by_step_instructions);
    }

    /**
     * Instantiate the {@link android.view.View} which should be displayed at {@code position}. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        View view = createViewForPosition(position, container);
        // Add the newly created View to the ViewPager
        container.addView(view);

        Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

        // Return the View
        return view;
    }

    private View createViewForPosition(int position, ViewGroup container) {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.recipe_detail_page, container, false);

        if(position == PAGE_INGRIDIENTS)
        {
            configureViewIngridients(view);
        }
        else if(position == PAGE_INSTRUCTIONS)
        {
            configureViewInstructions(view);
        }

        return view;
    }

    private void configureViewInstructions(View view) {
        ListView listViewIngridients = (ListView) view.findViewById(R.id.list_step_by_step);
        InstructionsAdapter adapter = new InstructionsAdapter(mContext,R.layout.item_instructions,mRecipe.getListInstructions());
        listViewIngridients.setAdapter(adapter);
    }

    private void configureViewIngridients(View view) {
        ListView listViewIngridients = (ListView) view.findViewById(R.id.list_step_by_step);
        IngridientsAdapter adapter = new IngridientsAdapter(mContext,R.layout.item_ingridient,mRecipe.getListIngridients());
        listViewIngridients.setAdapter(adapter);

    }

    /**
     * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the
     * {@link android.view.View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
    }
}
