package recetas.sherpa.studio.com.recetas.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    private Button mOpenAppButton;

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

        mOpenAppButton = (Button) rootView.findViewById(R.id.open_app);

        if(((RecipeFile)mRecipe).getFilePath().endsWith(".pdf"))
        {
            mOpenAppButton.setText(MessageFormat.format(getActivity().getString(R.string.open_external_app), "PDF"));
            mOpenAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDocument(new File(((RecipeFile)mRecipe).getFilePath()), "application/pdf");
                }
            });
        }
        else if(((RecipeFile)mRecipe).getFilePath().endsWith(".doc"))
        {
            mOpenAppButton.setText(MessageFormat.format(getActivity().getString(R.string.open_external_app), "DOC"));
            mOpenAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDocument(new File(((RecipeFile)mRecipe).getFilePath()),"application/msword");
                }
            });
        }
        else if(((RecipeFile)mRecipe).getFilePath().endsWith(".docx"))
        {
            mOpenAppButton.setText(MessageFormat.format(getActivity().getString(R.string.open_external_app), "DOC"));
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
