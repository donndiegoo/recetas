package recetas.sherpa.studio.com.recetas.data;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import recetas.sherpa.studio.com.recetas.Constants;
import recetas.sherpa.studio.com.recetas.MyApplication;

/**
 * Created by diego on 13/12/14.
 */
public class RecipesManager {

    private List<Recipe> mListRecipes;
    private static RecipesManager mInstance;
    private static final String TAG = "RecipesManager";


    private RecipesManager()
    {
        mListRecipes = new ArrayList<Recipe>();
    }

    public static RecipesManager getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new RecipesManager();
        }
        return mInstance;
    }

    public void setListReceipes(List<Recipe> list)
    {
        mListRecipes = list;
    }

    public List<Recipe> getListReceipes() {
        return mListRecipes;
    }

    public void loadRecipesFromCache() {
        try{
            String recipesFolderNameLocal = MyApplication.mGeneralContext.getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY;
            File recipesFolder = new File(recipesFolderNameLocal);


            for (File recipeFile : recipesFolder.listFiles())
            {
                Recipe recipe = new Recipe();
                recipe.setTitle(recipeFile.getName());

                if (recipeFile.isDirectory()) {
                    File recipeDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.getName());
                    for (File recipeElement : recipeDirectory.listFiles()) {
                        if (recipeElement.isDirectory() && recipeElement.getName().equals("Imagenes")) {
                            if (recipeElement.listFiles().length > 0) {
                                File imagesDirectory = new File(recipesFolderNameLocal + "/" + recipeFile.getName() + "/" + "Imagenes");

                                for (File imageElement : imagesDirectory.listFiles()) {
                                    if (!imageElement.isDirectory()) {
                                        String imagePath = recipesFolderNameLocal + "/" + recipeFile.getName() + "/Imagenes/" + imageElement.getName();
                                        recipe.addPicture(imagePath);
                                    }
                                }
                            }
                        }
                    }
                }

                mListRecipes.add(recipe);
            }
            RecipesManager.getInstance().setListReceipes(mListRecipes);
        }
        catch (Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
    }
}
