package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.dd.CircularProgressButton;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.activities.ImageGalleryActivity;
import recetas.sherpa.studio.com.recetas.adapters.FullScreenImageAdapter;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipeFile;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link recetas.sherpa.studio.com.recetas.fragments.RecipeDetailImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailImagesFragment extends RecipeDetailBaseFragment {

    private ImageButton mButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe recipe
     * @return A new instance of fragment RecipeDetailPDF.
     */
    public static RecipeDetailImagesFragment newInstance(Recipe recipe, int recipeIndex) {
        RecipeDetailImagesFragment fragment = new RecipeDetailImagesFragment();
        fragment.setRecipe(recipe, recipeIndex);
        return fragment;
    }

    public RecipeDetailImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail_images, container, false);

        mButton = (ImageButton) rootView.findViewById(R.id.button_gallery);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageGalleryActivity.startActivity(getActivity(),mRecipeIndex);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Resources r = getResources();
        int actionBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        height = height - actionBarHeight;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mButton.getLayoutParams();
        params.height = height;
        mButton.setLayoutParams(params);
    }

    public void setRecipe(Recipe recipe, int recipeIndex) {
        mRecipe = recipe;
        mRecipeIndex = recipeIndex;
    }
}
