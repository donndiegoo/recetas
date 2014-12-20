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

import com.dd.CircularProgressButton;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipeFile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailWebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailWebFragment extends RecipeDetailBaseFragment {

    private WebView mWebView;
    private CircularProgressButton mProgress;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe recipe
     * @return A new instance of fragment RecipeDetailPDF.
     */
    public static RecipeDetailWebFragment newInstance(RecipeFile recipe) {
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
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail_web, container, false);

        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mProgress = (CircularProgressButton) rootView.findViewById(R.id.progress);
        mProgress.setIndeterminateProgressMode(true);
        mProgress.setProgress(50);
        mProgress.setVisibility(View.VISIBLE);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgress.setProgress(0);
                mProgress.setVisibility(View.GONE);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.loadUrl("file://" + ((RecipeFile) mRecipe).getFilePath());

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

    public void setRecipe(RecipeFile recipe) {
        mRecipe = recipe;
    }
}
