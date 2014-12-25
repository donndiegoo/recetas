package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.io.File;
import java.text.MessageFormat;

import recetas.sherpa.studio.com.recetas.R;
import recetas.sherpa.studio.com.recetas.data.RecipeFile;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link recetas.sherpa.studio.com.recetas.fragments.RecipeDetailExternalAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailExternalAppFragment extends RecipeDetailBaseFragment {

    private ImageButton mOpenAppButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe recipe
     * @return A new instance of fragment RecipeDetailPDF.
     */
    public static RecipeDetailExternalAppFragment newInstance(RecipeFile recipe) {
        RecipeDetailExternalAppFragment fragment = new RecipeDetailExternalAppFragment();
        fragment.setRecipe(recipe);
        return fragment;
    }

    public RecipeDetailExternalAppFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail_external_app, container, false);

        mOpenAppButton = (ImageButton) rootView.findViewById(R.id.open_app);

        if(((RecipeFile)mRecipe).getFilePath().endsWith(".pdf"))
        {
            //mOpenAppButton.setText(MessageFormat.format(getActivity().getString(R.string.open_external_app), "PDF"));
            mOpenAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDocument(new File(((RecipeFile)mRecipe).getFilePath()), "application/pdf");
                }
            });
        }
        else if(((RecipeFile)mRecipe).getFilePath().endsWith(".doc"))
        {
            //mOpenAppButton.setText(MessageFormat.format(getActivity().getString(R.string.open_external_app), "DOC"));
            mOpenAppButton.setImageResource(R.drawable.icno_word);
            mOpenAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDocument(new File(((RecipeFile)mRecipe).getFilePath()),"application/msword");
                }
            });
        }
        else if(((RecipeFile)mRecipe).getFilePath().endsWith(".docx"))
        {
            //mOpenAppButton.setText(MessageFormat.format(getActivity().getString(R.string.open_external_app), "DOC"));
            mOpenAppButton.setImageResource(R.drawable.icno_word);
            mOpenAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDocument(new File(((RecipeFile)mRecipe).getFilePath()),"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                }
            });
        }


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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mOpenAppButton.getLayoutParams();
        params.height = height;
        mOpenAppButton.setLayoutParams(params);
    }

    public void setRecipe(RecipeFile recipe) {
        mRecipe = recipe;
    }

    public void openDocument(File file, String mimeType)
    {
        String pathApp = getActivity().getFilesDir().getPath();
        String relativePath = file.getPath().replace(pathApp,"");
        Uri uri = Uri.parse("content://recetas.sherpa.studio.com.recetas/" + relativePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        if(getActivity().getPackageManager().queryIntentActivities(intent,Intent.FLAG_ACTIVITY_NO_HISTORY).size() > 0)
        {
            getActivity().startActivity(intent);
        }
        else
        {
            //TODO error handling
        }
    }
}
