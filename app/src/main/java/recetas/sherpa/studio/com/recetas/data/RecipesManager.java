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
            List<Recipe> temporaryList = new ArrayList<Recipe>();

            String recipesFolderNameLocal = MyApplication.mGeneralContext.getFilesDir().getAbsolutePath() + "/" + Constants.RECIPES_DIRECTORY;
            File recipesFolder = new File(recipesFolderNameLocal);


            for (File recipeDirectory : recipesFolder.listFiles())
            {
                if (recipeDirectory.isDirectory()) {

                    Recipe recipe = null;
                    List<String> images = new ArrayList<>();

                    for (File recipeElement : recipeDirectory.listFiles()) {
                        if (recipeElement.isDirectory() && recipeElement.getName().equals("Imagenes")) {
                            for (File imageElement : recipeElement.listFiles()) {
                                String imagePath = imageElement.getAbsolutePath();
                                images.add(imagePath);
                            }
                        }
                        else
                        {
                            if(recipeElement.getName().endsWith(".txt"))
                            {
                                recipe = new RecipeStepByStep();
                                ((RecipeStepByStep)recipe).parseListIngridientsAndInstructions(recipeElement);
                            }
                            else if(recipeElement.getName().endsWith(".html"))
                            {
                                recipe = new RecipeFile();
                                ((RecipeFile)recipe).setFilePath(recipeElement.getAbsolutePath());
                            }
                            else if(recipeElement.getName().endsWith(".pdf"))
                            {
                                recipe = new RecipeFile();
                                ((RecipeFile)recipe).setFilePath(recipeElement.getAbsolutePath());
                            }
                            else if(recipeElement.getName().endsWith(".doc"))
                            {
                                recipe = new RecipeFile();
                                ((RecipeFile)recipe).setFilePath(recipeElement.getAbsolutePath());
                            }
                            else if(recipeElement.getName().endsWith(".docx"))
                            {
                                recipe = new RecipeFile();
                                ((RecipeFile)recipe).setFilePath(recipeElement.getAbsolutePath());
                            }
                            else
                            {
                                recipe = new Recipe();
                                recipe.setPath(recipeElement.getAbsolutePath());
                            }
                        }
                    }

                    if(recipe != null)
                    {
                        recipe.setTitle(recipeDirectory.getName());
                        recipe.setListPictures(images);
                        temporaryList.add(recipe);
                    }
                }
            }

            mListRecipes.clear();
            mListRecipes.addAll(temporaryList);

            RecipesManager.getInstance().setListReceipes(mListRecipes);
        }
        catch (Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
    }
}
