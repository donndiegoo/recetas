package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.Recipe;
import recetas.sherpa.studio.com.recetas.data.RecipesManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailWebView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailWebView extends Fragment {
    private static final String ARG_FILE_PATH = "arg_file_path";

    private Recipe mRecipe;

    private WebView mWebView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipeIndex index of the recipe
     * @return A new instance of fragment RecipeDetailPDF.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailWebView newInstance(int recipeIndex) {
        RecipeDetailWebView fragment = new RecipeDetailWebView();
        Bundle args = new Bundle();
        args.putInt(ARG_FILE_PATH, recipeIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public RecipeDetailWebView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = RecipesManager.getInstance().getListReceipes().get(getArguments().getInt(ARG_FILE_PATH));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail_pd, container, false);

        mWebView = (WebView) rootView.findViewById(R.id.webView);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.loadUrl("file://" + mRecipe.getPath().replace("Recetas_aux", "Recetas"));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}