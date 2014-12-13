package recetas.sherpa.studio.com.recetas.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ListView;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.adapters.RecipesAdapter;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;
import recetas.sherpa.studio.com.recetas.widgets.FloatingActinButtons.FloatingActionsMenu;
import recetas.sherpa.studio.com.recetas.widgets.FloatingActinButtons.FloatingActionsMenuButtonListener;


/**
 * Created by diego on 11/12/14.
 */
public class RecipesFragment extends Fragment implements FloatingActionsMenuButtonListener{

    private String mBackgroundFilename;
    private View mBackgroundContainer;
    private FrameLayout mBlurImage;

    private View mRootView;
    private ListView mListView;
    private FloatingActionsMenu mAddButton;

    public RecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        Recipe[] listobjects = RecipesManager.getInstance().getListReceipes().toArray(new Recipe[0]);

        mListView = (ListView) mRootView.findViewById(R.id.listView);
        RecipesAdapter adapter = new RecipesAdapter(getActivity(),R.layout.item_recipe,listobjects);
        mListView.setAdapter(adapter);

        mBackgroundContainer = mRootView.findViewById(R.id.container);
        mBlurImage = (FrameLayout) mRootView.findViewById(R.id.blur_image);
        mAddButton = (FloatingActionsMenu) mRootView.findViewById(R.id.multiple_actions);
        mAddButton.setListener(this);

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onToggle() {
        if(mAddButton.isExpanded())
        {
            mBlurImage.setVisibility(View.VISIBLE);
            AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.85f);
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
}
