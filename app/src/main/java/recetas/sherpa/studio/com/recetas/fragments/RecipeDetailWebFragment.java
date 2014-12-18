package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.Recipe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailWebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailWebFragment extends Fragment {

    private Recipe mRecipe;

    private WebView mWebView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe recipe
     * @return A new instance of fragment RecipeDetailPDF.
     */
    public static RecipeDetailWebFragment newInstance(Recipe recipe) {
        RecipeDetailWebFragment fragment = new RecipeDetailWebFragment();
        fragment.setRecipe(recipe);
        return fragment;
    }

    public RecipeDetailWebFragment() {
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

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }
}
